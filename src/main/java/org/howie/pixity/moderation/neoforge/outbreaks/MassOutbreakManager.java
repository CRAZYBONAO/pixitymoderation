package org.howie.pixity.moderation.neoforge.outbreaks;

import net.minecraft.server.MinecraftServer;

import net.minecraft.server.level.ServerLevel;
import org.howie.pixity.moderation.chat.TextFormatter;

import java.util.Random;

public class MassOutbreakManager {





    private static MassOutbreakDefinition current;





    private static long endTime = 0L;

    private static long durationMillis = 0L;





    private static long nextOutbreak =
            System.currentTimeMillis()
                    + (1000L * 60L * 60L * 2L);






    private static final String[] BIOMES = {

            "Plains",
            "Forest",
            "Taiga",
            "Badlands",
            "Desert",
            "Jungle",
            "Swamp",
            "Ocean"
    };





    public static void generate(
            MinecraftServer server
    ) {

        Random r =
                new Random();

        OutbreakTier tier =
                OutbreakTier.random();

        OutbreakPool pool =
                OutbreakPools.random();

        current =
                new MassOutbreakDefinition(

                        pool.randomSpecies(),

                        BIOMES[
                                r.nextInt(
                                        BIOMES.length
                                )
                                ],

                        tier,

                        pool,

                        20
                );

        durationMillis =
                current.durationMinutes
                        * 60L
                        * 1000L;

        endTime =
                System.currentTimeMillis()
                        + durationMillis;

        nextOutbreak =
                System.currentTimeMillis()
                        + (1000L * 60L * 60L * 2L);

        MassOutbreakBossBar.remove();

        MassOutbreakBossBar.create(server);





        server.getPlayerList()
                .broadcastSystemMessage(

                        TextFormatter.parse(
                                current.tier.formatted
                                        + "\n\n"

                                        + "<gold>"
                                        + current.getDisplayName()
                                        + "</gold>"

                                        + "<gray> [</gray>"

                                        + "<aqua>"
                                        + current.pool.display
                                        + "</aqua>"

                                        + "<gray>]</gray>"

                                        + "<gray> are appearing in the </gray>"

                                        + "<yellow>"
                                        + current.biomeName
                                        + "</yellow>\n\n"

                                        + "<aqua>✨ Shiny Odds Boosted "
                                        + current.tier.shinyOdds
                                        + "x</aqua>\n"

                                        + "<green>🔥 Increased Spawn Rates</green>\n"

                                        + "<light_purple>🧬 Hidden Ability Odds: 1/"
                                        + current.tier.hiddenAbilityOdds
                                        + "</light_purple>\n"

                                        + (
                                        current.tier.alphaChance > 0

                                                ? "<gold>⭐ Alpha Chance: "
                                                + current.tier.alphaChance
                                                + "%</gold>\n\n"

                                                : "\n"
                                )

                                        + "<gray>Time Remaining: </gray>"

                                        + "<yellow>"
                                        + current.durationMinutes
                                        + " Minutes</yellow>"
                        ),
                        false
                );
    }





    public static void end(
            MinecraftServer server
    ) {

        if (current == null) {
            return;
        }

        server.getPlayerList()
                .broadcastSystemMessage(

                        TextFormatter.parse(
                                "<red><bold>Mass Outbreak Ended!</bold></red>\n\n"

                                        + "<gold>"
                                        + current.getDisplayName()
                                        + "</gold>"

                                        + "<gray> outbreaks have disappeared.</gray>"
                        ),
                        false
                );





        for (ServerLevel level
                : server.getAllLevels()) {

            MassOutbreakCleanup.cleanup(level);
        }

        current = null;

        MassOutbreakBossBar.remove();
    }





    public static MassOutbreakDefinition getCurrent() {
        return current;
    }

    public static long getEndTime() {
        return endTime;
    }

    public static long getDurationMillis() {
        return durationMillis;
    }

    public static long getNextOutbreak() {
        return nextOutbreak;
    }

    public static boolean isActive() {
        return current != null;
    }
}