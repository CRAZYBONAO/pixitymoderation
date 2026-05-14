package org.howie.pixity.moderation.neoforge.shop.gui;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.ItemLore;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;

import org.howie.pixity.moderation.neoforge.shop.*;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

import java.util.*;

public class ShopSearchMenu {

    public static void open(ServerPlayer p,
                            ShopService shopService,
                            EconomyBridge econ,
                            String query,
                            int page) {

        List<ShopItem> results = shopService.search(query);

        if (results.isEmpty()) {
            p.sendSystemMessage(Component.literal("§cNo results found."));
            return;
        }

        int rows = 6;
        int size = rows * 9;

        SimpleContainer cont = new SimpleContainer(size);

        int maxPerPage = size - 9;

        int maxPage = (int) Math.ceil((double) results.size() / maxPerPage);
        if (maxPage <= 0) maxPage = 1;

        if (page < 0) page = 0;
        if (page >= maxPage) page = maxPage - 1;

        final int currentPage = page;
        final int finalMaxPage = maxPage;


        ItemStack filler = new ItemStack(Items.BLACK_STAINED_GLASS_PANE);
        filler.set(DataComponents.CUSTOM_NAME, Component.literal(""));

        for (int i = 0; i < size; i++) {
            cont.setItem(i, filler.copy());
        }



        int start = page * maxPerPage;
        int end = Math.min(start + maxPerPage, results.size());

        for (int i = start; i < end; i++) {

            ShopItem item = results.get(i);

            int slot = i - start;

            ItemStack it = new ItemStack(
                    BuiltInRegistries.ITEM.get(ResourceLocation.parse(item.item))
            );

            List<Component> lore = new ArrayList<>();

            lore.add(Component.literal(""));
            lore.add(LegacyAmpersand.parse("&aBuy: &f" + item.buy));
            lore.add(LegacyAmpersand.parse("&cSell: &f" + item.sell));

            lore.add(Component.literal(""));
            lore.add(LegacyAmpersand.parse("&eLeft Click: &aBuy"));
            lore.add(LegacyAmpersand.parse("&eRight Click: &cSell"));

            it.set(DataComponents.CUSTOM_NAME,
                    LegacyAmpersand.parse(item.name));

            it.set(DataComponents.LORE,
                    new ItemLore(lore));

            cont.setItem(slot, it);
        }


        int prevSlot = size - 9;
        int nextSlot = size - 1;

        if (page > 0) {
            ItemStack prev = new ItemStack(Items.ARROW);
            prev.set(DataComponents.CUSTOM_NAME,
                    LegacyAmpersand.parse("&ePrevious"));
            cont.setItem(prevSlot, prev);
        }

        if (page < maxPage - 1) {
            ItemStack next = new ItemStack(Items.ARROW);
            next.set(DataComponents.CUSTOM_NAME,
                    LegacyAmpersand.parse("&eNext"));
            cont.setItem(nextSlot, next);
        }



        p.openMenu(new SimpleMenuProvider(
                (id, inv, player) -> new ChestMenu(MenuType.GENERIC_9x6, id, inv, cont, 6) {

                    @Override
                    public void clicked(int slot, int button, ClickType type, net.minecraft.world.entity.player.Player player) {

                        if (!(player instanceof ServerPlayer sp)) return;

                        if (slot == prevSlot && currentPage > 0) {
                            sp.closeContainer();
                            open(sp, shopService, econ, query, currentPage - 1);
                            return;
                        }

                        if (slot == nextSlot && currentPage < finalMaxPage - 1) {
                            sp.closeContainer();
                            open(sp, shopService, econ, query, currentPage + 1);
                            return;
                        }

                        for (int i = start; i < end; i++) {

                            ShopItem item = results.get(i);

                            if (slot != (i - start)) continue;

                            int amount = (type == ClickType.QUICK_MOVE) ? 16 : 1;

                            if (button == 0 && item.buy > 0) {

                                double price = item.buy * amount;

                                if (!econ.has(sp, price, item.currency)) {
                                    sp.sendSystemMessage(Component.literal("§cNot enough money."));
                                    return;
                                }

                                econ.take(sp, price, item.currency);

                                ItemStack give = new ItemStack(
                                        BuiltInRegistries.ITEM.get(ResourceLocation.parse(item.item)),
                                        amount
                                );

                                sp.getInventory().add(give);

                                return;
                            }

                            if (button == 1 && item.sell > 0) {

                                ItemStack hand = sp.getMainHandItem();

                                if (hand.isEmpty()) return;

                                int sellAmount = Math.min(hand.getCount(), amount);

                                hand.shrink(sellAmount);

                                econ.give(sp, item.sell * sellAmount, item.currency);
                                return;
                            }
                        }
                    }
                },
                LegacyAmpersand.parse("&aSearch: &f" + query)
        ));
    }
}