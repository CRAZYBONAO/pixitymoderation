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

import org.howie.pixity.moderation.chat.TextFormatter;
import org.howie.pixity.moderation.neoforge.auctionhouse.AuctionDatabase;
import org.howie.pixity.moderation.neoforge.auctionhouse.AuctionListing;

import java.util.*;

public class AuctionMyListingsGui {

    private static final int SIZE = 54;

    public static void open(ServerPlayer player) {


        List<AuctionListing> listings = new ArrayList<>();

        for (AuctionListing l : AuctionDatabase.getActive()) {
            if (l.seller.equals(player.getUUID())) {
                listings.add(l);
            }
        }

        MenuConstructor ctor = (id, inv, p) -> new ChestMenu(
                net.minecraft.world.inventory.MenuType.GENERIC_9x6,
                id,
                inv,
                new net.minecraft.world.SimpleContainer(SIZE),
                6
        ) {

            @Override
            public void broadcastChanges() {
                super.broadcastChanges();

                for (int i = 0; i < Math.min(listings.size(), 45); i++) {

                    AuctionListing listing = listings.get(i);

                    try {

                        ItemStack item = listing.getItem(player.registryAccess()).copy();

                        List<Component> lore = new ArrayList<>();
                        lore.add(TextFormatter.parse("&6Price: &e" + listing.price + " " + listing.currency));
                        lore.add(TextFormatter.parse("&cLeft-click to cancel"));

                        item.set(DataComponents.LORE, new ItemLore(lore));

                        this.getSlot(i).set(item);

                    } catch (Exception e) {
                        this.getSlot(i).set(new ItemStack(Items.BARRIER));
                    }
                }


                for (int i = 45; i < 54; i++) {
                    this.getSlot(i).set(new ItemStack(Items.GRAY_STAINED_GLASS_PANE));
                }
            }

            @Override
            public void clicked(int slot, int button,
                                net.minecraft.world.inventory.ClickType type,
                                net.minecraft.world.entity.player.Player p) {

                if (!(p instanceof ServerPlayer sp)) return;

                if (slot >= listings.size()) return;

                AuctionListing listing = listings.get(slot);

                cancelListing(sp, listing);


                open(sp);
            }
        };

        player.openMenu(new SimpleMenuProvider(
                ctor,
                TextFormatter.parse("<gold>Your Listings</gold>")
        ));
    }


    private static void cancelListing(ServerPlayer player, AuctionListing listing) {

        try {
            ItemStack item = listing.getItem(player.registryAccess()).copy();
            player.getInventory().add(item);

        } catch (Exception e) {
            e.printStackTrace();
        }

        AuctionDatabase.remove(listing.id);

        player.sendSystemMessage(
                TextFormatter.parse("&e&lAUCTIONHOUSE&7&l➤ &aListing cancelled. Item returned.")
        );
    }
}