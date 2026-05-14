package org.howie.pixity.moderation.neoforge.miningevents;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

public class MiningEventListener {





    @SubscribeEvent
    public static void breakBlock(
            BlockEvent.BreakEvent event
    ) {





        if (
                !(event.getPlayer()
                        instanceof ServerPlayer sp)
        ) {
            return;
        }





        if (!MiningEventManager.isActive()) {
            return;
        }





        BlockPos pos =
                event.getPos();

        Block block =
                event.getState()
                        .getBlock();





        if (
                block == Blocks.COAL_ORE
                        ||
                        block == Blocks.DEEPSLATE_COAL_ORE
        ) {

            MiningEventManager.handleMine(
                    sp,
                    "coal_ore_mined"
            );

            return;
        }





        if (
                block == Blocks.COPPER_ORE
                        ||
                        block == Blocks.DEEPSLATE_COPPER_ORE
        ) {

            MiningEventManager.handleMine(
                    sp,
                    "copper_ore_mined"
            );

            return;
        }





        if (
                block == Blocks.IRON_ORE
                        ||
                        block == Blocks.DEEPSLATE_IRON_ORE
        ) {

            MiningEventManager.handleMine(
                    sp,
                    "iron_ore_mined"
            );

            return;
        }





        if (
                block == Blocks.GOLD_ORE
                        ||
                        block == Blocks.DEEPSLATE_GOLD_ORE
        ) {

            MiningEventManager.handleMine(
                    sp,
                    "gold_ore_mined"
            );

            return;
        }





        if (
                block == Blocks.REDSTONE_ORE
                        ||
                        block == Blocks.DEEPSLATE_REDSTONE_ORE
        ) {

            MiningEventManager.handleMine(
                    sp,
                    "redstone_ore_mined"
            );

            return;
        }





        if (
                block == Blocks.LAPIS_ORE
                        ||
                        block == Blocks.DEEPSLATE_LAPIS_ORE
        ) {

            MiningEventManager.handleMine(
                    sp,
                    "lapis_ore_mined"
            );

            return;
        }





        if (
                block == Blocks.DIAMOND_ORE
                        ||
                        block == Blocks.DEEPSLATE_DIAMOND_ORE
        ) {

            MiningEventManager.handleMine(
                    sp,
                    "diamond_ore_mined"
            );
        }
    }
}