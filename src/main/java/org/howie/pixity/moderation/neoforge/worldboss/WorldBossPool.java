package org.howie.pixity.moderation.neoforge.worldboss;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorldBossPool {





    private static final Random RANDOM =
            new Random();





    private static final List<WorldBossDefinition> SINGLE_TYPE =
            new ArrayList<>();





    private static final List<WorldBossDefinition> DUAL_TYPE =
            new ArrayList<>();





    static {





        SINGLE_TYPE.add(
                new WorldBossDefinition(
                        "arcanine",
                        "Infernal Arcanine",
                        1_000_000,
                        3.5F,
                        250,
                        50000,
                        30
                )
        );

        SINGLE_TYPE.add(
                new WorldBossDefinition(
                        "milotic",
                        "Tidal Milotic",
                        1_000_000,
                        3.5F,
                        250,
                        50000,
                        30
                )
        );

        SINGLE_TYPE.add(
                new WorldBossDefinition(
                        "serperior",
                        "Nature Serperior",
                        1_000_000,
                        3.5F,
                        250,
                        50000,
                        30
                )
        );

        SINGLE_TYPE.add(
                new WorldBossDefinition(
                        "ampharos",
                        "Storm Ampharos",
                        1_000_000,
                        3.5F,
                        250,
                        50000,
                        30
                )
        );

        SINGLE_TYPE.add(
                new WorldBossDefinition(
                        "alakazam",
                        "Mindbreaker Alakazam",
                        1_000_000,
                        3.5F,
                        250,
                        50000,
                        30
                )
        );

        SINGLE_TYPE.add(
                new WorldBossDefinition(
                        "machamp",
                        "Titan Machamp",
                        1_000_000,
                        3.5F,
                        250,
                        50000,
                        30
                )
        );

        SINGLE_TYPE.add(
                new WorldBossDefinition(
                        "gengar",
                        "Nightmare Gengar",
                        1_000_000,
                        3.5F,
                        250,
                        50000,
                        30
                )
        );

        SINGLE_TYPE.add(
                new WorldBossDefinition(
                        "glaceon",
                        "Frozen Glaceon",
                        1_000_000,
                        3.5F,
                        250,
                        50000,
                        30
                )
        );

        SINGLE_TYPE.add(
                new WorldBossDefinition(
                        "dragonite",
                        "Skybreaker Dragonite",
                        1_200_000,
                        4.0F,
                        350,
                        75000,
                        30
                )
        );

        SINGLE_TYPE.add(
                new WorldBossDefinition(
                        "sylveon",
                        "Celestial Sylveon",
                        1_000_000,
                        3.5F,
                        250,
                        50000,
                        30
                )
        );

        SINGLE_TYPE.add(
                new WorldBossDefinition(
                        "muk",
                        "Toxic Muk",
                        1_000_000,
                        3.5F,
                        250,
                        50000,
                        30
                )
        );

        SINGLE_TYPE.add(
                new WorldBossDefinition(
                        "donphan",
                        "Earthshaker Donphan",
                        1_000_000,
                        3.5F,
                        250,
                        50000,
                        30
                )
        );

        SINGLE_TYPE.add(
                new WorldBossDefinition(
                        "noctowl",
                        "Shadow Noctowl",
                        1_000_000,
                        3.5F,
                        250,
                        50000,
                        30
                )
        );

        SINGLE_TYPE.add(
                new WorldBossDefinition(
                        "aggron",
                        "Ironclad Aggron",
                        1_200_000,
                        4.0F,
                        350,
                        75000,
                        30
                )
        );

        SINGLE_TYPE.add(
                new WorldBossDefinition(
                        "umbreon",
                        "Eclipse Umbreon",
                        1_000_000,
                        3.5F,
                        250,
                        50000,
                        30
                )
        );

        SINGLE_TYPE.add(
                new WorldBossDefinition(
                        "snorlax",
                        "Colossal Snorlax",
                        1_500_000,
                        4.5F,
                        500,
                        100000,
                        30
                )
        );





        DUAL_TYPE.add(
                new WorldBossDefinition(
                        "charizard",
                        "Inferno Charizard",
                        1_500_000,
                        4.0F,
                        500,
                        100000,
                        30
                )
        );

        DUAL_TYPE.add(
                new WorldBossDefinition(
                        "greninja",
                        "Shadow Greninja",
                        1_500_000,
                        4.0F,
                        500,
                        100000,
                        30
                )
        );

        DUAL_TYPE.add(
                new WorldBossDefinition(
                        "aegislash",
                        "Royal Aegislash",
                        1_500_000,
                        4.0F,
                        500,
                        100000,
                        30
                )
        );

        DUAL_TYPE.add(
                new WorldBossDefinition(
                        "garchomp",
                        "Desert Garchomp",
                        1_800_000,
                        4.5F,
                        750,
                        150000,
                        30
                )
        );

        DUAL_TYPE.add(
                new WorldBossDefinition(
                        "gardevoir",
                        "Astral Gardevoir",
                        1_500_000,
                        4.0F,
                        500,
                        100000,
                        30
                )
        );

        DUAL_TYPE.add(
                new WorldBossDefinition(
                        "chandelure",
                        "Soulfire Chandelure",
                        1_500_000,
                        4.0F,
                        500,
                        100000,
                        30
                )
        );

        DUAL_TYPE.add(
                new WorldBossDefinition(
                        "scizor",
                        "Steelwing Scizor",
                        1_500_000,
                        4.0F,
                        500,
                        100000,
                        30
                )
        );

        DUAL_TYPE.add(
                new WorldBossDefinition(
                        "weavile",
                        "Frostfang Weavile",
                        1_500_000,
                        4.0F,
                        500,
                        100000,
                        30
                )
        );
    }





    public static WorldBossDefinition randomBoss() {

        int roll =
                RANDOM.nextInt(100);





        if (roll < 5) {

            return LegendaryWorldBossPool
                    .randomLegendary();
        }





        if (roll < 70) {

            return SINGLE_TYPE.get(

                    RANDOM.nextInt(
                            SINGLE_TYPE.size()
                    )
            );
        }





        return DUAL_TYPE.get(

                RANDOM.nextInt(
                        DUAL_TYPE.size()
                )
        );
    }




    public static WorldBossDefinition randomSingle() {

        return SINGLE_TYPE.get(

                RANDOM.nextInt(
                        SINGLE_TYPE.size()
                )
        );
    }





    public static WorldBossDefinition randomDual() {

        return DUAL_TYPE.get(

                RANDOM.nextInt(
                        DUAL_TYPE.size()
                )
        );
    }

    public static List<WorldBossDefinition> getAll() {

        List<WorldBossDefinition> all =
                new ArrayList<>();

        all.addAll(SINGLE_TYPE);

        all.addAll(DUAL_TYPE);

        return all;
    }

}