package org.howie.pixity.moderation.neoforge.voucher;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import org.howie.pixity.moderation.chat.TextFormatter;

public class VoucherListener {

    @SubscribeEvent
    public void onUse(PlayerInteractEvent.RightClickItem e) {

        if (!(e.getEntity() instanceof ServerPlayer player)) return;

        ItemStack stack = e.getItemStack();

        if (!VoucherService.isVoucher(stack)) return;

        String cmd = VoucherService.getCommand(stack)
                .replace("%player%", player.getName().getString());

        player.server.getCommands().performPrefixedCommand(
                player.server.createCommandSourceStack(),
                cmd
        );

        stack.shrink(1);

        player.sendSystemMessage(TextFormatter.parse("§4&lVOUCHERS &7&l➤ &r" + stack.getDisplayName() + " &ahas been redeemed!"));

        e.setCanceled(true);
    }
}