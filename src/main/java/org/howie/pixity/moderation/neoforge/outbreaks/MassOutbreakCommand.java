package org.howie.pixity.moderation.neoforge.outbreaks;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.chat.TextFormatter;
import org.howie.pixity.moderation.neoforge.rank.RankService;

public class MassOutbreakCommand {





    private static final RankService RANKS =
            new RankService();

    private static final String ADMIN_PERMISSION =
            "pixity.admin";





    public static void register(
            CommandDispatcher<CommandSourceStack> dispatcher
    ) {

        dispatcher.register(

                Commands.literal("outbreak")





                        .executes(ctx -> {

                            if (
                                    !(ctx.getSource()
                                            .getEntity()
                                            instanceof ServerPlayer player)
                            ) {
                                return 0;
                            }

                            MassOutbreakDefinition outbreak =
                                    MassOutbreakManager.getCurrent();

                            if (outbreak == null) {

                                long remaining =
                                        MassOutbreakManager.getNextOutbreak()
                                                - System.currentTimeMillis();

                                long hours =
                                        remaining / (1000L * 60L * 60L);

                                long minutes =
                                        (remaining / (1000L * 60L))
                                                % 60L;

                                player.sendSystemMessage(
                                        TextFormatter.parse(
                                                "<red>No outbreak is currently active.</red>\n\n"

                                                        + "<gray>Next outbreak in: </gray>"

                                                        + "<yellow>"
                                                        + hours
                                                        + "h "
                                                        + minutes
                                                        + "m</yellow>"
                                        )
                                );

                                return 1;
                            }

                            long remaining =
                                    MassOutbreakManager.getEndTime()
                                            - System.currentTimeMillis();

                            long minutes =
                                    remaining / (1000L * 60L);

                            player.sendSystemMessage(

                                    TextFormatter.parse(
                                            outbreak.tier.formatted
                                                    + "\n\n"

                                                    + "<gold>"
                                                    + outbreak.getDisplayName()
                                                    + "</gold>"

                                                    + "<gray> [</gray>"

                                                    + "<aqua>"
                                                    + outbreak.pool.display
                                                    + "</aqua>"

                                                    + "<gray>]</gray>"

                                                    + "<gray> are spawning in the </gray>"

                                                    + "<yellow>"
                                                    + outbreak.biomeName
                                                    + "</yellow>\n\n"

                                                    + "<aqua>✨ Shiny Odds: 1/"
                                                    + outbreak.tier.shinyOdds
                                                    + "</aqua>\n"

                                                    + "<green>🔥 Increased Spawn Rates</green>\n"

                                                    + "<light_purple>🧬 Hidden Ability Odds: 1/"
                                                    + outbreak.tier.hiddenAbilityOdds
                                                    + "</light_purple>\n"

                                                    + (
                                                    outbreak.tier.alphaChance > 0

                                                            ? "<gold>⭐ Alpha Chance: "
                                                            + outbreak.tier.alphaChance
                                                            + "%</gold>\n\n"

                                                            : "\n"
                                            )

                                                    + "<gray>Time Remaining: </gray>"

                                                    + "<yellow>"
                                                    + minutes
                                                    + " Minutes</yellow>"
                                    )
                            );

                            return 1;
                        })





                        .then(
                                Commands.literal(
                                                "force"
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

                                            MassOutbreakManager.generate(
                                                    player.server
                                            );

                                            player.sendSystemMessage(
                                                    TextFormatter.parse(
                                                            "<green>Forced a mass outbreak.</green>"
                                                    )
                                            );

                                            return 1;
                                        })
                        )
        );
    }
}