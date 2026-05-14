package org.howie.pixity.moderation.neoforge.spawners;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

public class MobStackHologramFollowListener {

    @SubscribeEvent
    public void onTick(EntityTickEvent.Post e) {

        if (!(e.getEntity() instanceof ArmorStand stand)) return;
        if (!(stand.level() instanceof ServerLevel level)) return;

        var tag = stand.getPersistentData();

        if (!tag.getBoolean("pixity_mob_holo")) return;

        int id = tag.getInt("pixity_follow");

        Entity target = level.getEntity(id);

        if (target == null) {
            stand.discard();
            return;
        }

        double offset = tag.getDouble("pixity_y");

        stand.setPos(
                target.getX(),
                target.getY() + offset,
                target.getZ()
        );
    }
}