package org.howie.pixity.moderation.neoforge.auctionhouse;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.*;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.chat.TextFormatter;
import org.howie.pixity.moderation.neoforge.auctionhouse.gui.*;
import org.howie.pixity.moderation.neoforge.economy.CurrencyType;
import org.howie.pixity.moderation.neoforge.economy.EconomyService;
import org.howie.pixity.moderation.neoforge.rank.RankService;

import net.minecraft.nbt.TagParser;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class AuctionCommands {

    private static final RankService RANK = new RankService();


    private final EconomyService econ;

    public AuctionCommands(EconomyService econ) {
        this.econ = econ;
    }




    public void register(CommandDispatcher<CommandSourceStack> d) {

        d.register(
                Commands.literal("ah")




                        .executes(ctx -> {
                            var player = ctx.getSource().getPlayerOrException();


                            try {
                                AuctionBrowserGui.open(player, new AuctionMenuState(player.getUUID()));
                            } catch (Exception e) {
                                e.printStackTrace();
                                player.sendSystemMessage(TextFormatter.parse("&e&lAUCTIONHOUSE &7&l➤ &cError! Please contact a admin!"));
                            }

                            return 1;
                        })

                        .then(Commands.literal("search")
                                .then(Commands.argument("query", StringArgumentType.greedyString())
                                        .executes(ctx -> {

                                            ServerPlayer player = ctx.getSource().getPlayerOrException();

                                            String query = StringArgumentType.getString(ctx, "query");

                                            AuctionMenuState state = new AuctionMenuState(player.getUUID());
                                            state.search = query;

                                            AuctionBrowserGui.open(player, state);

                                            return 1;
                                        })
                                )
                        )




                        .then(Commands.literal("sell")
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .then(Commands.argument("price", DoubleArgumentType.doubleArg(0.01))
                                                .then(Commands.argument("currency", StringArgumentType.word())
                                                        .executes(ctx -> {

                                                            ServerPlayer player = ctx.getSource().getPlayerOrException();

                                                            int amount = IntegerArgumentType.getInteger(ctx, "amount");
                                                            double price = DoubleArgumentType.getDouble(ctx, "price");
                                                            String currencyStr = StringArgumentType.getString(ctx, "currency");




                                                            CurrencyType currency;

                                                            try {
                                                                currency = CurrencyType.valueOf(currencyStr.toUpperCase());
                                                            } catch (Exception e) {
                                                                player.sendSystemMessage(TextFormatter.parse("&e&lAUCTIONHOUSE &7&l➤ <red>Error! Invalid currency!</red>"));
                                                                return 0;
                                                            }




                                                            ItemStack held = player.getMainHandItem();

                                                            if (held.isEmpty()) {
                                                                player.sendSystemMessage(TextFormatter.parse("&e&lAUCTIONHOUSE &7&l➤ <red>Error! You must hold an item!</red>"));
                                                                return 0;
                                                            }

                                                            if (held.getCount() < amount) {
                                                                player.sendSystemMessage(TextFormatter.parse("&e&lAUCTIONHOUSE &7&l➤ <red>Error! Not enough items!</red>"));
                                                                return 0;
                                                            }




                                                            int maxListings = org.howie.pixity.moderation.neoforge.util.PermissionNumber.highest(
                                                                    RANK,
                                                                    player,
                                                                    "pixity.auction.limit",
                                                                    100
                                                            );


                                                            if (maxListings < 0) {
                                                                maxListings = 3;
                                                            }

                                                            int currentListings = AuctionDatabase.getActiveCount(player.getUUID());

                                                            if (currentListings >= maxListings) {

                                                                player.sendSystemMessage(
                                                                        TextFormatter.parse(
                                                                                "&e&lAUCTIONHOUSE &7&l➤ &cError! You reached your auction limit! &e("
                                                                                        + currentListings + "/" + maxListings + ")</red>"
                                                                        )
                                                                );

                                                                return 0;
                                                            }




                                                            ItemStack toSell = held.copy();
                                                            toSell.setCount(amount);




                                                            double min = AuctionConfig.getMinPrice(toSell);

                                                            if (price < min) {
                                                                player.sendSystemMessage(
                                                                        TextFormatter.parse("&e&lAUCTIONHOUSE &7&l➤ Minimum price for this item is &e" + min + "!")
                                                                );
                                                                return 0;
                                                            }




                                                            held.shrink(amount);




                                                            String snbt = toSell.save(player.registryAccess()).toString();

                                                            long duration = 1000L * 60 * 60 * 168;

                                                            AuctionListing listing = new AuctionListing(
                                                                    player.getUUID(),
                                                                    player.getName().getString(),
                                                                    snbt,
                                                                    amount,
                                                                    price,
                                                                    currency.name(),
                                                                    duration
                                                            );

                                                            MinecraftServer server = ctx.getSource().getServer();
                                                            AuctionDatabase.create(listing, server);

                                                            player.sendSystemMessage(
                                                                    TextFormatter.parse(
                                                                            "&e&lAUCTIONHOUSE &7&l➤ <green>Listed item for &e" + price + " &a" + currency.name() +
                                                                                    " &e(" + (currentListings + 1) + "/" + maxListings + ")</green>"
                                                                    )
                                                            );

                                                            return 1;
                                                        })
                                                )
                                        )
                                )
                        )

                        .then(Commands.literal("my")
                                .executes(ctx -> {
                                    var player = ctx.getSource().getPlayerOrException();
                                    AuctionMyListingsGui.open(player);
                                    return 1;
                                })
                        )




                        .then(Commands.literal("delete")
                                .then(Commands.argument("id", StringArgumentType.string())
                                        .executes(ctx -> {

                                            ServerPlayer player = ctx.getSource().getPlayerOrException();

                                            String idStr = StringArgumentType.getString(ctx, "id");

                                            UUID id;
                                            try {
                                                id = UUID.fromString(idStr);
                                            } catch (Exception e) {
                                                player.sendSystemMessage(TextFormatter.parse("&e&lAUCTIONHOUSE &7&l➤ &cInvalid listing ID."));
                                                return 0;
                                            }

                                            AuctionListing listing = AuctionDatabase.get(id);

                                            if (listing == null) {
                                                player.sendSystemMessage(TextFormatter.parse("&e&lAUCTIONHOUSE &7&l➤ <red>Error! Listing not found</red>"));
                                                return 0;
                                            }

                                            if (!listing.seller.equals(player.getUUID())) {
                                                player.sendSystemMessage(TextFormatter.parse("&e&lAUCTIONHOUSE &7&l➤ <red>Error! Not your listing</red>"));
                                                return 0;
                                            }

                                            giveItemBack(player, listing);

                                            AuctionDatabase.remove(id);

                                            player.sendSystemMessage(TextFormatter.parse("&e&lAUCTIONHOUSE &7&l➤ <green>Listing removed</green>"));

                                            return 1;
                                        })
                                )
                        )




                        .then(Commands.literal("deleteother")
                                .requires(src -> has(src, "pixity.ah.admin"))
                                .then(Commands.argument("target", EntityArgument.player())
                                        .then(Commands.argument("id", StringArgumentType.string())
                                                .executes(ctx -> {

                                                    ServerPlayer admin = ctx.getSource().getPlayerOrException();
                                                    ServerPlayer target = EntityArgument.getPlayer(ctx, "target");

                                                    String idStr = StringArgumentType.getString(ctx, "id");

                                                    UUID id;
                                                    try {
                                                        id = UUID.fromString(idStr);
                                                    } catch (Exception e) {
                                                        admin.sendSystemMessage(TextFormatter.parse("&e&lAUCTIONHOUSE &7&l➤ &cInvalid listing ID."));
                                                        return 0;
                                                    }

                                                    AuctionListing listing = AuctionDatabase.get(id);

                                                    if (listing == null) {
                                                        admin.sendSystemMessage(TextFormatter.parse("&e&lAUCTIONHOUSE &7&l➤ <red>Error! Listing not found</red>"));
                                                        return 0;
                                                    }

                                                    giveItemBack(target, listing);

                                                    AuctionDatabase.remove(id);

                                                    admin.sendSystemMessage(TextFormatter.parse("&e&lAUCTIONHOUSE &7&l➤ <green>Listing removed</green>"));

                                                    return 1;
                                                })
                                        )
                                )
                        )




                        .then(Commands.literal("expired")
                                .executes(ctx -> {

                                    var player = ctx.getSource().getPlayerOrException();

                                    ExpiredGUI.open(player);

                                    return 1;
                                })
                        )
                        .then(Commands.literal("history")
                                .executes(ctx -> {

                                    ServerPlayer player = ctx.getSource().getPlayerOrException();

                                    AuctionHistoryGui.open(player, "");

                                    return 1;
                                })
                                .then(Commands.argument("search", StringArgumentType.greedyString())
                                        .executes(ctx -> {

                                            ServerPlayer player = ctx.getSource().getPlayerOrException();

                                            String search = StringArgumentType.getString(ctx, "search");

                                            player.sendSystemMessage(TextFormatter.parse("&e&lAUCTIONHOUSE&7&l➤ &aOpening auction house and searching for &e" + search + "&a!"));
                                            AuctionHistoryGui.open(player, search);

                                            return 1;
                                        })
                                )
                        )
        );
    }


    private static void giveItemBack(ServerPlayer player, AuctionListing listing) {

        try {
            var tag = TagParser.parseTag(listing.itemData);
            ItemStack item = ItemStack.parseOptional(player.registryAccess(), tag);

            player.getInventory().add(item);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean has(CommandSourceStack src, String node) {
        try {
            return RANK.hasPerm(src.getPlayerOrException(), node);
        } catch (Exception e) {
            return false;
        }
    }
}