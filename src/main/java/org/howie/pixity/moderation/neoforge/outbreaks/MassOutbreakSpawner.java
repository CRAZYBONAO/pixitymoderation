package org.howie.pixity.moderation.neoforge.outbreaks;

import com.cobblemon.mod.common.CobblemonEntities;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.Species;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;


import net.minecraft.core.BlockPos;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import net.minecraft.tags.FluidTags;

import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Random;

public class MassOutbreakSpawner {





    private static final Random RANDOM =
            new Random();





    public static void tick(
            MinecraftServer server
    ) {

        if (!MassOutbreakManager.isActive()) {
            return;
        }

        MassOutbreakDefinition outbreak =
                MassOutbreakManager.getCurrent();

        if (outbreak == null) {
            return;
        }

        for (ServerPlayer player
                : server.getPlayerList().getPlayers()) {

            attemptSpawns(
                    player,
                    outbreak
            );
        }
    }





    private static void attemptSpawns(
            ServerPlayer player,
            MassOutbreakDefinition outbreak
    ) {

        ServerLevel level =
                player.serverLevel();





        String biomeName =
                level.getBiome(
                                player.blockPosition()
                        )
                        .unwrapKey()
                        .map(key ->
                                key.location()
                                        .getPath()
                        )
                        .orElse("")
                        .toLowerCase();

        if (
                !biomeName.contains(
                        outbreak.biomeName
                                .toLowerCase()
                )
        ) {
            return;
        }





        int nearby =
                getNearbyOutbreaks(
                        player,
                        64
                );

        if (nearby >= 8) {
            return;
        }





        for (int i = 0; i < outbreak.tier.spawnAttempts; i++) {

            BlockPos pos =
                    randomNearbyPosition(
                            player.blockPosition()
                    );

            if (
                    !isValidSpawn(
                            level,
                            pos
                    )
            ) {
                continue;
            }

            spawnPokemon(
                    level,
                    pos,
                    outbreak
            );
        }
    }





    private static BlockPos randomNearbyPosition(
            BlockPos center
    ) {

        int x =
                center.getX()
                        + RANDOM.nextInt(32)
                        - 16;

        int z =
                center.getZ()
                        + RANDOM.nextInt(32)
                        - 16;

        return new BlockPos(
                x,
                center.getY(),
                z
        );
    }





    private static boolean isValidSpawn(
            ServerLevel level,
            BlockPos pos
    ) {

        BlockPos top =
                level.getHeightmapPos(
                        net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        pos
                );





        if (
                level.getFluidState(top)
                        .is(FluidTags.WATER)
        ) {
            return false;
        }

        return level.getBlockState(
                top.below()
        ).isSolid();
    }





    private static void spawnPokemon(
            ServerLevel level,
            BlockPos pos,
            MassOutbreakDefinition outbreak
    ) {





        var species =
                PokemonSpecies.INSTANCE.getByName(
                        outbreak.species
                );

        if (species == null) {
            return;
        }





        Pokemon pokemon =
                new Pokemon();

        pokemon.setSpecies(species);





        boolean alpha = false;

        if (
                outbreak.tier.alphaChance > 0
                        &&
                        RANDOM.nextInt(100)
                                < outbreak.tier.alphaChance
        ) {

            alpha = true;

            try {





                pokemon.setScaleModifier(2.0F);

            } catch (Exception ignored) {
            }
        }





        if (
                RANDOM.nextInt(
                        outbreak.tier.hiddenAbilityOdds
                ) == 0
        ) {





            for (int i = 0; i < 10; i++) {

                pokemon.rollAbility();

                try {

                    var ability =
                            pokemon.getAbility();

                    if (
                            ability != null
                                    &&
                                    ability.getPriority()
                                            != null
                                    &&
                                    ability.getPriority()
                                            .name()
                                            .equalsIgnoreCase("LOW")
                    ) {

                        break;
                    }

                } catch (Exception ignored) {
                }
            }
        }





        if (
                RANDOM.nextInt(
                        outbreak.tier.shinyOdds
                ) == 0
        ) {

            pokemon.setShiny(true);
        }





        PokemonEntity entity =
                new PokemonEntity(
                        level,
                        pokemon,
                        CobblemonEntities.POKEMON
                );

        BlockPos top =
                level.getHeightmapPos(
                        net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        pos
                );

        entity.setPos(
                top.getX() + 0.5,
                top.getY() + 1,
                top.getZ() + 0.5
        );





        entity.getPersistentData()
                .putBoolean(
                        "pixity_outbreak",
                        true
                );





        if (alpha) {

            entity.getPersistentData()
                    .putBoolean(
                            "pixity_alpha",
                            true
                    );

            entity.setCustomName(

                    net.minecraft.network.chat.Component.literal(
                            "⭐ Alpha "
                                    + outbreak.getDisplayName()
                    )
            );

            entity.setCustomNameVisible(true);
        }

        level.addFreshEntity(entity);
    }





    private static int getNearbyOutbreaks(
            ServerPlayer player,
            double radius
    ) {

        List<PokemonEntity> list =
                player.level()
                        .getEntitiesOfClass(
                                PokemonEntity.class,

                                player.getBoundingBox()
                                        .inflate(radius)
                        );

        int count = 0;

        for (PokemonEntity entity : list) {

            if (
                    entity.getPersistentData()
                            .getBoolean(
                                    "pixity_outbreak"
                            )
            ) {

                count++;
            }
        }

        return count;
    }
}