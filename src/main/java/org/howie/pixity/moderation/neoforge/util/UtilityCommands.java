package org.howie.pixity.moderation.neoforge.util;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CompletableFuture;

public final class UtilityCommands {

    private final Set<UUID> trashAuto = ConcurrentHashMap.newKeySet();
    private final org.howie.pixity.moderation.neoforge.rank.RankService ranks;
    private long lastGiveAll = 0;
    private static final long GIVEALL_COOLDOWN = 10_000;

    public UtilityCommands(org.howie.pixity.moderation.neoforge.rank.RankService ranks) {
        this.ranks = ranks;
    }



    @SubscribeEvent
    public void onRegister(RegisterCommandsEvent e) {

        CommandDispatcher<CommandSourceStack> d = e.getDispatcher();



        d.register(Commands.literal("heal")
                .executes(ctx -> {
                    ServerPlayer p = ctx.getSource().getPlayerOrException();
                    return heal(p);
                })

                .then(Commands.argument("target", StringArgumentType.word())
                        .suggests(this::suggestPlayersAll)
                        .executes(ctx -> {

                            String arg = StringArgumentType.getString(ctx, "target");

                            if (isAll(arg)) {
                                for (ServerPlayer p : ctx.getSource().getServer().getPlayerList().getPlayers()) {
                                    heal(p);
                                }
                                return 1;
                            }

                            return heal(getPlayer(ctx, arg));
                        })
                )
        );



        d.register(Commands.literal("feed")
                .executes(ctx -> {
                    ServerPlayer p = ctx.getSource().getPlayerOrException();
                    return feed(p);
                })

                .then(Commands.argument("target", StringArgumentType.word())
                        .suggests(this::suggestPlayersAll)
                        .executes(ctx -> {

                            String arg = StringArgumentType.getString(ctx, "target");

                            if (isAll(arg)) {
                                for (ServerPlayer p : ctx.getSource().getServer().getPlayerList().getPlayers()) {
                                    feed(p);
                                }
                                return 1;
                            }

                            return feed(getPlayer(ctx, arg));
                        })
                )
        );


        d.register(Commands.literal("clearinventory")
                .executes(ctx -> clear(ctx.getSource().getPlayerOrException()))

                .then(Commands.argument("target", StringArgumentType.word())
                        .suggests(this::suggestPlayersAll)
                        .executes(ctx -> {

                            String arg = StringArgumentType.getString(ctx, "target");

                            if (isAll(arg)) {
                                for (ServerPlayer p : ctx.getSource().getServer().getPlayerList().getPlayers()) {
                                    clear(p);
                                }
                                return 1;
                            }

                            return clear(getPlayer(ctx, arg));
                        })
                )
        );


        d.register(Commands.literal("give")
                .requires(cs -> cs.hasPermission(2))

                .then(Commands.argument("item", ItemArgument.item(e.getBuildContext()))
                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                .executes(ctx -> {
                                    ServerPlayer p = ctx.getSource().getPlayerOrException();
                                    ItemInput input = ItemArgument.getItem(ctx, "item");
                                    int amount = IntegerArgumentType.getInteger(ctx, "amount");

                                    p.getInventory().add(input.createItemStack(amount, false));
                                    return 1;
                                })
                        )
                )
        );





        d.register(Commands.literal("giveall")
                .requires(cs -> cs.hasPermission(2))

                .then(Commands.argument("item", ItemArgument.item(e.getBuildContext()))
                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))


                                .executes(ctx -> {
                                    giveAll(ctx, null);
                                    return 1;
                                })


