package org.howie.pixity.moderation.neoforge.skills;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;

import java.util.Map;
import java.util.Set;

public class SkillXpTables {




    public static final Map<Block, Integer> MINING = Map.ofEntries(




            Map.entry(Blocks.COAL_ORE, 5),
            Map.entry(Blocks.DEEPSLATE_COAL_ORE, 5),

            Map.entry(Blocks.COPPER_ORE, 5),
            Map.entry(Blocks.DEEPSLATE_COPPER_ORE, 5),

            Map.entry(Blocks.NETHER_QUARTZ_ORE, 5),




            Map.entry(Blocks.IRON_ORE, 15),
            Map.entry(Blocks.DEEPSLATE_IRON_ORE, 15),

            Map.entry(Blocks.REDSTONE_ORE, 15),
            Map.entry(Blocks.DEEPSLATE_REDSTONE_ORE, 15),

            Map.entry(Blocks.LAPIS_ORE, 15),
            Map.entry(Blocks.DEEPSLATE_LAPIS_ORE, 15),

            Map.entry(Blocks.GOLD_ORE, 25),
            Map.entry(Blocks.DEEPSLATE_GOLD_ORE, 25),
            Map.entry(Blocks.NETHER_GOLD_ORE, 25),




            Map.entry(Blocks.DIAMOND_ORE, 50),
            Map.entry(Blocks.DEEPSLATE_DIAMOND_ORE, 50),

            Map.entry(Blocks.EMERALD_ORE, 100),
            Map.entry(Blocks.DEEPSLATE_EMERALD_ORE, 100),

            Map.entry(Blocks.ANCIENT_DEBRIS, 250),




            Map.entry(modBlock("cobblemon", "dawn_stone_ore"), 40),
            Map.entry(modBlock("cobblemon", "deepslate_dawn_stone_ore"), 40),

            Map.entry(modBlock("cobblemon", "dusk_stone_ore"), 40),
            Map.entry(modBlock("cobblemon", "deepslate_dusk_stone_ore"), 40),

            Map.entry(modBlock("cobblemon", "fire_stone_ore"), 50),
            Map.entry(modBlock("cobblemon", "deepslate_fire_stone_ore"), 50),
            Map.entry(modBlock("cobblemon", "nether_fire_stone_ore"), 50),

            Map.entry(modBlock("cobblemon", "ice_stone_ore"), 40),
            Map.entry(modBlock("cobblemon", "deepslate_ice_stone_ore"), 40),

            Map.entry(modBlock("cobblemon", "leaf_stone_ore"), 40),
            Map.entry(modBlock("cobblemon", "deepslate_leaf_stone_ore"), 40),

            Map.entry(modBlock("cobblemon", "moon_stone_ore"), 60),
            Map.entry(modBlock("cobblemon", "deepslate_moon_stone_ore"), 60),
            Map.entry(modBlock("cobblemon", "dripstone_moon_stone_ore"), 60),

            Map.entry(modBlock("cobblemon", "shiny_stone_ore"), 75),
            Map.entry(modBlock("cobblemon", "deepslate_shiny_stone_ore"), 75),

            Map.entry(modBlock("cobblemon", "sun_stone_ore"), 75),
            Map.entry(modBlock("cobblemon", "deepslate_sun_stone_ore"), 75),
            Map.entry(modBlock("cobblemon", "terracotta_sun_stone_ore"), 75),

            Map.entry(modBlock("cobblemon", "thunder_stone_ore"), 75),
            Map.entry(modBlock("cobblemon", "deepslate_thunder_stone_ore"), 75),

            Map.entry(modBlock("cobblemon", "water_stone_ore"), 50),
            Map.entry(modBlock("cobblemon", "deepslate_waterstone_ore"), 50)
    );




    public static final Map<Block, Integer> WOODCUTTING = Map.ofEntries(


            Map.entry(Blocks.OAK_LOG, 3),
            Map.entry(Blocks.OAK_LEAVES, 3),


            Map.entry(Blocks.SPRUCE_LOG, 3),
            Map.entry(Blocks.SPRUCE_LEAVES, 3),


            Map.entry(Blocks.BIRCH_LOG, 3),
            Map.entry(Blocks.BIRCH_LEAVES, 3),


            Map.entry(Blocks.DARK_OAK_LOG, 5),
            Map.entry(Blocks.DARK_OAK_LEAVES, 5),


            Map.entry(Blocks.JUNGLE_LOG, 5),
            Map.entry(Blocks.JUNGLE_LEAVES, 5),


            Map.entry(Blocks.MANGROVE_LOG, 5),
            Map.entry(Blocks.MANGROVE_LEAVES, 5),


            Map.entry(Blocks.ACACIA_LOG, 7),
            Map.entry(Blocks.ACACIA_LEAVES, 7),


            Map.entry(Blocks.CHERRY_LOG, 7),
            Map.entry(Blocks.CHERRY_LEAVES, 7),

            Map.entry(modBlock("cobblemon", "apricorn_log"), 15),
            Map.entry(modBlock("cobblemon", "apricorn_leaves"), 15),

            Map.entry(modBlock("cobblemon", "saccharine_log"), 15),
            Map.entry(modBlock("cobblemon", "saccharine_leaves"), 15)
    );




