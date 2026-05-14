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

import net.minecraft.nbt.TagParser;

import java.util.*;

public class ExpiredGUI {

    private static final int SIZE = 54;

    public static void open(ServerPlayer player) {

        UUID uuid = player.getUUID();
        List<AuctionListing> expired = AuctionDatabase.getExpired(uuid);

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




                for (int i = 0; i < Math.min(expired.size(), 45); i++) {

                    AuctionListing listing = expired.get(i);

                    try {
                        var tag = TagParser.parseTag(listing.itemData);
                        ItemStack item = ItemStack.parseOptional(player.registryAccess(), tag);

                        List<Component> lore = new ArrayList<>();
                        lore.add(TextFormatter.parse("<&eStored Item"));
                        lore.add(TextFormatter.parse("&7Expired &e/ Returned / &cAdmin Removed"));
                        lore.add(TextFormatter.parse(""));
                        lore.add(TextFormatter.parse("&aClick to claim"));

                        item.set(DataComponents.LORE, new ItemLore(lore));

                        this.getSlot(i).set(item);

                    } catch (Exception e) {
                        this.getSlot(i).set(new ItemStack(Items.BARRIER));
                    }
                }




                ItemStack claimAll = new ItemStack(Items.EMERALD_BLOCK);
                claimAll.set(DataComponents.CUSTOM_NAME,
                        TextFormatter.parse("<green>&lCLAIM ALL</green>"));

                List<Component> lore = new ArrayList<>();
                lore.add(TextFormatter.parse("&7Claim all expired items"));
                lore.add(TextFormatter.parse("&eTotal: &6" + expired.size()));

                claimAll.set(DataComponents.LORE, new ItemLore(lore));

                this.getSlot(49).set(claimAll);




                for (int i = 45; i < 54; i++) {
                    if (i == 49) continue;
                    this.getSlot(i).set(new ItemStack(Items.GRAY_STAINED_GLASS_PANE));
                }
            }

            @Override
            public void clicked(int slot, int button,
                                net.minecraft.world.inventory.ClickType type,
                                net.minecraft.world.entity.player.Player p) {

                if (!(p instanceof ServerPlayer sp)) return;




                if (slot == 49) {
                    claimAll(sp, expired);
                    open(sp);
                    return;
                }




                if (slot >= expired.size()) return;

                AuctionListing listing = expired.get(slot);

                claim(sp, listing);

                open(sp);
            }
        };

        player.openMenu(new SimpleMenuProvider(
                ctor,
                TextFormatter.parse("<gold>Expired Listings</gold>")
        ));
    }


    private static void claim(ServerPlayer player, AuctionListing listing) {

        giveItem(player, listing);

        AuctionDatabase.removeExpired(player.getUUID(), listing);

        player.sendSystemMessage(
                TextFormatter.parse("&e&lAUCTIONHOUSE&7&l➤ &aItem claimed!")
        );
    }


    private static void claimAll(ServerPlayer player, List<AuctionListing> expired) {

        if (expired.isEmpty()) {
            player.sendSystemMessage(TextFormatter.parse("&e&lAUCTIONHOUSE&7&l➤ &cError! No items to claim"));
            return;
        }

        int count = 0;

        for (AuctionListing listing : new ArrayList<>(expired)) {

            giveItem(player, listing);
            AuctionDatabase.removeExpired(player.getUUID(), listing);

            count++;
        }

        player.sendSystemMessage(
                TextFormatter.parse("&e&lAUCTIONHOUSE&7&l➤ &aClaimed &e" + count + " &aitems!")
        );
    }


    private static void giveItem(ServerPlayer player, AuctionListing listing) {

        try {
            ItemStack item = listing.getItem(player.registryAccess()).copy();

            if (!player.getInventory().add(item)) {
                player.drop(item, false);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}