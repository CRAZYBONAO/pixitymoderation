package org.howie.pixity.moderation.neoforge.tab;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundTabListPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import org.howie.pixity.moderation.chat.NickHolder;
import org.howie.pixity.moderation.neoforge.chat.cosmetics.ChatCosmeticsService;
import org.howie.pixity.moderation.neoforge.economy.EconomyService;
import org.howie.pixity.moderation.neoforge.fly.FlyTimeService;
import org.howie.pixity.moderation.neoforge.mixin.ServerPlayerAccessor;
import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.state.PlayerStateManager;
import org.howie.pixity.moderation.neoforge.afk.AfkService;
import org.howie.pixity.moderation.chat.NickManager;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;
import org.howie.pixity.moderation.tab.TabListConfigManager;

import java.util.*;

public final class TabListTicker {


    private final RankService ranks;
    private final PlayerStateManager states;
    private final AfkService afk;
    private final PixitySidebar sidebar;
    private final ChatCosmeticsService cosmetics;

    private int tick = 0;
    private int anim = 0;

    public TabListTicker(TabListConfigManager cfgMgr,
                         RankService ranks,
                         PlayerStateManager states,
                         AfkService afk,
                         NickManager nick,
                         EconomyService economy,
                         FlyTimeService fly,
                         ChatCosmeticsService cosmetics) {

        this.ranks = ranks;
        this.states = states;
        this.afk = afk;
        this.sidebar = new PixitySidebar(economy, fly, ranks);
        this.cosmetics = cosmetics;
    }

    @SubscribeEvent
    public void onTick(ServerTickEvent.Post e) {
        MinecraftServer server = e.getServer();
        if (server == null) return;

        sidebar.tick(server);

        tick++;
        if (tick % 5 != 0) return;

        anim++;

        applyTeams(server);

        for (ServerPlayer viewer : server.getPlayerList().getPlayers()) {
            buildTab(viewer, server);
        }


    }


    private void buildTab(ServerPlayer viewer, MinecraftServer server) {

        for (ServerPlayer p : server.getPlayerList().getPlayers()) {

            if (hidden(viewer, p)) continue;

            viewer.connection.send(
                    new ClientboundPlayerInfoUpdatePacket(
                            ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME,
                            p
                    )
            );
        }

        viewer.connection.send(new ClientboundTabListPacket(
                header(server),
                footer(server)
        ));
    }


    @SubscribeEvent
    public void onJoin(PlayerEvent.PlayerLoggedInEvent e) {
        ServerPlayer p = (ServerPlayer)e.getEntity();

        p.server.getPlayerList().getPlayers().forEach(viewer ->
                viewer.connection.send(
                        new ClientboundPlayerInfoUpdatePacket(
                                ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME,
                                p
                        )
                )
        );
    }

    private void applyTeams(MinecraftServer server) {

        Scoreboard sb = server.getScoreboard();
        if (sb == null) return;

        for (ServerPlayer p : server.getPlayerList().getPlayers()) {

            int weight = ranks.weight(p);
            int sort = 999 - weight;

            String teamName =
                    String.format("%03d_%s",
                            sort,
                            p.getGameProfile().getName());

            PlayerTeam team = sb.getPlayerTeam(teamName);
            if (team == null) {
                team = sb.addPlayerTeam(teamName);
            }




            String realName = p.getGameProfile().getName();

            PlayerTeam current = sb.getPlayersTeam(realName);

            if (current != team) {

                if (current != null) {
                    sb.removePlayerFromTeam(realName, current);
                }

                sb.addPlayerToTeam(realName, team);

            }

        }
    }



