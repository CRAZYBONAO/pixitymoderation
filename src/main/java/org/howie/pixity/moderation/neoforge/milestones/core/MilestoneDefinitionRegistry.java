package org.howie.pixity.moderation.neoforge.milestones.core;

import java.util.HashMap;
import java.util.Map;

public class MilestoneDefinitionRegistry {

    private static final Map<String, MilestoneDefinition> DEFINITIONS =
            new HashMap<>();





    public static void init() {

        DEFINITIONS.clear();

        registerFishing();
        registerMining();
        registerFarming();
        registerMobs();
        registerProfessor();
        registerTrainer();
        registerEvents();
    }





    public static MilestoneDefinition get(String id) {
        return DEFINITIONS.get(id);
    }

    public static Map<String, MilestoneDefinition> getAll() {
        return DEFINITIONS;
    }





    private static void add(
            MilestoneDefinition definition
    ) {

        DEFINITIONS.put(
                definition.id,
                definition
        );
    }





    private static void registerFishing() {

        add(new MilestoneDefinition(
                "bronze_fish",
                "bronze_fish_caught",
                100,
                250,
                250,
                500,
                1000,
                2500,
                5000,
                10000,
                250,
                1
        ));

        add(new MilestoneDefinition(
                "silver_fish",
                "silver_fish_caught",
                100,
                150,
                150,
                300,
                600,
                1200,
                2500,
                5000,
                500,
                2
        ));

        add(new MilestoneDefinition(
                "gold_fish",
                "gold_fish_caught",
                100,
                100,
                100,
                250,
                500,
                1000,
                2500,
                5000,
                1000,
                3
        ));

        add(new MilestoneDefinition(
                "diamond_fish",
                "diamond_fish_caught",
                100,
                50,
                50,
                100,
                250,
                500,
                1000,
                2500,
                2500,
                5
        ));

        add(new MilestoneDefinition(
                "platinum_fish",
                "platinum_fish_caught",
                100,
                25,
                25,
                50,
                100,
                250,
                500,
                1000,
                5000,
                7
        ));

        add(new MilestoneDefinition(
                "mythical_fish",
                "mythical_fish_caught",
                100,
                10,
                10,
                20,
                40,
                80,
                160,
                320,
                10000,
                10
        ));

        add(new MilestoneDefinition(
                "crabs_killed",
                "crabs_killed",
                100,
                50,
                50,
                100,
                250,
                500,
                1000,
                2500,
                1500,
                2
        ));

        add(new MilestoneDefinition(
                "squid_kills",
                "squid_kills",
                100,
                100,
                100,
                200,
                400,
                800,
                1600,
                3200,
                1000,
                2
        ));

        add(new MilestoneDefinition(
                "dolphin_kills",
                "dolphin_kills",
                100,
                25,
                25,
                50,
                100,
                250,
                500,
                1000,
                2500,
                4
        ));

        add(new MilestoneDefinition(
                "total_fish_caught",
                "total_fish_caught",
                100,
                1000,
                1000,
                2000,
                4000,
                8000,
                16000,
                32000,
                5000,
                5
        ));
    }





