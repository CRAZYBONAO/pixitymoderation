package org.howie.pixity.moderation.neoforge.milestones.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MilestonePlayerData {







    private final Map<String, Set<Integer>> claimed =
            new HashMap<>();





    public void claim(
            String milestoneId,
            int level
    ) {

        claimed
                .computeIfAbsent(
                        milestoneId,
                        k -> new HashSet<>()
                )
                .add(level);
    }





    public boolean hasClaimed(
            String milestoneId,
            int level
    ) {

        return claimed
                .getOrDefault(
                        milestoneId,
                        Set.of()
                )
                .contains(level);
    }





    public Set<Integer> getClaimed(
            String milestoneId
    ) {

        return claimed.getOrDefault(
                milestoneId,
                Set.of()
        );
    }





    public Map<String, Set<Integer>> getAll() {
        return claimed;
    }

    public void setAll(
            Map<String, Set<Integer>> data
    ) {

        claimed.clear();
        claimed.putAll(data);
    }
}