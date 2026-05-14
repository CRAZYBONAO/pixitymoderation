package org.howie.pixity.moderation.neoforge.worldboss;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WorldBossBattleAttempts {





    private static final Map<UUID, Integer> ATTEMPTS =
            new HashMap<>();





    public static void addAttempt(
            UUID uuid
    ) {

        ATTEMPTS.put(

                uuid,

                getAttempts(uuid) + 1
        );
    }





    public static int getAttempts(
            UUID uuid
    ) {

        return ATTEMPTS.getOrDefault(
                uuid,
                0
        );
    }





    public static void clear() {

        ATTEMPTS.clear();
    }

    public static Map<UUID, Integer> getAll() {
        return ATTEMPTS;
    }

    public static void setAttempts(
            UUID uuid,
            int amount
    ) {

        ATTEMPTS.put(
                uuid,
                amount
        );
    }
}