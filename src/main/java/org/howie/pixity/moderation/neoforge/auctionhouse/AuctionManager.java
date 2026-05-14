package org.howie.pixity.moderation.neoforge.auctionhouse;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.TagParser;

import org.howie.pixity.moderation.chat.CachedText;
import org.howie.pixity.moderation.chat.TextFormatter;
import org.howie.pixity.moderation.neoforge.auctionhouse.AuctionDatabase;
import org.howie.pixity.moderation.neoforge.auctionhouse.AuctionListing;
import org.howie.pixity.moderation.neoforge.auctionhouse.history.AuctionHistoryDatabase;
import org.howie.pixity.moderation.neoforge.economy.CurrencyType;
import org.howie.pixity.moderation.neoforge.economy.EconomyService;

import java.util.UUID;

import static org.howie.pixity.moderation.neoforge.auctionhouse.AuctionDatabase.remove;

public class AuctionManager {


    public static EconomyService ECON;

    public static void deleteListingAdmin(ServerPlayer admin, AuctionListing listing) {

        if (listing == null) return;

        try {
            ItemStack item = listing.getItem(admin.registryAccess()).copy();

            ServerPlayer seller = admin.server.getPlayerList().getPlayer(listing.seller);

            if (seller != null) {
                if (!seller.getInventory().add(item)) {
                    seller.drop(item, false);
                }
            } else {

                AuctionDatabase.addExpired(listing.seller, listing);
            }

        } catch (Exception ignored) {}

        AuctionHistoryDatabase.add(listing);

        AuctionDatabase.remove(listing.id);

        admin.sendSystemMessage(
                TextFormatter.parse("&e&lAUCTIONHOUSE&7&l➤ &cListing deleted (admin).")
        );
    }




    public static void sell(ServerPlayer player, int amount, double price, String currencyRaw, MinecraftServer server) {

        CurrencyType currency;

        try {
            currency = CurrencyType.valueOf(currencyRaw.toUpperCase());
        } catch (Exception e) {
            player.sendSystemMessage(CachedText.of("&e&lAUCTIONHOUSE&7&l➤ &cError! Invalid currency, use Money, Coins or Tokens"));
            return;
        }

        ItemStack hand = player.getMainHandItem();

        if (hand.isEmpty()) {
            player.sendSystemMessage(CachedText.of("&e&lAUCTIONHOUSE&7&l➤ &cError! Hold an item to sell"));
            return;
        }

        if (amount <= 0 || amount > hand.getCount()) {
            player.sendSystemMessage(CachedText.of("&e&lAUCTIONHOUSE&7&l➤ &cError! Invalid amount"));
            return;
        }


        ItemStack copy = hand.copy();
        copy.setCount(amount);


        hand.shrink(amount);


        String snbt = copy.save(player.registryAccess()).toString();

        long duration = 1000L * 60 * 60 * 24;

        AuctionListing listing = new AuctionListing(
                player.getUUID(),
                player.getName().getString(),
                snbt,
                amount,
                price,
                currency.name(),
                duration
        );

        AuctionDatabase.create(listing, server);

        player.sendSystemMessage(CachedText.of(
                "&e&lAUCTIONHOUSE&7&l➤ &aListed item for &e" + price + " " + currency.name() + "!"
        ));
    }




