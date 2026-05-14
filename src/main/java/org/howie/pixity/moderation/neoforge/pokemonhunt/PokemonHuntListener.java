package org.howie.pixity.moderation.neoforge.pokemonhunt;

import com.cobblemon.mod.common.api.events.CobblemonEvents;

import net.minecraft.server.level.ServerPlayer;

public class PokemonHuntListener {

    public static void register() {

        CobblemonEvents.POKEMON_CAPTURED.subscribe(event -> {

            if (
                    !(event.getPlayer()
                            instanceof ServerPlayer player)
            ) {
                return;
            }

            PokemonHuntDefinition hunt =
                    PokemonHuntManager.getCurrent();

            if (hunt == null) {
                return;
            }

            String species =
                    event.getPokemon()
                            .getSpecies()
                            .getName()
                            .toLowerCase();

            switch (hunt.type) {





                case SPECIES -> {

                    if (
                            species.equalsIgnoreCase(
                                    hunt.target
                            )
                    ) {

                        PokemonHuntManager.handleProgress(
                                player
                        );
                    }
                }





                case SHINY -> {

                    if (
                            event.getPokemon()
                                    .getShiny()
                    ) {

                        PokemonHuntManager.handleProgress(
                                player
                        );
                    }
                }





                case HIDDEN_ABILITY -> {

                    String ability =
                            event.getPokemon()
                                    .getAbility()
                                    .getName();

                    if (
                            ability != null
                                    &&
                                    !ability.equalsIgnoreCase("blaze")
                    ) {

                        PokemonHuntManager.handleProgress(
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