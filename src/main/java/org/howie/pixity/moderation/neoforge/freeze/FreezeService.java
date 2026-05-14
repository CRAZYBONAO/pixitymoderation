package org.howie.pixity.moderation.neoforge.freeze;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.neoforge.punish.PunishAction;
import org.howie.pixity.moderation.neoforge.punish.PunishmentManager;
import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class FreezeService {

    public static final String PERM_FREEZE = "pixity.freeze";
    public static final String PERM_UNFREEZE = "pixity.unfreeze";
    public static final String PERM_BYPASS = "pixity.freeze.bypass";
    public static final String PERM_NOTIFY = "pixity.freeze.notify";

    private final FreezeStore store;
    private final FreezeConfig config;
    private final PunishmentManager punish;
    private final RankService ranks;

    private final Map<UUID, FreezeRecord> frozen = new ConcurrentHashMap<>();

    public FreezeService(final FreezeStore store, final FreezeConfig config, final PunishmentManager punish, final RankService ranks) {
        this.store = store;
        this.config = config;
        this.punish = punish;
        this.ranks = ranks;

        this.frozen.putAll(store.load());
    }

    public FreezeConfig config() { return config; }

    public boolean isFrozen(final UUID u) { return u != null && frozen.containsKey(u); }

    public FreezeRecord get(final UUID u) { return u == null ? null : frozen.get(u); }

    public Set<String> listFrozenNames() {
        TreeSet<String> out = new TreeSet<>();
        for (FreezeRecord r : frozen.values()) {
            if (r != null && r.playerName != null) out.add(r.playerName);
        }
        return out;
    }

    public boolean freeze(final MinecraftServer server,
                          final ServerPlayer staff,
                          final ServerPlayer target,
                          final String reason) {

        if (server == null || staff == null || target == null) return false;

        FreezeRecord r = new FreezeRecord();
        r.player = target.getUUID();
        r.playerName = target.getGameProfile().getName();
        r.createdAtMs = System.currentTimeMillis();
        r.staffUuid = staff.getUUID();
        r.staffName = staff.getGameProfile().getName();
        r.reason = reason;

        frozen.put(r.player, r);
        store.save(new HashMap<>(frozen));

        punish.logCustom(PunishAction.FREEZE, staff, r.player, r.playerName, null, reason);

        target.sendSystemMessage(
                LegacyAmpersand.parse(
                        "&c&lPUNISHMENTS &7&l➤ &cYou were frozen by &e" +
                                r.staffName
                )
        );

        staff.sendSystemMessage(
                LegacyAmpersand.parse(
                        "&c&lPUNISHMENTS &7&l➤ &cYou have frozen &e" +
                                r.playerName
                )
        );

        return true;
    }

    public boolean unfreeze(final MinecraftServer server,
                            final ServerPlayer staff,
                            final UUID targetUuid,
                            final String targetName,
                            final String reason) {

        FreezeRecord r = frozen.remove(targetUuid);
        if (r == null) return false;

        store.save(new HashMap<>(frozen));

        punish.logCustom(
                PunishAction.UNFREEZE,
                staff,
                targetUuid,
                targetName,
                null,
                reason == null ? "Unfrozen" : reason
        );

        ServerPlayer online =
                server.getPlayerList().getPlayer(targetUuid);

        if (online != null) {
            online.sendSystemMessage(
                    LegacyAmpersand.parse(
                            "&c&lPUNISHMENTS &7&l➤ &aYou have been unfrozen."
                    )
            );
        }

        staff.sendSystemMessage(
                LegacyAmpersand.parse(
                        "&c&lPUNISHMENTS &7&l➤ &aYou have unfrozen &e" +
                                targetName
                )
        );

        return true;
    }

    public void unfreezeSilently(final UUID targetUuid) {
        if (targetUuid == null) return;
        if (frozen.remove(targetUuid) != null) store.save(new HashMap<>(frozen));
    }

    private void notifyStaff(final MinecraftServer server, final String msg) {
        if (server == null) return;

        for (ServerPlayer sp : server.getPlayerList().getPlayers()) {
            try {
                boolean allowed =
                        sp.hasPermissions(2) ||
                                (ranks != null && ranks.hasPerm(sp, PERM_NOTIFY));

                if (allowed) {
                    sp.sendSystemMessage(LegacyAmpersand.parse(msg));
                }
            } catch (Throwable ignored) {}
        }
    }

    public Collection<FreezeRecord> listFrozenRecords() {
        return frozen.values();
    }
}
