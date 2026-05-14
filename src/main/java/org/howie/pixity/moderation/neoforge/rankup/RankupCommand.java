package org.howie.pixity.moderation.neoforge.rankup;

import com.mojang.brigadier.Command;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.shop.EconomyBridge;

public class RankupCommand {

    public static void register(com.mojang.brigadier.CommandDispatcher<CommandSourceStack> dispatcher,
                                RankService rankService,
                                EconomyBridge econ) {

        dispatcher.register(
                Commands.literal("rankup")
                        .executes(ctx -> {

                            ServerPlayer player = ctx.getSource().getPlayer();

                            RankupService.rankup(player, rankService, econ);

                            return Command.SINGLE_SUCCESS;
                        })
        );
    }
}