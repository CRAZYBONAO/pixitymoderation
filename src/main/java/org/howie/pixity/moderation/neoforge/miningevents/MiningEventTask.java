package org.howie.pixity.moderation.neoforge.miningevents;

import net.minecraft.server.MinecraftServer;

public class MiningEventTask {





    private static long lastStandingsAnnouncement = 0L;





    public static void tick(
            MinecraftServer server
    ) {

        long now =
                System.currentTimeMillis();





        if (
                !MiningEventManager.isActive()
                        &&
                        now >= MiningEventManager.getNextEventTime()
        ) {

            MiningEventManager.startEvent(
                    server,
                    MiningEventOre.random()
            );

            lastStandingsAnnouncement = now;

            return;
        }





        if (!MiningEventManager.isActive()) {
            return;
        }

        MiningEventManager.updateBossBar(
                server
        );





        if (
                now >= MiningEventManager.getEndTime()
        ) {

            MiningEventManager.endEvent(server);

            return;
        }





        if (
                now - lastStandingsAnnouncement
                        >= (1000L * 60L * 5L)
        ) {

            lastStandingsAnnouncement = now;

            MiningEventManager.announceStandings(
                    server
            );
        }
    }
}