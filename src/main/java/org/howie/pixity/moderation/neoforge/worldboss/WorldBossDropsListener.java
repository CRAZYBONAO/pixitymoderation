package org.howie.pixity.moderation.neoforge.worldboss;

import net.neoforged.bus.api.SubscribeEvent;

import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;

import net.minecraft.world.entity.Entity;

public class WorldBossDropsListener {





    @SubscribeEvent
    public void onDrops(
            LivingDropsEvent event
    ) {

        Entity entity =
                event.getEntity();

        if (
                !entity.getPersistentData()
                        .getBoolean(
                                "pixity_worldboss"
                        )
        ) {
            return;
        }





        event.getDrops().clear();

        System.out.println(
                "[WorldBoss] Prevented vanilla drops."
        );
    }
}