    public static final Map<Block, Integer> EXCAVATION = Map.ofEntries(

            Map.entry(Blocks.CLAY, 1),
            Map.entry(Blocks.COARSE_DIRT, 1),
            Map.entry(Blocks.DIRT, 1),
            Map.entry(Blocks.DIRT_PATH, 1),
            Map.entry(Blocks.SAND, 1),
            Map.entry(Blocks.SNOW, 1),

            Map.entry(Blocks.SNOW_BLOCK, 2),

            Map.entry(Blocks.RED_SAND, 3),
            Map.entry(Blocks.SOUL_SOIL, 3),

            Map.entry(Blocks.GRAVEL, 5),
            Map.entry(Blocks.MUD, 5),
            Map.entry(Blocks.MUDDY_MANGROVE_ROOTS, 5),
            Map.entry(Blocks.PODZOL, 5),
            Map.entry(Blocks.SOUL_SAND, 5),

            Map.entry(Blocks.SUSPICIOUS_GRAVEL, 50),
            Map.entry(Blocks.SUSPICIOUS_SAND, 50)
    );


    public static final Map<EntityType<?>, Integer> HUNTER = Map.ofEntries(




            Map.entry(EntityType.ALLAY, 1),
            Map.entry(EntityType.ARMADILLO, 1),
            Map.entry(EntityType.AXOLOTL, 1),
            Map.entry(EntityType.BAT, 1),
            Map.entry(EntityType.CAT, 1),
            Map.entry(EntityType.CHICKEN, 1),
            Map.entry(EntityType.COD, 1),
            Map.entry(EntityType.FROG, 1),
            Map.entry(EntityType.OCELOT, 1),
            Map.entry(EntityType.PARROT, 1),
            Map.entry(EntityType.RABBIT, 1),
            Map.entry(EntityType.SALMON, 1),
            Map.entry(EntityType.SQUID, 1),
            Map.entry(EntityType.TADPOLE, 1),
            Map.entry(EntityType.TROPICAL_FISH, 1),
            Map.entry(EntityType.DOLPHIN, 1),
            Map.entry(EntityType.PUFFERFISH, 1),
            Map.entry(EntityType.ENDERMITE, 1),




            Map.entry(EntityType.CAMEL, 3),
            Map.entry(EntityType.COW, 3),
            Map.entry(EntityType.DONKEY, 3),
            Map.entry(EntityType.HORSE, 3),
            Map.entry(EntityType.MULE, 3),
            Map.entry(EntityType.PIG, 3),
            Map.entry(EntityType.SHEEP, 3),
            Map.entry(EntityType.TURTLE, 3),
            Map.entry(EntityType.BEE, 3),
            Map.entry(EntityType.FOX, 3),
            Map.entry(EntityType.STRIDER, 3),
            Map.entry(EntityType.WANDERING_TRADER, 3),
            Map.entry(EntityType.VILLAGER, 3),
            Map.entry(EntityType.CAVE_SPIDER, 3),
            Map.entry(EntityType.SPIDER, 3),




            Map.entry(EntityType.CREEPER, 5),
            Map.entry(EntityType.HUSK, 5),
            Map.entry(EntityType.PIGLIN, 5),
            Map.entry(EntityType.POLAR_BEAR, 5),
            Map.entry(EntityType.ZOMBIE, 5),
            Map.entry(EntityType.SKELETON, 5),
            Map.entry(EntityType.STRAY, 5),
            Map.entry(EntityType.SLIME, 5),
            Map.entry(EntityType.SILVERFISH, 5),
            Map.entry(EntityType.PILLAGER, 5),
            Map.entry(EntityType.EVOKER, 5),
            Map.entry(EntityType.VINDICATOR, 5),




            Map.entry(EntityType.BLAZE, 10),
            Map.entry(EntityType.BREEZE, 10),
            Map.entry(EntityType.MAGMA_CUBE, 10),
            Map.entry(EntityType.ZOMBIE_VILLAGER, 10),
            Map.entry(EntityType.ENDERMAN, 10),




            Map.entry(EntityType.PHANTOM, 25),
            Map.entry(EntityType.WITHER_SKELETON, 25),




            Map.entry(EntityType.ELDER_GUARDIAN, 250),
            Map.entry(EntityType.WITHER, 500),
            Map.entry(EntityType.RAVAGER, 750),
            Map.entry(EntityType.ENDER_DRAGON, 1000)
    );

