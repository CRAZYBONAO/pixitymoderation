package org.howie.pixity.moderation.neoforge.tp.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;

import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;
import org.howie.pixity.moderation.neoforge.tp.TeleportWarmupManager;
import org.howie.pixity.moderation.neoforge.tp.TpService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class TpChatPromptService {

    public enum PromptType {
        HOMES,
        PWARPS,
        RENAME_WARP,
        SET_DESC
    }

    private static final class Pending {
        PromptType type;
        String data;

        Pending(PromptType type, String data) {
            this.type = type;
            this.data = data;
        }
    }

    private final TpService tp;
    private final TeleportWarmupManager warmup;
    private final Map<UUID, Pending> pending = new ConcurrentHashMap<>();

    public TpChatPromptService(TpService tp, TeleportWarmupManager warmup) {
        this.tp = tp;
        this.warmup = warmup;
    }



    public void requestHomesSearch(ServerPlayer p, String current) {
        pending.put(p.getUUID(), new Pending(PromptType.HOMES, current));
        p.sendSystemMessage(LegacyAmpersand.parse("&a&lHOMES &7&l➤ &eType home search (or 'cancel')"));
    }

    public void requestPWarpsSearch(ServerPlayer p, String current) {
        pending.put(p.getUUID(), new Pending(PromptType.PWARPS, current));
        p.sendSystemMessage(LegacyAmpersand.parse("&d&lPLAYER WARPS &7&l➤ &eType pwarp search (or 'cancel')"));
    }

    public void requestRenameWarp(ServerPlayer p, String oldName) {
        pending.put(p.getUUID(), new Pending(PromptType.RENAME_WARP, oldName));
        p.sendSystemMessage(LegacyAmpersand.parse("&d&lPLAYER WARPS &7&l➤ &eType new warp name (or 'cancel')"));
    }

    public void requestWarpDescription(ServerPlayer p, String warpName) {
        pending.put(p.getUUID(), new Pending(PromptType.SET_DESC, warpName));
        p.sendSystemMessage(LegacyAmpersand.parse("&d&lPLAYER WARPS &7&l➤ &eType warp description (or 'cancel')"));
    }



    public boolean tryConsume(MinecraftServer server, ServerPlayer p, String msg) {
        Pending pen = pending.remove(p.getUUID());
        if (pen == null) return false;

        String input = msg == null ? "" : msg.trim();

        if (input.equalsIgnoreCase("cancel")) {
            reopenMenu(p, pen);
            return true;
        }

        switch (pen.type) {

            case HOMES -> openHomes(p, filter(tp.listHomes(p), input), input);

            case PWARPS -> openPWarps(p, filter(tp.listPlayerWarps(), input), input);

            case RENAME_WARP -> {
                boolean ok = tp.renamePlayerWarp(p, pen.data, input);
                p.sendSystemMessage(LegacyAmpersand.parse(ok ? "&d&lPLAYER WARPS &7&l➤ &aRenamed warp!" : "&d&lPLAYER WARPS &7&l➤ &cRename failed."));
                openPWarps(p, tp.listPlayerWarps(), "");
            }

            case SET_DESC -> {
                var w = tp.getPlayerWarp(pen.data);
                if (w != null) {
                    w.description = input;

                    tp.savePlayerWarp(w);

                    p.sendSystemMessage(LegacyAmpersand.parse("&d&lPLAYER WARPS &7&l➤ &aDescription updated!"));
                }
                openPWarps(p, tp.listPlayerWarps(), "");
            }
        }

        return true;
    }

    private void reopenMenu(ServerPlayer p, Pending pen) {
        if (pen.type == PromptType.HOMES)
            openHomes(p, tp.listHomes(p), "");
        else
            openPWarps(p, tp.listPlayerWarps(), "");
    }

    private static List<String> filter(List<String> base, String q) {
        if (q == null || q.isBlank()) return base;

        List<String> out = new ArrayList<>();
        for (String s : base)
            if (s != null && s.toLowerCase().contains(q.toLowerCase()))
                out.add(s);

        return out;
    }



    public void openHomes(ServerPlayer p, List<String> list, String query) {
        MenuProvider prov = new SimpleMenuProvider(
                (id, inv, pl) -> new PixityHomesMenu(id, inv, tp, warmup, this, list, 0, query),
                LegacyAmpersand.parse("&a&lHOMES"));
        ;
        p.openMenu(prov);
    }

    public void openPWarps(ServerPlayer p, List<String> list, String query) {
        MenuProvider prov = new SimpleMenuProvider(
                (id, inv, pl) -> new PixityPWarpsMenu(id, inv, tp, warmup, this, list, 0, query, false),
                LegacyAmpersand.parse("&d&lPLAYER WARPS")
        );
        p.openMenu(prov);
    }
}