package org.howie.pixity.moderation.neoforge.shop;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;

import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;

public class SellWandCommand {

    public static void register(CommandDispatcher<CommandSourceStack> d) {

        d.register(Commands.literal("sellwand")

                .requires(src -> src.hasPermission(2))

                .then(Commands.argument("player", EntityArgument.player())

                        .then(Commands.argument("value", StringArgumentType.word())

                                .then(Commands.argument("multiplier", DoubleArgumentType.doubleArg(0.1))

                                        .executes(ctx -> {

                                            ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
                                            String input = StringArgumentType.getString(ctx, "value");
                                            double multiplier = DoubleArgumentType.getDouble(ctx, "multiplier");

                                            int usesFinal;
                                            String tierFinal;

                                            try {
                                                usesFinal = Integer.parseInt(input);
                                                tierFinal = "custom";

                                            } catch (NumberFormatException e) {

                                                int tierUses = SellWandService.getTierUses(input);

                                                if (tierUses == -1) {
                                                    ctx.getSource().sendFailure(Component.literal(
                                                            "§cInvalid tier. Available: " + SellWandService.getTiers()
                                                    ));
                                                    return 0;
                                                }

                                                usesFinal = tierUses;
                                                tierFinal = input;
                                            }

                                            final int uses = usesFinal;
                                            final String tier = tierFinal;
                                            final double mult = multiplier;

                                            var wand = SellWandService.create(uses, tier, mult);

                                            target.getInventory().add(wand);

                                            ctx.getSource().sendSuccess(() ->
                                                            Component.literal("§aGave sell wand (" + uses + " uses, " + mult + "x) to " + target.getName().getString()),
                                                    true
                                            );

                                            return 1;
                                        })
                                )
                        )
                )
        );
    }
}