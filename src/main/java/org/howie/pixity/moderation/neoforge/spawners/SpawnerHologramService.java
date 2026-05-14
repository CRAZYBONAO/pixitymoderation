package org.howie.pixity.moderation.neoforge.spawners;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.phys.AABB;

public class SpawnerHologramService {

    public void spawn(ServerLevel level, BlockPos pos, String text) {

        remove(level, pos);

        String[] lines = text.split("\n");

        for (int i = 0; i < lines.length; i++) {

            ArmorStand stand =
                    new ArmorStand(EntityType.ARMOR_STAND, level);

            stand.setPos(
                    pos.getX() + 0.5,
                    pos.getY() - 1 + ((lines.length - i - 1) * 0.25),
                    pos.getZ() + 0.5
            );

            stand.setInvisible(true);
            stand.setNoGravity(true);
            stand.setInvulnerable(true);
            stand.setCustomNameVisible(true);
            stand.setCustomName(Component.literal(lines[i]));


            var tag = stand.getPersistentData();
            tag.putBoolean("Marker", true);
            tag.putBoolean("Small", true);

            level.addFreshEntity(stand);
        }
    }

    public void remove(ServerLevel level, BlockPos pos) {

        level.getEntitiesOfClass(
                ArmorStand.class,
                new AABB(
                        pos.getX() - 1,
                        pos.getY() - 1,
                        pos.getZ() - 1,
                        pos.getX() + 2,
                        pos.getY() + 3,
                        pos.getZ() + 2
                )
        ).forEach(entity -> entity.discard());
    }
}