package org.howie.pixity.moderation.neoforge.fishing;

import com.mojang.brigadier.CommandDispatcher;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.suggestion.Suggestions;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.howie.pixity.moderation.chat.TextFormatter;
import org.howie.pixity.moderation.neoforge.fishing.augment.FishingAugment;
import org.howie.pixity.moderation.neoforge.fishing.events.FishingEventManager;
import org.howie.pixity.moderation.neoforge.fishing.events.FishingEventType;
import org.howie.pixity.moderation.neoforge.fishing.gui.*;
import org.howie.pixity.moderation.neoforge.rank.RankService;

import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.util.NameResolver;


public class FishingCommands {

    private static final RankService RANKS = new RankService();

    private static final String PREFIX =
            "<gradient:#00CFFF:#0066FF>&lFISHING</gradient> &7&l➤ ";


    public static void register(CommandDispatcher<CommandSourceStack> d) {



        d.register(Commands.literal("fishing")
                .requires(src -> hasPerm(src, "pixity.fishing.use"))

                .executes(ctx -> {

                    ServerPlayer player = ctx.getSource().getPlayerOrException();

                    FishingMainMenu.open(player);

                    ctx.getSource().sendSystemMessage(
                            TextFormatter.parse("<gradient:#00CFFF:#0066FF>&lFISHING</gradient> &7&l➤ &aOpening fishing menu...")
                    );

                    return 1;
                })

                .then(Commands.literal("entropy")
                        .requires(src -> hasPerm(src, "pixity.fishing.use"))
                        .executes(ctx -> {

                            ServerPlayer player = ctx.getSource().getPlayerOrException();

                            int entropy = FishingDatabase.getEntropy(player.getUUID());

                            player.sendSystemMessage(
                                    TextFormatter.parse(
                                            "<gradient:#00CFFF:#0066FF>&lFISHING</gradient>" + "&7&l➤ &7You currently have &b" +
                                                    entropy +
                                                    " entropy&7."
                                    )
                            );

                            return 1;
                        })
                )




                .then(Commands.literal("gut")
                        .executes(ctx -> {
                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                            FishingGutGui.open(player);

                            ctx.getSource().sendSystemMessage(TextFormatter.parse("<gradient:#00CFFF:#0066FF>&lFISHING</gradient> &7&l➤ &aOpening menu..."));

                            return 1;
                        })
                )




                .then(Commands.literal("stats")
                        .requires(src -> hasPerm(src, "pixity.fishing.use"))
                        .executes(ctx -> {
                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                            FishingStatsGui.open(player);

                            ctx.getSource().sendSystemMessage(TextFormatter.parse("<gradient:#00CFFF:#0066FF>&lFISHING</gradient> &7&l➤ &aOpening stats..."));

                            return 1;
                        })
                )




                .then(Commands.literal("leaderboard")
                        .requires(src -> hasPerm(src, "pixity.fishing.use"))
                        .executes(ctx -> {
                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                            FishingLeaderboardCategoriesGui.open(player);

                            ctx.getSource().sendSystemMessage(TextFormatter.parse("<gradient:#00CFFF:#0066FF>&lFISHING</gradient> &7&l➤ &aOpening leaderboards..."));

                            return 1;
                        })
                )



                .then(Commands.literal("skills")
                        .requires(src -> hasPerm(src, "pixity.fishing.use"))
                        .executes(ctx -> {
                            FishingSkillsGui.open(ctx.getSource().getPlayerOrException());

                            ctx.getSource().sendSystemMessage(TextFormatter.parse("<gradient:#00CFFF:#0066FF>&lFISHING</gradient> &7&l➤ &aOpening menu..."));

                            return 1;
                        })
                )



                .then(Commands.literal("sell")
                        .requires(src -> hasPerm(src, "pixity.fishing.use"))
                        .executes(ctx -> {
                            FishingShopGui.open(ctx.getSource().getPlayerOrException());

                            ctx.getSource().sendSystemMessage(TextFormatter.parse("<gradient:#00CFFF:#0066FF>&lFISHING</gradient> &7&l➤ &aOpening menu..."));

                            return 1;
                        })
                )



                .then(Commands.literal("augment")
                        .requires(src -> hasPerm(src, "pixity.fishing.use"))
                        .executes(ctx -> {
                            FishingApplyAugmentGui.open(ctx.getSource().getPlayerOrException());

                            ctx.getSource().sendSystemMessage(TextFormatter.parse("<gradient:#00CFFF:#0066FF>&lFISHING</gradient> &7&l➤ &aOpening menu..."));

                            return 1;
                        })
                )



                .then(Commands.literal("augments")
                        .requires(src -> hasPerm(src, "pixity.fishing.use"))
                        .executes(ctx -> {
                            FishingAugmentsGui.open(ctx.getSource().getPlayerOrException());

                            ctx.getSource().sendSystemMessage(TextFormatter.parse("<gradient:#00CFFF:#0066FF>&lFISHING</gradient> &7&l➤ &aOpening menu..."));

                            return 1;
                        })
                )



                .then(Commands.literal("codex")
                        .requires(src -> hasPerm(src, "pixity.fishing.use"))
                        .executes(ctx -> {
                            FishingCodexGui.open(ctx.getSource().getPlayerOrException());

                            ctx.getSource().sendSystemMessage(TextFormatter.parse("<gradient:#00CFFF:#0066FF>&lFISHING</gradient> &7&l➤ &aOpening menu..."));

                            return 1;
                        })
                )



                .then(Commands.literal("scales")
                        .requires(src -> hasPerm(src, "pixity.fishing.use"))
                        .executes(ctx -> {
                            FishScalesGui.open(ctx.getSource().getPlayerOrException());

                            ctx.getSource().sendSystemMessage(TextFormatter.parse("<gradient:#00CFFF:#0066FF>&lFISHING</gradient> &7&l➤ &aOpening menu..."));

                            return 1;
                        })
                )

                .then(Commands.literal("deliveries")
                        .requires(src -> hasPerm(src, "pixity.fishing.use"))
                        .executes(ctx -> {
                            FishingDeliveriesGui.open(ctx.getSource().getPlayerOrException());

                            ctx.getSource().sendSystemMessage(TextFormatter.parse("<gradient:#00CFFF:#0066FF>&lFISHING</gradient> &7&l➤ &aOpening menu..."));

                            return 1;
                        })
                )

                .then(Commands.literal("deliveryupgrades")
                        .requires(src -> hasPerm(src, "pixity.fishing.use"))
                        .executes(ctx -> {
                            FishingDeliveryUpgradesGui.open(ctx.getSource().getPlayerOrException());


                            ctx.getSource().sendSystemMessage(TextFormatter.parse("<gradient:#00CFFF:#0066FF>&lFISHING</gradient> &7&l➤ &aOpening menu..."));
                            return 1;
                        })
                )

                .then(Commands.literal("event")
                        .requires(src -> hasPerm(src, "pixity.fishing.use"))
                        .executes(ctx -> {

                            var src = ctx.getSource();
                            var player = src.getPlayer();




                            if (!FishingEventManager.isActive()) {

                                long ms = FishingEventManager.getTimeUntilNextEvent();

                                long seconds = (ms / 1000) % 60;
                                long minutes = (ms / 60000) % 60;
                                long hours = (ms / 3600000);

                                src.sendSuccess(() -> TextFormatter.parse(
                                        "<gradient:#00CFFF:#0066FF>&lFISHING</gradient> &7&l➤ <gray>No active event</gray>\n" +
                                                "<yellow>Next event in:</yellow> <aqua>"
                                                + hours + "h " + minutes + "m " + seconds + "s</aqua>"
                                ), false);

                                return 1;
                            }




                            var type = FishingEventManager.getCurrentEvent();
                            var scores = FishingEventManager.getScores();

                            long ms = FishingEventManager.getTimeRemaining();

                            long seconds = (ms / 1000) % 60;
                            long minutes = (ms / 60000);


                            var top = scores.entrySet().stream()
                                    .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                                    .limit(3)
                                    .toList();

                            List<Map.Entry<UUID, Integer>> sorted = scores.entrySet().stream()
                                    .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                                    .toList();

                            UUID playerId = player.getUUID();

                            int playerRank = -1;
                            int playerScore = 0;

                            for (int i = 0; i < sorted.size(); i++) {
                                if (sorted.get(i).getKey().equals(playerId)) {
                                    playerRank = i + 1;
                                    playerScore = sorted.get(i).getValue();
                                    break;
                                }
                            }

                            StringBuilder msg = new StringBuilder();

                            msg.append("🏆 <gradient:#00CFFF:#0066FF>&lFISHING EVENT</gradient> 🏆\n");

                            msg.append("<gray>Current Event:</gray> <yellow>")
                                    .append(formatEvent(type))
                                    .append("</yellow>\n\n");

                            msg.append("<aqua>Time Remaining:</aqua> <white>")
                                    .append(minutes).append("m ").append(seconds).append("s</white>\n\n");

                            msg.append("<green>Top Players:</green>\n");

                            if (top.isEmpty()) {
                                msg.append("<gray>No scores yet</gray>\n");
                            } else {

                                int rank = 1;

                                for (var entry : top) {

                                    String name = NameResolver.nameOrUuid(player.server, entry.getKey());

                                    String color = switch (rank) {
                                        case 1 -> "<gold>";
                                        case 2 -> "<gray>";
                                        case 3 -> "&#cd7f32";
                                        default -> "<white>";
                                    };

                                    msg.append(color)
                                            .append("#").append(rank).append(" </")
                                            .append(color.replace("<","").replace(">",""))
                                            .append("> ")
                                            .append("<white>").append(name).append("</white>")
                                            .append(" <yellow>").append(entry.getValue()).append("</yellow>\n");

                                    rank++;
                                }

                                msg.append("\n<yellow>Your Position:</yellow>\n");

                                if (playerRank == -1) {

                                    msg.append("<gray>You have not participated yet</gray>\n");

                                } else {

                                    int index = playerRank - 1;




                                    if (index - 1 >= 0) {

                                        var above = sorted.get(index - 1);

                                        String name = NameResolver.nameOrUuid(player.server, above.getKey());

                                        msg.append("<gray>#")
                                                .append(index)
                                                .append("</gray> <white>")
                                                .append(name)
                                                .append("</white> <yellow>")
                                                .append(above.getValue())
                                                .append("</yellow>\n");
                                    }




                                    msg.append("<aqua>#")
                                            .append(playerRank)
                                            .append("</aqua> &l<white>")
                                            .append(player.getName().getString())
                                            .append("</white> <yellow>")
                                            .append(playerScore)
                                            .append("</yellow>\n");




                                    if (index + 1 < sorted.size()) {

                                        var below = sorted.get(index + 1);

                                        String name = NameResolver.nameOrUuid(player.server, below.getKey());

                                        msg.append("<gray>#")
                                                .append(index + 2)
                                                .append("</gray> <white>")
                                                .append(name)
                                                .append("</white> <yellow>")
                                                .append(below.getValue())
                                                .append("</yellow>\n");
                                    }
                                }
                            }

                            src.sendSuccess(() -> TextFormatter.parse(msg.toString()), false);

                            return 1;
                        })
                )

                .then(Commands.literal("setlevel")
                        .requires(src -> hasPerm(src, "pixity.fishing.admin"))
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("level", IntegerArgumentType.integer(1, 100))
                                        .executes(ctx -> {

                                            ServerPlayer target =
                                                    EntityArgument.getPlayer(ctx, "player");

                                            int level =
                                                    IntegerArgumentType.getInteger(ctx, "level");

                                            UUID uuid = target.getUUID();




                                            try {

                                                var ps = FishingDatabase.getConnection()
                                                        .prepareStatement(
                                                                "UPDATE fishing_stats SET level = ? WHERE uuid = ?"
                                                        );

                                                ps.setInt(1, level);
                                                ps.setString(2, uuid.toString());

                                                ps.executeUpdate();

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                            ctx.getSource().sendSuccess(() ->
                                                            TextFormatter.parse(
                                                                    "<gradient:#00CFFF:#0066FF>&lFISHING</gradient> &7&l➤ &aSet &e" +
                                                                            target.getName().getString() +
                                                                            "&a's fishing level to &e" +
                                                                            level
                                                            ),
                                                    true
                                            );

                                            return 1;
                                        })
                                )
                        )
                )

                .then(Commands.literal("addskillpoints")
                        .requires(src -> hasPerm(src, "pixity.fishing.admin"))
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(ctx -> {

                                            ServerPlayer target =
                                                    EntityArgument.getPlayer(ctx, "player");

                                            int amount =
                                                    IntegerArgumentType.getInteger(ctx, "amount");

                                            UUID uuid = target.getUUID();

                                            int current =
                                                    FishingDatabase.getSkillPoints(uuid);

                                            int newAmount = current + amount;




                                            try {

                                                var ps = FishingDatabase.getConnection()
                                                        .prepareStatement(
                                                                "UPDATE fishing_stats SET skill_points = ? WHERE uuid = ?"
                                                        );

                                                ps.setInt(1, newAmount);
                                                ps.setString(2, uuid.toString());

                                                ps.executeUpdate();

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                            ctx.getSource().sendSuccess(() ->
                                                            TextFormatter.parse(
                                                                    "<gradient:#00CFFF:#0066FF>&lFISHING</gradient> &7&l➤ &aAdded &e" +
                                                                            amount +
                                                                            " &askill points to &e" +
                                                                            target.getName().getString()
                                                            ),
                                                    true
                                            );

                                            return 1;
                                        })
                                )
                        )
                )

