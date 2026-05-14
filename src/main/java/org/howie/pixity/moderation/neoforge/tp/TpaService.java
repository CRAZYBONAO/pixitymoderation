package org.howie.pixity.moderation.neoforge.tp;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;
import org.howie.pixity.moderation.neoforge.util.PermissionNumber;
import org.howie.pixity.moderation.neoforge.afk.AfkService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class TpaService {

    public static final String PERM_TPA = "pixity.tpa";
    public static final String PERM_TPAHERE = "pixity.tpahere";
    public static final String PERM_TPACCEPT = "pixity.tpaccept";
    public static final String PERM_TPDENY = "pixity.tpdeny";
    public static final String PERM_TPTOGGLE = "pixity.tptoggle";

    public static final String PERM_TPO = "pixity.tpo";
    public static final String PERM_TPOHERE = "pixity.tpohere";




    private static final int SCAN_MAX = 60;

    private final RankService perms;
    private final TpService tp;
    private final TeleportWarmupManager warmup;
    private final TpaStore store;
    private final AfkService afk;

    private final Map<UUID, TpaRequest> inbound = new ConcurrentHashMap<>();
    private final Map<UUID, UUID> outbound = new ConcurrentHashMap<>();

    private final Map<UUID, Boolean> toggles = new ConcurrentHashMap<>();

    private final Map<String, Long> lastUse = new ConcurrentHashMap<>();

    public TpaService(final RankService perms, final TpService tp, final TeleportWarmupManager warmup, final TpaStore store, final AfkService afk) {
        this.perms = perms;
        this.tp = tp;
        this.warmup = warmup;
        this.store = store;
        this.afk = afk;

        this.toggles.putAll(store.loadToggles());
    }

    public boolean isRequestsOff(final UUID u) {
        if (u == null) return false;
        Boolean b = toggles.get(u);
        return b != null && b.booleanValue();
    }

    public boolean toggleRequests(final ServerPlayer p) {
        UUID u = p.getUUID();
        boolean nowOff = !isRequestsOff(u);
        toggles.put(u, nowOff);
        store.saveToggles(new HashMap<>(toggles));
        return nowOff;
    }

    public boolean sendRequest(final MinecraftServer server, final ServerPlayer from, final ServerPlayer to, final TpaRequest.Type type) {
        if (server == null || from == null || to == null) return false;

        if (isRequestsOff(to.getUUID())) {
            from.sendSystemMessage(LegacyAmpersand.parse("&e&lTELEPORTS &7&l➤ &c" + to.getGameProfile().getName() + " &eis not accepting teleport requests."));
            return false;
        }

        String cdBase = (type == TpaRequest.Type.HERE) ? "pixity.tpaherecd" : "pixity.tpacd";
        int cd = PermissionNumber.highest(perms, from, cdBase, SCAN_MAX);
        if (cd > 0) {
            long now = System.currentTimeMillis();
            String key = from.getUUID() + ":" + cdBase;
            Long last = lastUse.get(key);
            if (last != null) {
                long left = (cd * 1000L) - (now - last);
                if (left > 0) {
                    long sec = (left + 999) / 1000;
                    from.sendSystemMessage(LegacyAmpersand.parse("&e&lTELEPORTS &7&l➤ &eYou must wait &c" + sec + "s &ebefore sending that again."));
                    return false;
                }
            }
            lastUse.put(key, now);
        }

        int timeout = PermissionNumber.highest(perms, from, "pixity.tpatimeout", SCAN_MAX);
        if (timeout <= 0) timeout = 60;

        UUID prevTo = outbound.remove(from.getUUID());
        if (prevTo != null) inbound.remove(prevTo);

        TpaRequest r = new TpaRequest();
        r.from = from.getUUID();
        r.to = to.getUUID();
        r.fromName = from.getGameProfile().getName();
        r.type = type;
        r.expiresAtMs = System.currentTimeMillis() + (timeout * 1000L);

        inbound.put(to.getUUID(), r);
        outbound.put(from.getUUID(), to.getUUID());

        String kind = (type == TpaRequest.Type.HERE) ? "&ewants you to teleport to them" : "&ewants to teleport to you";
        to.sendSystemMessage(LegacyAmpersand.parse("&e&lTELEPORTS &7&l➤ &e" + r.fromName + " " + kind + "&e. Use &a/tpaccept &eor &c/tpdeny."));
        from.sendSystemMessage(LegacyAmpersand.parse("&e&lTELEPORTS &7&l➤ &eRequest sent to " + to.getGameProfile().getName() + " &e(" + timeout + "s)."));
        return true;
    }

    public boolean cancelOutgoing(final ServerPlayer from) {
        UUID to = outbound.remove(from.getUUID());
        if (to == null) {
            from.sendSystemMessage(LegacyAmpersand.parse("&e&lTELEPORTS &7&l➤ &eYou have &cno pending outgoing &erequest!"));
            return false;
        }
        inbound.remove(to);
        from.sendSystemMessage(LegacyAmpersand.parse("&e&lTELEPORTS &7&l➤ &eRequest &ccancelled."));
        return true;
    }

    public boolean deny(final MinecraftServer server, final ServerPlayer to) {
        if (server == null || to == null) return false;
        TpaRequest r = inbound.remove(to.getUUID());
        if (r == null) {
            to.sendSystemMessage(LegacyAmpersand.parse("&e&lTELEPORTS &7&l➤ &eYou have &cno pending outgoing &erequest!"));
            return false;
        }
        outbound.remove(r.from);
        ServerPlayer from = server.getPlayerList().getPlayer(r.from);
        if (from != null) from.sendSystemMessage(LegacyAmpersand.parse("&e&lTELEPORTS &7&l➤ &e" + to.getGameProfile().getName() + "&c denied your request."));
        to.sendSystemMessage(LegacyAmpersand.parse("&e&lTELEPORTS &7&l➤ &cDenied."));
        return true;
    }

    public boolean accept(final MinecraftServer server, final ServerPlayer to) {
        if (server == null || to == null) return false;

        TpaRequest r = inbound.remove(to.getUUID());
        if (r == null) {
            to.sendSystemMessage(LegacyAmpersand.parse("&e&lTELEPORTS &7&l➤ &cNo pending request."));
            return false;
        }
        outbound.remove(r.from);

        ServerPlayer from = server.getPlayerList().getPlayer(r.from);
        if (from == null) {
            to.sendSystemMessage(LegacyAmpersand.parse("&e&lTELEPORTS &7&l➤ &eThat player is &cno longer online&e."));
            return false;
        }

        if (r.type == TpaRequest.Type.TO) {
            WarpPos target = capture(to);
            warmup.request(server, from, target, "to " + to.getGameProfile().getName());
            from.sendSystemMessage(LegacyAmpersand.parse("&e&lTELEPORTS &7&l➤ &eTeleporting..."));
            to.sendSystemMessage(LegacyAmpersand.parse("&e&lTELEPORTS &7&l➤ &aAccepted."));
            return true;
        } else {
            WarpPos target = capture(from);
            warmup.request(server, to, target, "to " + from.getGameProfile().getName());
            from.sendSystemMessage(LegacyAmpersand.parse("&e&lTELEPORTS &7&l➤ &aAccepted. &eTeleporting them..."));
            to.sendSystemMessage(LegacyAmpersand.parse("&e&lTELEPORTS &7&l➤ &eTeleporting..."));
            return true;
        }
    }



    public boolean forceTeleportTo(final MinecraftServer server, final ServerPlayer actor, final ServerPlayer target) {
        if (server == null || actor == null || target == null) return false;
        WarpPos pos = capture(target);
        return tp.teleportNow(server, actor, pos);
    }

    public boolean forceTeleportHere(final MinecraftServer server, final ServerPlayer actor, final ServerPlayer target) {
        if (server == null || actor == null || target == null) return false;
        WarpPos pos = capture(actor);
        return tp.teleportNow(server, target, pos);
    }

    @SubscribeEvent
    public void onServerTick(final ServerTickEvent.Post e) {
        long now = System.currentTimeMillis();

        for (Iterator<Map.Entry<UUID, TpaRequest>> it = inbound.entrySet().iterator(); it.hasNext();) {
            Map.Entry<UUID, TpaRequest> en = it.next();
            TpaRequest r = en.getValue();

            if (r == null || now >= r.expiresAtMs) {
                it.remove();
                if (r != null) outbound.remove(r.from);
            }
        }
    }

    @SubscribeEvent
    public void onLogout(final PlayerEvent.PlayerLoggedOutEvent e) {
        if (!(e.getEntity() instanceof ServerPlayer p)) return;
        UUID u = p.getUUID();

        TpaRequest in = inbound.remove(u);
        if (in != null) outbound.remove(in.from);

        UUID to = outbound.remove(u);
        if (to != null) inbound.remove(to);
    }

    private static WarpPos capture(final ServerPlayer p) {
        WarpPos wp = new WarpPos();
        wp.dimension = p.level().dimension().location().toString();
        wp.x = p.getX();
        wp.y = p.getY();
        wp.z = p.getZ();
        wp.yaw = p.getYRot();
        wp.pitch = p.getXRot();
        return wp;
    }
}
