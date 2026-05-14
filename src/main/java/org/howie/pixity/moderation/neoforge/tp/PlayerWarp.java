package org.howie.pixity.moderation.neoforge.tp;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class PlayerWarp {

    public String name;
    public UUID owner;
    public String ownerName;
    public WarpPos pos;

    public int visits = 0;
    public long lastVisit = 0;
    public int recentVisits = 0;

    public Map<UUID, Integer> ratings = new HashMap<>();

    public boolean featured = false;
    public String category = "default";

    public String icon = "default";

    public String description = "";

    public PlayerWarp() {}

    public PlayerWarp(String name, UUID owner, String ownerName, WarpPos pos) {
        this.name = name;
        this.owner = owner;
        this.ownerName = ownerName;
        this.pos = pos;
    }

    public double getRating() {
        if (ratings == null || ratings.isEmpty()) return 0.0;

        int total = 0;
        for (int r : ratings.values()) total += r;

        return total / (double) ratings.size();
    }

    public void rate(UUID player, int stars) {
        if (player == null) return;

        if (stars < 1) stars = 1;
        if (stars > 5) stars = 5;

        ratings.put(player, stars);
    }

    public int getTrendingScore() {
        return visits + (recentVisits * 2);
    }
}