package org.howie.pixity.moderation.neoforge.punish;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import org.apache.logging.log4j.Logger;
import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class PunishmentManager {

    private final Logger logger;
    private final SQLitePunishStore store;
    private final RankService ranks;

    private final Map<UUID, ActiveBan> bans = new ConcurrentHashMap<>();
    private final Set<String> bannedIps = ConcurrentHashMap.newKeySet();

    public static final String PERM_STAFF_ALERTS = "pixity.staff.alerts";

    public PunishmentManager(final Logger logger, final SQLitePunishStore store, final RankService ranks) {
        this.logger = logger;
        this.store = store;
        this.ranks = ranks;

        this.bans.putAll(store.loadBans());
        this.bannedIps.addAll(store.loadIpBans());
        cleanupExpiredBans(System.currentTimeMillis());
    }





    public void warn(ServerPlayer staff, ServerPlayer target, String reason) {

        store.insertHistory(baseEntry(PunishAction.WARN, staff, target, null, reason));

        staff.server.getPlayerList().broadcastSystemMessage(
                LegacyAmpersand.parse(
                        "&c&lPUNISHMENTS &7&l➤ &e" +
                                staff.getName().getString() +
                                " &chas warned &e" +
                                target.getName().getString() +
                                " &cfor &e" +
                                reason
                ),
                false
        );

    }





    public void kick(MinecraftServer server,
                     ServerPlayer staff,
                     ServerPlayer target,
                     String reason) {

        store.insertHistory(baseEntry(PunishAction.KICK, staff, target, null, reason));

        server.getPlayerList().broadcastSystemMessage(
                LegacyAmpersand.parse(
                        "&c&lPUNISHMENTS &7&l➤ &e" +
                                staff.getName().getString() +
                                " &chas kicked &e" +
                                target.getName().getString() +
                                " &cfor &e" +
                                reason
                ),
                false
        );

        target.connection.disconnect(
                LegacyAmpersand.parse(
                        "&c&lPUNISHMENTS\n" +
                                "&7You were kicked by &e" +
                                staff.getName().getString() +
                                "\n&7Reason: &e" + reason
                )
        );
    }





    public void ban(MinecraftServer server,
                    ServerPlayer staff,
                    UUID targetUuid,
                    String targetName,
                    Long durationSeconds,
                    String reason) {

        long now = System.currentTimeMillis();
        long expiresAt = durationSeconds == null ? -1 : now + durationSeconds * 1000;

        ActiveBan ban = new ActiveBan();
        ban.targetUuid = targetUuid;
        ban.targetNameLower = targetName.toLowerCase();
        ban.createdAtMs = now;
        ban.expiresAtMs = expiresAt;
        ban.reason = reason;
        ban.staffUuid = staff.getUUID();
        ban.staffName = staff.getName().getString();

        bans.put(targetUuid, ban);
        store.saveBan(ban);

        store.insertHistory(
                baseBanEntry(staff, targetUuid, targetName, durationSeconds, reason)
        );

        String message;

        if (durationSeconds == null) {
            message =
                    "&c&lPUNISHMENTS &7&l➤ &e" +
                            staff.getName().getString() +
                            " &chas permanently banned &e" +
                            targetName +
                            " &cfor &e" +
                            reason;
        } else {
            message =
                    "&c&lPUNISHMENTS &7&l➤ &e" +
                            staff.getName().getString() +
                            " &chas temp banned &e" +
                            targetName +
                            " &cfor &e" +
                            reason +
                            " &cfor &e" +
                            formatDuration(durationSeconds);
        }

        server.getPlayerList().broadcastSystemMessage(
                LegacyAmpersand.parse(message),
                false
        );

        ServerPlayer online = server.getPlayerList().getPlayer(targetUuid);
        if (online != null) {
            online.connection.disconnect(buildBanMessage(ban));
        }
    }





    public void ipBan(MinecraftServer server,
                      ServerPlayer staff,
                      ServerPlayer target,
                      String reason) {

        String ip = target.connection.getRemoteAddress().toString();

        bannedIps.add(ip);
        store.saveIpBan(ip, reason, staff.getName().getString());

        store.insertHistory(
                baseEntry(PunishAction.IPBAN, staff, target, null, reason)
        );

        server.getPlayerList().broadcastSystemMessage(
                LegacyAmpersand.parse(
                        "&c&lPUNISHMENTS &7&l➤ &e" +
                                staff.getName().getString() +
                                " &chas IP banned &e" +
                                target.getName().getString() +
                                " &cfor &e" +
                                reason
                ),
                false
        );

        target.connection.disconnect(
                LegacyAmpersand.parse("&cYou are IP banned: &e" + reason)
        );
    }





    public boolean unban(MinecraftServer server,
                         ServerPlayer staff,
                         UUID targetUuid,
                         String targetName) {

        if (!bans.containsKey(targetUuid))
            return false;

        bans.remove(targetUuid);
        store.removeBan(targetUuid);

        String staffName = staff == null
                ? "Console"
                : staff.getName().getString();

        server.getPlayerList().broadcastSystemMessage(
                LegacyAmpersand.parse(
                        "&c&lPUNISHMENTS &7&l➤ &e" +
                                staffName +
                                " &chas unbanned &e" +
                                targetName
                ),
                false
        );

        return true;
    }





    public Optional<ActiveBan> getActiveBan(UUID target) {
        ActiveBan b = bans.get(target);

        if (b == null) return Optional.empty();

        if (b.isExpired(System.currentTimeMillis())) {
            bans.remove(target);
            store.removeBan(target);
            return Optional.empty();
        }

        return Optional.of(b);
    }

    public boolean isIpBanned(String ip) {
        return bannedIps.contains(ip);
    }

    public void cleanupExpiredBans(long nowMs) {
        for (Iterator<Map.Entry<UUID, ActiveBan>> it = bans.entrySet().iterator(); it.hasNext();) {
            Map.Entry<UUID, ActiveBan> e = it.next();

            if (e.getValue().isExpired(nowMs)) {
                store.removeBan(e.getKey());
                it.remove();
            }
        }
    }





    public List<PunishEntry> historyFor(UUID targetUuid) {
        return store.getHistory(targetUuid);
    }





    public Component buildBanMessage(ActiveBan ban) {

        if (ban.isPermanent()) {
            return LegacyAmpersand.parse(
                    "&c&lPUNISHMENTS\n" +
                            "&7You are permanently banned\n" +
                            "&7Reason: &e" + ban.reason
            );
        }

        long remaining =
                Math.max(0, ban.expiresAtMs - System.currentTimeMillis());

        return LegacyAmpersand.parse(
                "&c&lPUNISHMENTS\n" +
                        "&7Temp banned for &e" +
                        formatDuration(remaining / 1000) +
                        "\n&7Reason: &e" +
                        ban.reason
        );
    }





    private static String formatDuration(long seconds) {

        long s = seconds % 60;
        long m = (seconds / 60) % 60;
        long h = (seconds / 3600) % 24;
        long d = seconds / 86400;

        if (d > 0) return d + "d " + h + "h";
        if (h > 0) return h + "h " + m + "m";
        if (m > 0) return m + "m " + s + "s";
        return s + "s";
    }

    private PunishEntry baseEntry(
            PunishAction action,
            ServerPlayer staff,
            ServerPlayer target,
            Long duration,
            String reason) {

        PunishEntry e = new PunishEntry();
        e.tsEpochMs = System.currentTimeMillis();
        e.action = action;
        e.staffUuid = staff.getUUID();
        e.staffName = staff.getName().getString();
        e.targetUuid = target.getUUID();
        e.targetName = target.getName().getString();
        e.durationSeconds = duration;
        e.reason = reason;
        return e;
    }

    private PunishEntry baseBanEntry(
            ServerPlayer staff,
            UUID target,
            String targetName,
            Long duration,
            String reason) {

        PunishEntry e = new PunishEntry();
        e.tsEpochMs = System.currentTimeMillis();
        e.action = PunishAction.BAN;
        e.staffUuid = staff.getUUID();
        e.staffName = staff.getName().getString();
        e.targetUuid = target;
        e.targetName = targetName;
        e.durationSeconds = duration;
        e.reason = reason;
        return e;
    }

    public void logCustom(PunishAction action,
                          ServerPlayer staff,
                          UUID targetUuid,
                          String targetName,
                          Long durationSeconds,
                          String reason) {

        PunishEntry e = new PunishEntry();
        e.tsEpochMs = System.currentTimeMillis();
        e.action = action;
        e.staffUuid = staff.getUUID();
        e.staffName = staff.getName().getString();
        e.targetUuid = targetUuid;
        e.targetName = targetName;
        e.durationSeconds = durationSeconds;
        e.reason = reason;

        store.insertHistory(e);
    }
}