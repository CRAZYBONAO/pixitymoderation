package org.howie.pixity.moderation.neoforge.globalhunt;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.chat.TextFormatter;
import org.howie.pixity.moderation.neoforge.rank.RankService;

public class GlobalHuntCommand {





    private static final RankService RANKS =
            new RankService();

    private static final String ADMIN_PERMISSION =
            "pixity.admin";





    public static void register(
            CommandDispatcher<CommandSourceStack> dispatcher
    ) {

        dispatcher.register(

                Commands.literal("globalhunt")





                        .executes(ctx -> {

                            if (
                                    !(ctx.getSource()
                                            .getEntity()
                                            instanceof ServerPlayer player)
                            ) {
                                return 0;
                            }

                            GlobalHuntDefinition hunt =
                                    GlobalHuntManager.getCurrent();

                            if (hunt == null) {

                                player.sendSystemMessage(
                                        TextFormatter.parse(
                                                "<red>No global hunt is active.</red>"
                                        )
                                );

                                return 1;
                            }

                            long remaining =
                                    GlobalHuntManager.getNextReset()
                                            - System.currentTimeMillis();

                            long hours =
                                    remaining / (1000L * 60L * 60L);

                            long minutes =
                                    (remaining / (1000L * 60L))
                                            % 60L;

                            player.sendSystemMessage(

                                    TextFormatter.parse(
                                            "<gradient:#00ffff:#0066ff>&lGLOBAL HUNT</gradient>\n\n"

                                                    + "<yellow>"
                                                    + hunt.getDisplay()
                                                    + "</yellow>\n\n"

                                                    + "&b&LProgress: "
                                                    + "<yellow>"
                                                    + GlobalHuntManager.getProgress()
                                                    + "/"
                                                    + hunt.required
                                                    + "</yellow>\n\n"

                                                    + "<green>&lRewards:</green>\n"

                                                    + "&b"
                                                    + hunt.tokens
                                                    + " Tokens\n"

                                                    + "<green>$"
                                                    + String.format(
                                                    "%,d",
                                                    hunt.money
                                            )
                                                    + "</green>\n\n"

                                                    + "<gray>Resets In: </gray>"
                                                    + "<yellow>"
                                                    + hours
                                                    + "h "
                                                    + minutes
                                                    + "m</yellow>"
                                    )
                            );

                            return 1;
                        })





                        .then(
                                Commands.literal(
                                                "forcegenerate"
                                        )

                                        .executes(ctx -> {

                                            if (
                                                    !(ctx.getSource()
                                                            .getEntity()
                                                            instanceof ServerPlayer player)
                                            ) {
                                                return 0;
                                            }

                                            if (
                                                    !RANKS.hasPerm(
                                                            player,
                                                            ADMIN_PERMISSION
                                                    )
                                            ) {

                                                player.sendSystemMessage(
                                                        TextFormatter.parse(
                                                                "<red>You do not have permission.</red>"
                                                        )
                                                );

                                                return 0;
                                            }

                                            GlobalHuntManager.generate(
                                                    player.server
                                            );

                                            player.sendSystemMessage(
                                                    TextFormatter.parse(
                                                            "<green>Generated a new global hunt.</green>"
                                                    )
                                            );

                                            return 1;
                                        })
                        )
        );
    }
}