    private ChatFormatting nearest(String hex) {

        if (hex == null || hex.isEmpty()) {
            return ChatFormatting.WHITE;
        }

        int rgb = Integer.parseInt(hex.replace("#",""),16);

        int r = (rgb >> 16) & 255;
        int g = (rgb >> 8) & 255;
        int b = rgb & 255;

        if (r > 200 && g < 80 && b < 80) return ChatFormatting.RED;
        if (r < 80 && g > 200 && b < 80) return ChatFormatting.GREEN;
        if (r < 80 && g < 80 && b > 200) return ChatFormatting.BLUE;

        if (r > 200 && g > 200 && b < 80) return ChatFormatting.YELLOW;
        if (r > 200 && g < 80 && b > 200) return ChatFormatting.LIGHT_PURPLE;
        if (r < 80 && g > 200 && b > 200) return ChatFormatting.AQUA;

        return ChatFormatting.WHITE;
    }

    private boolean hidden(ServerPlayer viewer, ServerPlayer target) {
        if (states == null) return false;
        if (!states.isVanished(target.getUUID())) return false;

        return !hasPerm(viewer, "pixity.vanish.see");
    }

    private boolean hasPerm(ServerPlayer p, String node) {
        return ranks != null && ranks.hasPerm(p, node);
    }

    private String safe(String s) {
        return (s == null) ? "" : s;
    }


    private String getDisplayName(ServerPlayer p) {

        String raw = (NickHolder.INSTANCE.getDisplayName(p) != null)
                ? NickHolder.INSTANCE.getDisplayName(p)
                : p.getGameProfile().getName();

        boolean colored =
                raw.contains("§") ||
                        raw.contains("&") ||
                        raw.contains("&#");

        String name = colored
                ? parseColors(raw)
                : "§f" + raw;

        if (afk != null && afk.isAfk(p.getUUID())) {
            name = "§8[§7AFK§8] §r" + name;
        }

        return "";
    }

    private String buildPrefix(ServerPlayer p) {
        String icon = glyph(p);
        String lpPrefix = (ranks != null) ? safe(ranks.prefix(p)) : "";
        return icon + " " + lpPrefix + "§r";
    }

    private String glyph(ServerPlayer p) {
        if (hasPerm(p, "pixity.owner")) return "\uE001";
        if (hasPerm(p, "pixity.admin")) return "\uE002";
        if (hasPerm(p, "pixity.mod")) return "\uE003";
        if (hasPerm(p, "pixity.gymleader")) return "\uE010";
        if (hasPerm(p, "pixity.donator")) return "\uE020";
        return "\uE000";
    }

    private String gradient(String text) {
        String[] colors = {"§c","§6","§e","§a","§b","§d"};
        StringBuilder out = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            out.append(colors[(i + anim) % colors.length])
                    .append(text.charAt(i));
        }

        return out.toString();
    }



    private Component header(MinecraftServer server) {
        return Component.literal(
                "§6Pixity Network\n" +
                        "§7TPS: §a" + tps(server) +
                        " §7Players: §b" + server.getPlayerList().getPlayerCount()
        );
    }

    private Component footer(MinecraftServer server) {
        String[] f = {
                "§7store.pixity.net",
                "§7discord.gg/pixity",
                "§eWelcome to Pixity!"
        };
        return Component.literal(f[(anim / 20) % f.length]);
    }

    private String tps(MinecraftServer server) {
        try {
            long[] t = server.getTickTimesNanos();
            long total = 0;
            for (long l : t) total += l;

            double mspt = (total / (double) t.length) / 1_000_000.0;
            double tps = Math.min(20.0, 1000.0 / mspt);

            return String.format("%.1f", tps);
        } catch (Throwable e) {
            return "20.0";
        }
    }

    private String parseColors(String text) {
        return org.howie.pixity.moderation.neoforge.text
                .LegacyAmpersand
                .parse(text)
                .getString();
    }

    private String redWhiteGradient(String text) {

        StringBuilder out = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {

            boolean red = ((i + anim) % 6) < 3;

            out.append(red ? "§c" : "§f")
                    .append(text.charAt(i));
        }

        return out.toString();
    }

}
