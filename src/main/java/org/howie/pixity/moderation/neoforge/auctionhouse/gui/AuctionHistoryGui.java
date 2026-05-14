package org.howie.pixity.moderation.neoforge.auctionhouse.gui;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.SimpleContainer;

import org.howie.pixity.moderation.chat.TextFormatter;
import org.howie.pixity.moderation.neoforge.auctionhouse.AuctionListing;
import org.howie.pixity.moderation.neoforge.auctionhouse.history.AuctionHistoryDatabase;

import java.util.*;

public class AuctionHistoryGui {

    public static void open(ServerPlayer player, String search) {

        List<AuctionListing> list = AuctionHistoryDatabase.search(search);

        MenuConstructor ctor = (id, inv, p) -> new ChestMenu(
                net.minecraft.world.inventory.MenuType.GENERIC_9x6,
                id,
                inv,
                new SimpleContainer(54),
                6
        ) {

            @Override
            public void broadcastChanges() {
                super.broadcastChanges();

                for (int i = 0; i < Math.min(list.size(), 54); i++) {

                    AuctionListing l = list.get(i);

                    try {
                        ItemStack item = l.getItem(player.registryAccess()).copy();

                        List<Component> lore = new ArrayList<>();
                        lore.add(TextFormatter.parse("&6Price: &e" + l.price + " " + l.currency));
                        lore.add(TextFormatter.parse("&aSeller: &e" + l.sellerName));
                        lore.add(TextFormatter.parse("&6Status: &aSold"));

                        item.set(DataComponents.LORE, new ItemLore(lore));

                        this.getSlot(i).set(item);

                    } catch (Exception e) {
                        this.getSlot(i).set(new ItemStack(Items.BARRIER));
                    }
                }
            }
        };

        player.openMenu(new SimpleMenuProvider(
                ctor,
                TextFormatter.parse("<gold>Auction History</gold>")
        ));
    }
}