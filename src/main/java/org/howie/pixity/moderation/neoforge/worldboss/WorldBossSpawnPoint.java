package org.howie.pixity.moderation.neoforge.worldboss;

import net.minecraft.core.BlockPos;

import net.minecraft.resources.ResourceKey;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import net.minecraft.world.level.Level;

public class WorldBossSpawnPoint {





    private static ResourceKey<Level> dimension =
            Level.OVERWORLD;

    private static BlockPos position =
            new BlockPos(
                    0,
                    100,
                    0
            );

    public static void set(
            ServerPlayer player
    ) {

        dimension =
                player.serverLevel()
                        .dimension();

        position =
                player.blockPosition();

        WorldBossSpawnStorage.save(

                player.server,

                dimension,

                position
        );
    }





    public static ServerLevel getLevel(
            ServerPlayer player
    ) {

        return player.server
                .getLevel(
                        dimension
                );
    }





    public static BlockPos getPosition() {
        return position;
    }





    public static void load(

            ResourceKey<Level> dim,

            BlockPos pos
    ) {

        dimension = dim;

        position = pos;
    }
}