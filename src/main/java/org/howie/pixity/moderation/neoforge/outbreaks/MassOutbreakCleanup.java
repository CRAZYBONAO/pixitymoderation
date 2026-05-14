package org.howie.pixity.moderation.neoforge.outbreaks;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;

import net.minecraft.server.level.ServerLevel;

import java.util.List;

public class MassOutbreakCleanup {





    public static void cleanup(
            ServerLevel level
    ) {

        List<PokemonEntity> pokemon =

                level.getEntitiesOfClass(
                        PokemonEntity.class,

                        level.getWorldBorder()
                                .getCollisionShape()
                                .bounds()
                );

        int removed = 0;

        for (PokemonEntity entity : pokemon) {





            if (
                    !entity.getPersistentData()
                            .getBoolean(
                                    "pixity_outbreak"
                            )
            ) {
                continue;
            }





            entity.discard();

            removed++;
        }





        System.out.println(
                "[MassOutbreak] Cleaned up "
                        + removed
                        + " outbreak Pokémon."
        );
    }
}