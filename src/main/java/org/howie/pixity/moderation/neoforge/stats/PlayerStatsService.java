package org.howie.pixity.moderation.neoforge.stats;

import java.util.UUID;

public class PlayerStatsService {




    private static void add(UUID uuid, String column, int amount) {
        PlayerStatsDatabase.add(uuid, column, amount);
    }





    public static void addBronzeFish(UUID uuid) {
        add(uuid, "bronze_fish_caught", 1);
        add(uuid, "total_fish_caught", 1);
    }

    public static void addSilverFish(UUID uuid) {
        add(uuid, "silver_fish_caught", 1);
        add(uuid, "total_fish_caught", 1);
    }

    public static void addGoldFish(UUID uuid) {
        add(uuid, "gold_fish_caught", 1);
        add(uuid, "total_fish_caught", 1);
    }

    public static void addDiamondFish(UUID uuid) {
        add(uuid, "diamond_fish_caught", 1);
        add(uuid, "total_fish_caught", 1);
    }

    public static void addPlatinumFish(UUID uuid) {
        add(uuid, "platinum_fish_caught", 1);
        add(uuid, "total_fish_caught", 1);
    }

    public static void addMythicalFish(UUID uuid) {
        add(uuid, "mythical_fish_caught", 1);
        add(uuid, "total_fish_caught", 1);
    }

    public static void addSquidTentacle(UUID uuid) {
        add(uuid, "squid_tentacles_collected", 1);
    }

    public static void addDolphinTreasure(UUID uuid) {
        add(uuid, "dolphin_treasure_collected", 1);
    }

    public static void addCrabKill(UUID uuid) {
        add(uuid, "crabs_killed", 1);
    }





    public static void addBlocksMined(UUID uuid) {
        add(uuid, "blocks_mined", 1);
    }

    private static void addOre(UUID uuid, String oreColumn) {

        add(uuid, oreColumn, 1);
        add(uuid, "total_ores_mined", 1);
    }

    public static void addCoalOre(UUID uuid) {
        addOre(uuid, "coal_ore_mined");
    }

    public static void addCopperOre(UUID uuid) {
        addOre(uuid, "copper_ore_mined");
    }

    public static void addIronOre(UUID uuid) {
        addOre(uuid, "iron_ore_mined");
    }

    public static void addGoldOre(UUID uuid) {
        addOre(uuid, "gold_ore_mined");
    }

    public static void addLapisOre(UUID uuid) {
        addOre(uuid, "lapis_ore_mined");
    }

    public static void addRedstoneOre(UUID uuid) {
        addOre(uuid, "redstone_ore_mined");
    }

    public static void addDiamondOre(UUID uuid) {
        addOre(uuid, "diamond_ore_mined");
    }

    public static void addEmeraldOre(UUID uuid) {
        addOre(uuid, "emerald_ore_mined");
    }

    public static void addAncientDebris(UUID uuid) {
        addOre(uuid, "ancient_debris_mined");
    }

    public static void addDawnStoneOre(UUID uuid) {
        addOre(uuid, "dawn_stone_ore_mined");
    }

    public static void addDuskStoneOre(UUID uuid) {
        addOre(uuid, "dusk_stone_ore_mined");
    }

    public static void addFireStoneOre(UUID uuid) {
        addOre(uuid, "fire_stone_ore_mined");
    }

    public static void addIceStoneOre(UUID uuid) {
        addOre(uuid, "ice_stone_ore_mined");
    }

    public static void addLeafStoneOre(UUID uuid) {
        addOre(uuid, "leaf_stone_ore_mined");
    }

    public static void addMoonStoneOre(UUID uuid) {
        addOre(uuid, "moon_stone_ore_mined");
    }

    public static void addShinyStoneOre(UUID uuid) {
        addOre(uuid, "shiny_stone_ore_mined");
    }

    public static void addSunStoneOre(UUID uuid) {
        addOre(uuid, "sun_stone_ore_mined");
    }

    public static void addThunderStoneOre(UUID uuid) {
        addOre(uuid, "thunder_stone_ore_mined");
    }

    public static void addWaterStoneOre(UUID uuid) {
        addOre(uuid, "water_stone_ore_mined");
    }

    public static void addOverworldOre(UUID uuid) {
        add(uuid, "overworld_ores_mined", 1);
    }

    public static void addNetherOre(UUID uuid) {
        add(uuid, "nether_ores_mined", 1);
    }

    public static void addDeepslateOre(UUID uuid) {
        add(uuid, "deepslate_ores_mined", 1);
    }

    public static void addCobblemonOre(UUID uuid) {
        add(uuid, "cobblemon_ores_mined", 1);
    }





    private static void addCrop(UUID uuid, String column, int amount) {

        add(uuid, column, amount);
        add(uuid, "total_crops_harvested", amount);
    }

