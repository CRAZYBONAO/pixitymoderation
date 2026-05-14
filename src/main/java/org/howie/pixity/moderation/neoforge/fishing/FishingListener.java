package org.howie.pixity.moderation.neoforge.fishing;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;

import net.minecraft.world.item.ItemStack;

public class FishingListener {

    @SubscribeEvent
    public void onFish(ItemFishedEvent event) {

        if (!(event.getEntity() instanceof ServerPlayer player)) return;


        event.getDrops().clear();


        FishData fish = FishingManager.rollFish(player);

        int size = FishingManager.rollFishSize(fish);




        event.setCanceled(true);

        FishingManager.giveFish(player, fish, size);
        FishingManager.handleCatch(player, fish, size);
    }
}