package org.howie.pixity.moderation.neoforge.skills;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class KeepHelper {

    public static void duplicateDrops(ServerPlayer player, Block block, net.minecraft.world.level.block.state.BlockState state, net.minecraft.core.BlockPos pos) {

        List<ItemStack> drops = Block.getDrops(
                state,
                (ServerLevel) player.level(),
                pos,
                null
        );

        for (ItemStack drop : drops) {
            player.addItem(drop.copy());
        }
    }

    public static void duplicateCropOnly(ServerPlayer player, Block block, BlockPos pos) {

        ItemStack drop = ItemStack.EMPTY;


        if (block == net.minecraft.world.level.block.Blocks.WHEAT) {
            drop = new ItemStack(net.minecraft.world.item.Items.WHEAT);
        }


        else if (block == net.minecraft.world.level.block.Blocks.CARROTS) {
            drop = new ItemStack(net.minecraft.world.item.Items.CARROT);
        }


        else if (block == net.minecraft.world.level.block.Blocks.POTATOES) {
            drop = new ItemStack(net.minecraft.world.item.Items.POTATO);
        }


        else if (block == net.minecraft.world.level.block.Blocks.BEETROOTS) {
            drop = new ItemStack(net.minecraft.world.item.Items.BEETROOT);
        }


        else if (block == net.minecraft.world.level.block.Blocks.MELON) {
            drop = new ItemStack(net.minecraft.world.item.Items.MELON_SLICE);
        }


        else if (block == net.minecraft.world.level.block.Blocks.PUMPKIN) {
            drop = new ItemStack(net.minecraft.world.item.Items.PUMPKIN);
        }

        if (!drop.isEmpty()) {
            Block.popResource(player.level(), pos, drop);
        }
    }
}