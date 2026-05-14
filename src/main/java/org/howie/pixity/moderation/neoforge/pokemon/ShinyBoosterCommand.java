package org.howie.pixity.moderation.neoforge.pokemon;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.chat.CachedText;
import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.chat.TextFormatter;

public class ShinyBoosterCommand {

    private static final RankService RANK = new RankService();

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(
                Commands.literal("shinybooster")
                        .requires(src -> has(src, "pixity.shinybooster"))




                        .then(Commands.literal("give")
                                .requires(src -> has(src, "pixity.shinybooster.give"))

                                .then(Commands.argument("target", EntityArgument.player())
                                        .then(Commands.argument("multiplier", DoubleArgumentType.doubleArg(1.0))
                                                .then(Commands.argument("time", IntegerArgumentType.integer(1))
                                                        .executes(ctx -> {

                                                            ServerPlayer target = EntityArgument.getPlayer(ctx, "target");

                                                            double multi = DoubleArgumentType.getDouble(ctx, "multiplier");
                                                            int time = IntegerArgumentType.getInteger(ctx, "time");

                                                            ShinyBoostManager.enablePlayer(target, multi, time);

                                                            return 1;
                                                        })
                                                )
                                        )
                                )
                        )




                        .then(Commands.literal("check")
                                .executes(ctx -> {

                                    ServerPlayer player = ctx.getSource().getPlayerOrException();

                                    sendStatus(player, player);
                                    return 1;
                                })

                                .then(Commands.argument("target", EntityArgument.player())
                                        .requires(src -> has(src, "pixity.shinybooster.check.others"))
                                        .executes(ctx -> {

                                            ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
                                            sendStatus(ctx.getSource().getPlayerOrException(), target);

                                            return 1;
                                        })
                                )
                        )




                        .then(Commands.literal("clear")
                                .executes(ctx -> {

                                    ServerPlayer player = ctx.getSource().getPlayerOrException();

                                    if (!has(ctx.getSource(), "pixity.shinybooster.clear.self")) {
                                        player.sendSystemMessage(CachedText.of("<red>No permission.</red>"));
                                        return 0;
                                    }

                                    ShinyBoostManager.clearPlayer(player);
                                    return 1;
                                })

                                .then(Commands.argument("target", EntityArgument.player())
                                        .requires(src -> has(src, "pixity.shinybooster.clear.others"))
                                        .executes(ctx -> {

                                            ServerPlayer target = EntityArgument.getPlayer(ctx, "target");

                                            ShinyBoostManager.clearPlayer(target);

                                            return 1;
                                        })
                                )
                        )
        );
    }




    private static void sendStatus(ServerPlayer viewer, ServerPlayer target) {

        double multi = ShinyBoostManager.getPlayerMultiplier(target);
        long remaining = ShinyBoostManager.getRemaining(target) / 1000;

        if (multi <= 1.0) {
            viewer.sendSystemMessage(CachedText.of("<gray>No active shiny boost.</gray>"));
            return;
        }

        viewer.sendSystemMessage(CachedText.of(
                "<rainbow>&l✨ SHINY BOOST ✨</rainbow> <light_purple>"
                        + target.getName().getString()
                        + ": " + multi + "x (" + remaining + "s remaining)"
        ));
    }

    private static boolean has(CommandSourceStack src, String node) {
        try {
            return RANK.hasPerm(src.getPlayerOrException(), node);
        } catch (Exception e) {
            return false;
        }
    }
}