package org.howie.pixity.moderation.neoforge.inspect;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import org.apache.logging.log4j.Logger;
import org.howie.pixity.moderation.neoforge.rank.RankService;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class InspectService {


    private final Logger logger;
    private final InspectConfig cfg;
    private final SQLiteBlockLogStore db;
    private final RankService ranks;


    private final Set<UUID> inspectors = ConcurrentHashMap.newKeySet();

    public InspectService(Logger logger,
                          InspectConfig cfg,
                          SQLiteBlockLogStore db,
                          RankService ranks) {

        this.logger = logger;
        this.cfg = cfg;
        this.db = db;
        this.ranks = ranks;
    }




    public InspectConfig config() {
        return cfg;
    }




    public boolean canUse(ServerPlayer p) {
        return p.hasPermissions(2)
                || (ranks != null && ranks.hasPerm(p, cfg.permissionUse))
                || (ranks != null && ranks.hasPerm(p, "pixity.admin"));
    }




    public boolean isInspecting(UUID u) {
        return inspectors.contains(u);
    }

    public boolean toggle(UUID u) {
        if (inspectors.contains(u)) {
            inspectors.remove(u);
            return false;
        } else {
            inspectors.add(u);
            return true;
        }
    }

    public void setInspecting(UUID u, boolean on) {
        if (on) inspectors.add(u);
        else inspectors.remove(u);
    }




    public void recordPlace(ServerLevel level, BlockPos pos, ServerPlayer actor, String blockId) {
        if (cfg == null || !cfg.enabled || !cfg.logPlaces) return;
        record(level, pos, actor, blockId, "PLACE");
    }

    public void recordBreak(ServerLevel level, BlockPos pos, ServerPlayer actor, String blockId) {
        if (cfg == null || !cfg.enabled || !cfg.logBreaks) return;
        record(level, pos, actor, blockId, "BREAK");
    }

    public void recordInteract(ServerLevel level, BlockPos pos, ServerPlayer actor, String blockId) {
        if (cfg == null || !cfg.enabled || !cfg.logInteractions) return;
        record(level, pos, actor, blockId, "INTERACT");
    }

    private void record(ServerLevel level,
                        BlockPos pos,
                        ServerPlayer actor,
                        String blockId,
                        String action) {

        if (level == null || pos == null || actor == null) return;

        try {
            String dim = level.dimension().location().toString();

            BlockLogEntry e = new BlockLogEntry();
            e.ts = System.currentTimeMillis();
            e.action = action;
            e.playerUuid = actor.getUUID().toString();
            e.playerName = actor.getGameProfile().getName();
            e.blockId = blockId;

            db.insert(dim, pos.getX(), pos.getY(), pos.getZ(), e);

        } catch (Exception ex) {
            if (logger != null) {
                logger.error("[Inspect] Failed to record block action", ex);
            }
        }
    }




    public List<BlockLogEntry> get(ServerLevel level, BlockPos pos) {

        if (level == null || pos == null) return List.of();

        try {
            String dim = level.dimension().location().toString();

            return db.get(
                    dim,
                    pos.getX(),
                    pos.getY(),
                    pos.getZ(),
                    cfg.maxEventsPerBlock
            );

        } catch (Exception ex) {
            if (logger != null) {
                logger.error("[Inspect] Failed to fetch logs", ex);
            }
            return List.of();
        }
    }




    public void onServerTick() {

    }


}
