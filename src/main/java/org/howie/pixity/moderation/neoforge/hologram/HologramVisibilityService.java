package org.howie.pixity.moderation.neoforge.hologram;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.decoration.ArmorStand;

public class HologramVisibilityService {

    public static void update(Hologram hologram) {

        double maxDistanceSq =
                hologram.viewDistance()
                        * hologram.viewDistance();

        for (ArmorStand stand :
                hologram.getStands()) {

            if (stand == null ||
                    !stand.isAlive())
                continue;

            for (ServerPlayer player :
                    hologram.level()
                            .players()) {

                double distanceSq =
                        player.distanceToSqr(stand);

                boolean visible =
                        distanceSq <= maxDistanceSq;

                stand.setCustomNameVisible(
                        visible
                );
            }
        }
    }
}