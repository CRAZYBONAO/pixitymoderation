package org.howie.pixity.moderation.neoforge.spawners;

import net.minecraft.world.entity.Mob;

public class MobStackService {

    private static final String KEY = "pixity_stack";

    public int get(Mob mob) {
        var tag = mob.getPersistentData();
        return tag.contains(KEY) ? tag.getInt(KEY) : 1;
    }

    public void set(Mob mob, int amount) {
        mob.getPersistentData().putInt(KEY, amount);
    }

    public void add(Mob mob, int amount) {
        set(mob, get(mob) + amount);
    }

    public boolean isStacked(Mob mob) {
        return get(mob) > 1;
    }
}