    public static void addWheat(UUID uuid, int amount) {
        addCrop(uuid, "wheat_crops_harvested", amount);
    }

    public static void addPotato(UUID uuid, int amount) {
        addCrop(uuid, "potato_crops_harvested", amount);
    }

    public static void addCarrot(UUID uuid, int amount) {
        addCrop(uuid, "carrot_crops_harvested", amount);
    }

    public static void addSugarcane(UUID uuid, int amount) {
        addCrop(uuid, "sugarcane_crops_harvested", amount);
    }

    public static void addMelon(UUID uuid, int amount) {
        addCrop(uuid, "melon_crops_harvested", amount);
    }

    public static void addPumpkin(UUID uuid, int amount) {
        addCrop(uuid, "pumpkin_crops_harvested", amount);
    }

    public static void addCocoa(UUID uuid, int amount) {
        addCrop(uuid, "cocoa_crops_harvested", amount);
    }

    public static void addBeetroot(UUID uuid, int amount) {
        addCrop(uuid, "beetroot_crops_harvested", amount);
    }

    public static void addBamboo(UUID uuid, int amount) {
        addCrop(uuid, "bamboo_crops_harvested", amount);
    }





    private static void addMob(UUID uuid, String column) {

        add(uuid, column, 1);
        add(uuid, "total_mob_kills", 1);
    }

    public static void addChickenKill(UUID uuid) {
        addMob(uuid, "chicken_kills");
    }

    public static void addCowKill(UUID uuid) {
        addMob(uuid, "cow_kills");
    }

    public static void addPigKill(UUID uuid) {
        addMob(uuid, "pig_kills");
    }

    public static void addSheepKill(UUID uuid) {
        addMob(uuid, "sheep_kills");
    }

    public static void addZombieKill(UUID uuid) {
        addMob(uuid, "zombie_kills");
    }

    public static void addSkeletonKill(UUID uuid) {
        addMob(uuid, "skeleton_kills");
    }

    public static void addSpiderKill(UUID uuid) {
        addMob(uuid, "spider_kills");
    }

    public static void addCreeperKill(UUID uuid) {
        addMob(uuid, "creeper_kills");
    }

    public static void addBlazeKill(UUID uuid) {
        addMob(uuid, "blaze_kills");
    }

    public static void addMagmaCubeKill(UUID uuid) {
        addMob(uuid, "magma_cube_kills");
    }

    public static void addEndermanKill(UUID uuid) {
        addMob(uuid, "enderman_kills");
    }

    public static void addPhantomKill(UUID uuid) {
        addMob(uuid, "phantom_kills");
    }

    public static void addBossKill(UUID uuid) {
        add(uuid, "boss_kills", 1);
    }





    public static void addTrainerLevel(UUID uuid) {
        add(uuid, "trainer_levels_gained", 1);
    }

    public static void addTrainer100(UUID uuid) {
        add(uuid, "trainer_level100", 1);
    }

    public static void addEvolution(UUID uuid) {
        add(uuid, "trainer_evolutions", 1);
    }

    public static void addInfusion(UUID uuid) {
        add(uuid, "trainer_infusion", 1);
    }

    public static void addPokemonHappiness(UUID uuid, int amount) {
        add(uuid, "trainer_happiness_gained", amount);
    }





    public static void addPokemonCatch(UUID uuid) {
        add(uuid, "pokemon_caught", 1);
    }

    public static void addHiddenAbility(UUID uuid) {

        add(uuid, "pokemon_hidden_ability", 1);
        add(uuid, "pokemon_total_special", 1);
    }

    public static void addShiny(UUID uuid) {

        add(uuid, "pokemon_shiny", 1);
        add(uuid, "pokemon_total_special", 1);
    }

    public static void addLegendary(UUID uuid) {

        add(uuid, "pokemon_legendary", 1);
        add(uuid, "pokemon_total_special", 1);
    }

    public static void addMythical(UUID uuid) {

        add(uuid, "pokemon_mythical", 1);
        add(uuid, "pokemon_total_special", 1);
    }





    private static void addEvent(UUID uuid, String column) {

        add(uuid, column, 1);
        add(uuid, "total_event_wins", 1);
    }

    public static void addFishingEventWin(UUID uuid) {
        addEvent(uuid, "fishing_event_wins");
    }

    public static void addMiningEventWin(UUID uuid) {
        addEvent(uuid, "mining_event_wins");
    }

    public static void addFarmingEventWin(UUID uuid) {
        addEvent(uuid, "farming_event_wins");
    }

    public static void addPvpEventWin(UUID uuid) {
        addEvent(uuid, "pvp_event_wins");
    }

    public static void addTournamentEventWin(UUID uuid) {
        addEvent(uuid, "tournament_event_wins");
    }
}