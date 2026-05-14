package org.howie.pixity.moderation.neoforge.fishing;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;


@EventBusSubscriber
public class FishingEvents {

    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickItem event) {

        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (event.getHand() != InteractionHand.MAIN_HAND) return;

        ItemStack stack = event.getItemStack();

        if (stack.isEmpty()) return;


        FishingItemHandler.onRightClick(player, stack);
    }
}