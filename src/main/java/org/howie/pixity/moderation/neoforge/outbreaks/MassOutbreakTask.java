package org.howie.pixity.moderation.neoforge.outbreaks;

import net.minecraft.server.MinecraftServer;

public class MassOutbreakTask {





    public static void tick(
            MinecraftServer server
    ) {

        long now =
                System.currentTimeMillis();





        if (
                !MassOutbreakManager.isActive()
                        &&
                        now >= MassOutbreakManager.getNextOutbreak()
        ) {

            MassOutbreakManager.generate(server);

            return;
        }





        if (!MassOutbreakManager.isActive()) {
            return;
        }





        MassOutbreakBossBar.update(server);
        MassOutbreakSpawner.tick(
                server
        );





        if (
                now >= MassOutbreakManager.getEndTime()
        ) {

            MassOutbreakManager.end(server);
        }
    }
}