    private static void registerMining() {

        add(new MilestoneDefinition(
                "coal_ore",
                "coal_ore_mined",
                100,
                250,
                125,
                1
        ));

        add(new MilestoneDefinition(
                "copper_ore",
                "copper_ore_mined",
                100,
                175,
                125,
                1
        ));

        add(new MilestoneDefinition(
                "iron_ore",
                "iron_ore_mined",
                100,
                175,
                250,
                2
        ));

        add(new MilestoneDefinition(
                "gold_ore",
                "gold_ore_mined",
                100,
                75,
                350,
                2
        ));

        add(new MilestoneDefinition(
                "redstone_ore",
                "redstone_ore_mined",
                100,
                50,
                350,
                2
        ));

        add(new MilestoneDefinition(
                "lapis_ore",
                "lapis_ore_mined",
                100,
                50,
                350,
                2
        ));

        add(new MilestoneDefinition(
                "diamond_ore",
                "diamond_ore_mined",
                100,
                25,
                450,
                4
        ));

        add(new MilestoneDefinition(
                "emerald_ore",
                "emerald_ore_mined",
                100,
                15,
                450,
                4
        ));

        add(new MilestoneDefinition(
                "quartz_ore",
                "quartz_ore_mined",
                100,
                250,
                125,
                1
        ));

        add(new MilestoneDefinition(
                "nether_gold_ore",
                "nether_gold_ore_mined",
                100,
                250,
                125,
                1
        ));

        add(new MilestoneDefinition(
                "ancient_debris",
                "ancient_debris_mined",
                100,
                8,
                500,
                5
        ));





        add(new MilestoneDefinition(
                "dawn_stone_ore",
                "dawn_stone_ore_mined",
                100,
                8,
                250,
                1
        ));

        add(new MilestoneDefinition(
                "dusk_stone_ore",
                "dusk_stone_ore_mined",
                100,
                8,
                250,
                1
        ));

        add(new MilestoneDefinition(
                "fire_stone_ore",
                "fire_stone_ore_mined",
                100,
                8,
                250,
                1
        ));

        add(new MilestoneDefinition(
                "ice_stone_ore",
                "ice_stone_ore_mined",
                100,
                8,
                250,
                1
        ));

        add(new MilestoneDefinition(
                "water_stone_ore",
                "water_stone_ore_mined",
                100,
                8,
                250,
                1
        ));

        add(new MilestoneDefinition(
                "thunder_stone_ore",
                "thunder_stone_ore_mined",
                100,
                8,
                250,
                1
        ));

        add(new MilestoneDefinition(
                "leaf_stone_ore",
                "leaf_stone_ore_mined",
                100,
                8,
                250,
                1
        ));

        add(new MilestoneDefinition(
                "moon_stone_ore",
                "moon_stone_ore_mined",
                100,
                8,
                250,
                1
        ));

        add(new MilestoneDefinition(
                "shiny_stone_ore",
                "shiny_stone_ore_mined",
                100,
                8,
                250,
                1
        ));

        add(new MilestoneDefinition(
                "sun_stone_ore",
                "sun_stone_ore_mined",
                100,
                8,
                250,
                1
        ));
    }





    private static void registerFarming() {

        add(new MilestoneDefinition(
                "wheat",
                "wheat_crops_harvested",
                100,
                500,
                250,
                2
        ));

        add(new MilestoneDefinition(
                "carrot",
                "carrot_crops_harvested",
                100,
                500,
                250,
                2
        ));

        add(new MilestoneDefinition(
                "potato",
                "potato_crops_harvested",
                100,
                500,
                250,
                2
        ));

        add(new MilestoneDefinition(
                "beetroot",
                "beetroot_crops_harvested",
                100,
                500,
                350,
                3
        ));

        add(new MilestoneDefinition(
                "sugarcane",
                "sugarcane_crops_harvested",
                100,
                500,
                250,
                2
        ));

        add(new MilestoneDefinition(
                "cocoa",
                "cocoa_crops_harvested",
                100,
                125,
                450,
                4
        ));

        add(new MilestoneDefinition(
                "pumpkin",
                "pumpkin_crops_harvested",
                100,
                250,
                350,
                3
        ));

        add(new MilestoneDefinition(
                "melon",
                "melon_crops_harvested",
                100,
                250,
                350,
                3
        ));

        add(new MilestoneDefinition(
                "bamboo",
                "bamboo_crop_harvested",
                100,
                1000,
                250,
                2
        ));

        add(new MilestoneDefinition(
                "total_crops",
                "total_crops_harvested",
                100,
                2500,
                750,
                10
        ));
    }





