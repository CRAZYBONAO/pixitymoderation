package org.howie.pixity.moderation.neoforge.fly.gui;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;

import net.minecraft.world.SimpleMenuProvider;

import org.howie.pixity.moderation.neoforge.fly.FlyTimeService;
import org.howie.pixity.moderation.neoforge.economy.*;

import java.util.*;

public final class FlyTimeGui {

    public static void open(ServerPlayer player,
                            FlyTimeService fly,
                            EconomyService economy) {

        SimpleContainer cont = new SimpleContainer(27);

        ItemStack filler = new ItemStack(Items.BLACK_STAINED_GLASS_PANE);

        for (int i = 0; i < 27; i++) {
            if (i < 9 || i >= 18 || i % 9 == 0 || i % 9 == 8) {
                cont.setItem(i, filler);
            }
        }
        filler.set(
                DataComponents.CUSTOM_NAME,
                Component.empty().withStyle(style -> style.withItalic(false))
        );

        add(cont, player, fly, economy, 10, "§e5 Minutes", 300, 100);
        add(cont, player, fly, economy, 11, "§e15 Minutes", 900, 250);
        add(cont, player, fly, economy, 12, "§e30 Minutes", 1800, 400);
        add(cont, player, fly, economy, 13, "§e1 Hour", 3600, 800);
        add(cont, player, fly, economy, 14, "§e2 Hours", 7200, 1600);
        add(cont, player, fly, economy, 15, "§e4 Hours", 14400, 3200);
        add(cont, player, fly, economy, 16, "§e8 Hours", 28800, 6400);

        player.openMenu(new SimpleMenuProvider(
                (id, inv, p) -> new ChestMenu(
                        net.minecraft.world.inventory.MenuType.GENERIC_9x3,
                        id, inv, cont, 3) {

                    @Override
                    public void clicked(int slot, int button,
                                        net.minecraft.world.inventory.ClickType type,
                                        net.minecraft.world.entity.player.Player p) {

                        if (!(p instanceof ServerPlayer sp)) return;

                        handleClick(sp, slot, fly, economy);
                    }
                },
                Component.literal("§6Flight Shop §7| §b" +
                        formatTokens((long) economy.get(player, CurrencyType.TOKENS)) + " tokens")
        ));
    }


    private static void add(SimpleContainer cont,
                            ServerPlayer player,
                            FlyTimeService fly,
                            EconomyService economy,
                            int slot,
                            String name,
                            int seconds,
                            int price) {

        ItemStack it = new ItemStack(Items.FEATHER);
        it.set(DataComponents.CUSTOM_NAME, Component.literal(name));

        double discount = fly.getDiscount(player);
        double finalPrice = Math.max(0, price * (1.0 - discount));

        double balance = economy.get(player, CurrencyType.TOKENS);

        List<Component> lore = new ArrayList<>();
        lore.add(Component.literal("§7Time: §e" + formatTime(seconds)));

        if (finalPrice <= 0) {
            lore.add(Component.literal("§aPrice: FREE"));
        }
        else if (finalPrice < price) {
            lore.add(Component.literal("§aDiscounted: " + formatTokens((long) finalPrice)));
            lore.add(Component.literal("§8Original: §7" + formatTokens(price)));
        }
        else {
            lore.add(Component.literal("§7Price: §b" + formatTokens(price)));
        }

        lore.add(Component.literal(""));

        if (balance >= finalPrice) {
            lore.add(Component.literal("§aClick to purchase"));
        } else {
            lore.add(Component.literal("§cNot enough tokens"));
        }

        it.set(DataComponents.LORE, new ItemLore(lore));

        cont.setItem(slot, it);
    }

