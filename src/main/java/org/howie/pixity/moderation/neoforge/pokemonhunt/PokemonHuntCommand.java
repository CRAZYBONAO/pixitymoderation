package org.howie.pixity.moderation.neoforge.pokemonhunt;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.chat.TextFormatter;
import org.howie.pixity.moderation.neoforge.rank.RankService;

public class PokemonHuntCommand {





    private static final RankService RANKS =
            new RankService();

    private static final String ADMIN_PERMISSION =
            "pixity.admin";





    public static void register(
            CommandDispatcher<CommandSourceStack> dispatcher
    ) {

        dispatcher.register(

                Commands.literal("hunt")





                        .executes(ctx -> {

                            if (
                                    !(ctx.getSource()
                                            .getEntity()
                                            instanceof ServerPlayer player)
                            ) {
                                return 0;
                            }

                            PokemonHuntDefinition hunt =
                                    PokemonHuntManager.getCurrent();





                            if (hunt == null) {

                                player.sendSystemMessage(
                                        TextFormatter.parse(
                                                "<gradient:#ff1a1a:#8b0000>&lHUNT</gradient><gray>&l ➤</gray> <red>Error! No Pokémon hunt is currently active.</red>"
                                        )
                                );

                                return 1;
                            }

                            int progress =
                                    PokemonHuntDatabase.getProgress(
                                            player.getUUID()
                                    );

                            long remaining =
                                    PokemonHuntManager.getNextReset()
                                            - System.currentTimeMillis();

                            long hours =
                                    remaining / (1000L * 60L * 60L);

                            long minutes =
                                    (remaining / (1000L * 60L))
                                            % 60L;





                            player.sendSystemMessage(

                                    TextFormatter.parse(
                                            "<gold>🎯 <rainbow>DAILY POKÉMON HUNT</rainbow> 🎯</gold>"

                                                    + "<yellow>"
                                                    + hunt.getDisplay()
                                                    + "</yellow>\n\n"

                                                    + "<aqua><bold>Progress:</bold></aqua> "
                                                    + "<yellow>"
                                                    + progress
                                                    + "/"
                                                    + hunt.required
                                                    + "</yellow>\n\n"

                                                    + "<green><bold>Rewards:</bold></green>\n"

                                                    + "<aqua>"
                                                    + hunt.tokens
                                                    + " Tokens</aqua>\n"

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
                                                                "<gradient:#ff1a1a:#8b0000>&lHUNT</gradient><gray>&l ➤</gray> <red>Error! You do not have permission.</red>"
                                                        )
                                                );

                                                return 0;
                                            }

                                            PokemonHuntManager.generateNewHunt(
                                                    player.server
                                            );

                                            player.sendSystemMessage(
                                                    TextFormatter.parse(
                                                            "<gradient:#ff1a1a:#8b0000>&lHUNT</gradient><gray>&l ➤</gray> <green>Generated a new Pokémon hunt.</green>"
                                                    )
                                            );

                                            return 1;
                                        })
                        )





                        .then(
                                Commands.literal(
                                                "forcecomplete"
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
                                                                "<gradient:#ff1a1a:#8b0000>&lHUNT</gradient><gray>&l ➤</gray> <red>Error! You do not have permission.</red>"
                                                        )
                                                );

                                                return 0;
                                            }

                                            PokemonHuntDefinition hunt =
                                                    PokemonHuntManager.getCurrent();

                                            if (hunt == null) {

                                                player.sendSystemMessage(
                                                        TextFormatter.parse(
                                                                "<gradient:#ff1a1a:#8b0000>&lHUNT</gradient><gray>&l ➤</gray> <red>No active hunt.</red>"
                                                        )
                                                );

                                                return 0;
                                            }

                                            int current =
                                                    PokemonHuntDatabase.getProgress(
                                                            player.getUUID()
                                                    );

                                            int needed =
                                                    hunt.required
                                                            - current;

                                            if (needed <= 0) {

                                                player.sendSystemMessage(
                                                        TextFormatter.parse(
                                                                "<gradient:#ff1a1a:#8b0000>&lHUNT</gradient><gray>&l ➤ </gray> <red>Error! You already completed this hunt.</red>"
                                                        )
                                                );

                                                return 0;
                                            }

                                            for (int i = 0; i < needed; i++) {

                                                PokemonHuntManager.handleProgress(
                                                        player
                                                );
                                            }

                                            player.sendSystemMessage(
                                                    TextFormatter.parse(
                                                            "<gradient:#ff1a1a:#8b0000>&lHUNT</gradient><gray>&l ➤ </gray> <green>Force completed your hunt.</green>"
                                                    )
                                            );

                                            return 1;
                                        })
                        )
        );
    }
}