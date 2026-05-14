package org.howie.pixity.moderation.neoforge.skills;

import net.minecraft.core.BlockPos;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SkillPlaceTracker {

    private static final Map<UUID, Map<BlockPos, Long>> recent = new HashMap<>();

    private static final long COOLDOWN = 8000;

    public static boolean canGainXp(UUID uuid, BlockPos pos) {

        long now = System.currentTimeMillis();

        Map<BlockPos, Long> map = recent.computeIfAbsent(uuid, k -> new HashMap<>());

        Long last = map.get(pos);

        if (last != null && (now - last) < COOLDOWN) {
            return false;
        }

        map.put(pos, now);
        return true;
    }
}