package org.howie.pixity.moderation.neoforge.listeners;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import org.howie.pixity.moderation.neoforge.stats.PlayerStatsDatabase;

@EventBusSubscriber
public class PlayerJoinListener {

    @SubscribeEvent
    public static void onJoin(PlayerEvent.PlayerLoggedInEvent event) {

        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }




        PlayerStatsDatabase.updateName(
                player.getUUID(),
                player.getGameProfile().getName()
        );
    }
}