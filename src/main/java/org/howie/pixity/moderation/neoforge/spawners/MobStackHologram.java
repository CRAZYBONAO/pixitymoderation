package org.howie.pixity.moderation.neoforge.spawners;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.decoration.ArmorStand;

public class MobStackHologram {

    public static void update(Mob mob, int stack) {

        remove(mob);

        if (stack <= 1) return;

        String name =
                mob.getType()
                        .getDescription()
                        .getString();

        spawn(mob, "§e" + name, 0.25);
        spawn(mob, "§7x§f" + stack, 0.0);
    }

    private static void spawn(
            Mob mob,
            String text,
            double yOffset
    ) {

        ServerLevel level = (ServerLevel) mob.level();

        ArmorStand stand =
                new ArmorStand(
                        net.minecraft.world.entity.EntityType.ARMOR_STAND,
                        level
                );

        stand.setInvisible(true);
        stand.setNoGravity(true);
        stand.setInvulnerable(true);
        stand.setCustomNameVisible(true);
        stand.setCustomName(Component.literal(text));

        var tag = stand.getPersistentData();
        tag.putBoolean("pixity_mob_holo", true);
        tag.putInt("pixity_follow", mob.getId());
        tag.putDouble("pixity_y", yOffset);

        level.addFreshEntity(stand);
    }

    public static void remove(Mob mob) {

        mob.level().getEntities(
                mob,
                mob.getBoundingBox().inflate(1),
                e -> e.getPersistentData().getBoolean("pixity_mob_holo")
        ).forEach(Entity::discard);
    }
}