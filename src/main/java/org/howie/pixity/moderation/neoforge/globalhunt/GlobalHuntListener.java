package org.howie.pixity.moderation.neoforge.globalhunt;

import com.cobblemon.mod.common.api.events.CobblemonEvents;

import net.minecraft.server.level.ServerPlayer;
import org.howie.pixity.moderation.neoforge.outbreaks.MassOutbreakDefinition;
import org.howie.pixity.moderation.neoforge.outbreaks.MassOutbreakManager;
import org.howie.pixity.moderation.neoforge.outbreaks.OutbreakChainManager;

public class GlobalHuntListener {

    public static void register() {

        CobblemonEvents.POKEMON_CAPTURED.subscribe(event -> {

            if (
                    !(event.getPlayer()
                            instanceof ServerPlayer player)
            ) {
                return;
            }

            GlobalHuntDefinition hunt =
                    GlobalHuntManager.getCurrent();

            if (hunt == null) {
                return;
            }

            String species =
                    event.getPokemon()
                            .getSpecies()
                            .getName()
                            .toLowerCase();





            MassOutbreakDefinition outbreak =
                    MassOutbreakManager.getCurrent();

            if (
                    outbreak != null
                            &&
                            species.equalsIgnoreCase(
                                    outbreak.species
                            )
            ) {

                OutbreakChainManager.onCatch(
                        player,
                        species
                );
            }

            switch (hunt.type) {





                case SPECIES -> {

                    if (
                            species.equalsIgnoreCase(
                                    hunt.target
                            )
                    ) {

                        GlobalHuntManager.addProgress(
                                player
                        );
                    }
                }





                case SHINY -> {

                    if (
                            event.getPokemon()
                                    .getShiny()
                    ) {

                        GlobalHuntManager.addProgress(
                                player
                        );
                    }
                }

                default -> {
                }
            }
        });
    }
}