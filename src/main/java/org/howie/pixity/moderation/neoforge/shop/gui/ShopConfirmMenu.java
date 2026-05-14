package org.howie.pixity.moderation.neoforge.shop.gui;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import org.howie.pixity.moderation.chat.TextFormatter;
import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.shop.*;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

import java.util.ArrayList;

public class ShopConfirmMenu {

    public static void open(ServerPlayer p,
                            ShopService shopService,
                            EconomyBridge econ,
                            RankService rankService,
                            ShopItem item,
                            int amount) {

        SimpleContainer cont = new SimpleContainer(27);


        ItemStack filler = new ItemStack(Items.BLACK_STAINED_GLASS_PANE);
        filler.set(DataComponents.CUSTOM_NAME, Component.literal(""));

        for (int i = 0; i < 27; i++) {
            cont.setItem(i, filler.copy());
        }

        ItemStack back = new ItemStack(Items.BARRIER);
        back.set(DataComponents.CUSTOM_NAME,
                LegacyAmpersand.parse("&c&lBACK"));
        cont.setItem(4, back);


        ItemStack preview = new ItemStack(
                BuiltInRegistries.ITEM.get(ResourceLocation.parse(item.item)),
                amount
        );

        preview.set(DataComponents.CUSTOM_NAME,
                LegacyAmpersand.parse(item.name));

        double totalCost = item.buy * amount;
        String symbol = getCurrencySymbol(item.currency);

        var lore = new ArrayList<Component>();
        lore.add(LegacyAmpersand.parse("&7Amount: &e" + amount));
        lore.add(Component.literal(""));
        lore.add(LegacyAmpersand.parse("&fPrice Each: &a" + formatNumber(item.buy) + symbol));
        lore.add(LegacyAmpersand.parse("&fTotal Cost: &6" + formatNumber(totalCost) + symbol));

        preview.set(DataComponents.LORE,
                new net.minecraft.world.item.component.ItemLore(lore));

        cont.setItem(13, preview);


        cont.setItem(14, button(Items.LIME_STAINED_GLASS_PANE, "&a&l+1"));
        cont.setItem(15, button(Items.LIME_STAINED_GLASS_PANE, "&a&l+16"));
        cont.setItem(16, button(Items.LIME_STAINED_GLASS_PANE, "&a&l+64"));

        cont.setItem(12, button(Items.RED_STAINED_GLASS_PANE, "&c&l-1"));
        cont.setItem(11, button(Items.RED_STAINED_GLASS_PANE, "&c&l-16"));
        cont.setItem(10, button(Items.RED_STAINED_GLASS_PANE, "&c&l-64"));


        ItemStack hopper = new ItemStack(Items.HOPPER);
        hopper.set(DataComponents.CUSTOM_NAME,
                LegacyAmpersand.parse("&e&lENTER AMOUNT (CHAT)"));
        cont.setItem(22, hopper);


        ItemStack confirm = new ItemStack(Items.LIME_WOOL);
        confirm.set(DataComponents.CUSTOM_NAME,
                LegacyAmpersand.parse("&a&lCONFIRM"));
        cont.setItem(21, confirm);


        p.openMenu(new SimpleMenuProvider(
                (menuId, inv, player) -> new ChestMenu(MenuType.GENERIC_9x3, menuId, inv, cont, 3) {

                    @Override
                    public void clicked(int slot, int button, ClickType type, net.minecraft.world.entity.player.Player player) {

                        if (!(player instanceof ServerPlayer sp)) return;

                        if (slot != 4 && slot != 2 && slot !=14 && slot != 15 && slot != 16 && slot !=12 && slot != 11 && slot != 10 && slot != 21) {
                            return;
                        }

                        if (slot == 4) {
                            sp.closeContainer();
                            ShopCategoryMenu.open(sp, shopService, econ, rankService, item.category, 0, ShopSortType.NONE);
                            return;
                        }

                        if (slot == 22) {
                            sp.closeContainer();
                            ShopInputService.start(sp, item, item.category);
                            sp.sendSystemMessage(Component.literal("§eEnter amount in chat or type cancel."));
                            return;
                        }

                        int newAmount = amount;

                        switch (slot) {
                            case 14 -> newAmount += 1;
                            case 15 -> newAmount += 16;
                            case 16 -> newAmount += 64;

                            case 12 -> newAmount -= 1;
                            case 11 -> newAmount -= 16;
                            case 10 -> newAmount -= 64;
                        }

                        if (newAmount < 1) newAmount = 1;
                        if (newAmount > 10000) newAmount = 10000;

                        if (slot == 21) {
                            boolean success = shopService.purchase(sp, item, amount, econ, rankService);
                            if (success) sp.closeContainer();
                            return;
                        }

                        if (newAmount != amount) {
                            open(sp, shopService, econ, rankService, item, newAmount);
                        }
                    }
                },
                LegacyAmpersand.parse("&eSelect Amount")
        ));
    }

    private static ItemStack button(Item item, String name) {
        ItemStack it = new ItemStack(item);
        it.set(DataComponents.CUSTOM_NAME, LegacyAmpersand.parse(name));
        return it;
    }

    private static String formatNumber(double value) {
        if (value >= 1_000_000_000) return formatCompact(value, 1_000_000_000, "B");
        if (value >= 1_000_000) return formatCompact(value, 1_000_000, "M");
        if (value >= 1_000) return formatCompact(value, 1_000, "K");
        return String.valueOf((int) value);
    }

    private static String formatCompact(double value, double divisor, String suffix) {
        double result = value / divisor;
        if (result % 1 == 0) return (int) result + suffix;
        return String.format("%.2f", result).replaceAll("\\.?0+$", "") + suffix;
    }

    private static String getCurrencySymbol(String currency) {
        if (currency == null) return "$";

        return switch (currency.toLowerCase()) {
            case "money" -> "$";
            case "tokens" -> " Tokens";
            case "coins" -> " Coins";
            default -> "$";
        };
    }
}