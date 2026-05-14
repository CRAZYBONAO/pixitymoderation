package org.howie.pixity.moderation.neoforge.kits.firstjoin;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.minecraft.server.level.ServerPlayer;

public final class FirstJoinListener {

    private final FirstJoinService service;

    public FirstJoinListener(final FirstJoinService service) {
        this.service = service;
    }

    @SubscribeEvent
    public void onJoin(final PlayerEvent.PlayerLoggedInEvent e) {

        if (!(e.getEntity() instanceof ServerPlayer player)) return;

        service.handleJoin(player.server, player);
    }


}