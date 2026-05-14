package org.howie.pixity.moderation.neoforge.milestones.listeners;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.server.level.ServerPlayer;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import net.neoforged.neoforge.event.level.BlockEvent;

import org.howie.pixity.moderation.neoforge.stats.PlayerStatsDatabase;

@EventBusSubscriber
public class MiningMilestoneListener {





    @SubscribeEvent
    public static void breakBlock(
            BlockEvent.BreakEvent event
    ) {





        if (!(event.getPlayer() instanceof ServerPlayer sp)) {
            return;
        }





        Block block =
                event.getState().getBlock();

        ResourceLocation id =
                BuiltInRegistries.BLOCK.getKey(block);

        if (id == null) {
            return;
        }

        String key =
                id.toString();





        PlayerStatsDatabase.add(
                sp.getUUID(),
                "blocks_mined",
                1
        );





        if (
                block == Blocks.COAL_ORE ||
                        block == Blocks.DEEPSLATE_COAL_ORE
        ) {

            addOre(
                    sp,
                    "coal_ore_mined"
            );

            return;
        }

        if (
                block == Blocks.COPPER_ORE ||
                        block == Blocks.DEEPSLATE_COPPER_ORE
        ) {

            addOre(
                    sp,
                    "copper_ore_mined"
            );

            return;
        }

        if (
                block == Blocks.IRON_ORE ||
                        block == Blocks.DEEPSLATE_IRON_ORE
        ) {

            addOre(
                    sp,
                    "iron_ore_mined"
            );

            return;
        }

        if (
                block == Blocks.GOLD_ORE ||
                        block == Blocks.DEEPSLATE_GOLD_ORE
        ) {

            addOre(
                    sp,
                    "gold_ore_mined"
            );

            return;
        }

        if (
                block == Blocks.REDSTONE_ORE ||
                        block == Blocks.DEEPSLATE_REDSTONE_ORE
        ) {

            addOre(
                    sp,
                    "redstone_ore_mined"
            );

            return;
        }

        if (
                block == Blocks.LAPIS_ORE ||
                        block == Blocks.DEEPSLATE_LAPIS_ORE
        ) {

            addOre(
                    sp,
                    "lapis_ore_mined"
            );

            return;
        }

        if (
                block == Blocks.DIAMOND_ORE ||
                        block == Blocks.DEEPSLATE_DIAMOND_ORE
        ) {

            addOre(
                    sp,
                    "diamond_ore_mined"
            );

            return;
        }

        if (
                block == Blocks.EMERALD_ORE ||
                        block == Blocks.DEEPSLATE_EMERALD_ORE
        ) {

            addOre(
                    sp,
                    "emerald_ore_mined"
            );

            return;
        }

        if (
                block == Blocks.NETHER_QUARTZ_ORE
        ) {

            addOre(
                    sp,
                    "quartz_ore_mined"
            );

            PlayerStatsDatabase.add(
                    sp.getUUID(),
                    "nether_ores_mined",
                    1
            );

            return;
        }

        if (
                block == Blocks.NETHER_GOLD_ORE
        ) {

            addOre(
                    sp,
                    "nether_gold_ore_mined"
            );

            PlayerStatsDatabase.add(
                    sp.getUUID(),
                    "nether_ores_mined",
                    1
            );

            return;
        }

        if (
                block == Blocks.ANCIENT_DEBRIS
        ) {

            addOre(
                    sp,
                    "ancient_debris_mined"
            );

            PlayerStatsDatabase.add(
                    sp.getUUID(),
                    "nether_ores_mined",
                    1
            );

            return;
        }





        if (
                key.contains("deepslate")
        ) {

            PlayerStatsDatabase.add(
                    sp.getUUID(),
                    "deepslate_ores_mined",
                    1
            );
        }





        if (
                key.contains("dawn_stone_ore")
        ) {

            addCobblemonOre(
                    sp,
                    "dawn_stone_ore_mined"
            );

            return;
        }

        if (
                key.contains("dusk_stone_ore")
        ) {

            addCobblemonOre(
                    sp,
                    "dusk_stone_ore_mined"
            );

            return;
        }

        if (
                key.contains("fire_stone_ore")
        ) {

            addCobblemonOre(
                    sp,
                    "fire_stone_ore_mined"
            );

            return;
        }

        if (
                key.contains("ice_stone_ore")
        ) {

            addCobblemonOre(
                    sp,
                    "ice_stone_ore_mined"
            );

            return;
        }

        if (
                key.contains("leaf_stone_ore")
        ) {

            addCobblemonOre(
                    sp,
                    "leaf_stone_ore_mined"
            );

            return;
        }

        if (
                key.contains("moon_stone_ore")
        ) {

            addCobblemonOre(
                    sp,
                    "moon_stone_ore_mined"
            );

            return;
        }

        if (
                key.contains("shiny_stone_ore")
        ) {

            addCobblemonOre(
                    sp,
                    "shiny_stone_ore_mined"
            );

            return;
        }

        if (
                key.contains("sun_stone_ore")
        ) {

            addCobblemonOre(
                    sp,
                    "sun_stone_ore_mined"
            );

            return;
        }

        if (
                key.contains("thunder_stone_ore")
        ) {

            addCobblemonOre(
                    sp,
                    "thunder_stone_ore_mined"
            );

            return;
        }

        if (
                key.contains("water_stone_ore")
        ) {

            addCobblemonOre(
                    sp,
                    "water_stone_ore_mined"
            );

            return;
        }
    }





    private static void addOre(
            ServerPlayer player,
            String column
    ) {

        PlayerStatsDatabase.add(
                player.getUUID(),
                column,
                1
        );

        PlayerStatsDatabase.add(
                player.getUUID(),
                "total_ores_mined",
                1
        );

        Level level =
                player.level();

        if (
                level.dimension() == Level.NETHER
        ) {

            PlayerStatsDatabase.add(
                    player.getUUID(),
                    "nether_ores_mined",
                    1
            );
        }
        else {

            PlayerStatsDatabase.add(
                    player.getUUID(),
                    "overworld_ores_mined",
                    1
            );
        }
    }





    private static void addCobblemonOre(
            ServerPlayer player,
            String column
    ) {

        addOre(
                player,
                column
        );

        PlayerStatsDatabase.add(
                player.getUUID(),
                "cobblemon_ores_mined",
                1
        );
    }
}