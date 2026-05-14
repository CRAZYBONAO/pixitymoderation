package org.howie.pixity.moderation.neoforge.pokemon;

import java.util.*;

public class PokedexManager {

    private static final Map<UUID, Set<String>> pokedex = new HashMap<>();

    public static boolean addCatch(UUID player, String species) {

        var set = pokedex.computeIfAbsent(player, k -> new HashSet<>());


        return set.add(species.toLowerCase());
    }

    public static int getCount(UUID player) {
        return pokedex.getOrDefault(player, Collections.emptySet()).size();
    }
}