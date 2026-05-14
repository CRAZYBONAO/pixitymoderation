package org.howie.pixity.moderation.neoforge.worldboss;

import com.cobblemon.mod.common.api.events.CobblemonEvents;

public class WorldBossCatchListener {





    public static void register() {

        CobblemonEvents.POKE_BALL_CAPTURE_CALCULATED.subscribe(event -> {

            try {

                var pokemon =
                        event.getPokemonEntity();

                if (pokemon == null) {
                    return;
                }





                if (
                        !pokemon.getPersistentData()
                                .getBoolean(
                                        "pixity_worldboss"
                                )
                ) {
                    return;
                }





                var result =
                        event.getCaptureResult();





                var failed =
                        result.copy(
                                0,

                                false,

                                false
                        );

                event.setCaptureResult(
                        failed
                );

                System.out.println(
                        "[WorldBoss] Prevented raid boss capture."
                );

            } catch (Exception e) {

                e.printStackTrace();
            }
        });
    }
}