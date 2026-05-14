package org.howie.pixity.moderation.neoforge.pokemon;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import net.minecraft.server.level.ServerPlayer;

public class PlayerJoinListener {

    @SubscribeEvent
    public void onJoin(PlayerEvent.PlayerLoggedInEvent e) {

        if (!(e.getEntity() instanceof ServerPlayer player)) return;

        PokedexDatabase.updateName(
                player.getUUID(),
                player.getName().getString()
        );
    }
}