package org.howie.pixity.moderation.neoforge.worldboss;

import com.cobblemon.mod.common.api.events.CobblemonEvents;

public class WorldBossBattleDebug {





    public static void register() {

        System.out.println(
                "[WorldBoss] Registering battle debug..."
        );





        try {

            for (var field
                    : CobblemonEvents.class.getFields()) {

                System.out.println(
                        "[WorldBoss] Event Field: "
                                + field.getName()
                );
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}