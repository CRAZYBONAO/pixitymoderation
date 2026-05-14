package org.howie.pixity.moderation.neoforge.pokemonhunt;

import net.minecraft.server.level.ServerPlayer;

import net.neoforged.bus.api.SubscribeEvent;

import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class PokemonHuntJoinListener {

    @SubscribeEvent
    public void onJoin(
            PlayerEvent.PlayerLoggedInEvent event
    ) {

        if (
                !(event.getEntity()
                        instanceof ServerPlayer player)
        ) {
            return;
        }

        PokemonHuntBossBarManager.create(
                player
        );
    }

    @SubscribeEvent
    public void onQuit(
            PlayerEvent.PlayerLoggedOutEvent event
    ) {

        if (
                !(event.getEntity()
                        instanceof ServerPlayer player)
        ) {
            return;
        }

        PokemonHuntBossBarManager.remove(
                player.getUUID()
        );
    }
}