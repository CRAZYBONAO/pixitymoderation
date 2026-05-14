package org.howie.pixity.moderation.neoforge.pokemon;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;

import java.util.UUID;

public class PokemonRecieveListener {

    @SubscribeEvent
    public void onSpawn(EntityJoinLevelEvent e) {

        if (!(e.getLevel() instanceof ServerLevel level)) return;
        if (!(e.getEntity() instanceof PokemonEntity entity)) return;


        if (entity.getPersistentData().getBoolean("pixity_dex_checked")) return;
        entity.getPersistentData().putBoolean("pixity_dex_checked", true);

        level.getServer().tell(new net.minecraft.server.TickTask(1, () -> {

            try {
                var pokemon = entity.getPokemon();

                if (pokemon == null) return;


                ServerPlayer nearest = null;
                double bestDist = 10 * 10;

                for (ServerPlayer p : level.getServer().getPlayerList().getPlayers()) {

                    double dist = p.distanceToSqr(entity);

                    if (dist < bestDist) {
                        bestDist = dist;
                        nearest = p;
                    }
                }

                if (nearest == null) return;

                handle(nearest, pokemon);

            } catch (Exception ignored) {}
        }));
    }




    private void handle(ServerPlayer player, Object pokemonObj) {

        try {
            var pokemon = (com.cobblemon.mod.common.pokemon.Pokemon) pokemonObj;

            String species = pokemon.getSpecies().getName();

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


            if (pokemon.getShiny()) {

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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}