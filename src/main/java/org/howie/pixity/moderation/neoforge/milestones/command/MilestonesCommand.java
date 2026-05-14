package org.howie.pixity.moderation.neoforge.milestones.command;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.neoforge.milestones.gui.MilestoneCategoriesMenu;

public class MilestonesCommand {

    public static void register(
            CommandDispatcher<CommandSourceStack> dispatcher
    ) {

        dispatcher.register(

                Commands.literal("milestones")

                        .requires(source ->
                                source.getEntity() instanceof ServerPlayer
                        )

                        .executes(ctx -> {

                            ServerPlayer player =
                                    ctx.getSource().getPlayerOrException();

                            MilestoneCategoriesMenu.open(player);

                            return 1;
                        })
        );
    }
}