package org.howie.pixity.moderation.neoforge.shop.gui;

import net.minecraft.network.chat.Style;
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

import org.howie.pixity.moderation.chat.TextFormatter;
import org.howie.pixity.moderation.neoforge.shop.*;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;
import org.howie.pixity.moderation.neoforge.rank.RankService;

import java.util.*;

public class ShopCategoryMenu {

    public String gradientStart;
    public String gradientEnd;

    private static final int[] SAFE_SLOTS = {
            10,11,12,13,14,15,16,
            19,20,21,22,23,24,25,
            28,29,30,31,32,33,34
    };

    public static void open(ServerPlayer p,
                            ShopService shopService,
                            EconomyBridge econ,
                            RankService rankService,
                            String id,
                            int page,
                            ShopSortType sort) {

        String subFilter = null;
        String originalId = id;

        if (id.contains("|")) {
            String[] split = id.split("\\|");

            id = split[0];
            subFilter = split[1];
        }

        Shop shop = shopService.getShop(id);


        if (id.contains("|")) {
            String[] split = id.split("\\|");

            id = split[0];
            subFilter = split[1];
        }

        if (shop == null) {
            p.sendSystemMessage(Component.literal("§cShop not found."));
            return;
        }



        int size = shop.rows * 9;
        int maxItemsPerPage = SAFE_SLOTS.length;

        List<ShopItem> items = new ArrayList<>();

        String baseId = shop.id.contains(":")
                ? shop.id.split(":")[1]
                : shop.id;




        for (ShopItem item : shop.items) {


            boolean passes;

            if (subFilter == null) {
                passes = true;
            } else {

                String sub = subFilter.contains(":")
                        ? subFilter.substring(subFilter.lastIndexOf(":") + 1)
                        : subFilter;

                passes = (item.category != null &&
                        item.category.toLowerCase().endsWith("." + sub.toLowerCase()));
            }


        }


        items = shopService.sort(items, sort);

        int totalItems = items.size();
        int maxPage = Math.max(1, (int) Math.ceil((double) totalItems / maxItemsPerPage));

        if (page < 0) page = 0;
        if (page >= maxPage) page = maxPage - 1;

        final int currentPage = page;
        final int finalMaxPage = maxPage;
        final ShopSortType currentSort = sort;

        SimpleContainer cont = new SimpleContainer(size);

        ItemStack filler = new ItemStack(Items.BLACK_STAINED_GLASS_PANE);
        filler.set(DataComponents.CUSTOM_NAME, Component.literal(""));

        for (int i = 0; i < size; i++) {
            cont.setItem(i, filler.copy());
        }

        int prevSlot = size - 9;
        int nextSlot = size - 1;
        int backSlot = size - 5;

        if (currentPage > 0) {
            ItemStack prev = new ItemStack(Items.ARROW);
            prev.set(DataComponents.CUSTOM_NAME, LegacyAmpersand.parse("&ePrevious Page"));
            cont.setItem(prevSlot, prev);
        }

        if (currentPage < finalMaxPage - 1) {
            ItemStack next = new ItemStack(Items.ARROW);
            next.set(DataComponents.CUSTOM_NAME, LegacyAmpersand.parse("&eNext Page"));
            cont.setItem(nextSlot, next);
        }

        ItemStack back = new ItemStack(Items.ARROW);
        back.set(DataComponents.CUSTOM_NAME, LegacyAmpersand.parse("&c&lBACK"));
        cont.setItem(backSlot, back);


        if (subFilter == null && !shop.subcategories.isEmpty()) {

            int index = 0;

            for (ShopSubCategory sub : shop.subcategories) {

                if (index >= SAFE_SLOTS.length) break;

                int slot = sub.slot;

                ItemStack it = new ItemStack(
                        BuiltInRegistries.ITEM.get(ResourceLocation.parse(sub.icon))
                );


                it.set(DataComponents.CUSTOM_NAME, LegacyAmpersand.parse(sub.name));

                cont.setItem(slot, it);
            }

            p.openMenu(new SimpleMenuProvider(
                    (menuId, inv, player) -> new ChestMenu(MenuType.GENERIC_9x6, menuId, inv, cont, shop.rows) {

                        @Override
                        public void clicked(int slot, int button, ClickType type, net.minecraft.world.entity.player.Player player) {

                            if (!(player instanceof ServerPlayer sp)) return;

                            if (slot == backSlot) {
                                sp.closeContainer();
                                ShopMainMenu.open(sp, shopService, econ, rankService);
                                return;
                            }

                            for (ShopSubCategory sub : shop.subcategories) {

                                if (slot == sub.slot) {

                                    sp.closeContainer();

                                    String nextId;

                                    if (!shop.id.contains(":")) {
                                        nextId = sub.id;
                                    } else {
                                        nextId = shop.id + "|" + sub.id;
                                    }

                                    ShopCategoryMenu.open(
                                            sp,
                                            shopService,
                                            econ,
                                            rankService,
                                            nextId,
                                            0,
                                            currentSort
                                    );

                                    return;
                                }
                            }
                        }

                    },
                    LegacyAmpersand.parse("&a&lSHOP")
            ));

            return;
        }

        int start = currentPage * maxItemsPerPage;
        int end = Math.min(start + maxItemsPerPage, items.size());

        for (int i = start; i < end; i++) {

            ShopItem item = items.get(i);

            boolean hasPermission = item.permission != null && !item.permission.isBlank();
            boolean owned = hasPermission && rankService.hasPerm(p, item.permission);

            int slot = SAFE_SLOTS[i - start];

            ItemStack it;

            if (item.mob != null && !item.mob.isBlank()) {
                it = org.howie.pixity.moderation.neoforge.spawners.SpawnerAPI.create(item.mob);
            } else {
                try {
                    it = new ItemStack(
                            BuiltInRegistries.ITEM.get(ResourceLocation.parse(item.item))
                    );
                } catch (Exception e) {
                    it = new ItemStack(Items.BARRIER);
                }
            }

            if (!owned && hasPermission) {
                it.set(DataComponents.CUSTOM_NAME, LegacyAmpersand.parse("&7" + item.name));
            } else {
                Component nameComp;

                if (item.gradientStart != null && item.gradientEnd != null) {

                    nameComp = TextFormatter.gradient(
                            item.name,
                            item.gradientStart,
                            item.gradientEnd
                    ).copy().setStyle(
                            Style.EMPTY.withItalic(false)
                    );

                }
                else if (item.colorCode != null) {

                    nameComp = LegacyAmpersand.parse(
                            item.colorCode + item.name
                    );

                }
                else {
                    nameComp = LegacyAmpersand.parse(item.name);
                }

                it.set(DataComponents.CUSTOM_NAME, nameComp);
            }

            if (owned) {
                it.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
            }

            List<Component> lore = new ArrayList<>();


            if (item.colorCode != null) {

                lore.add(LegacyAmpersand.parse("&eDescription:"));
                lore.add(LegacyAmpersand.parse("&7Stand out from &aother players&7 in chat"));
                lore.add(LegacyAmpersand.parse(
                        "&echat " + item.colorCode + "by using this color."
                ));

                lore.add(Component.literal(""));
            }

            if (item.gradientStart != null && item.gradientEnd != null) {

                lore.add(LegacyAmpersand.parse("&eDescription:"));
                lore.add(LegacyAmpersand.parse("&7Stand out from &aother players&7 in chat"));

                Component desc = TextFormatter.gradient(
                        "by using this gradient:",
                        item.gradientStart,
                        item.gradientEnd
                ).copy().setStyle(Style.EMPTY.withItalic(false));

                lore.add(desc);
                lore.add(Component.literal(""));
            }


            if (shop.id.startsWith("tokens")) {

                if (!owned && item.buy > 0) {
                    lore.add(LegacyAmpersand.parse("&eCost: &6" + (int) item.buy + " tokens"));
                    lore.add(Component.literal(""));
                }

            }
            else {

                if (item.buy > 0) {
                    lore.add(LegacyAmpersand.parse("&aBuy: &a$" + (int) item.buy));
                }

                if (item.sell > 0) {
                    lore.add(LegacyAmpersand.parse("&cSell: &c$" + (int) item.sell));
                }

                if (item.buy > 0 || item.sell > 0) {
                    lore.add(Component.literal(""));
                }

            }




            if (owned) {
                lore.add(LegacyAmpersand.parse("&a&l✔ UNLOCKED"));
            }
            else if (shop.id.startsWith("tokens")) {

                lore.add(LegacyAmpersand.parse("&eClick to unlock"));

            }
            else {

                lore.add(LegacyAmpersand.parse("&e&lCLICK &eto Purchase"));
                lore.add(LegacyAmpersand.parse("&e&nRIGHT CLICK &eto Sell"));

            }


            it.set(DataComponents.LORE, new ItemLore(lore));

            cont.setItem(slot, it);
        }

        final List<ShopItem> finalItems = items;
        final int finalStart = start;
        final int finalEnd = end;

        p.openMenu(new SimpleMenuProvider(
                (menuId, inv, player) -> new ChestMenu(MenuType.GENERIC_9x6, menuId, inv, cont, shop.rows) {

                    @Override
                    public void clicked(int slot, int button, ClickType type, net.minecraft.world.entity.player.Player player) {



                        if (!(player instanceof ServerPlayer sp)) return;

                        if (slot == prevSlot && currentPage > 0) {
                            open(sp, shopService, econ, rankService, originalId, currentPage - 1, currentSort);
                        }

                        if (slot == nextSlot && currentPage < finalMaxPage - 1) {
                            open(sp, shopService, econ, rankService, originalId, currentPage + 1, currentSort);
                        }

                        if (slot == backSlot) {
                            sp.closeContainer();
                            ShopMainMenu.open(sp, shopService, econ, rankService);
                            return;
                        }

                        for (int i = finalStart; i < finalEnd; i++) {

                            int displaySlot = SAFE_SLOTS[i - finalStart];

                            if (slot == displaySlot) {

                                ShopItem item = finalItems.get(i);

                                boolean owned = item.permission != null &&
                                        rankService.hasPerm(sp, item.permission);

                                if (owned) {
                                    sp.sendSystemMessage(Component.literal("§aYou already own this!"));
                                    return;
                                }

                                if (button == 0) {
                                    sp.closeContainer();

                                    ShopConfirmMenu.open(
                                            sp,
                                            shopService,
                                            econ,
                                            rankService,
                                            item,
                                            1
                                    );
                                }
                                return;
                            }
                        }
                    }

                },
                LegacyAmpersand.parse("&a&lSHOP")
        ));
    }
}