package org.howie.pixity.moderation.neoforge.alts.smart;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;
import org.howie.pixity.moderation.neoforge.util.NameResolver;

import java.net.SocketAddress;
import java.util.List;
import java.util.UUID;
import java.util.Set;


public final class SmartAltListener {

    private final SmartAltService smart;
    private final RankService ranks;

    public SmartAltListener(final SmartAltService smart, final RankService ranks) {
        this.smart = smart;
        this.ranks = ranks;
    }

    private boolean has(ServerPlayer p, String perm) {
        return ranks.hasPerm(p, perm) || ranks.hasPerm(p, "pixity.admin");
    }

    @SubscribeEvent
    public void onLogin(final PlayerEvent.PlayerLoggedInEvent e) {

        if (!(e.getEntity() instanceof ServerPlayer sp)) return;

        SmartAltConfig cfg = SmartAltConfigStore.get();
        if (cfg == null || !cfg.enabled) return;

        try {
            SocketAddress addr = sp.connection != null
                    ? sp.connection.getConnection().getRemoteAddress()
                    : null;

            if (addr == null) return;

            String masked = SmartAltService.maskAddress(addr);
            if (masked == null) return;

            if (cfg.ignoreLocal) {
                if (masked.startsWith("127.") || masked.startsWith("0.") || masked.equals("::1::/64")) return;
            }


            smart.record(sp.getUUID(), masked, System.currentTimeMillis());


            List<UUID> suggestions = smart.suggest(sp.getUUID(), Set.of());

            if (suggestions.isEmpty()) return;

            MinecraftServer server = sp.server;

            String playerName = sp.getGameProfile().getName();

            for (UUID altId : suggestions) {

                String altName = NameResolver.nameOrUuid(server, altId);
                if (altName == null) altName = altId.toString();

                String msg = "&c&lSMART ALTS &7&l➤ &e" + playerName +
                        " &7may have an alt named &e" + altName;

                for (ServerPlayer staff : server.getPlayerList().getPlayers()) {

                    if (!has(staff, "pixity.smartalts.alerts")) continue;

                    staff.sendSystemMessage(LegacyAmpersand.parse(msg));
                }
            }

        } catch (Throwable ignored) {}
    }
}