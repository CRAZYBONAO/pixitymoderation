package org.howie.pixity.moderation.neoforge.pokemon;

import net.minecraft.server.level.ServerPlayer;

import com.cobblemon.mod.common.api.events.CobblemonEvents;

public class PokemonCatchListener {

    public static void register() {

        CobblemonEvents.POKEMON_CAPTURED.subscribe(event -> {

            if (!(event.getPlayer() instanceof ServerPlayer player)) return;

            String species = event.getPokemon().getSpecies().getName();

            boolean dirty = false;




            boolean isNew = PokedexDatabase.addCatch(player.getUUID(), species);

            if (isNew) {
                int count = PokedexDatabase.getCount(player.getUUID());

                player.sendSystemMessage(
                        org.howie.pixity.moderation.chat.CachedText.of(
                                "<green>New Pokémon added! (" + count + ")</green>"
                        )
                );

                PokedexRewardManager.checkRewards(player, count);

                HologramManager.markDirty("normal");
                dirty = true;
            }




            if (event.getPokemon().getShiny()) {

                boolean newShiny = PokedexDatabase.addShiny(player.getUUID(), species);

                if (newShiny) {
                    int shinyCount = PokedexDatabase.getShinyCount(player.getUUID());

                    player.sendSystemMessage(
                            org.howie.pixity.moderation.chat.CachedText.of(
                                    "<light_purple>✨ Shiny added! (" + shinyCount + ")</light_purple>"
                            )
                    );

                    HologramManager.markDirty("shiny");
                    dirty = true;
                }
            }




            if (dirty) {
                HologramManager.queueRefresh(player.server);
            }
        });
    }
}