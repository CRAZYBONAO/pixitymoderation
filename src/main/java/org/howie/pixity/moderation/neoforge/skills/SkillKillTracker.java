package org.howie.pixity.moderation.neoforge.skills;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SkillKillTracker {

    private static final Map<UUID, Map<UUID, Long>> lastKills = new HashMap<>();

    private static final long COOLDOWN = 60_000;

    public static boolean canGainXp(UUID killer, UUID victim) {

        long now = System.currentTimeMillis();

        Map<UUID, Long> map = lastKills.computeIfAbsent(killer, k -> new HashMap<>());

        Long last = map.get(victim);

        if (last != null && (now - last) < COOLDOWN) {
            return false;
        }

        map.put(victim, now);
        return true;
    }
}