    public static final Map<Holder<Potion>, Integer> BREWING = Map.ofEntries(




            Map.entry(Potions.SWIFTNESS, 25),
            Map.entry(Potions.POISON, 25),
            Map.entry(Potions.STRENGTH, 25),




            Map.entry(Potions.HEALING, 75),
            Map.entry(Potions.REGENERATION, 75),
            Map.entry(Potions.FIRE_RESISTANCE, 75),
            Map.entry(Potions.SLOWNESS, 75),
            Map.entry(Potions.WEAKNESS, 75),




            Map.entry(Potions.LEAPING, 150),
            Map.entry(Potions.SLOW_FALLING, 150)
    );

    public static final Set<Block> BUILDER_BLACKLIST = Set.of(
            Blocks.BEACON,
            Blocks.DRAGON_EGG,
            Blocks.ANCIENT_DEBRIS,
            Blocks.DIAMOND_BLOCK,
            Blocks.EMERALD_BLOCK,
            Blocks.IRON_BLOCK,
            Blocks.GOLD_BLOCK,
            Blocks.COAL_BLOCK,
            Blocks.LAPIS_BLOCK,
            Blocks.REDSTONE_BLOCK,
            Blocks.COPPER_BLOCK,
            Blocks.NETHERITE_BLOCK,
            Blocks.SUGAR_CANE,
            Blocks.WHEAT,
            Blocks.POTATOES,
            Blocks.CARROTS,
            Blocks.BEETROOTS,
            Blocks.MELON_STEM,
            Blocks.PUMPKIN_STEM,
            Blocks.COCOA,
            Blocks.TORCHFLOWER_CROP,
            Blocks.PITCHER_CROP,
            Blocks.DIAMOND_ORE,
            Blocks.EMERALD_ORE,
            Blocks.LAPIS_ORE,
            Blocks.REDSTONE_ORE,
            Blocks.GOLD_ORE,
            Blocks.IRON_ORE,
            Blocks.COAL_ORE,
            Blocks.COPPER_ORE,
            Blocks.DEEPSLATE_DIAMOND_ORE,
            Blocks.DEEPSLATE_EMERALD_ORE,
            Blocks.DEEPSLATE_LAPIS_ORE,
            Blocks.DEEPSLATE_REDSTONE_ORE,
            Blocks.DEEPSLATE_GOLD_ORE,
            Blocks.DEEPSLATE_IRON_ORE,
            Blocks.DEEPSLATE_COAL_ORE,
            Blocks.DEEPSLATE_COPPER_ORE

    );

    public static final Map<Block, Double> FARMING = Map.ofEntries(




            Map.entry(Blocks.WHEAT, 2.0),
            Map.entry(Blocks.CARROTS, 2.0),
            Map.entry(Blocks.POTATOES, 2.0),
            Map.entry(Blocks.BEETROOTS, 2.0),

            Map.entry(Blocks.MELON, 3.0),
            Map.entry(Blocks.PUMPKIN, 3.0),

            Map.entry(Blocks.NETHER_WART, 5.0),
            Map.entry(Blocks.COCOA, 5.0),




            Map.entry(Blocks.BAMBOO, 0.1),
            Map.entry(Blocks.SUGAR_CANE, 2.0),
            Map.entry(Blocks.CACTUS, 3.0),

            Map.entry(Blocks.SWEET_BERRY_BUSH, 1.0),
            Map.entry(Blocks.CAVE_VINES, 1.0),

            Map.entry(Blocks.CHORUS_PLANT, 25.0),

            Map.entry(Blocks.KELP, 1.0),
            Map.entry(Blocks.SEA_PICKLE, 1.0),

            Map.entry(Blocks.MOSS_BLOCK, 10.0),
            Map.entry(Blocks.TORCHFLOWER_CROP, 10.0),
            Map.entry(Blocks.PITCHER_CROP, 10.0)
    );



    private static Block modBlock(String mod, String id) {

        Block block = BuiltInRegistries.BLOCK.get(
                ResourceLocation.fromNamespaceAndPath(mod, id)
        );

        if (block == net.minecraft.world.level.block.Blocks.AIR) {
            System.out.println("[Skills] Missing block: " + mod + ":" + id);
        }

        return block;
    }
}