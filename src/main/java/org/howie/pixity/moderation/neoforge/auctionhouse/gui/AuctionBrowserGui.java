package org.howie.pixity.moderation.neoforge.auctionhouse.gui;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;

import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;

import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.SimpleContainer;

import org.howie.pixity.moderation.chat.TextFormatter;
import org.howie.pixity.moderation.neoforge.auctionhouse.*;
import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

import java.util.*;

import static org.howie.pixity.moderation.neoforge.auctionhouse.AuctionManager.deleteListingAdmin;

public class AuctionBrowserGui {

    private static final int SIZE = 54;
    private static final RankService RANK = new RankService();

    private static boolean isAdmin(ServerPlayer p) {
        try {
            return RANK.hasPerm(p, "pixity.ah.admin");
        } catch (Exception e) {
            return false;
        }
    }

    public static void open(ServerPlayer player, AuctionMenuState state) {

        List<AuctionListing> filtered = new ArrayList<>();

        String search = state.search == null ? "" : state.search.toLowerCase();




        for (AuctionListing l : AuctionDatabase.getActive()) {

            if (!search.isEmpty()) {

                boolean match = false;

                if (l.sellerName.toLowerCase().contains(search)) {
                    match = true;
                } else if (l.itemData != null && l.itemData.toLowerCase().contains(search)) {
                    match = true;
                }

                if (!match) continue;
            }

            filtered.add(l);
        }




        List<AuctionListing> sorted = new ArrayList<>(filtered);

        switch (state.sort) {
            case NEWEST -> sorted.sort((a, b) -> Long.compare(b.createdAt, a.createdAt));
            case OLDEST -> sorted.sort(Comparator.comparingLong(a -> a.createdAt));
            case PRICE_LOW -> sorted.sort(Comparator.comparingDouble(a -> a.price));
            case PRICE_HIGH -> sorted.sort((a, b) -> Double.compare(b.price, a.price));
        }

        int totalPages = (int) Math.ceil(sorted.size() / 45.0);
        if (state.page >= totalPages) state.page = Math.max(0, totalPages - 1);




        MenuConstructor ctor = (id, inv, p) -> new ChestMenu(
                net.minecraft.world.inventory.MenuType.GENERIC_9x6,
                id,
                inv,
                new SimpleContainer(SIZE),
                6
        ) {

            @Override
            public void broadcastChanges() {
                super.broadcastChanges();

                int start = state.page * 45;

                for (int i = 0; i < 45; i++) {

                    int index = start + i;
                    if (index >= sorted.size()) break;

                    AuctionListing listing = sorted.get(index);

                    try {
                        ItemStack item = listing.getItem(player.registryAccess()).copy();

                        List<Component> lore = new ArrayList<>();

                        lore.add(TextFormatter.parse("&6Price: " + listing.price + " " + listing.currency));
                        lore.add(TextFormatter.parse("&7Seller: " + listing.sellerName));
                        lore.add(TextFormatter.parse("&eLeft-click to buy"));

                        if (isAdmin(player)) {
                            lore.add(TextFormatter.parse("&cADMIN &eRight-click to delete"));
                        }

                        item.set(DataComponents.LORE, new ItemLore(lore));

                        this.getSlot(i).set(item);

                    } catch (Exception e) {
                        this.getSlot(i).set(new ItemStack(Items.BARRIER));
                    }
                }




                if (state.page > 0) {
                    ItemStack prev = new ItemStack(Items.ARROW);
                    prev.set(DataComponents.CUSTOM_NAME,
                            TextFormatter.parse("&cPrevious Page"));
                    this.getSlot(45).set(prev);
                }

                if (state.page < totalPages - 1) {
                    ItemStack next = new ItemStack(Items.ARROW);
                    next.set(DataComponents.CUSTOM_NAME,
                            TextFormatter.parse("&eNext Page"));
                    this.getSlot(53).set(next);
                }

                ItemStack sortItem = new ItemStack(Items.HOPPER);

                String mode = switch (state.sort) {
                    case NEWEST -> "&aNewest → Oldest";
                    case OLDEST -> "&aOldest → Newest";
                    case PRICE_LOW -> "&aLowest → Highest";
                    case PRICE_HIGH -> "&aHighest → Lowest";
                };

                sortItem.set(DataComponents.CUSTOM_NAME,
                        TextFormatter.parse("&e&lSORT"));

                List<Component> sortLore = new ArrayList<>();
                sortLore.add(TextFormatter.parse("&7Current: " + mode));
                sortLore.add(TextFormatter.parse("&eClick to change"));

                sortItem.set(DataComponents.LORE, new ItemLore(sortLore));

                this.getSlot(49).set(sortItem);

                ItemStack refresh = new ItemStack(Items.CLOCK);

                refresh.set(DataComponents.CUSTOM_NAME,
                        TextFormatter.parse("&e&lREFRESH"));

                List<Component> refreshLore = new ArrayList<>();
                refreshLore.add(TextFormatter.parse("&7Click to refresh listings"));
                refreshLore.add(TextFormatter.parse("&8Keeps filters & sorting"));

                refresh.set(DataComponents.LORE, new ItemLore(refreshLore));

                this.getSlot(48).set(refresh);
            }


            @Override
            public void clicked(int slot, int button,
                                ClickType type,
                                net.minecraft.world.entity.player.Player p) {

                if (!(p instanceof ServerPlayer sp)) return;


                if (type != ClickType.PICKUP && type != ClickType.PICKUP_ALL) return;

                this.setCarried(ItemStack.EMPTY);




                if (slot == 45 && state.page > 0) {
                    state.page--;
                    open(sp, state);
                    return;
                }



                if (slot == 53 && state.page < totalPages - 1) {
                    state.page++;
                    open(sp, state);
                    return;
                }




                if (slot == 49) {

                    state.sort = switch (state.sort) {
                        case NEWEST -> AuctionMenuState.SortType.OLDEST;
                        case OLDEST -> AuctionMenuState.SortType.PRICE_LOW;
                        case PRICE_LOW -> AuctionMenuState.SortType.PRICE_HIGH;
                        case PRICE_HIGH -> AuctionMenuState.SortType.NEWEST;
                    };

                    sp.sendSystemMessage(
                            TextFormatter.parse("&e&lAUCTIONHOUSE&7&l➤ &aSort changed!")
                    );

                    open(sp, state);
                    return;
                }




                if (slot == 48) {

                    open(sp, state);

                    sp.sendSystemMessage(
                            TextFormatter.parse("&e&lAUCTIONHOUSE&7&l➤ &aRefreshed listings.")
                    );

                    return;
                }

                if (slot >= 45) return;

                int start = state.page * 45;
                int index = start + slot;

                if (index >= sorted.size()) return;

                AuctionListing listing = sorted.get(index);

                if (listing == null || listing.sold) {
                    sp.sendSystemMessage(TextFormatter.parse("&e&lAUCTIONHOUSE&7&l➤ &cError! Listing no longer available."));
                    open(sp, state);
                    return;
                }



                boolean rightClick = button == 1;
                boolean leftClick = button == 0;






                if (rightClick) {

                    if (!isAdmin(sp)) {
                        sp.sendSystemMessage(TextFormatter.parse("&e&lAUCTIONHOUSE&7&l➤ &cError! No permission."));
                        return;
                    }

                    AuctionManager.deleteListingAdmin(sp, listing);
                    open(sp, state);
                    return;
                }




                if (leftClick) {

                    if (listing.seller.equals(sp.getUUID())) {
                        sp.sendSystemMessage(TextFormatter.parse("&e&lAUCTIONHOUSE&7&l➤ &cError! You cannot buy your own listing."));
                        return;
                    }

                    ConfirmPurchaseGui.open(sp,
                            AuctionManager.ECON,
                            listing,
                            state.page);
                }
            }
        };

        player.openMenu(new SimpleMenuProvider(
                ctor,
                TextFormatter.parse("<gold>Auction House</gold>")
        ));

    }
}

