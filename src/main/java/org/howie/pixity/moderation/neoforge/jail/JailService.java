package org.howie.pixity.moderation.neoforge.jail;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityMountEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import org.howie.pixity.moderation.neoforge.punish.PunishAction;
import org.howie.pixity.moderation.neoforge.punish.PunishmentManager;
import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;
import org.howie.pixity.moderation.neoforge.tp.TeleportWarmupManager;
import org.howie.pixity.moderation.neoforge.tp.TpService;
import org.howie.pixity.moderation.neoforge.tp.WarpPos;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class JailService {

    public static final String PERM_SETJAIL = "pixity.setjail";
    public static final String PERM_JAIL = "pixity.jail";
    public static final String PERM_UNJAIL = "pixity.unjail";
    public static final String PERM_JAIL_BYPASS = "pixity.jail.bypass";
    public static final String PERM_JAIL_NOTIFY = "pixity.jail.notify";

    private final SQLiteJailStore store;
    private final JailConfig config;
    private final TpService tp;
    private final TeleportWarmupManager warmup;
    private final PunishmentManager punish;
    private final RankService ranks;

    private final Map<String, WarpPos> jails = new ConcurrentHashMap<>();
    private final Map<UUID, JailRecord> active = new ConcurrentHashMap<>();

    public JailService(final SQLiteJailStore store,
                       final JailConfig config,
                       final TpService tp,
                       final TeleportWarmupManager warmup,
                       final PunishmentManager punish,
                       final RankService ranks) {

        this.store = store;
        this.config = config;
        this.tp = tp;
        this.warmup = warmup;
        this.punish = punish;
        this.ranks = ranks;

        this.jails.putAll(store.loadJails());
        this.active.putAll(store.loadActive());
    }

    private boolean has(ServerPlayer p, String perm) {
        return ranks.hasPerm(p, perm) || ranks.hasPerm(p, "pixity.admin");
    }



    public Set<String> listJailedNames() {
        TreeSet<String> out = new TreeSet<>();
        for (JailRecord r : active.values()) {
            if (r != null && r.playerName != null) out.add(r.playerName);
        }
        return out;
    }

    public Set<String> listJails() {
        return new TreeSet<>(jails.keySet());
    }

    public WarpPos getJailPos(final String name) {
        if (name == null) return null;
        return jails.get(name.toLowerCase(Locale.ROOT));
    }

    public boolean isJailed(final UUID u) {
        return u != null && active.containsKey(u);
    }

    public long remainingSeconds(final UUID uuid) {
        if (uuid == null) return -1L;

        JailRecord rec = active.get(uuid);
        if (rec == null) return -1L;

        if (rec.expiresAtMs < 0) return -1L;

        long now = System.currentTimeMillis();
        long left = rec.expiresAtMs - now;

        return Math.max(0, left / 1000L);
    }

    public void setJail(final ServerPlayer p, final String name) {
        if (p == null || name == null || name.isBlank()) return;

        if (!has(p, PERM_SETJAIL)) {
            LegacyAmpersand.parse("&c&lPUNISHMENTS &7&l➤ &cNo permission.");
            return;
        }

        String key = name.toLowerCase(Locale.ROOT);

        WarpPos pos = new WarpPos();
        pos.dimension = p.level().dimension().location().toString();
        pos.x = p.getX();
        pos.y = p.getY();
        pos.z = p.getZ();
        pos.yaw = p.getYRot();
        pos.pitch = p.getXRot();

        jails.put(key, pos);
        store.saveJail(key, pos);

        p.sendSystemMessage(
                LegacyAmpersand.parse(
                        "&c&lPUNISHMENTS &7&l➤ &aJail &e" + key + " &aset."
                )
        );
    }



    public boolean jail(final MinecraftServer server,
                        final ServerPlayer staff,
                        final ServerPlayer target,
                        final String jailName,
                        final Long durationSeconds,
                        final String reason) {

        if (server == null || staff == null || target == null) return false;

        if (!has(staff, PERM_JAIL)) {
            LegacyAmpersand.parse("&c&lPUNISHMENTS &7&l➤ &cNo permission.");
            return false;
        }

        if (has(target, PERM_JAIL_BYPASS)) {
            staff.sendSystemMessage(LegacyAmpersand.parse("&c&lPUNISHMENTS &7&l➤ That player cannot be jailed."));
            return false;
        }

        WarpPos jailPos = getJailPos(jailName);
        if (jailPos == null) {
            staff.sendSystemMessage(
                    LegacyAmpersand.parse(
                            "&c&lPUNISHMENTS &7&l➤ &cUnknown jail &e" + jailName
                    )
            );
            return false;
        }

        long now = System.currentTimeMillis();
        long expiresAt = (durationSeconds == null || durationSeconds <= 0)
                ? -1L
                : now + (durationSeconds * 1000L);

        JailRecord rec = new JailRecord();
        rec.player = target.getUUID();
        rec.playerName = target.getGameProfile().getName();
        rec.jailName = jailName.toLowerCase(Locale.ROOT);
        rec.expiresAtMs = expiresAt;
        rec.staffUuid = staff.getUUID();
        rec.staffName = staff.getGameProfile().getName();
        rec.reason = reason;

        active.put(rec.player, rec);
        store.saveJailRecord(rec);

        if (punish != null) {
            punish.logCustom(PunishAction.JAIL, staff, rec.player, rec.playerName, durationSeconds, reason);
        }

        tp.teleportNow(server, target, jailPos);

        target.sendSystemMessage(
                LegacyAmpersand.parse(
                        "&c&lPUNISHMENTS &7&l➤ &cYou were jailed by &e" +
                                rec.staffName +
                                (reason != null ? " &cfor &e" + reason : "")
                )
        );

        staff.sendSystemMessage(
                LegacyAmpersand.parse(
                        "&c&lPUNISHMENTS &7&l➤ &cYou jailed &e" +
                                rec.playerName
                )
        );

        return true;
    }

    public boolean unjail(final MinecraftServer server,
                          final ServerPlayer staff,
                          final UUID targetUuid,
                          final String targetName,
                          final String reason) {

        if (!has(staff, PERM_UNJAIL)) {
            LegacyAmpersand.parse("&c&lPUNISHMENTS &7&l➤ &cNo permission.");
            return false;
        }

        JailRecord rec = active.remove(targetUuid);
        if (rec == null) return false;

        store.remove(targetUuid);

        if (punish != null) {
            punish.logCustom(PunishAction.UNJAIL, staff, targetUuid, targetName, null, reason);
        }

        ServerPlayer online = server.getPlayerList().getPlayer(targetUuid);
        if (online != null) {
            WarpPos spawn = tp.getSpawnPos();
            if (spawn != null) {
                tp.teleportNow(server, online, spawn);
            }
            online.sendSystemMessage(
                    LegacyAmpersand.parse(
                            "&c&lPUNISHMENTS &7&l➤ &aYou have been released from jail."
                    )
            );
        }

        staff.sendSystemMessage(
                LegacyAmpersand.parse(
                        "&c&lPUNISHMENTS &7&l➤ &aYou unjailed &e" +
                                targetName
                )
        );

        return true;
    }



    @SubscribeEvent
    public void onServerTick(final ServerTickEvent.Post e) {
        tickExpire(e.getServer());
    }

    public void tickExpire(final MinecraftServer server) {
        long now = System.currentTimeMillis();

        for (Iterator<Map.Entry<UUID, JailRecord>> it = active.entrySet().iterator(); it.hasNext();) {
            Map.Entry<UUID, JailRecord> en = it.next();
            JailRecord rec = en.getValue();

            if (rec.expiresAtMs > 0 && now >= rec.expiresAtMs) {
                it.remove();
                store.remove(en.getKey());

                ServerPlayer online = server.getPlayerList().getPlayer(en.getKey());
                if (online != null) {
                    WarpPos spawn = tp.getSpawnPos();
                    if (spawn != null) {
                        tp.teleportNow(server, online, spawn);
                    }

                    online.sendSystemMessage(
                            LegacyAmpersand.parse(
                                    "&c&lPUNISHMENTS &7&l➤ &aYour jail sentence has ended."
                            )
                    );
                    notifyStaff(server, "&c&lPUNISHMENTS &7&l➤ &cThe player &e" + online.getGameProfile().getName() + "'s &cjail sentence has ended and they have been auto-released.");
                }
            }
        }
    }

    public JailConfig getConfig() {
        return config;
    }

    public JailRecord getActive(UUID uuid) {
        return active.get(uuid);
    }

    private void notifyStaff(final MinecraftServer server, final String msg) {
        if (server == null || !config.notifyStaff) return;

        for (ServerPlayer sp : server.getPlayerList().getPlayers()) {
            if (has(sp, PERM_JAIL_NOTIFY)) {
                sp.sendSystemMessage(LegacyAmpersand.parse(msg));
            }
        }
    }

    public void deleteJail(String name) {
        if (name == null) return;

        jails.remove(name.toLowerCase(Locale.ROOT));
        store.deleteJail(name.toLowerCase(Locale.ROOT));
    }


    @SubscribeEvent
    public void onPlayerTick(EntityTickEvent.Post event) {

        if (!(event.getEntity() instanceof ServerPlayer player))
            return;

        JailRecord rec = active.get(player.getUUID());
        if (rec == null)
            return;

        WarpPos pos = jails.get(rec.jailName);
        if (pos == null)
            return;

        player.getAbilities().flying = false;
        player.getAbilities().mayfly = false;

        double dx = Math.abs(player.getX() - pos.x);
        double dy = Math.abs(player.getY() - pos.y);
        double dz = Math.abs(player.getZ() - pos.z);

        if (dx > 3 || dy > 3 || dz > 3) {

            player.teleportTo(
                    pos.x + 0.5,
                    pos.y,
                    pos.z + 0.5
            );

            player.setDeltaMovement(0, 0, 0);
        }
    }

    @SubscribeEvent
    public void onMount(EntityMountEvent event) {

        if (!(event.getEntityMounting() instanceof ServerPlayer player))
            return;

        if (!active.containsKey(player.getUUID()))
            return;

        event.setCanceled(true);
    }

}