    private static void registerMobs() {

        add(new MilestoneDefinition(
                "zombie",
                "zombie_kills",
                100,
                750,
                500,
                5
        ));

        add(new MilestoneDefinition(
                "skeleton",
                "skeleton_kills",
                100,
                750,
                500,
                5
        ));

        add(new MilestoneDefinition(
                "spider",
                "spider_kills",
                100,
                750,
                500,
                5
        ));

        add(new MilestoneDefinition(
                "creeper",
                "creeper_kills",
                100,
                500,
                500,
                5
        ));

        add(new MilestoneDefinition(
                "blaze",
                "blaze_kills",
                100,
                500,
                500,
                5
        ));

        add(new MilestoneDefinition(
                "magma_cube",
                "magma_cube_kills",
                100,
                1000,
                500,
                5
        ));

        add(new MilestoneDefinition(
                "enderman",
                "enderman_kills",
                100,
                500,
                1000,
                15
        ));

        add(new MilestoneDefinition(
                "phantom",
                "phantom_kills",
                100,
                15,
                1000,
                15
        ));

        add(new MilestoneDefinition(
                "cow",
                "cow_kills",
                100,
                1000,
                250,
                1
        ));

        add(new MilestoneDefinition(
                "pig",
                "pig_kills",
                100,
                1000,
                250,
                1
        ));

        add(new MilestoneDefinition(
                "sheep",
                "sheep_kills",
                100,
                1000,
                250,
                1
        ));

        add(new MilestoneDefinition(
                "chicken",
                "chicken_kills",
                100,
                1000,
                250,
                1
        ));
    }





    private static void registerProfessor() {

        add(new MilestoneDefinition(
                "pokemon_caught",
                "pokemon_caught",
                100,
                5,
                250,
                2
        ));

        add(new MilestoneDefinition(
                "pokemon_hidden_ability",
                "pokemon_hidden_ability_caught",
                100,
                2,
                1500,
                5
        ));

        add(new MilestoneDefinition(
                "pokemon_shiny",
                "pokemon_shiny_caught",
                100,
                2,
                2500,
                10
        ));

        add(new MilestoneDefinition(
                "pokemon_mythical",
                "pokemon_mythical_caught",
                100,
                1,
                5000,
                25
        ));

        add(new MilestoneDefinition(
                "pokemon_legendary",
                "pokemon_legendary_caught",
                100,
                1,
                10000,
                50
        ));

        add(new MilestoneDefinition(
                "pokemon_special",
                "pokemon_special_caught",
                100,
                5,
                2500,
                25
        ));
    }





    private static void registerTrainer() {

        add(new MilestoneDefinition(
                "trainer_levels_gained",
                "trainer_levels_gained",
                100,
                5,
                250,
                2
        ));

        add(new MilestoneDefinition(
                "trainer_level100",
                "trainer_level100",
                100,
                1,
                2500,
                3
        ));

        add(new MilestoneDefinition(
                "trainer_evolutions",
                "trainer_evolutions",
                100,
                2,
                350,
                3
        ));

        add(new MilestoneDefinition(
                "trainer_infusion",
                "trainer_infusion",
                100,
                1,
                250,
                2
        ));

        add(new MilestoneDefinition(
                "trainer_happiness_gained",
                "trainer_happiness_gained",
                100,
                500,
                250,
                2
        ));
    }





    private static void registerEvents() {

        add(new MilestoneDefinition(
                "total_event_wins",
                "total_event_wins",
                100,
                1,
                5000,
                5
        ));

        add(new MilestoneDefinition(
                "events_fishing",
                "fishing_event_wins",
                100,
                1,
                5000,
                5
        ));

        add(new MilestoneDefinition(
                "events_mining",
                "mining_event_wins",
                100,
                1,
                5000,
                5
        ));

        add(new MilestoneDefinition(
                "events_farming",
                "farming_event_wins",
                100,
                1,
                5000,
                5
        ));

        add(new MilestoneDefinition(
                "events_pvp",
                "pvp_event_wins",
                100,
                1,
                5000,
                5
        ));

        add(new MilestoneDefinition(
                "events_tournaments",
                "tournament_event_wins",
                100,
                1,
                10000,
                10
        ));
    }
}