    public static void buy(ServerPlayer buyer, AuctionListing listing) {

        AuctionListing live = AuctionDatabase.get(listing.id);

        if (live == null || live.sold) {
            buyer.sendSystemMessage(CachedText.of("&e&lAUCTIONHOUSE&7&l➤ &cError! Listing not available"));
            return;
        }

        if (live.seller.equals(buyer.getUUID())) {
            buyer.sendSystemMessage(CachedText.of("&e&lAUCTIONHOUSE&7&l➤ &cError! You cannot buy your own listing, cancel it via /ah my"));
            return;
        }

        if (ECON == null) {
            buyer.sendSystemMessage(CachedText.of("&e&lAUCTIONHOUSE&7&l➤ &cError! <red>Economy not loaded, please contact PrestigeMidnight</red>"));
            return;
        }

        CurrencyType currency = CurrencyType.valueOf(live.currency);

        if (!ECON.remove(buyer, currency, live.price)) {
            buyer.sendSystemMessage(CachedText.of("&e&lAUCTIONHOUSE&7&l➤ &cError! <red>Not enough funds</red>"));
            return;
        }


        live.sold = true;

        try {
            ItemStack item = ItemStack.parseOptional(
                    buyer.registryAccess(),
                    TagParser.parseTag(live.itemData)
            );

            boolean added = buyer.getInventory().add(item);

            if (!added) {
                buyer.drop(item, false);
                buyer.sendSystemMessage(TextFormatter.parse("&e&lAUCTIONHOUSE&7&l➤ &cInventory full — &eitem dropped."));
            }

        } catch (Exception e) {
            buyer.sendSystemMessage(CachedText.of("&e&lAUCTIONHOUSE&7&l➤ &cError! Item failed to load, please make a ticket in discord!"));
            return;
        }

        ECON.add(live.seller, currency, live.price);

        AuctionDatabase.remove(live.id);

        buyer.sendSystemMessage(CachedText.of("&e&lAUCTIONHOUSE&7&l➤ <green>Purchase successful</green>"));
    }




    public static void delete(ServerPlayer player, UUID id){

        AuctionListing listing = AuctionDatabase.get(id);

        if (listing == null) {
            player.sendSystemMessage(CachedText.of("&e&lAUCTIONHOUSE&7&l➤ &cError! Listing not found"));
            return;
        }

        if (!listing.seller.equals(player.getUUID())) {
            player.sendSystemMessage(CachedText.of("&e&lAUCTIONHOUSE&7&l➤ &cError! This is not your listing"));
            return;
        }

        AuctionHistoryDatabase.add(listing);

        returnItem(player, listing);

        remove(id);

        player.sendSystemMessage(CachedText.of("&e&lAUCTIONHOUSE&7&l➤ &aListing removed"));
    }




    public static void deleteOther(ServerPlayer admin, UUID target, UUID id) {

        AuctionListing listing = AuctionDatabase.get(id);

        if (listing == null) {
            admin.sendSystemMessage(CachedText.of("&e&lAUCTIONHOUSE&7&l➤ &cError! Listing not found"));
            return;
        }

        returnItemOffline(target, listing);

        remove(id);

        admin.sendSystemMessage(CachedText.of("&e&lAUCTIONHOUSE&7&l➤ &aListing removed (admin)"));
    }




    public static void claimExpired(ServerPlayer player) {

        var list = AuctionDatabase.getExpired(player.getUUID());

        if (list.isEmpty()) {
            player.sendSystemMessage(CachedText.of("&e&lAUCTIONHOUSE&7&l➤ <gray>No expired listings</gray>"));
            return;
        }

        int claimed = 0;

        for (AuctionListing listing : list) {

            try {
                ItemStack item = ItemStack.parseOptional(
                        player.registryAccess(),
                        TagParser.parseTag(listing.itemData)
                );

                player.getInventory().add(item);

                AuctionDatabase.addExpired(listing);
                remove(listing.id);
                claimed++;

            } catch (Exception ignored) {}
        }

        player.sendSystemMessage(CachedText.of(
                "&e&lAUCTIONHOUSE&7&l➤ <green>Claimed &e" + claimed + " &cexpired items</green>"
        ));
    }




    private static void returnItem(ServerPlayer player, AuctionListing listing) {

        try {
            ItemStack item = ItemStack.parseOptional(
                    player.registryAccess(),
                    TagParser.parseTag(listing.itemData)
            );

            player.getInventory().add(item);

        } catch (Exception ignored) {}
    }

    private static void returnItemOffline(UUID uuid, AuctionListing listing) {


        ECON.add(uuid, CurrencyType.MONEY, 0);
    }
}