package org.howie.pixity.moderation.neoforge.shop;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.ServerChatEvent;

import org.howie.pixity.moderation.neoforge.shop.EconomyBridge;
import org.howie.pixity.moderation.neoforge.shop.gui.ShopConfirmMenu;
import org.howie.pixity.moderation.neoforge.rank.RankService;

public class ShopChatListener {

    private final ShopService shopService;
    private final EconomyBridge econ;
    private final RankService rankService;

    public ShopChatListener(ShopService shopService, EconomyBridge econ, RankService rankService) {
        this.shopService = shopService;
        this.econ = econ;
        this.rankService = rankService;
    }

    @SubscribeEvent
    public void onChat(ServerChatEvent e) {

        if (!(e.getPlayer() instanceof ServerPlayer player)) return;

        if (!ShopInputService.isWaiting(player)) return;

        e.setCanceled(true);

        String msg = e.getRawText();

        if (msg.equalsIgnoreCase("cancel")) {
            ShopInputService.clear(player);
            player.sendSystemMessage(Component.literal("§cPurchase cancelled."));
            return;
        }

        int amount;

        try {
            amount = Integer.parseInt(msg);
        } catch (Exception ex) {
            player.sendSystemMessage(Component.literal("§cEnter a valid number."));
            return;
        }

        if (amount <= 0 || amount > 10000) {
            player.sendSystemMessage(Component.literal("§cInvalid amount (1-10000)."));
            return;
        }

        ShopInputService.InputContext ctx = ShopInputService.get(player);
        ShopInputService.clear(player);

        ShopConfirmMenu.open(player, shopService, econ, rankService, ctx.item, amount);
    }
}