                                .then(Commands.argument("rank", StringArgumentType.word())
                                        .suggests((c, b) -> {
                                            b.suggest("all");
                                            b.suggest("*");
                                            b.suggest("vip");
                                            b.suggest("default");
                                            return b.buildFuture();
                                        })
                                        .executes(ctx -> {
                                            String rank = StringArgumentType.getString(ctx, "rank");
                                            giveAll(ctx, rank);
                                            return 1;
                                        })
                                )
                        )
                )

                .then(Commands.literal("hand")
                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))

                                .executes(ctx -> {
                                    giveAllHand(ctx, null);
                                    return 1;
                                })

                                .then(Commands.argument("rank", StringArgumentType.word())
                                        .executes(ctx -> {
                                            String rank = StringArgumentType.getString(ctx, "rank");
                                            giveAllHand(ctx, rank);
                                            return 1;
                                        })
                                )
                        )
                )
        );
    }





    private int give(ServerPlayer p, CommandContext<CommandSourceStack> ctx) {
        if (p == null) return 0;

        try {
            ItemInput input = ItemArgument.getItem(ctx, "item");
            int amount = IntegerArgumentType.getInteger(ctx, "amount");

            p.getInventory().add(input.createItemStack(amount, false));
            return 1;

        } catch (Exception e) {
            ctx.getSource().sendFailure(LegacyAmpersand.parse("&e&lUTILITY &7&l➤ §cError! Invalid item."));
            return 0;
        }
    }

    private int giveAll(CommandContext<CommandSourceStack> ctx, String rank) {

        long now = System.currentTimeMillis();

        if (now - lastGiveAll < GIVEALL_COOLDOWN) {
            ctx.getSource().sendFailure(LegacyAmpersand.parse("&e&lUTILITY &7&l➤ §cError! Giveaway is on cooldown!"));
            return 0;
        }

        lastGiveAll = now;

        try {
            ItemInput input = ItemArgument.getItem(ctx, "item");
            int amount = IntegerArgumentType.getInteger(ctx, "amount");

            ItemStack preview = input.createItemStack(amount, false);

            var players = ctx.getSource().getServer().getPlayerList().getPlayers();

            for (ServerPlayer p : players) {

                if (rank != null && !isAll(rank)) {
                    if (!ranks.hasPerm(p, "group." + rank)) continue;
                }

                p.getInventory().add(preview.copy());


                p.playNotifySound(net.minecraft.sounds.SoundEvents.PLAYER_LEVELUP,
                        net.minecraft.sounds.SoundSource.PLAYERS,
                        1.0f, 1.2f);


                spawnFirework(p);
            }



            ServerPlayer sender = ctx.getSource().getPlayer();

            Component senderName = (sender != null)
                    ? sender.getDisplayName()
                    : Component.literal("Console");

            Component itemName = preview.getDisplayName();

            Component hover = Component.literal("")
                    .append(Component.literal("§6Item Preview:\n"))
                    .append(itemName);

            Component base = Component.literal("")
                    .append(senderName)
                    .append(Component.literal(" gave "))
                    .append(Component.literal(rank != null && !isAll(rank)
                            ? rank + " players "
                            : "everyone "))
                    .append(Component.literal(amount + "x "))
                    .append(itemName)
                    .append(Component.literal("!"))
                    .withStyle(style -> style.withHoverEvent(
                            new net.minecraft.network.chat.HoverEvent(
                                    net.minecraft.network.chat.HoverEvent.Action.SHOW_ITEM,
                                    new net.minecraft.network.chat.HoverEvent.ItemStackInfo(preview)
                            )
                    ));

            Component finalMsg = Component.literal("§4§lGIVEAWAY §c§l>> §r")
                    .append(base);

            ctx.getSource().getServer().getPlayerList().broadcastSystemMessage(finalMsg, false);

            return 1;

        } catch (Exception e) {
            ctx.getSource().sendFailure(LegacyAmpersand.parse("&e&lUTILITY &7&l➤ §cError! Invalid item."));
            return 0;
        }
    }

    private int giveAllHand(CommandContext<CommandSourceStack> ctx, String rank) {

        long now = System.currentTimeMillis();

        if (now - lastGiveAll < GIVEALL_COOLDOWN) {
            ctx.getSource().sendFailure(LegacyAmpersand.parse("&e&lUTILITY &7&l➤ §cError! Giveaway is on cooldown!"));
            return 0;
        }

        lastGiveAll = now;

        ServerPlayer sender = ctx.getSource().getPlayer();
        if (sender == null) return 0;

        int amount = IntegerArgumentType.getInteger(ctx, "amount");
        ItemStack hand = sender.getMainHandItem();

        if (hand.isEmpty()) {
            sender.sendSystemMessage(LegacyAmpersand.parse("&e&lUTILITY &7&l➤ §cError! You must hold an item."));
            return 0;
        }

        var players = ctx.getSource().getServer().getPlayerList().getPlayers();

        for (ServerPlayer p : players) {

            if (rank != null && !isAll(rank)) {
                if (!ranks.hasPerm(p, "group." + rank)) continue;
            }

            ItemStack copy = hand.copy();
            copy.setCount(amount);
            p.getInventory().add(copy);


            p.playNotifySound(
                    net.minecraft.sounds.SoundEvents.PLAYER_LEVELUP,
                    net.minecraft.sounds.SoundSource.PLAYERS,
                    1.0f, 1.2f
            );


            spawnFirework(p);
        }



        Component base = Component.literal("")
                .append(sender.getDisplayName())
                .append(Component.literal(" gave "))
                .append(Component.literal(rank != null && !isAll(rank)
                        ? rank + " players "
                        : "everyone "))
                .append(Component.literal(amount + "x "))
                .append(hand.getDisplayName())
                .append(Component.literal("!"))
                .withStyle(style -> style.withHoverEvent(
                        new net.minecraft.network.chat.HoverEvent(
                                net.minecraft.network.chat.HoverEvent.Action.SHOW_ITEM,
                                new net.minecraft.network.chat.HoverEvent.ItemStackInfo(hand)
                        )
                ));

        Component finalMsg = Component.literal("§4§lGIVEAWAY §c§l>> §r")
                .append(base);

        ctx.getSource().getServer().getPlayerList().broadcastSystemMessage(finalMsg, false);

        return 1;
    }

    private void spawnFirework(ServerPlayer p) {

        var level = p.level();

        for (int i = 0; i < 3; i++) {

            FireworkRocketEntity fw = new FireworkRocketEntity(
                    level,
                    p.getX(),
                    p.getY() + 1,
                    p.getZ(),
                    new ItemStack(Items.FIREWORK_ROCKET)
            );

            fw.setDeltaMovement(
                    (level.random.nextDouble() - 0.5) * 0.2,
                    0.5,
                    (level.random.nextDouble() - 0.5) * 0.2
            );

            level.addFreshEntity(fw);
        }
    }

    private int heal(ServerPlayer p) {
        p.setHealth(p.getMaxHealth());
        p.clearFire();
        return 1;
    }

    private int feed(ServerPlayer p) {
        p.getFoodData().setFoodLevel(20);
        p.getFoodData().setSaturation(20f);
        return 1;
    }

    private int clear(ServerPlayer p) {
        p.getInventory().clearContent();
        return 1;
    }

    private static ServerPlayer getPlayer(CommandContext<CommandSourceStack> ctx, String name) {
        return ctx.getSource().getServer().getPlayerList()
                .getPlayerByName(StringArgumentType.getString(ctx, name));
    }

    private static boolean isAll(String s) {
        return s.equalsIgnoreCase("*") || s.equalsIgnoreCase("all");
    }





    private CompletableFuture<Suggestions> suggestPlayers(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder b) {
        for (ServerPlayer p : ctx.getSource().getServer().getPlayerList().getPlayers()) {
            b.suggest(p.getGameProfile().getName());
        }
        return b.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestPlayersAll(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder b) {
        b.suggest("*");
        b.suggest("all");
        return suggestPlayers(ctx, b);
    }
}