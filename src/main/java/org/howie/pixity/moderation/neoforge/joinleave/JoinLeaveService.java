package org.howie.pixity.moderation.neoforge.joinleave;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.chat.DisplayFormatter;
import org.howie.pixity.moderation.chat.NickManager;
import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;
import org.howie.pixity.moderation.neoforge.economy.EconomyService;
import org.howie.pixity.moderation.neoforge.fly.FlyTimeService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class JoinLeaveService {


    private final JoinLeaveConfigManager cfg;
    private final SQLiteJoinLeaveStore store;
    private final RankService perms;
    private final NickManager nick;
    private final EconomyService economy;
    private final FlyTimeService fly;

    private final Map<UUID, PlayerJoinLeave> personal = new ConcurrentHashMap<>();
    private final Set<UUID> seen = ConcurrentHashMap.newKeySet();

    public JoinLeaveService(JoinLeaveConfigManager cfg,
                            SQLiteJoinLeaveStore store,
                            RankService perms,
                            NickManager nick,
                            EconomyService economy,
                            FlyTimeService fly) {

        this.cfg = cfg;
        this.store = store;
        this.perms = perms;
        this.nick = nick;
        this.economy = economy;
        this.fly = fly;

        this.personal.putAll(store.loadMessages());
        this.seen.addAll(store.loadSeen());
    }

    public void reload() {
        cfg.reload();
    }





    public boolean canSet(ServerPlayer p) {
        return perms != null && perms.hasPerm(p, "pixity.joinleave.set");
    }

    public boolean canSetOthers(ServerPlayer p) {
        return perms != null && perms.hasPerm(p, "pixity.joinleave.set.others");
    }

    public boolean canReload(ServerPlayer p) {
        return perms != null && perms.hasPerm(p, "pixity.joinleave.reload");
    }

    public boolean canBypass(ServerPlayer p) {
        return perms != null && perms.hasPerm(p, "pixity.joinleave.bypass");
    }





    public void onJoin(MinecraftServer server, ServerPlayer p) {

        JoinLeaveConfig c = cfg.get();

        boolean bypass = canBypass(p);
        String join = resolveJoin(p);


        if (!bypass && c.firstJoinEnabled && !seen.contains(p.getUUID())) {
            broadcast(server, render(c.firstJoinMessage, server, p));
            seen.add(p.getUUID());
            store.saveSeen(p.getUUID());
        }


        PlayerJoinLeave pj = personal.get(p.getUUID());

        if (pj != null && pj.join != null && !pj.join.isBlank()) {
            broadcast(server, render(pj.join, server, p));
            return;
        }


        if (bypass) return;

        broadcast(server, render(c.joinFormat, server, p));
    }

    public void onLeave(MinecraftServer server, ServerPlayer p) {

        boolean bypass = canBypass(p);

        PlayerJoinLeave pj = personal.get(p.getUUID());

        if (pj != null && pj.leave != null && !pj.leave.isBlank()) {
            broadcast(server, render(pj.leave, server, p));
            return;
        }

        if (bypass) return;

        broadcast(server, render(cfg.get().leaveFormat, server, p));
    }





    public void setJoin(UUID uuid, String msg) {
        PlayerJoinLeave pj = personal.computeIfAbsent(uuid, k -> new PlayerJoinLeave());
        pj.join = msg;
        store.saveMessage(uuid, pj);
    }

    public void setLeave(UUID uuid, String msg) {
        PlayerJoinLeave pj = personal.computeIfAbsent(uuid, k -> new PlayerJoinLeave());
        pj.leave = msg;
        store.saveMessage(uuid, pj);
    }

    public void setJoin(ServerPlayer p, String msg) {
        setJoin(p.getUUID(), msg);
    }

    public void setLeave(ServerPlayer p, String msg) {
        setLeave(p.getUUID(), msg);
    }

    public void clearJoin(ServerPlayer p) {
        PlayerJoinLeave pj = personal.get(p.getUUID());
        if (pj != null) {
            pj.join = null;
            store.saveMessage(p.getUUID(), pj);
        }
    }

    public void clearLeave(ServerPlayer p) {
        PlayerJoinLeave pj = personal.get(p.getUUID());
        if (pj != null) {
            pj.leave = null;
            store.saveMessage(p.getUUID(), pj);
        }
    }





    private String resolveJoin(ServerPlayer p) {
        PlayerJoinLeave pj = personal.get(p.getUUID());
        return (pj != null && pj.join != null && !pj.join.isBlank())
                ? pj.join
                : cfg.get().joinFormat;
    }

    private String resolveLeave(ServerPlayer p) {
        PlayerJoinLeave pj = personal.get(p.getUUID());
        return (pj != null && pj.leave != null && !pj.leave.isBlank())
                ? pj.leave
                : cfg.get().leaveFormat;
    }





    private Component render(final String raw,
                             final MinecraftServer server,
                             final ServerPlayer p) {

        String s = raw == null ? "" : raw;

        int online = server == null ? 0 : server.getPlayerCount();
        s = s.replace("{online}", String.valueOf(online));

        Component display = DisplayFormatter.formatPlayer(p);

        Component hover = org.howie.pixity.moderation.neoforge.util.PlayerHoverUtil
                .buildHover(p, nick, economy, fly);

        Component nameComponent = display.copy().withStyle(style ->
                style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover))
        );

        String[] parts = s.split("\\{player\\}", -1);

        MutableComponent out = Component.literal("");

        for (int i = 0; i < parts.length; i++) {

            if (!parts[i].isEmpty()) {
                out.append(LegacyAmpersand.parse(parts[i]));
            }

            if (i < parts.length - 1) {
                out.append(nameComponent);
            }
        }

        return out;
    }





    private void broadcast(MinecraftServer server, Component msg) {
        if (server != null && msg != null) {
            server.getPlayerList().broadcastSystemMessage(msg, false);
        }
    }


}
