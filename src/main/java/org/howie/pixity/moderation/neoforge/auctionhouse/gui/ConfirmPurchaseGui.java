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
import org.howie.pixity.moderation.neoforge.auctionhouse.AuctionManager;
import org.howie.pixity.moderation.neoforge.auctionhouse.history.AuctionHistoryDatabase;
import org.howie.pixity.moderation.neoforge.economy.CurrencyType;
import org.howie.pixity.moderation.neoforge.economy.EconomyService;

import net.minecraft.nbt.TagParser;

import java.util.*;

public class ConfirmPurchaseGui {

    private static final Set<UUID> BUY_LOCKS = Collections.synchronizedSet(new HashSet<>());


    public static void open(ServerPlayer player,
                            EconomyService econ,
                            AuctionListing listing,
                            int returnPage) {

        ConfirmContainer cont = new ConfirmContainer(econ, listing);




        try {
            ItemStack item = listing.getItem(player.registryAccess()).copy();


            List<Component> lore = new ArrayList<>();

            lore.add(TextFormatter.parse("&6Price: &e" + listing.price + " " + listing.currency));
            lore.add(TextFormatter.parse("&6Seller: &e" + listing.sellerName));

            item.set(DataComponents.LORE, new ItemLore(lore));

            cont.setItem(13, item);

        } catch (Exception e) {
            cont.setItem(13, new ItemStack(Items.BARRIER));
        }




        ItemStack confirm = new ItemStack(Items.LIME_STAINED_GLASS_PANE);
        confirm.set(DataComponents.CUSTOM_NAME,
                TextFormatter.parse("&a&lCONFIRM PURCHASE"));

        cont.setItem(11, confirm);




        ItemStack cancel = new ItemStack(Items.RED_STAINED_GLASS_PANE);
        cancel.set(DataComponents.CUSTOM_NAME,
                TextFormatter.parse("&c&lCANCEL"));

        cont.setItem(15, cancel);




        MenuConstructor ctor = (id, inv, p) ->
                new ChestMenu(
                        net.minecraft.world.inventory.MenuType.GENERIC_9x3,
                        id, inv, cont, 3) {

                    ConfirmContainer c = (ConfirmContainer) this.getContainer();


                    @Override
                    public void clicked(int slot, int button,
                                        net.minecraft.world.inventory.ClickType type,
                                        net.minecraft.world.entity.player.Player p) {

                        if (!(p instanceof ServerPlayer sp)) return;


                        if (slot == 11) {

                            ConfirmContainer c = (ConfirmContainer) this.getContainer();


                            if (c.listing.seller.equals(sp.getUUID())) {
                                sp.sendSystemMessage(TextFormatter.parse("&e&lAUCTIONHOUSE &7&l➤ &cError! You cannot buy your own listing."));
                                return;
                            }

                            buy(sp, c.listing);
                            AuctionBrowserGui.open(sp, new AuctionMenuState(sp.getUUID()));
                            return;
                        }


                        if (slot == 15) {
                            AuctionBrowserGui.open(sp, new AuctionMenuState(sp.getUUID()));
                        }
                    }
                };

        player.openMenu(new SimpleMenuProvider(
                ctor,
                TextFormatter.parse("<gold>Confirm Purchase?</gold>")
        ));
    }


    private static void buy(ServerPlayer buyer, AuctionListing listing) {


        if (listing.seller.equals(buyer.getUUID())) {
            buyer.sendSystemMessage(TextFormatter.parse("&e&lAUCTIONHOUSE&7&l➤ &cError! You cannot buy your own listing."));
            return;
        }


        if (!BUY_LOCKS.add(listing.id)) {
            buyer.sendSystemMessage(TextFormatter.parse("&e&lAUCTIONHOUSE&7&l➤ &cHold on, this listing is being purchased."));
            return;
        }

        try {


            AuctionListing live = AuctionDatabase.get(listing.id);

            if (live == null || live.sold) {
                buyer.sendSystemMessage(TextFormatter.parse("&e&lAUCTIONHOUSE&7&l➤ &cError! This listing is no longer available."));
                return;
            }

            EconomyService econ = AuctionManager.ECON;
            if (econ == null) {
                buyer.sendSystemMessage(TextFormatter.parse("&e&lAUCTIONHOUSE&7&l➤ &cError! Economy not loaded, please contact PrestigeMidnight"));
                return;
            }

            CurrencyType type = CurrencyType.valueOf(live.currency);


            if (!econ.remove(buyer, type, live.price)) {
                buyer.sendSystemMessage(TextFormatter.parse("&e&lAUCTIONHOUSE&7&l➤ &cError! Not enough money."));
                return;
            }


            live.sold = true;


            ItemStack item = live.getItem(buyer.registryAccess()).copy();

            boolean added = buyer.getInventory().add(item);

            if (!added) {
                buyer.drop(item, false);
                buyer.sendSystemMessage(TextFormatter.parse("&e&lAUCTIONHOUSE&7&l➤ &cInventory full — &eitem dropped."));
            }


            econ.add(live.seller, type, live.price);


            AuctionHistoryDatabase.add(live);


            AuctionDatabase.remove(live.id);

            buyer.sendSystemMessage(TextFormatter.parse("&e&lAUCTIONHOUSE&7&l➤ &aPurchase successful!"));

        } finally {
            BUY_LOCKS.remove(listing.id);
        }
    }
}