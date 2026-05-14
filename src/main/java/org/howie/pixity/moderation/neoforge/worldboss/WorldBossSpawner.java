package org.howie.pixity.moderation.neoforge.worldboss;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;

import com.cobblemon.mod.common.pokemon.IVs;
import com.cobblemon.mod.common.pokemon.Pokemon;

import net.minecraft.core.BlockPos;

import net.minecraft.network.chat.Component;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;

import kotlin.Unit;

public class WorldBossSpawner {





    public static PokemonEntity spawn(
            ServerPlayer player,
            WorldBossDefinition boss
    ) {

        ServerLevel level =
                player.serverLevel();





        var species =
                PokemonSpecies.getByIdentifier(

                        ResourceLocation.parse(
                                "cobblemon:"
                                        + boss.species
                        )
                );

        if (species == null) {

            System.out.println(
                    "[WorldBoss] Failed to find species: "
                            + boss.species
            );

            return null;
        }





        Pokemon pokemon =
                new Pokemon();

        System.out.println(
                "Pokemon methods:"
        );

        for (var m : pokemon.getClass().getMethods()) {

            if (
                    m.getName()
                            .toLowerCase()
                            .contains("spawn")

                            ||

                            m.getName()
                                    .toLowerCase()
                                    .contains("send")
            ) {

                System.out.println(
                        m.toString()
                );
            }
        }

        pokemon.setSpecies(species);





        pokemon.setLevel(100);





        try {

            pokemon.initializeMoveset(true);

        } catch (Exception ignored) {
        }





        try {

            var ivs =
                    pokemon.getIvs();

            ivs.set(
                    com.cobblemon.mod.common.api.pokemon.stats.Stats.HP,
                    31
            );

            ivs.set(
                    com.cobblemon.mod.common.api.pokemon.stats.Stats.ATTACK,
                    31
            );

            ivs.set(
                    com.cobblemon.mod.common.api.pokemon.stats.Stats.DEFENCE,
                    31
            );

            ivs.set(
                    com.cobblemon.mod.common.api.pokemon.stats.Stats.SPECIAL_ATTACK,
                    31
            );

            ivs.set(
                    com.cobblemon.mod.common.api.pokemon.stats.Stats.SPECIAL_DEFENCE,
                    31
            );

            ivs.set(
                    com.cobblemon.mod.common.api.pokemon.stats.Stats.SPEED,
                    31
            );

        } catch (Exception ignored) {
        }





        try {

            var evs =
                    pokemon.getEvs();

            evs.set(
                    com.cobblemon.mod.common.api.pokemon.stats.Stats.HP,
                    252
            );

            evs.set(
                    com.cobblemon.mod.common.api.pokemon.stats.Stats.ATTACK,
                    252
            );

            evs.set(
                    com.cobblemon.mod.common.api.pokemon.stats.Stats.DEFENCE,
                    252
            );

            evs.set(
                    com.cobblemon.mod.common.api.pokemon.stats.Stats.SPECIAL_ATTACK,
                    252
            );

            evs.set(
                    com.cobblemon.mod.common.api.pokemon.stats.Stats.SPECIAL_DEFENCE,
                    252
            );

            evs.set(
                    com.cobblemon.mod.common.api.pokemon.stats.Stats.SPEED,
                    252
            );

        } catch (Exception ignored) {
        }





        try {

            pokemon.heal();

        } catch (Exception ignored) {
        }

        System.out.println(
                "[WorldBoss] Created Pokemon: "
                        + species.getName()
        );





        try {

            pokemon.setScaleModifier(
                    boss.scale
            );

        } catch (Exception ignored) {
        }









        ServerLevel spawnLevel =
                WorldBossSpawnPoint.getLevel(
                        player
                );

        BlockPos spawnPos =
                WorldBossSpawnPoint.getPosition();





        PokemonEntity entity =
                pokemon.sendOut(

                        spawnLevel,

                        new net.minecraft.world.phys.Vec3(

                                spawnPos.getX() + 0.5,

                                spawnPos.getY() + 1,

                                spawnPos.getZ() + 0.5
                        ),

                        null,

                        spawned -> {

                            return kotlin.Unit.INSTANCE;
                        }
                );

        if (entity == null) {

            System.out.println(
                    "[WorldBoss] Failed to spawn entity."
            );

            return null;
        }






       entity.setGlowingTag(true);





        entity.setNoAi(true);







        entity.setCustomName(

                Component.literal(
                        "👑 "
                                + boss.display
                )
        );

        entity.setCustomNameVisible(true);





         entity.getPersistentData()
                .putBoolean(
                        "pixity_worldboss",
                        true
                );





        entity.setPersistenceRequired();






        return entity;
    }

    public static PokemonEntity spawnDirect(

            ServerLevel level,

            BlockPos spawnPos,

            WorldBossDefinition boss
    ) {

        try {

            var species =
                    PokemonSpecies.getByIdentifier(

                            ResourceLocation.parse(
                                    "cobblemon:"
                                            + boss.species
                            )
                    );

            if (species == null) {
                return null;
            }

            Pokemon pokemon =
                    new Pokemon();

            pokemon.setSpecies(species);

            pokemon.setLevel(100);

            try {

                pokemon.initializeMoveset(true);

            } catch (Exception ignored) {
            }

            try {

                pokemon.setScaleModifier(
                        boss.scale
                );

            } catch (Exception ignored) {
            }

            try {

                pokemon.heal();

            } catch (Exception ignored) {
            }

            PokemonEntity entity =
                    pokemon.sendOut(

                            level,

                            new net.minecraft.world.phys.Vec3(

                                    spawnPos.getX() + 0.5,

                                    spawnPos.getY() + 1,

                                    spawnPos.getZ() + 0.5
                            ),

                            null,

                            spawned -> {

                                return kotlin.Unit.INSTANCE;
                            }
                    );

            if (entity == null) {
                return null;
            }

            entity.setNoAi(true);

            entity.setPersistenceRequired();

            entity.setGlowingTag(true);

            entity.setCustomName(

                    Component.literal(
                            "👑 " + boss.display
                    )
            );

            entity.setCustomNameVisible(true);

            entity.getPersistentData()
                    .putBoolean(
                            "pixity_worldboss",
                            true
                    );

            entity.getPersistentData()
                    .putBoolean(
                            "uncatchable",
                            true
                    );

            return entity;

        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }
    }
}