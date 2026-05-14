package org.howie.pixity.moderation.neoforge.rollback;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import org.apache.logging.log4j.Logger;
import org.howie.pixity.moderation.neoforge.rank.RankService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class RollbackService {


    private final Logger logger;
    private final RollbackConfig cfg;
    private final RollbackStore store;
    private final RankService ranks;

    private volatile List<RollbackEntry> cache;

    private final Map<UUID, OpenContainerSession> openSessions = new ConcurrentHashMap<>();

    private static final class OpenContainerSession {
        ResourceKey<Level> dim;
        BlockPos pos;
        List<String> before;
        String beforeNbt;
        String blockId;
    }

    public RollbackService(Logger logger, RollbackConfig cfg, RollbackStore store, RankService ranks) {
        this.logger = logger;
        this.cfg = cfg;
        this.store = store;
        this.ranks = ranks;
        this.cache = store.load();
        prune();
    }

    public boolean canUse(ServerPlayer p) {
        return ranks != null && ranks.hasPerm(p, cfg.permissionUse);
    }





    public void onOpenContainer(ServerLevel lvl, BlockPos pos, ServerPlayer p, String blockId) {
        if (!cfg.enabled || !cfg.containerRollbackEnabled) return;

        BlockEntity be = lvl.getBlockEntity(pos);
        if (be == null) return;

        OpenContainerSession s = new OpenContainerSession();
        s.dim = lvl.dimension();
        s.pos = pos.immutable();
        s.blockId = blockId;

        if (be instanceof Container c) {
            s.before = snapshotContainer(lvl, c);
        }

        s.beforeNbt = snapshotBlockEntityNbt(lvl, be);

        if (s.before == null && s.beforeNbt == null) return;

        openSessions.put(p.getUUID(), s);
    }

    public void onCloseContainer(MinecraftServer server, ServerPlayer p) {
        if (!cfg.enabled || !cfg.containerRollbackEnabled) return;

        OpenContainerSession s = openSessions.remove(p.getUUID());
        if (s == null) return;

        ServerLevel lvl = server.getLevel(s.dim);
        if (lvl == null) return;

        BlockEntity be = lvl.getBlockEntity(s.pos);
        if (be == null) return;

        List<String> after = (be instanceof Container c) ? snapshotContainer(lvl, c) : null;
        String afterNbt = snapshotBlockEntityNbt(lvl, be);

        if (Objects.equals(after, s.before) && Objects.equals(afterNbt, s.beforeNbt)) return;

        RollbackEntry e = new RollbackEntry();
        e.ts = System.currentTimeMillis();
        e.action = "CONTAINER";
        e.playerUuid = p.getUUID().toString();
        e.playerName = p.getGameProfile().getName();
        e.dim = s.dim.location().toString();
        e.x = s.pos.getX();
        e.y = s.pos.getY();
        e.z = s.pos.getZ();
        e.beforeItems = s.before;
        e.beforeBlockEntityNbt = s.beforeNbt;
        e.blockId = s.blockId;

        append(e);
    }





    public void recordPlace(ServerLevel lvl, BlockPos pos, ServerPlayer p, BlockState beforeState, String blockId) {
        if (!cfg.enabled) return;

        RollbackEntry e = new RollbackEntry();
        e.ts = System.currentTimeMillis();
        e.action = "PLACE";
        e.playerUuid = p.getUUID().toString();
        e.playerName = p.getGameProfile().getName();
        e.dim = lvl.dimension().location().toString();
        e.x = pos.getX();
        e.y = pos.getY();
        e.z = pos.getZ();
        e.beforeBlockState = toSnbt(beforeState);
        e.blockId = blockId;

        append(e);
    }

    public void recordBreak(ServerLevel lvl, BlockPos pos, ServerPlayer p, BlockState beforeState, String blockId) {
        if (!cfg.enabled) return;

        RollbackEntry e = new RollbackEntry();
        e.ts = System.currentTimeMillis();
        e.action = "BREAK";
        e.playerUuid = p.getUUID().toString();
        e.playerName = p.getGameProfile().getName();
        e.dim = lvl.dimension().location().toString();
        e.x = pos.getX();
        e.y = pos.getY();
        e.z = pos.getZ();
        e.beforeBlockState = toSnbt(beforeState);
        e.blockId = blockId;

        append(e);
    }





    public int rollback(MinecraftServer server, UUID target, long sinceMs) {
        long cutoff = System.currentTimeMillis() - sinceMs;

        int applied = 0;

        for (int i = cache.size() - 1; i >= 0; i--) {
            RollbackEntry e = cache.get(i);
            if (e.ts < cutoff) continue;
            if (!target.toString().equals(e.playerUuid)) continue;

            if (apply(server, e)) applied++;
            if (applied >= cfg.maxActionsPerCommand) break;
        }

        return applied;
    }

    private boolean apply(MinecraftServer server, RollbackEntry e) {
        try {
            ResourceKey<Level> dim = ResourceKey.create(
                    net.minecraft.core.registries.Registries.DIMENSION,
                    net.minecraft.resources.ResourceLocation.parse(e.dim)
            );

            ServerLevel lvl = server.getLevel(dim);
            if (lvl == null) return false;

            BlockPos pos = new BlockPos(e.x, e.y, e.z);

            if ("CONTAINER".equalsIgnoreCase(e.action)) {
                BlockEntity be = lvl.getBlockEntity(pos);
                if (be == null) return false;

                if (be instanceof Container c && e.beforeItems != null) {
                    restoreContainer(lvl, c, e.beforeItems);
                }

                if (e.beforeBlockEntityNbt != null) {
                    restoreBlockEntityNbt(lvl, be, e.beforeBlockEntityNbt);
                }

                return true;
            }

            if ("PLACE".equalsIgnoreCase(e.action) || "BREAK".equalsIgnoreCase(e.action)) {
                BlockState st = fromSnbt(e.beforeBlockState, lvl);
                if (st == null) return false;

                lvl.setBlock(pos, st, 3);
                return true;
            }

        } catch (Throwable t) {
            if (logger != null) logger.error("Rollback failed", t);
        }

        return false;
    }





    private static List<String> snapshotContainer(ServerLevel lvl, Container c) {
        List<String> out = new ArrayList<>();
        for (int i = 0; i < c.getContainerSize(); i++) {
            ItemStack s = c.getItem(i);
            CompoundTag tag = new CompoundTag();
            if (!s.isEmpty()) s.save(lvl.registryAccess(), tag);
            out.add(tag.toString());
        }
        return out;
    }

    private static void restoreContainer(ServerLevel lvl, Container c, List<String> items) throws Exception {
        for (int i = 0; i < items.size(); i++) {
            String snbt = items.get(i);
            if (snbt == null || snbt.isEmpty() || "{}".equals(snbt)) {
                c.setItem(i, ItemStack.EMPTY);
            } else {
                CompoundTag tag = TagParser.parseTag(snbt);
                c.setItem(i, ItemStack.parseOptional(lvl.registryAccess(), tag));
            }
        }
    }

    private static String snapshotBlockEntityNbt(ServerLevel lvl, BlockEntity be) {
        try {
            CompoundTag tag = be.saveWithFullMetadata(lvl.registryAccess());
            return tag.toString();
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static void restoreBlockEntityNbt(ServerLevel lvl, BlockEntity be, String snbt) {
        try {
            CompoundTag tag = TagParser.parseTag(snbt);
            be.loadWithComponents(tag, lvl.registryAccess());
        } catch (Throwable ignored) {}
    }

    private static String toSnbt(BlockState st) {
        return NbtUtils.writeBlockState(st).toString();
    }

    private static BlockState fromSnbt(String snbt, ServerLevel lvl) throws Exception {
        CompoundTag tag = TagParser.parseTag(snbt);
        return NbtUtils.readBlockState(lvl.holderLookup(net.minecraft.core.registries.Registries.BLOCK), tag);
    }

    private void append(RollbackEntry e) {
        List<RollbackEntry> list = new ArrayList<>(cache);
        list.add(e);
        cache = list;
        store.save(cache);
    }

    private void prune() {
        long cutoff = System.currentTimeMillis() - (cfg.retentionDays * 86400L * 1000L);

        List<RollbackEntry> list = new ArrayList<>();
        for (RollbackEntry e : cache) {
            if (e.ts >= cutoff) list.add(e);
        }

        cache = list;
        store.save(cache);
    }


}
