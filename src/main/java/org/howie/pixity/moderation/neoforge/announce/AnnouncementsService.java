package org.howie.pixity.moderation.neoforge.announce;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;
import org.howie.pixity.moderation.neoforge.tp.TpService;

import java.util.List;
import java.util.Locale;

public final class AnnouncementsService {


    public static final String PERM_ANNOUNCE = "pixity.announce";
    public static final String PERM_BYPASS = "pixity.announce.bypass";

    private volatile AnnouncementsConfig cfg;
    private final TpService perms;

    private int index = 0;

    public AnnouncementsService(final AnnouncementsConfig cfg, final TpService perms) {
        this.cfg = cfg;
        this.perms = perms;
    }

    public void setConfig(final AnnouncementsConfig cfg) {
        if (cfg != null) {
            this.cfg = cfg;
            this.index = 0;
        }
    }

    public AnnouncementsConfig config() { return cfg; }

    public void broadcastNow(final MinecraftServer server, final String raw) {
        if (server == null || raw == null) return;
        deliver(server, raw);
    }

    public void broadcastNext(final MinecraftServer server) {
        if (server == null || cfg == null || !cfg.enabled) return;

        List<String> msgs = cfg.messages;
        if (msgs == null || msgs.isEmpty()) return;

        if (index >= msgs.size()) index = 0;

        deliver(server, msgs.get(index++));
    }

    private void deliver(final MinecraftServer server, final String raw) {

        String mode = cfg.mode == null ? "CHAT" : cfg.mode.toUpperCase(Locale.ROOT);

        String formatted = format(raw);
        Component msg = LegacyAmpersand.parse(formatted);

        for (ServerPlayer p : server.getPlayerList().getPlayers()) {




            switch (mode) {

                case "TITLE" -> sendTitle(p, raw);

                case "ACTIONBAR" -> p.sendSystemMessage(msg, true);

                default -> p.sendSystemMessage(msg);
            }
        }
    }

    private String format(String msg) {
        return "&c&lANNOUNCEMENTS &7&l➤ &f" + msg;
    }

    private void sendTitle(ServerPlayer p, String raw) {
        try {
            String title = raw;
            String subtitle = "";

            if (raw.contains("||")) {
                String[] split = raw.split("\\Q||\\E", 2);
                title = split[0];
                subtitle = split.length > 1 ? split[1] : "";
            }

            Component t = LegacyAmpersand.parse(title);
            Component st = subtitle.isEmpty() ? Component.empty() : LegacyAmpersand.parse(subtitle);

            p.connection.send(new ClientboundClearTitlesPacket(false));
            p.connection.send(new ClientboundSetTitlesAnimationPacket(
                    cfg.titleFadeInTicks,
                    cfg.titleStayTicks,
                    cfg.titleFadeOutTicks
            ));
            p.connection.send(new ClientboundSetTitleTextPacket(t));

            if (!subtitle.isEmpty()) {
                p.connection.send(new ClientboundSetSubtitleTextPacket(st));
            }

        } catch (Exception e) {
            p.sendSystemMessage(LegacyAmpersand.parse(raw));
        }
    }


}
