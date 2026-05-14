package org.howie.pixity.moderation.neoforge.spawners;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

public class MobStackMergeListener {

    @SubscribeEvent
    public void onTick(EntityTickEvent.Post e) {

        if (!(e.getEntity() instanceof Mob mob)) return;
        if (!(mob.level() instanceof ServerLevel level)) return;

        var tag = mob.getPersistentData();

        if (!tag.getBoolean("pixity_spawner_mob"))
            return;

        int stack =
                tag.contains("pixity_mob_stack")
                        ? tag.getInt("pixity_mob_stack")
                        : 1;

        var nearby =
                level.getEntitiesOfClass(
                        mob.getClass(),
                        mob.getBoundingBox().inflate(6),
                        m -> m != mob
                                && m.getPersistentData().getBoolean("pixity_spawner_mob")
                );

        for (Mob other : nearby) {

            var otherTag = other.getPersistentData();

            int otherStack =
                    otherTag.contains("pixity_mob_stack")
                            ? otherTag.getInt("pixity_mob_stack")
                            : 1;

            int merged = stack + otherStack;

            tag.putInt("pixity_mob_stack", merged);

            stack = merged;

            MobStackHologram.update(mob, merged);

            other.discard();
        }
    }
}