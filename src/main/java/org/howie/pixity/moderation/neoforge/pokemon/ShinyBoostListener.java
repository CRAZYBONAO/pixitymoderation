package org.howie.pixity.moderation.neoforge.pokemon;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;

import org.howie.pixity.moderation.neoforge.rank.RankService;

public class ShinyBoostListener {

    private static final RankService RANK = new RankService();

    @SubscribeEvent
    public void onSpawn(EntityJoinLevelEvent e) {

        if (!(e.getLevel() instanceof ServerLevel level)) return;
        if (!(e.getEntity() instanceof PokemonEntity entity)) return;

        var pokemon = entity.getPokemon();

        try {



            if (entity.getPersistentData().getBoolean("pixity_shiny_checked")) return;
            entity.getPersistentData().putBoolean("pixity_shiny_checked", true);


            if (pokemon.getShiny()) return;

            double baseChance = 1.0 / 4096.0;




            double globalMultiplier = ShinyBoostManager.isGlobalActive()
                    ? ShinyBoostManager.getGlobalMultiplier()
                    : 1.0;




            double playerMultiplier = 1.0;
            double rankMultiplier = 1.0;

            net.minecraft.world.entity.player.Player nearestPlayer = level.getNearestPlayer(
                    entity.getX(),
                    entity.getY(),
                    entity.getZ(),
                    64,
                    false
            );

            ServerPlayer nearest = (nearestPlayer instanceof ServerPlayer sp) ? sp : null;

            if (nearest != null) {


                playerMultiplier = ShinyBoostManager.getPlayerMultiplier(nearest);


                int weight = RANK.weight(nearest);

                if (weight > 0) {
                    double perWeight = PokePartyConfig.get().rankBoostPerWeight;
                    rankMultiplier += (weight * perWeight);
                }
            }




            double finalChance = baseChance * globalMultiplier * playerMultiplier * rankMultiplier;

            if (finalChance > 1.0) finalChance = 1.0;




            if (level.random.nextDouble() < finalChance) {
                pokemon.setShiny(true);
            }

        } catch (Exception ignored) {}
    }
}