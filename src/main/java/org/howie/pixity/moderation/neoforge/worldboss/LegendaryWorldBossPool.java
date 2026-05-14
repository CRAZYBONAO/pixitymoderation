package org.howie.pixity.moderation.neoforge.worldboss;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LegendaryWorldBossPool {





    private static final Random RANDOM =
            new Random();





    private static final List<WorldBossDefinition> LEGENDARIES =
            new ArrayList<>();





    static {

        LEGENDARIES.add(
                new WorldBossDefinition(
                        "mewtwo",
                        "Genetic Overlord Mewtwo",
                        5_000_000,
                        2.0F,
                        2500,
                        500000,
                        45
                )
        );

        LEGENDARIES.add(
                new WorldBossDefinition(
                        "rayquaza",
                        "Sky Emperor Rayquaza",
                        6_000_000,
                        2.0F,
                        3000,
                        750000,
                        45
                )
        );

        LEGENDARIES.add(
                new WorldBossDefinition(
                        "giratina",
                        "Distortion Lord Giratina",
                        7_500_000,
                        2.0F,
                        4000,
                        1000000,
                        60
                )
        );

        LEGENDARIES.add(
                new WorldBossDefinition(
                        "darkrai",
                        "Nightmare King Darkrai",
                        4_500_000,
                        2.0F,
                        2200,
                        450000,
                        45
                )
        );

        LEGENDARIES.add(
                new WorldBossDefinition(
                        "lugia",
                        "Tempest Lugia",
                        5_500_000,
                        2.0F,
                        2750,
                        650000,
                        45
                )
        );

        LEGENDARIES.add(
                new WorldBossDefinition(
                        "dialga",
                        "Chrono Dialga",
                        7_000_000,
                        2.0F,
                        3500,
                        900000,
                        60
                )
        );

        LEGENDARIES.add(
                new WorldBossDefinition(
                        "palkia",
                        "Void Palkia",
                        7_000_000,
                        2.0F,
                        3500,
                        900000,
                        60
                )
        );

        LEGENDARIES.add(
                new WorldBossDefinition(
                        "zacian",
                        "Crowned Zacian",
                        6_500_000,
                        2.0F,
                        3250,
                        850000,
                        45
                )
        );
    }





    public static WorldBossDefinition randomLegendary() {

        return LEGENDARIES.get(

                RANDOM.nextInt(
                        LEGENDARIES.size()
                )
        );
    }

    public static List<WorldBossDefinition> getAll() {

        return LEGENDARIES;
    }
}