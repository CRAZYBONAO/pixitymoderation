package org.howie.pixity.moderation.neoforge.worldboss;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WorldBossDamageTracker {





    private static final Map<UUID, Long> DAMAGE =
            new HashMap<>();





    public static void addDamage(
            UUID uuid,
            long amount
    ) {

        DAMAGE.put(

                uuid,

                DAMAGE.getOrDefault(
                        uuid,
                        0L
                ) + amount
        );
    }





    public static long getDamage(
            UUID uuid
    ) {

        return DAMAGE.getOrDefault(
                uuid,
                0L
        );
    }





    public static Map<UUID, Long> getAll() {
        return DAMAGE;
    }





    public static void clear() {
        DAMAGE.clear();
    }

    public static void setDamage(
            UUID uuid,
            long amount
    ) {

        DAMAGE.put(
                uuid,
                amount
        );
    }
}