    private static void handleClick(ServerPlayer p, int slot,
                                    FlyTimeService fly,
                                    EconomyService economy) {

        int seconds = 0;
        int price = 0;

        if (slot == 10) { seconds = 300; price = 100; }
        if (slot == 11) { seconds = 900; price = 250; }
        if (slot == 12) { seconds = 1800; price = 400; }
        if (slot == 13) { seconds = 3600; price = 800; }
        if (slot == 14) { seconds = 7200; price = 1600; }
        if (slot == 15) { seconds = 14400; price = 3200; }
        if (slot == 16) { seconds = 28800; price = 6400; }

        if (seconds <= 0) return;

        openConfirm(p, fly, economy, seconds, price);
    }

    private static void openConfirm(ServerPlayer player,
                                    FlyTimeService fly,
                                    EconomyService economy,
                                    int seconds,
                                    int price) {

        SimpleContainer cont = new SimpleContainer(27);

        ItemStack filler = new ItemStack(Items.BLACK_STAINED_GLASS_PANE);

        for (int i = 0; i < 27; i++) {
            if (i < 9 || i >= 18 || i % 9 == 0 || i % 9 == 8) {
                cont.setItem(i, filler);
            }
        }
        filler.set(
                DataComponents.CUSTOM_NAME,
                Component.empty().withStyle(style -> style.withItalic(false))
        );

        double discount = fly.getDiscount(player);
        double finalPrice = Math.max(0, price * (1.0 - discount));

        ItemStack confirm = new ItemStack(Items.LIME_WOOL);
        confirm.set(DataComponents.CUSTOM_NAME, Component.literal("§aConfirm Purchase"));

        List<Component> confirmLore = new ArrayList<>();
        confirmLore.add(Component.literal("§7Time: §e" + formatTime(seconds)));

        if (finalPrice < price) {
            confirmLore.add(Component.literal("§aDiscounted: " + formatTokens((long) finalPrice)));
            confirmLore.add(Component.literal("§8Original: §7" + formatTokens(price)));
        } else {
            confirmLore.add(Component.literal("§7Cost: §b" + formatTokens(price)));
        }

        confirm.set(DataComponents.LORE, new ItemLore(confirmLore));
        cont.setItem(11, confirm);

        ItemStack cancel = new ItemStack(Items.RED_WOOL);
        cancel.set(DataComponents.CUSTOM_NAME, Component.literal("§cCancel"));
        cont.setItem(15, cancel);

        player.openMenu(new SimpleMenuProvider(
                (id, inv, p) -> new ChestMenu(
                        net.minecraft.world.inventory.MenuType.GENERIC_9x3,
                        id, inv, cont, 3) {

                    @Override
                    public void clicked(int slot, int button,
                                        net.minecraft.world.inventory.ClickType type,
                                        net.minecraft.world.entity.player.Player p) {

                        if (!(p instanceof ServerPlayer sp)) return;

                        if (slot == 11) {
                            sp.closeContainer();
                            completePurchase(sp, fly, economy, seconds, price);
                        }

                        if (slot == 15) {
                            sp.closeContainer();
                        }
                    }
                },
                Component.literal("§cConfirm Purchase")
        ));
    }


    private static void completePurchase(ServerPlayer p,
                                         FlyTimeService fly,
                                         EconomyService economy,
                                         int seconds,
                                         int price) {

        double discount = fly.getDiscount(p);
        double finalPrice = Math.max(0, price * (1.0 - discount));

        double tokens = economy.get(p, CurrencyType.TOKENS);

        if (tokens < finalPrice) {
            p.sendSystemMessage(Component.literal("§cNot enough tokens."));
            return;
        }

        economy.remove(p, CurrencyType.TOKENS, finalPrice);
        fly.give(p.getUUID(), seconds);

        p.sendSystemMessage(Component.literal(
                "\n§aFlight Purchased!" +
                        "\n§7Time: §e" + formatTime(seconds) +
                        "\n§7Cost: §b" + formatTokens((long) finalPrice) + " tokens\n"
        ));

        p.closeContainer();
    }


    private static String formatTime(long seconds) {
        long m = seconds / 60;
        long h = m / 60;

        if (h > 0) return h + "h " + (m % 60) + "m";
        return m + "m";
    }

    private static String formatTokens(long n) {
        return String.format("%,d", n);
    }
}