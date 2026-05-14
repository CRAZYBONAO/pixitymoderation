package org.howie.pixity.moderation.neoforge.fishing.deliveries;

import java.util.*;

public class Delivery {

    public int slot;
    public int tier;


    public Map<String, Integer> requiredFish = new HashMap<>();

    public long startTime;
    public long endTime;

    public boolean started;
    public boolean completed;

    public String npcName;
    public int entropyReward;
    public List<DeliveryReward> rolledRewards = new ArrayList<>();
    public UUID owner;

    public Delivery(int slot, int tier) {
        this.slot = slot;
        this.tier = tier;
    }
}