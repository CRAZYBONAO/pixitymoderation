package org.howie.pixity.moderation.neoforge.pokemonhunt;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.chat.TextFormatter;

import java.util.Random;
import java.util.UUID;

public class PokemonHuntManager {





    private static PokemonHuntDefinition current;

    private static long nextReset =
            System.currentTimeMillis()
                    + (1000L * 60L * 60L * 24L);





    private static final String[] SPECIES = {

            "pikachu",
            "eevee",
            "riolu",
            "gastly",
            "dratini",
            "magikarp",
            "ralts",
            "zorua",
            "shinx",
            "growlithe",
            "vulpix",
            "abra"
    };





    public static void generateNewHunt(
            MinecraftServer server
    ) {

        Random r =
                new Random();

        int typeRoll =
                r.nextInt(4);

        current = switch (typeRoll) {

            case 0 -> new PokemonHuntDefinition(
                    PokemonHuntType.SPECIES,
                    SPECIES[r.nextInt(SPECIES.length)],
                    5,
                    200,
                    10000
            );

            case 1 -> new PokemonHuntDefinition(
                    PokemonHuntType.TYPE,
                    "fire",
                    10,
                    250,
                    15000
            );

            case 2 -> new PokemonHuntDefinition(
                    PokemonHuntType.SHINY,
                    "shiny",
                    1,
                    1000,
                    50000
            );

            default -> new PokemonHuntDefinition(
                    PokemonHuntType.HIDDEN_ABILITY,
                    "ha",
                    3,
                    750,
                    35000
            );
        };

        nextReset =
                System.currentTimeMillis()
                        + (1000L * 60L * 60L * 24L);

        PokemonHuntDatabase.reset();
        PokemonHuntBossBarManager.resetAll(
                server
        );





        server.getPlayerList()
                .broadcastSystemMessage(

                        TextFormatter.parse(
                                "<gold>🎯 <rainbow>NEW POKÉMON HUNT</rainbow> 🎯</gold>\n\n"

                                        + "<yellow>"
                                        + current.getDisplay()
                                        + "</yellow>\n\n"

                                        + "<green><bold>Rewards:</bold></green>\n"

                                        + "<aqua>"
                                        + current.tokens
                                        + " Tokens</aqua>\n"

                                        + "<green>$"
                                        + String.format(
                                        "%,d",
                                        current.money
                                )
                                        + "</green>"
                        ),
                        false
                );
    }





    public static void handleProgress(
            ServerPlayer player
    ) {

        if (current == null) {
            return;
        }

        UUID uuid =
                player.getUUID();

        int currentProgress =
                PokemonHuntDatabase.getProgress(uuid);

        if (currentProgress >= current.required) {
            return;
        }

        PokemonHuntDatabase.addProgress(
                uuid,
                1
        );

        int newProgress =
                PokemonHuntDatabase.getProgress(uuid);
        PokemonHuntBossBarManager.update(
                player
        );

        player.sendSystemMessage(
                TextFormatter.parse(
                        "<gold>🎯 Hunt Progress:</gold> "
                                + "<yellow>"
                                + newProgress
                                + "/"
                                + current.required
                                + "</yellow>"
                )
        );





        if (newProgress >= current.required) {

            player.sendSystemMessage(
                    TextFormatter.parse(
                            "<gold>🎉 HUNT COMPLETE!</gold>\n"

                                    + "<aqua>"
                                    + current.tokens
                                    + " Tokens</aqua>\n"

                                    + "<green>$"
                                    + String.format(
                                    "%,d",
                                    current.money
                            )
                                    + "</green>"
                    )
            );

            reward(player);
        }
    }





    private static void reward(
            ServerPlayer player
    ) {

        player.server.getCommands()
                .performPrefixedCommand(
                        player.server.createCommandSourceStack(),
                        "eco give "
                                + player.getGameProfile().getName()
                                + " "
                                + current.money
                );
    }





    public static PokemonHuntDefinition getCurrent() {
        return current;
    }

    public static long getNextReset() {
        return nextReset;
    }
}