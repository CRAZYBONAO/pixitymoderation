package org.howie.pixity.moderation.neoforge.pokemonhunt;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PokemonHuntDatabase {





    private static final Map<UUID, Integer> progress =
            new HashMap<>();





    public static int getProgress(
            UUID uuid
    ) {

        return progress.getOrDefault(
                uuid,
                0
        );
    }





    public static void addProgress(
            UUID uuid,
            int amount
    ) {

        progress.merge(
                uuid,
                amount,
                Integer::sum
        );
    }





    public static void reset() {

        progress.clear();
    }
}