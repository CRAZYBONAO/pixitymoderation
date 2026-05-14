package org.howie.pixity.moderation.neoforge.skills.leaderboard;

import org.howie.pixity.moderation.neoforge.skills.*;

import java.util.*;
import java.util.stream.Collectors;

public class LeaderboardService {

    private final SkillService skills;

    public LeaderboardService(SkillService skills) {
        this.skills = skills;
    }




    public List<Map.Entry<UUID, Integer>> getTop(SkillType type, int limit) {

        return skills.getAll().entrySet().stream()
                .map(e -> new AbstractMap.SimpleEntry<>(
                        e.getKey(),
                        e.getValue().getLevel(type)
                ))
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .limit(limit)
                .collect(Collectors.toList());
    }




    public List<Map.Entry<UUID, Integer>> getGlobalTop(int limit) {

        return skills.getAll().entrySet().stream()
                .map(e -> {

                    int total = 0;

                    for (SkillType type : SkillType.values()) {
                        total += e.getValue().getLevel(type);
                    }

                    return new AbstractMap.SimpleEntry<>(e.getKey(), total);
                })
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .limit(limit)
                .collect(Collectors.toList());
    }
}