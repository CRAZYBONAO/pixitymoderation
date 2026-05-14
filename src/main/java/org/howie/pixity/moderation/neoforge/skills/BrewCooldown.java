package org.howie.pixity.moderation.neoforge.skills;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BrewCooldown {

    private static final Map<UUID, Long> last = new HashMap<>();
    private static final long COOLDOWN = 500;

    public static boolean canGain(UUID uuid) {

        long now = System.currentTimeMillis();
        long prev = last.getOrDefault(uuid, 0L);

        if (now - prev < COOLDOWN) return false;

        last.put(uuid, now);
        return true;
    }
}