package org.howie.pixity.moderation.neoforge.shop;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import net.minecraft.server.level.ServerPlayer;

public class GlobalBoostCommand {

    public static void register(CommandDispatcher<CommandSourceStack> d) {

        d.register(
                Commands.literal("globalboost")
                        .requires(src -> src.hasPermission(4))
                        .then(Commands.argument("multiplier", DoubleArgumentType.doubleArg(1.0, 10.0))
                                .then(Commands.argument("minutes", IntegerArgumentType.integer(1, 1440))
                                        .executes(ctx -> {

                                            double mult = DoubleArgumentType.getDouble(ctx, "multiplier");
                                            int minutes = IntegerArgumentType.getInteger(ctx, "minutes");

                                            long duration = minutes * 60L * 1000L;

                                            ServerPlayer player = ctx.getSource().getPlayer();


                                            GlobalBoostService.activate(mult, duration, player.getUUID());

                                            GlobalBoostService.announce(player, mult, minutes);

                                            return Command.SINGLE_SUCCESS;
                                        })))
        );
    }
}