package org.howie.pixity.moderation.neoforge.shop.gui;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.ItemLore;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.shop.*;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

import java.util.ArrayList;
import java.util.List;

public class ShopMainMenu {

    public static void open(ServerPlayer p,
                            ShopService shopService,
                            EconomyBridge econ,
                            RankService rankService) {

        SimpleContainer cont = new SimpleContainer(54);

        ItemStack filler = new ItemStack(Items.BLACK_STAINED_GLASS_PANE);

        filler.set(DataComponents.CUSTOM_NAME, Component.literal(""));

        for (int i = 0; i < 54; i++) {
            cont.setItem(i, filler.copy());
        }





        for (ShopCategory cat : shopService.getCategories()) {

            ItemStack it = new ItemStack(
                    BuiltInRegistries.ITEM.get(ResourceLocation.parse(cat.icon))
            );

            List<Component> lore = new ArrayList<>();

            lore.add(LegacyAmpersand.parse("&7Shop Category"));
            lore.add(Component.literal(""));

            lore.add(LegacyAmpersand.parse("&eDescription:"));

            for (String line : cat.description) {
                lore.add(LegacyAmpersand.parse(line));
            }

            lore.add(Component.literal(""));
            lore.add(LegacyAmpersand.parse("&E&n&lCLICK&e&l TO BROWSE"));

            it.set(DataComponents.CUSTOM_NAME,
                    LegacyAmpersand.parse(cat.name));

            it.set(DataComponents.LORE,
                    new ItemLore(lore));

            cont.setItem(cat.slot, it);
        }



        ItemStack bal = new ItemStack(Items.EMERALD);

        double money = econ.get(p, "money");
        double tokens = econ.get(p, "tokens");
        double coins = econ.get(p, "coins");

        List<Component> lore = new ArrayList<>();

        lore.add(LegacyAmpersand.parse("&7Shop"));
        lore.add(Component.literal(""));

        lore.add(LegacyAmpersand.parse("&eDescription: &7Spend your &amoney&e, &btokens&f, and &ecoins"));
        lore.add(LegacyAmpersand.parse("&7to &ebuy &fthings or &9decorate your base"));
        lore.add(Component.literal(""));

        lore.add(LegacyAmpersand.parse("&aMONEY: &d$" + (int) money));
        lore.add(LegacyAmpersand.parse("&bTOKENS: &b" + (int) tokens + " Tokens"));
        lore.add(LegacyAmpersand.parse("&eCOINS: &e" + (int) coins + " Coins"));

        bal.set(DataComponents.CUSTOM_NAME,
                LegacyAmpersand.parse("&a&lCURRENT BALANCE"));

        bal.set(DataComponents.LORE,
                new ItemLore(lore));

        cont.setItem(4, bal);


        p.openMenu(new SimpleMenuProvider(
                (id, inv, player) -> new ChestMenu(MenuType.GENERIC_9x6, id, inv, cont, 6) {

                    @Override
                    public void clicked(int slot, int button, ClickType type, net.minecraft.world.entity.player.Player player) {

                        if (!(player instanceof ServerPlayer sp)) return;

                        for (ShopCategory cat : shopService.getCategories()) {

                            if (slot == cat.slot) {

                                sp.closeContainer();

                                ShopCategoryMenu.open(sp, shopService, econ, rankService, cat.id, 0, ShopSortType.NONE);

                                return;
                            }
                        }
                    }
                },
                LegacyAmpersand.parse("&a&lSHOP")
        ));
    }
}