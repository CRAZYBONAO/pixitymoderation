package org.howie.pixity.moderation.neoforge.worldboss;

import net.neoforged.bus.api.SubscribeEvent;

import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import net.minecraft.server.level.ServerPlayer;

public class WorldBossJoinListener {





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





        if (!WorldBossManager.isActive()) {
            return;
        }





        try {

            WorldBossBossBar.update(
                    player.server
            );

            System.out.println(
                    "[WorldBoss] Re-added boss bar to: "
                            + player.getGameProfile()
                            .getName()
            );

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}