package org.howie.pixity.moderation.neoforge.milestones.listeners;

import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.pokemon.LevelUpEvent;
import com.cobblemon.mod.common.api.events.pokemon.evolution.EvolutionCompleteEvent;

import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.neoforge.stats.PlayerStatsDatabase;

public class TrainerMilestoneListener {





    public static void register() {





        CobblemonEvents.LEVEL_UP_EVENT.subscribe(
                TrainerMilestoneListener::onLevelUp
        );

        CobblemonEvents.EVOLUTION_COMPLETE.subscribe(
                TrainerMilestoneListener::onEvolution
        );
    }





    private static void onLevelUp(
            LevelUpEvent event
    ) {





        if (
                event.getPokemon()
                        .getOwnerPlayer() == null
        ) {
            return;
        }

        ServerPlayer player =
                event.getPokemon()
                        .getOwnerPlayer();





        int oldLevel =
                event.getOldLevel();

        int newLevel =
                event.getNewLevel();

        int gained =
                Math.max(
                        0,
                        newLevel - oldLevel
                );

        if (gained > 0) {

            PlayerStatsDatabase.add(
                    player.getUUID(),
                    "trainer_levels_gained",
                    gained
            );
        }





        if (
                newLevel >= 100 &&
                        oldLevel < 100
        ) {

            PlayerStatsDatabase.add(
                    player.getUUID(),
                    "trainer_level100",
                    1
            );
        }





        int friendship =
                event.getPokemon()
                        .getFriendship();

        PlayerStatsDatabase.add(
                player.getUUID(),
                "trainer_happiness_gained",
                friendship
        );
    }





    private static void onEvolution(
            EvolutionCompleteEvent event
    ) {

        if (
                event.getPokemon()
                        .getOwnerPlayer() == null
        ) {
            return;
        }

        ServerPlayer player =
                event.getPokemon()
                        .getOwnerPlayer();

        PlayerStatsDatabase.add(
                player.getUUID(),
                "trainer_evolutions",
                1
        );
    }







    public static void addEvolution(
            ServerPlayer player
    ) {

        PlayerStatsDatabase.add(
                player.getUUID(),
                "trainer_evolutions",
                1
        );
    }







    public static void addInfusion(
            ServerPlayer player
    ) {

        PlayerStatsDatabase.add(
                player.getUUID(),
                "trainer_infusion",
                1
        );
    }
}