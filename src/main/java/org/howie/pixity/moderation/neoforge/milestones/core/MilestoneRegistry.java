package org.howie.pixity.moderation.neoforge.milestones.core;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public class MilestoneRegistry {

    private static final List<MilestoneEntry> ENTRIES = new ArrayList<>();





    public static void init() {

        ENTRIES.clear();

        registerMining();
        registerFishing();
        registerFarming();
        registerMobs();
        registerProfessor();
        registerTrainer();
        registerEvents();
    }





    public static List<MilestoneEntry> getAll() {
        return ENTRIES;
    }

    public static List<MilestoneEntry> getCategory(MilestoneCategory category) {

        List<MilestoneEntry> list = new ArrayList<>();

        for (MilestoneEntry e : ENTRIES) {
            if (e.category == category) {
                list.add(e);
            }
        }

        return list;
    }

    public static List<MilestoneEntry> getGroup(String group) {

        List<MilestoneEntry> list = new ArrayList<>();

        for (MilestoneEntry e : ENTRIES) {
            if (e.group.equalsIgnoreCase(group)) {
                list.add(e);
            }
        }

        return list;
    }





    private static void add(MilestoneEntry entry) {
        ENTRIES.add(entry);
    }





    private static void registerMining() {

        add(new MilestoneEntry(
                "coal_ore",
                "coal_ore_mined",
                new ItemStack(Items.COAL_ORE),
                "&8&lCOAL ORE MILESTONES",
                10,
                MilestoneCategory.MINING,
                "mining_vanilla"
        ));

        add(new MilestoneEntry(
                "copper_ore",
                "copper_ore_mined",
                new ItemStack(Items.COPPER_ORE),
                "&#B87333&lCOPPER ORE MILESTONES",
                12,
                MilestoneCategory.MINING,
                "mining_vanilla"
        ));

        add(new MilestoneEntry(
                "iron_ore",
                "iron_ore_mined",
                new ItemStack(Items.IRON_ORE),
                "&7&lIRON ORE MILESTONES",
                14,
                MilestoneCategory.MINING,
                "mining_vanilla"
        ));

        add(new MilestoneEntry(
                "gold_ore",
                "gold_ore_mined",
                new ItemStack(Items.GOLD_ORE),
                "&#FFD700&lGOLD ORE MILESTONES",
                16,
                MilestoneCategory.MINING,
                "mining_vanilla"
        ));

        add(new MilestoneEntry(
                "redstone_ore",
                "redstone_ore_mined",
                new ItemStack(Items.REDSTONE_ORE),
                "<gradient:#FF0000:#7A0000:#FF0000>&lREDSTONE ORE MILESTONES</gradient>",
                18,
                MilestoneCategory.MINING,
                "mining_vanilla"
        ));

        add(new MilestoneEntry(
                "lapis_ore",
                "lapis_ore_mined",
                new ItemStack(Items.LAPIS_ORE),
                "<gradient:#004CFF:#00B7FF:#004CFF>&lLAPIS ORE MILESTONES</gradient>",
                20,
                MilestoneCategory.MINING,
                "mining_vanilla"
        ));

        add(new MilestoneEntry(
                "quartz_ore",
                "quartz_ore_mined",
                new ItemStack(Items.NETHER_QUARTZ_ORE),
                "<gradient:#FFFFFF:#D9D9D9:#FFFFFF>&lQUARTZ ORE MILESTONES</gradient>",
                22,
                MilestoneCategory.MINING,
                "mining_vanilla"
        ));

        add(new MilestoneEntry(
                "nether_gold_ore",
                "nether_gold_ore_mined",
                new ItemStack(Items.NETHER_GOLD_ORE),
                "<gradient:#FFD700:#A67C00:#FFD700>&lNETHER GOLD ORE MILESTONES</gradient>",
                24,
                MilestoneCategory.MINING,
                "mining_vanilla"
        ));

        add(new MilestoneEntry(
                "diamond_ore",
                "diamond_ore_mined",
                new ItemStack(Items.DIAMOND_ORE),
                "&#00ebd7&lDIAMOND ORE MILESTONES",
                32,
                MilestoneCategory.MINING,
                "mining_vanilla"
        ));

        add(new MilestoneEntry(
                "emerald_ore",
                "emerald_ore_mined",
                new ItemStack(Items.EMERALD_ORE),
                "&#00ff33&lEMERALD ORE MILESTONES",
                34,
                MilestoneCategory.MINING,
                "mining_vanilla"
        ));

        add(new MilestoneEntry(
                "ancient_debris",
                "ancient_debris_mined",
                new ItemStack(Items.ANCIENT_DEBRIS),
                "<gradient:#7A2A2A:#434241:#7A2A2A>&lANCIENT DEBRIS MILESTONES</gradient>",
                22,
                MilestoneCategory.MINING,
                "mining_vanilla"
        ));



        add(new MilestoneEntry(
                "dawn_stone_ore",
                "dawn_stone_ore_mined",
                cobble("dawn_stone_ore"),
                "<gradient:#00F5FF:#FFFFFF:#00F5FF>&lDAWN STONE ORE MILESTONES</gradient>",
                10,
                MilestoneCategory.MINING,
                "mining_cobblemon"
        ));

        add(new MilestoneEntry(
                "dusk_stone_ore",
                "dusk_stone_ore_mined",
                cobble("dusk_stone_ore"),
                "<gradient:#5600FF:#404040:#4100FF>&lDUSK STONE ORE MILESTONES</gradient>",
                12,
                MilestoneCategory.MINING,
                "mining_cobblemon"
        ));

        add(new MilestoneEntry(
                "fire_stone_ore",
                "fire_stone_ore_mined",
                cobble("fire_stone_ore"),
                "<gradient:#FF0000:#FF9B00:#FF0000>&lFIRE STONE ORE MILESTONES</gradient>",
                14,
                MilestoneCategory.MINING,
                "mining_cobblemon"
        ));

        add(new MilestoneEntry(
                "ice_stone_ore",
                "ice_stone_ore_mined",
                cobble("ice_stone_ore"),
                "<gradient:#84FAF0:#0064FF:#91FFF6>&lICE STONE ORE MILESTONES</gradient>",
                16,
                MilestoneCategory.MINING,
                "mining_cobblemon"
        ));

        add(new MilestoneEntry(
                "water_stone_ore",
                "water_stone_ore_mined",
                cobble("water_stone_ore"),
                "<gradient:#0E00FF:#0064FF:#0007FF>&lWATER STONE ORE MILESTONES</gradient>",
                20,
                MilestoneCategory.MINING,
                "mining_cobblemon"
        ));

        add(new MilestoneEntry(
                "thunder_stone_ore",
                "thunder_stone_ore_mined",
                cobble("thunder_stone_ore"),
                "<gradient:#23D211:#F1FF00:#00FF11>&lTHUNDER STONE ORE MILESTONES</gradient>",
                24,
                MilestoneCategory.MINING,
                "mining_cobblemon"
        ));

        add(new MilestoneEntry(
                "leaf_stone_ore",
                "leaf_stone_ore_mined",
                cobble("leaf_stone_ore"),
                "<gradient:#0A4704:#91FFA1:#025107>&lLEAF STONE ORE MILESTONES</gradient>",
                28,
                MilestoneCategory.MINING,
                "mining_cobblemon"
        ));

        add(new MilestoneEntry(
                "moon_stone_ore",
                "moon_stone_ore_mined",
                cobble("moon_stone_ore"),
                "<gradient:#434343:#1E3A77:#585858>&lMOON STONE ORE MILESTONES</gradient>",
                30,
                MilestoneCategory.MINING,
                "mining_cobblemon"
        ));

        add(new MilestoneEntry(
                "shiny_stone_ore",
                "shiny_stone_ore_mined",
                cobble("shiny_stone_ore"),
                "<gradient:#F5E987:#9EF59D:#FEFF97>&lSHINY STONE ORE MILESTONES</gradient>",
                32,
                MilestoneCategory.MINING,
                "mining_cobblemon"
        ));

        add(new MilestoneEntry(
                "sun_stone_ore",
                "sun_stone_ore_mined",
                cobble("sun_stone_ore"),
                "<gradient:#FFAC12:#91720D:#FFB000>&lSUN STONE ORE MILESTONES</gradient>",
                34,
                MilestoneCategory.MINING,
                "mining_cobblemon"
        ));
    }





    private static void registerFishing() {

        add(new MilestoneEntry(
                "bronze_fish",
                "bronze_fish_caught",
                cobble("poke_ball"),
                "&#c99a73BRONZE FISH MILESTONES",
                10,
                MilestoneCategory.FISHING,
                "fishing"
        ));

        add(new MilestoneEntry(
                "silver_fish",
                "silver_fish_caught",
                cobble("great_ball"),
                "&#bdbdbdSILVER FISH MILESTONES",
                12,
                MilestoneCategory.FISHING,
                "fishing"
        ));

        add(new MilestoneEntry(
                "gold_fish",
                "gold_fish_caught",
                cobble("ultra_ball"),
                "&#f5f788GOLD FISH MILESTONES",
                14,
                MilestoneCategory.FISHING,
                "fishing"
        ));

        add(new MilestoneEntry(
                "diamond_fish",
                "diamond_fish_caught",
                cobble("beast_ball"),
                "&#57f2e8DIAMOND FISH MILESTONES",
                16,
                MilestoneCategory.FISHING,
                "fishing"
        ));

        add(new MilestoneEntry(
                "mythical_fish",
                "mythical_fish_caught",
                cobble("ancient_origin_ball"),
                "&#db2debMYTHICAL FISH MILESTONES",
                22,
                MilestoneCategory.FISHING,
                "fishing"
        ));

        add(new MilestoneEntry(
                "platinum_fish",
                "platinum_fish_caught",
                cobble("master_ball"),
                "&#2deb95PLATINUM FISH MILESTONES",
                20,
                MilestoneCategory.FISHING,
                "fishing"
        ));

        add(new MilestoneEntry(
                "crabs_killed",
                "crabs_killed",
                new ItemStack(Items.QUARTZ),
                "&c&lCRABS KILLED MILESTONES",
                22,
                MilestoneCategory.FISHING,
                "fishing"
        ));

        add(new MilestoneEntry(
                "squid_kills",
                "squid_kills",
                new ItemStack(Items.SQUID_SPAWN_EGG),
                "&8&lSQUIDS KILLED MILESTONES",
                30,
                MilestoneCategory.FISHING,
                "fishing"
        ));

        add(new MilestoneEntry(
                "dolphin_kills",
                "dolphin_kills",
                new ItemStack(Items.DOLPHIN_SPAWN_EGG),
                "&#57D4FF&lDOLPHINS KILLED MILESTONES",
                32,
                MilestoneCategory.FISHING,
                "fishing"
        ));

        add(new MilestoneEntry(
                "total_fish_caught",
                "total_fish_caught",
                new ItemStack(Items.TROPICAL_FISH),
                "&e&lTOTAL FISH CAUGHT MILESTONES",
                40,
                MilestoneCategory.FISHING,
                "fishing"
        ));
    }





    private static void registerFarming() {

        add(new MilestoneEntry(
                "wheat",
                "wheat_crops_harvested",
                new ItemStack(Items.WHEAT),
                "<gradient:#CED729:#FCAF04:#DAEF0D>&lWHEAT CROPS HARVESTED MILESTONES</gradient>",
                10,
                MilestoneCategory.FARMING,
                "farming"
        ));

        add(new MilestoneEntry(
                "carrot",
                "carrot_crops_harvested",
                new ItemStack(Items.CARROT),
                "<gradient:#FF9100:#115F08:#EF960D>&lCARROT CROPS HARVESTED MILESTONES</gradient>",
                12,
                MilestoneCategory.FARMING,
                "farming"
        ));

        add(new MilestoneEntry(
                "potato",
                "potato_crops_harvested",
                new ItemStack(Items.POTATO),
                "<gradient:#C69C6D:#8B5A2B:#C69C6D>&lPOTATO CROPS HARVESTED</gradient>",
                14,
                MilestoneCategory.FARMING,
                "farming"
        ));

        add(new MilestoneEntry(
                "beetroot",
                "beetroot_crops_harvested",
                new ItemStack(Items.BEETROOT),
                "<gradient:#FF0033:#8B0000:#FF0033>&lBEETROOT CROPS HARVESTED</gradient>",
                16,
                MilestoneCategory.FARMING,
                "farming"
        ));

        add(new MilestoneEntry(
                "sugarcane",
                "sugarcane_crops_harvested",
                new ItemStack(Items.SUGAR_CANE),
                "<gradient:#7CFC00:#228B22:#7CFC00>&lSUGARCANE HARVESTED</gradient>",
                20,
                MilestoneCategory.FARMING,
                "farming"
        ));

        add(new MilestoneEntry(
                "cocoa",
                "cocoa_crops_harvested",
                new ItemStack(Items.COCOA_BEANS),
                "<gradient:#6B3E26:#3B1F0B:#6B3E26>&lCOCOA HARVESTED</gradient>",
                24,
                MilestoneCategory.FARMING,
                "farming"
        ));

        add(new MilestoneEntry(
                "pumpkin",
                "pumpkin_crops_harvested",
                new ItemStack(Items.PUMPKIN),
                "<gradient:#FF7A00:#C45500:#FF7A00>&lPUMPKINS HARVESTED</gradient>",
                28,
                MilestoneCategory.FARMING,
                "farming"
        ));

        add(new MilestoneEntry(
                "melon",
                "melon_crops_harvested",
                new ItemStack(Items.MELON),
                "<gradient:#7CFC00:#228B22:#7CFC00>&lMELONS HARVESTED</gradient>",
                30,
                MilestoneCategory.FARMING,
                "farming"
        ));

        add(new MilestoneEntry(
                "bamboo",
                "bamboo_crop_harvested",
                new ItemStack(Items.BAMBOO),
                "<gradient:#B7FF00:#4CAF50:#B7FF00>&lBAMBOO HARVESTED</gradient>",
                32,
                MilestoneCategory.FARMING,
                "farming"
        ));

        add(new MilestoneEntry(
                "total_crops",
                "total_crops_harvested",
                new ItemStack(Items.GOLDEN_HOE),
                "<gradient:#FFD700:#FFF8DC:#FFD700>&lTOTAL CROPS HARVESTED</gradient>",
                34,
                MilestoneCategory.FARMING,
                "farming"
        ));
    }





    private static void registerMobs() {

        add(new MilestoneEntry(
                "zombie",
                "zombie_kills",
                new ItemStack(Items.ZOMBIE_SPAWN_EGG),
                "<gradient:#155B47:#1A54BF:#1A5637>ZOMBIES KILLED MILESTONES</gradient>",
                10,
                MilestoneCategory.MOBS,
                "mobs"
        ));

        add(new MilestoneEntry(
                "creeper",
                "creeper_kills",
                new ItemStack(Items.CREEPER_SPAWN_EGG),
                "<gradient:#2DFF00:#343435:#2DFF00>CREEPERS KILLED MILESTONES</gradient>",
                12,
                MilestoneCategory.MOBS,
                "mobs"
        ));

        add(new MilestoneEntry(
                "skeleton",
                "skeleton_kills",
                new ItemStack(Items.SKELETON_SPAWN_EGG),
                "<gradient:#DDDDDD:#888888:#DDDDDD>SKELETONS KILLED</gradient>",
                14,
                MilestoneCategory.MOBS,
                "mobs"
        ));

        add(new MilestoneEntry(
                "spider",
                "spider_kills",
                new ItemStack(Items.SPIDER_SPAWN_EGG),
                "<gradient:#2B2B2B:#5E5E5E:#2B2B2B>SPIDERS KILLED</gradient>",
                16,
                MilestoneCategory.MOBS,
                "mobs"
        ));

        add(new MilestoneEntry(
                "blaze",
                "blaze_kills",
                new ItemStack(Items.BLAZE_SPAWN_EGG),
                "<gradient:#FF9900:#FFD700:#FF9900>BLAZES KILLED</gradient>",
                20,
                MilestoneCategory.MOBS,
                "mobs"
        ));

        add(new MilestoneEntry(
                "magma_cube",
                "magma_cube_kills",
                new ItemStack(Items.MAGMA_CUBE_SPAWN_EGG),
                "<gradient:#FF3300:#660000:#FF3300>MAGMA CUBES KILLED</gradient>",
                24,
                MilestoneCategory.MOBS,
                "mobs"
        ));

        add(new MilestoneEntry(
                "enderman",
                "enderman_kills",
                new ItemStack(Items.ENDERMAN_SPAWN_EGG),
                "<gradient:#4B0082:#000000:#4B0082>ENDERMEN KILLED</gradient>",
                28,
                MilestoneCategory.MOBS,
                "mobs"
        ));

        add(new MilestoneEntry(
                "phantom",
                "phantom_kills",
                new ItemStack(Items.PHANTOM_SPAWN_EGG),
                "<gradient:#00BFFF:#191970:#00BFFF>PHANTOMS KILLED</gradient>",
                30,
                MilestoneCategory.MOBS,
                "mobs"
        ));

        add(new MilestoneEntry(
                "cow",
                "cow_kills",
                new ItemStack(Items.COW_SPAWN_EGG),
                "<gradient:#8B4513:#FFFFFF:#8B4513>COWS KILLED</gradient>",
                32,
                MilestoneCategory.MOBS,
                "mobs"
        ));

        add(new MilestoneEntry(
                "pig",
                "pig_kills",
                new ItemStack(Items.PIG_SPAWN_EGG),
                "<gradient:#FFB6C1:#FF69B4:#FFB6C1>PIGS KILLED</gradient>",
                34,
                MilestoneCategory.MOBS,
                "mobs"
        ));

        add(new MilestoneEntry(
                "sheep",
                "sheep_kills",
                new ItemStack(Items.SHEEP_SPAWN_EGG),
                "<gradient:#FFFFFF:#CCCCCC:#FFFFFF>SHEEP KILLED</gradient>",
                38,
                MilestoneCategory.MOBS,
                "mobs"
        ));

        add(new MilestoneEntry(
                "chicken",
                "chicken_kills",
                new ItemStack(Items.CHICKEN_SPAWN_EGG),
                "<gradient:#FFFACD:#FFD700:#FFFACD>CHICKENS KILLED</gradient>",
                40,
                MilestoneCategory.MOBS,
                "mobs"
        ));
    }





    private static void registerProfessor() {

        add(new MilestoneEntry(
                "pokemon_caught",
                "pokemon_caught",
                cobble("poke_ball"),
                "<gradient:#FF0000:#FFFFFF:#FF0000>POKEMON CAUGHT</gradient>",
                10,
                MilestoneCategory.PROFESSOR,
                "professor"
        ));

        add(new MilestoneEntry(
                "pokemon_shiny",
                "pokemon_shiny_caught",
                cobble("citrine_ball"),
                "<gradient:#C8FF00:#FFFFFF:#BEFF00>SHINY POKEMON CAUGHT</gradient>",
                12,
                MilestoneCategory.PROFESSOR,
                "professor"
        ));

        add(new MilestoneEntry(
                "pokemon_hidden_ability",
                "pokemon_hidden_ability_caught",
                cobble("premier_ball"),
                "<gradient:#FF0000:#FFFFFF:#FF0000>HIDDEN ABILITY POKEMON CAUGHT</gradient>",
                14,
                MilestoneCategory.PROFESSOR,
                "professor"
        ));

        add(new MilestoneEntry(
                "pokemon_mythical",
                "pokemon_mythical_caught",
                cobble("master_ball"),
                "<gradient:#FF81EC:#920CF3>MYTHICAL POKEMON CAUGHT</gradient>",
                16,
                MilestoneCategory.PROFESSOR,
                "professor"
        ));

        add(new MilestoneEntry(
                "pokemon_legendary",
                "pokemon_legendary_caught",
                cobble("ancient_origin_ball"),
                "<gradient:#FF0000:#9400FF>LEGENDARY POKEMON CAUGHT</gradient>",
                30,
                MilestoneCategory.PROFESSOR,
                "professor"
        ));

        add(new MilestoneEntry(
                "pokemon_special",
                "pokemon_special_caught",
                cobble("ancient_poke_ball"),
                "<gradient:#FF0808:#920CF3>TOTAL SPECIAL POKEMON CAUGHT</gradient>",
                32,
                MilestoneCategory.PROFESSOR,
                "professor"
        ));
    }





    private static void registerTrainer() {

        add(new MilestoneEntry(
                "trainer_levels_gained",
                "trainer_levels_gained",
                cobble("rare_candy"),
                "<gradient:#0064FF:#FFFFFF:#0E84F5>TOTAL LEVELS GAINED</gradient>",
                10,
                MilestoneCategory.TRAINER,
                "trainer"
        ));

        add(new MilestoneEntry(
                "trainer_level100",
                "trainer_level100",
                cobble("kings_rock"),
                "<gradient:#00FF3B:#FFFFFF:#0EF54C>TOTAL POKEMON GOTTEN TO 100</gradient>",
                12,
                MilestoneCategory.TRAINER,
                "trainer"
        ));

        add(new MilestoneEntry(
                "trainer_evolutions",
                "trainer_evolutions",
                cobble("shiny_stone"),
                "<gradient:#E4FF81:#FFFFFF:#F2FF74>TOTAL POKEMON EVOLVED</gradient>",
                14,
                MilestoneCategory.TRAINER,
                "trainer"
        ));

        add(new MilestoneEntry(
                "trainer_infusion",
                "trainer_infusion",
                new ItemStack(Items.END_CRYSTAL),
                "<gradient:#FF8181:#8174FF>TOTAL POKEMON INFUSED</gradient>",
                16,
                MilestoneCategory.TRAINER,
                "trainer"
        ));

        add(new MilestoneEntry(
                "trainer_happiness_gained",
                "trainer_happiness_gained",
                cobble("blue_mint_leaf"),
                "<gradient:#70FF91:#F7FF74>TOTAL HAPPINESS GAINED</gradient>",
                22,
                MilestoneCategory.TRAINER,
                "trainer"
        ));
    }





    private static void registerEvents() {

        add(new MilestoneEntry(
                "total_event_wins",
                "total_event_wins",
                new ItemStack(Items.NETHER_STAR),
                "<rainbow>TOTAL EVENTS WON MILESTONES</rainbow>",
                4,
                MilestoneCategory.EVENTS,
                "events"
        ));

        add(new MilestoneEntry(
                "events_fishing",
                "fishing_event_wins",
                new ItemStack(Items.FISHING_ROD),
                "<gradient:#00CFFF:#0066FF>TOTAL FISHING EVENTS WON</gradient>",
                9,
                MilestoneCategory.EVENTS,
                "events"
        ));

        add(new MilestoneEntry(
                "events_mining",
                "mining_event_wins",
                new ItemStack(Items.NETHERITE_PICKAXE),
                "<gradient:#5D5D5D:#FFFFFF:#777777>TOTAL MINING EVENTS WON</gradient>",
                11,
                MilestoneCategory.EVENTS,
                "events"
        ));

        add(new MilestoneEntry(
                "events_farming",
                "farming_event_wins",
                new ItemStack(Items.NETHERITE_HOE),
                "<gradient:#2DFF00:#14640C:#2DFF00>TOTAL FARMING EVENTS WON</gradient>",
                13,
                MilestoneCategory.EVENTS,
                "events"
        ));

        add(new MilestoneEntry(
                "events_pvp",
                "pvp_event_wins",
                new ItemStack(Items.NETHERITE_SWORD),
                "<gradient:#FF0000:#FFFFFF:#FF0000>TOTAL PVP EVENTS WON</gradient>",
                15,
                MilestoneCategory.EVENTS,
                "events"
        ));

        add(new MilestoneEntry(
                "events_tournaments",
                "tournament_event_wins",
                cobble("poke_ball"),
                "<gradient:#FFFFFF:#FF0000:#FFFFFF>TOTAL TOURNAMENT EVENTS WON</gradient>",
                17,
                MilestoneCategory.EVENTS,
                "events"
        ));
    }





    public static ItemStack cobble(String id) {

        var item = BuiltInRegistries.ITEM.get(
                ResourceLocation.tryParse("cobblemon:" + id)
        );

        if (item == null || item == Items.AIR) {
            return new ItemStack(Items.BARRIER);
        }

        return new ItemStack(item);
    }

    public static MilestoneEntry getEntry(
            String id
    ) {

        for (MilestoneEntry entry : ENTRIES) {

            if (entry.id.equalsIgnoreCase(id)) {
                return entry;
            }
        }

        return null;
    }
}
