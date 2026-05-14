package org.howie.pixity.moderation.neoforge.worldboss;

import net.minecraft.server.MinecraftServer;

public class WorldBossTask {





    private static long lastAnnouncement =
            0L;





    public static void tick(
            MinecraftServer server
    ) {

        if (!WorldBossManager.isActive()) {
            return;
        }

        WorldBossBossBar.update(server);
        WorldBossPhaseManager.tick(server);

        WorldBossTransformationManager.tryTransform(
                server
        );
        WorldBossScheduler.tick(server);





        long now =
                System.currentTimeMillis();

        if (
                now - lastAnnouncement
                        >= 300_000L
        ) {

            lastAnnouncement = now;

            WorldBossLeaderboard.announce(
                    server
            );
        }





        if (
                now
                        >= WorldBossManager.getEndTime()
        ) {

            WorldBossManager.kill(server);
        }
    }
}