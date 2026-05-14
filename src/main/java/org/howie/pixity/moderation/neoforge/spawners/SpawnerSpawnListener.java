package org.howie.pixity.moderation.neoforge.spawners;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

public class SpawnerSpawnListener {

    private final SpawnerStackService stacks;
    private static final int PER_SPAWNER = 3;

    public SpawnerSpawnListener(SpawnerStackService stacks) {
        this.stacks = stacks;
    }

    @SubscribeEvent
    public void onSpawn(EntityJoinLevelEvent e) {

        if (!(e.getEntity() instanceof Mob mob)) return;
        if (!(e.getLevel() instanceof ServerLevel level)) return;





        SpawnerBlockEntity spawner = null;

        BlockPos origin = mob.blockPosition();

        for (int x = -4; x <= 4; x++) {
            for (int y = -4; y <= 4; y++) {
                for (int z = -4; z <= 4; z++) {

                    var be = level.getBlockEntity(
                            origin.offset(x, y, z)
                    );

                    if (be instanceof SpawnerBlockEntity s) {
                        spawner = s;
                        break;
                    }
                }
            }
        }

        if (spawner == null) return;

        int spawnStack =
                stacks.getStack(spawner) * PER_SPAWNER;

        var tag = mob.getPersistentData();

        tag.putBoolean("pixity_spawner_mob", true);





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

            int merged = otherStack + spawnStack;

            otherTag.putInt("pixity_mob_stack", merged);

            MobStackHologram.update(other, merged);

            mob.discard();
            return;
        }

        tag.putInt("pixity_mob_stack", spawnStack);

        MobStackHologram.update(mob, spawnStack);
    }
}