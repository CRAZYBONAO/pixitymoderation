package org.howie.pixity.moderation.neoforge.milestones.listeners;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.server.level.ServerPlayer;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SugarCaneBlock;

import net.minecraft.world.level.block.state.BlockState;

import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import net.neoforged.neoforge.event.level.BlockEvent;

import org.howie.pixity.moderation.neoforge.stats.PlayerStatsDatabase;

@EventBusSubscriber
public class FarmingMilestoneListener {





    @SubscribeEvent
    public static void breakBlock(
            BlockEvent.BreakEvent event
    ) {





        if (!(event.getPlayer() instanceof ServerPlayer sp)) {
            return;
        }





        BlockState state =
                event.getState();

        Block block =
                state.getBlock();

        ResourceLocation id =
                BuiltInRegistries.BLOCK.getKey(block);

        if (id == null) {
            return;
        }

        String key =
                id.toString();





        if (block == Blocks.WHEAT) {

            if (
                    !((CropBlock) block)
                            .isMaxAge(state)
            ) {
                return;
            }

            addCrop(
                    sp,
                    "wheat_crops_harvested"
            );

            return;
        }





        if (block == Blocks.CARROTS) {

            if (
                    !((CropBlock) block)
                            .isMaxAge(state)
            ) {
                return;
            }

            addCrop(
                    sp,
                    "carrot_crops_harvested"
            );

            return;
        }





        if (block == Blocks.POTATOES) {

            if (
                    !((CropBlock) block)
                            .isMaxAge(state)
            ) {
                return;
            }

            addCrop(
                    sp,
                    "potato_crops_harvested"
            );

            return;
        }





        if (block == Blocks.BEETROOTS) {

            if (
                    !((CropBlock) block)
                            .isMaxAge(state)
            ) {
                return;
            }

            addCrop(
                    sp,
                    "beetroot_crops_harvested"
            );

            return;
        }





        if (block == Blocks.COCOA) {

            int age =
                    state.getValue(
                            CocoaBlock.AGE
                    );

            if (age < 2) {
                return;
            }

            addCrop(
                    sp,
                    "cocoa_crops_harvested"
            );

            return;
        }





        if (block == Blocks.MELON) {

            boolean valid = false;

            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {

                    BlockState nearby =
                            event.getLevel().getBlockState(
                                    event.getPos().offset(x, 0, z)
                            );

                    if (
                            nearby.getBlock() == Blocks.MELON_STEM ||
                                    nearby.getBlock() == Blocks.ATTACHED_MELON_STEM
                    ) {
                        valid = true;
                        break;
                    }
                }
            }

            if (!valid) {
                return;
            }

            addCrop(
                    sp,
                    "melon_crops_harvested"
            );

            return;
        }





        if (block == Blocks.PUMPKIN) {

            boolean valid = false;

            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {

                    BlockState nearby =
                            event.getLevel().getBlockState(
                                    event.getPos().offset(x, 0, z)
                            );

                    if (
                            nearby.getBlock() == Blocks.PUMPKIN_STEM ||
                                    nearby.getBlock() == Blocks.ATTACHED_PUMPKIN_STEM
                    ) {
                        valid = true;
                        break;
                    }
                }
            }

            if (!valid) {
                return;
            }

            addCrop(
                    sp,
                    "pumpkin_crops_harvested"
            );

            return;
        }





        if (block instanceof SugarCaneBlock) {

            int count = 1;

            int y = 1;

            while (true) {

                BlockState above =
                        event.getLevel().getBlockState(
                                event.getPos().above(y)
                        );

                if (
                        !(above.getBlock()
                                instanceof SugarCaneBlock)
                ) {
                    break;
                }

                count++;
                y++;
            }

            PlayerStatsDatabase.add(
                    sp.getUUID(),
                    "sugarcane_crops_harvested",
                    count
            );

            PlayerStatsDatabase.add(
                    sp.getUUID(),
                    "total_crops_harvested",
                    count
            );

            return;
        }





        if (block instanceof BambooStalkBlock) {

            int count = 1;

            int y = 1;

            while (true) {

                BlockState above =
                        event.getLevel().getBlockState(
                                event.getPos().above(y)
                        );

                if (
                        !(above.getBlock()
                                instanceof BambooStalkBlock)
                ) {
                    break;
                }

                count++;
                y++;
            }

            PlayerStatsDatabase.add(
                    sp.getUUID(),
                    "bamboo_crop_harvested",
                    count
            );

            PlayerStatsDatabase.add(
                    sp.getUUID(),
                    "total_crops_harvested",
                    count
            );

            return;
        }





        if (
                key.contains("red_apricorn")
        ) {

            addCrop(
                    sp,
                    "red_apricorn_harvested"
            );

            return;
        }

        if (
                key.contains("blue_apricorn")
        ) {

            addCrop(
                    sp,
                    "blue_apricorn_harvested"
            );

            return;
        }

        if (
                key.contains("yellow_apricorn")
        ) {

            addCrop(
                    sp,
                    "yellow_apricorn_harvested"
            );

            return;
        }

        if (
                key.contains("green_apricorn")
        ) {

            addCrop(
                    sp,
                    "green_apricorn_harvested"
            );

            return;
        }

        if (
                key.contains("pink_apricorn")
        ) {

            addCrop(
                    sp,
                    "pink_apricorn_harvested"
            );

            return;
        }

        if (
                key.contains("black_apricorn")
        ) {

            addCrop(
                    sp,
                    "black_apricorn_harvested"
            );

            return;
        }

        if (
                key.contains("white_apricorn")
        ) {

            addCrop(
                    sp,
                    "white_apricorn_harvested"
            );

            return;
        }
    }





    private static void addCrop(
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
                "total_crops_harvested",
                1
        );
    }
}