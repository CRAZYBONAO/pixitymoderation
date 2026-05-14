package org.howie.pixity.moderation.neoforge.punish;

import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.minecraft.server.level.ServerPlayer;

public final class BanEnforcementListener {

    private final PunishmentManager punishments;

    public BanEnforcementListener(final PunishmentManager punishments) {
        this.punishments = punishments;
    }

    @SubscribeEvent
    public void onLogin(final PlayerEvent.PlayerLoggedInEvent e) {
        if (!(e.getEntity() instanceof ServerPlayer p)) return;

        punishments.cleanupExpiredBans(System.currentTimeMillis());

        punishments.getActiveBan(p.getUUID()).ifPresent(ban -> {

            p.connection.disconnect(punishments.buildBanMessage(ban));
        });
        String ip = p.connection.getRemoteAddress().toString();

        if (punishments.isIpBanned(ip)) {
            p.connection.disconnect(Component.literal("You are IP banned."));
        }
    }
}