                .then(Commands.literal("addxp")
                        .requires(src -> hasPerm(src, "pixity.fishing.admin"))
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(ctx -> {

                                            var target = EntityArgument.getPlayer(ctx, "player");
                                            int amount = IntegerArgumentType.getInteger(ctx, "amount");

                                            FishingDatabase.addXP(target.getUUID(), amount);
                                            FishingManager.checkLevelUp(target);

                                            ctx.getSource().sendSuccess(() ->
                                                    TextFormatter.parse("<gradient:#00CFFF:#0066FF>&lFISHING</gradient> &7&l➤ &aSuccessfully added &e" + amount + " XP" + " &afrom &e" + target), true);


                                            return 1;
                                        })
                                ))
                )

                .then(Commands.literal("removexp")
                        .requires(src -> hasPerm(src, "pixity.fishing.admin"))
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(ctx -> {

                                            var target = EntityArgument.getPlayer(ctx, "player");
                                            int amount = IntegerArgumentType.getInteger(ctx, "amount");

                                            int current = FishingDatabase.getXP(target.getUUID());
                                            int newXP = Math.max(0, current - amount);


                                            try {
                                                var ps = FishingDatabase.getConnection().prepareStatement(
                                                        "UPDATE fishing_stats SET xp = ? WHERE uuid = ?"
                                                );
                                                ps.setInt(1, newXP);
                                                ps.setString(2, target.getUUID().toString());
                                                ps.executeUpdate();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                            ctx.getSource().sendSuccess(() ->
                                                    TextFormatter.parse("<gradient:#00CFFF:#0066FF>&lFISHING</gradient> &7&l➤ &aSuccessfully removed &e" + amount + " XP" + " &afrom &e" + target), true);

                                            return 1;
                                        })
                                ))
                )

                .then(Commands.literal("givelure")
                        .requires(src -> hasPerm(src, "pixity.fishing.admin"))
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("tier", StringArgumentType.word())
                                        .then(Commands.argument("duration", IntegerArgumentType.integer(1))
                                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                                        .executes(ctx -> {

                                                            var target = EntityArgument.getPlayer(ctx, "player");

                                                            String tierStr = StringArgumentType.getString(ctx, "tier").toUpperCase();
                                                            int duration = IntegerArgumentType.getInteger(ctx, "duration");
                                                            int amount = IntegerArgumentType.getInteger(ctx, "amount");

                                                            FishTier tier = FishTier.valueOf(tierStr);

                                                            for (int i = 0; i < amount; i++) {
                                                                target.getInventory().add(
                                                                        FishingManager.createLure(tier, duration)
                                                                );
                                                            }

                                                            target.sendSystemMessage(TextFormatter.parse("<gradient:#00CFFF:#0066FF>&lFISHING</gradient> &7&l➤ &7You have been given lures &7by &c" + ctx.getSource()));


                                                            return 1;
                                                        })
                                                ))))
                )

                .then(Commands.literal("giveitem")
                        .requires(src -> hasPerm(src, "pixity.fishing.admin"))
                        .then(Commands.argument("player", EntityArgument.player())




                                .then(Commands.literal("augment")
                                        .then(Commands.argument("name", StringArgumentType.word())
                                                .suggests((ctx, builder) -> suggestAugments(builder))
                                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                                        .executes(ctx -> {

                                                            var target = EntityArgument.getPlayer(ctx, "player");

                                                            String augmentName = StringArgumentType.getString(ctx, "name").toUpperCase();
                                                            int amount = IntegerArgumentType.getInteger(ctx, "amount");

                                                            FishingAugment augment;

                                                            try {
                                                                augment = FishingAugment.valueOf(augmentName);
                                                            } catch (Exception e) {
                                                                ctx.getSource().sendFailure(Component.literal("Invalid augment"));
                                                                return 0;
                                                            }

                                                            ItemStack stack = FishingManager.createAugmentItem(augment, target);
                                                            stack.setCount(amount);

                                                            target.getInventory().add(stack);

                                                            target.sendSystemMessage(TextFormatter.parse("<gradient:#00CFFF:#0066FF>&lFISHING</gradient> &7&l➤ &7You have been given &e" + amount + "x " + augmentName + " &7by &c" + ctx.getSource()));


                                                            return 1;
                                                        })
                                                )
                                        )
                                )




                                .then(Commands.literal("crabscale")
                                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                                .executes(ctx -> {

                                                    var target = EntityArgument.getPlayer(ctx, "player");
                                                    int amount = IntegerArgumentType.getInteger(ctx, "amount");

                                                    ItemStack stack = FishingManager.createCustomItem("crab_scale");
                                                    stack.setCount(amount);

                                                    target.getInventory().add(stack);

                                                    return 1;
                                                })
                                        )
                                )

                                .then(Commands.literal("crabclaw")
                                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                                .executes(ctx -> {

                                                    var target = EntityArgument.getPlayer(ctx, "player");
                                                    int amount = IntegerArgumentType.getInteger(ctx, "amount");

                                                    ItemStack stack = FishingManager.createCustomItem("crab_claw");
                                                    stack.setCount(amount);

                                                    target.getInventory().add(stack);

                                                    return 1;
                                                })
                                        )
                                )

                                .then(Commands.literal("dolphintreasure")
                                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                                .executes(ctx -> {

                                                    var target = EntityArgument.getPlayer(ctx, "player");
                                                    int amount = IntegerArgumentType.getInteger(ctx, "amount");

                                                    ItemStack stack = FishingManager.createCustomItem("dolphin_treasure");
                                                    stack.setCount(amount);

                                                    target.getInventory().add(stack);

                                                    return 1;
                                                })
                                        )
                                )

                                .then(Commands.literal("squidtentacle")
                                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                                .executes(ctx -> {

                                                    var target = EntityArgument.getPlayer(ctx, "player");
                                                    int amount = IntegerArgumentType.getInteger(ctx, "amount");

                                                    ItemStack stack = FishingManager.createCustomItem("squid_tentacle");
                                                    stack.setCount(amount);

                                                    target.getInventory().add(stack);

                                                    return 1;
                                                })
                                        )
                                )

                        )
                )

                .then(Commands.literal("reset")
                        .requires(src -> hasPerm(src, "pixity.fishing.admin"))
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(ctx -> {

                                    var target = EntityArgument.getPlayer(ctx, "player");

                                    try {
                                        var ps = FishingDatabase.getConnection().prepareStatement(
                                                "DELETE FROM fishing_stats WHERE uuid = ?"
                                        );
                                        ps.setString(1, target.getUUID().toString());
                                        ps.executeUpdate();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    ctx.getSource().sendSuccess(() ->
                                            Component.literal("<gradient:#00CFFF:#0066FF>&lFISHING</gradient> &7&l➤ &e" + target + " &ahas been reset."), true);

                                    return 1;
                                })
                        )
                )

                .then(Commands.literal("resetall")
                        .requires(src -> hasPerm(src, "pixity.fishing.owner"))
                        .executes(ctx -> {

                            try {
                                var ps = FishingDatabase.getConnection().prepareStatement(
                                        "DELETE FROM fishing_stats"
                                );
                                ps.executeUpdate();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            ctx.getSource().sendSuccess(() ->
                                    Component.literal("<gradient:#00CFFF:#0066FF>&lFISHING</gradient> &7&l➤ &aAll fishing data wiped"), true);

                            return 1;
                        })
                )

                .then(Commands.literal("forcestartevent")
                        .requires(src -> hasPerm(src, "pixity.fishing.admin"))
                        .then(Commands.argument("type", StringArgumentType.word())
                                .suggests((ctx, builder) -> suggestEvents(builder))
                                .executes(ctx -> {

                                    String type = StringArgumentType.getString(ctx, "type").toLowerCase();

                                    FishingEventType eventType;

                                    if (type.equals("random")) {
                                        eventType = FishingEventType.random();
                                    } else {
                                        try {
                                            eventType = FishingEventType.valueOf(type.toUpperCase());
                                        } catch (Exception e) {
                                            ctx.getSource().sendFailure(Component.literal("Invalid event type"));
                                            return 0;
                                        }
                                    }

                                    FishingEventManager.start(eventType);

                                    ctx.getSource().sendSuccess(() ->
                                                    TextFormatter.parse("<gradient:#00CFFF:#0066FF>&lFISHING</gradient> &7&l➤ &aStarted event: &e" + eventType.name()),
                                            true);

                                    return 1;
                                })
                        )
                )

                .then(Commands.literal("forceendevent")
                        .requires(src -> hasPerm(src, "pixity.fishing.admin"))
                        .executes(ctx -> {

                            FishingEventManager.end();

                            ctx.getSource().sendSuccess(() ->
                                            TextFormatter.parse("<gradient:#00CFFF:#0066FF>&lFISHING</gradient> &7&l➤ &cEvent force ended"),
                                    true);

                            return 1;
                        })
                )

                .then(Commands.literal("eventsetcooldown")
                        .requires(src -> hasPerm(src, "pixity.fishing.admin"))
                        .then(Commands.argument("time", StringArgumentType.word())
                                .executes(ctx -> {

                                    String input = StringArgumentType.getString(ctx, "time");

                                    long millis = parseTime(input);

                                    if (millis <= 0) {
                                        ctx.getSource().sendFailure(Component.literal("Invalid time format"));
                                        return 0;
                                    }

                                    FishingEventManager.setCooldown(millis);

                                    ctx.getSource().sendSuccess(() ->
                                                    TextFormatter.parse("<gradient:#00CFFF:#0066FF>&lFISHING</gradient> &7&l➤ &aCooldown set to &e" + input),
                                            true);

                                    return 1;
                                })
                        )
                )

                .then(Commands.literal("setentropy")
                        .requires(src -> hasPerm(src, "pixity.fishing.admin"))
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                                        .executes(ctx -> {

                                            ServerPlayer target =
                                                    EntityArgument.getPlayer(ctx, "player");

                                            int amount =
                                                    IntegerArgumentType.getInteger(ctx, "amount");

                                            UUID uuid = target.getUUID();

                                            try {

                                                var ps = FishingDatabase.getConnection()
                                                        .prepareStatement(
                                                                "UPDATE fishing_stats SET entropy = ? WHERE uuid = ?"
                                                        );

                                                ps.setInt(1, amount);
                                                ps.setString(2, uuid.toString());

                                                ps.executeUpdate();

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                            ctx.getSource().sendSuccess(() ->
                                                            TextFormatter.parse(
                                                                    PREFIX +
                                                                            "&aSet &e" +
                                                                            target.getName().getString() +
                                                                            "&a's entropy to &b" +
                                                                            amount
                                                            ),
                                                    true
                                            );

                                            return 1;
                                        })
                                )
                        )
                )

                .then(Commands.literal("removeskillpoints")
                        .requires(src -> hasPerm(src, "pixity.fishing.admin"))
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(ctx -> {

                                            ServerPlayer target =
                                                    EntityArgument.getPlayer(ctx, "player");

                                            int amount =
                                                    IntegerArgumentType.getInteger(ctx, "amount");

                                            UUID uuid = target.getUUID();

                                            int current =
                                                    FishingDatabase.getSkillPoints(uuid);

                                            int newAmount = Math.max(0, current - amount);

                                            try {

                                                var ps = FishingDatabase.getConnection()
                                                        .prepareStatement(
                                                                "UPDATE fishing_stats SET skill_points = ? WHERE uuid = ?"
                                                        );

                                                ps.setInt(1, newAmount);
                                                ps.setString(2, uuid.toString());

                                                ps.executeUpdate();

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                            ctx.getSource().sendSuccess(() ->
                                                            TextFormatter.parse(
                                                                    PREFIX +
                                                                            "&cRemoved &e" +
                                                                            amount +
                                                                            " &cskill points from &e" +
                                                                            target.getName().getString()
                                                            ),
                                                    true
                                            );

                                            return 1;
                                        })
                                )
                        )
                )

                .then(Commands.literal("resetskills")
                        .requires(src -> hasPerm(src, "pixity.fishing.admin"))
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(ctx -> {

                                    ServerPlayer target =
                                            EntityArgument.getPlayer(ctx, "player");

                                    UUID uuid = target.getUUID();

                                    try {

                                        var ps = FishingDatabase.getConnection()
                                                .prepareStatement(
                                                        """
                                                        UPDATE fishing_stats SET
                                                        gutting_skill = 0,
                                                        luck_skill = 0,
                                                        augment_skill = 0,
                                                        divine_unlocked = 0,
                                                        combo_unlocked = 0,
                                                        infusion_unlocked = 0,
                                                        skill_points = 0
                                                        WHERE uuid = ?
                                                        """
                                                );

                                        ps.setString(1, uuid.toString());

                                        ps.executeUpdate();

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    ctx.getSource().sendSuccess(() ->
                                                    TextFormatter.parse(
                                                            PREFIX +
                                                                    "&cReset all fishing skills for &e" +
                                                                    target.getName().getString()
                                                    ),
                                            true
                                    );

                                    return 1;
                                })
                        )
                )

                .then(Commands.literal("setskill")
                        .requires(src -> hasPerm(src, "pixity.fishing.admin"))
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("skill", StringArgumentType.word())
                                        .suggests((ctx, builder) -> {

                                            builder.suggest("gutting");
                                            builder.suggest("luck");
                                            builder.suggest("augment");

                                            return builder.buildFuture();
                                        })
                                        .then(Commands.argument("level", IntegerArgumentType.integer(0, 100))
                                                .executes(ctx -> {

                                                    ServerPlayer target =
                                                            EntityArgument.getPlayer(ctx, "player");

                                                    String skill =
                                                            StringArgumentType.getString(ctx, "skill");

                                                    int level =
                                                            IntegerArgumentType.getInteger(ctx, "level");

                                                    String column = switch (skill.toLowerCase()) {

                                                        case "gutting" -> "gutting_skill";
                                                        case "luck" -> "luck_skill";
                                                        case "augment" -> "augment_skill";

                                                        default -> null;
                                                    };

                                                    if (column == null) {

                                                        ctx.getSource().sendFailure(
                                                                TextFormatter.parse(
                                                                        PREFIX + "&cInvalid skill."
                                                                )
                                                        );

                                                        return 0;
                                                    }

                                                    try {

                                                        var ps = FishingDatabase.getConnection()
                                                                .prepareStatement(
                                                                        "UPDATE fishing_stats SET " +
                                                                                column +
                                                                                " = ? WHERE uuid = ?"
                                                                );

                                                        ps.setInt(1, level);
                                                        ps.setString(2, target.getUUID().toString());

                                                        ps.executeUpdate();

                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }

                                                    ctx.getSource().sendSuccess(() ->
                                                                    TextFormatter.parse(
                                                                            PREFIX +
                                                                                    "&aSet &e" +
                                                                                    skill +
                                                                                    " &askill to &e" +
                                                                                    level +
                                                                                    " &afor &e" +
                                                                                    target.getName().getString()
                                                                    ),
                                                            true
                                                    );

                                                    return 1;
                                                })
                                        )
                                )
                        )
                )

        );
    }

    private static CompletableFuture<Suggestions> suggestAugments(SuggestionsBuilder builder) {

        for (FishingAugment aug : FishingAugment.values()) {
            builder.suggest(aug.name().toLowerCase());
        }


        return builder.buildFuture();
    }

    private static CompletableFuture<Suggestions> suggestEvents(SuggestionsBuilder builder) {

        builder.suggest("random");
        builder.suggest("longest");
        builder.suggest("shortest");
        builder.suggest("biomes");
        builder.suggest("bronze");
        builder.suggest("silver");
        builder.suggest("gold");
        builder.suggest("squid");
        builder.suggest("dolphin");

        return builder.buildFuture();
    }

    private static boolean hasPerm(CommandSourceStack src, String perm) {

        try {
            ServerPlayer player = src.getPlayerOrException();
            return RANKS.hasPerm(player, perm);
        } catch (Exception e) {
            return true;
        }
    }

    private static long parseTime(String input) {

        try {
            input = input.toLowerCase();

            if (input.endsWith("s")) {
                return Long.parseLong(input.replace("s", "")) * 1000L;
            }
            if (input.endsWith("m")) {
                return Long.parseLong(input.replace("m", "")) * 60_000L;
            }
            if (input.endsWith("h")) {
                return Long.parseLong(input.replace("h", "")) * 3_600_000L;
            }

            return Long.parseLong(input);

        } catch (Exception e) {
            return -1;
        }
    }

    private static String formatEvent(FishingEventType type) {
        return switch (type) {
            case LONGEST -> "Catch the longest fish";
            case SHORTEST -> "Catch the shortest fish";
            case BIOMES -> "Fish in the most biomes";
            case BRONZE -> "Catch the most bronze fish";
            case SILVER -> "Catch the most silver fish";
            case GOLD -> "Catch the most gold fish";
            case SQUID -> "Kill the most squid";
            case DOLPHIN -> "Kill the most dolphins";
        };
    }
}