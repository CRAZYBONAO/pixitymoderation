package org.howie.pixity.moderation.neoforge.worldboss;

import net.minecraft.server.MinecraftServer;

import org.howie.pixity.moderation.chat.TextFormatter;

public class WorldBossPhaseManager {





    private static boolean phase75 =
            false;

    private static boolean phase50 =
            false;

    private static boolean phase25 =
            false;

    private static boolean phase10 =
            false;





    public static void reset() {

        phase75 = false;

        phase50 = false;

        phase25 = false;

        phase10 = false;
    }





    public static void tick(
            MinecraftServer server
    ) {

        if (!WorldBossManager.isActive()) {
            return;
        }

        WorldBossDefinition boss =
                WorldBossManager.getCurrent();

        if (boss == null) {
            return;
        }

        double percent =
                WorldBossManager.getHealth()

                        / (double) boss.maxHealth;





        if (
                !phase75
                        &&
                        percent <= 0.75
        ) {

            phase75 = true;

            WorldBossEffects.weatherPhase(
                    server
            );

            WorldBossMechanics.apply(

                    server,

                    boss.species,

                    75
            );
        }





        if (
                !phase50
                        &&
                        percent <= 0.50
        ) {

            phase50 = true;

            WorldBossEffects.phase50(
                    server
            );

            WorldBossMechanics.apply(

                    server,

                    boss.species,

                    50
            );
        }





        if (
                !phase25
                        &&
                        percent <= 0.25
        ) {

            phase25 = true;

            WorldBossEffects.phase25(
                    server
            );

            WorldBossMechanics.apply(

                    server,

                    boss.species,

                    25
            );
        }





        if (
                !phase10
                        &&
                        percent <= 0.10
        ) {

            phase10 = true;

            WorldBossEffects.phase10(
                    server
            );

            WorldBossMechanics.apply(

                    server,

                    boss.species,

                    10
            );
        }
    }





    private static void broadcast(
            MinecraftServer server,
            String msg
    ) {

        server.getPlayerList()
                .broadcastSystemMessage(

                        TextFormatter.parse(
                                msg
                        ),

                        false
                );
    }
}