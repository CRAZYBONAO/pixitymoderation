package org.howie.pixity.moderation.neoforge.fishing.deliveries;

import net.minecraft.server.level.ServerPlayer;
import org.howie.pixity.moderation.chat.TextFormatter;
import org.howie.pixity.moderation.neoforge.fishing.FishData;
import org.howie.pixity.moderation.neoforge.fishing.FishingDatabase;
import org.howie.pixity.moderation.neoforge.fishing.FishingManager;
import org.howie.pixity.moderation.neoforge.fishing.FishTier;

import java.util.*;


public class DeliveryManager {

    private static final Map<UUID, DeliveryProgress> progressMap = new HashMap<>();
    private static final Map<UUID, List<Delivery>> deliveries = new HashMap<>();
    private static final Map<UUID, List<Delivery>> queue = new HashMap<>();

    private static final Random R = new Random();





    public static DeliveryProgress getProgress(UUID uuid) {
        return progressMap.computeIfAbsent(uuid, u -> FishingDatabase.loadDeliveryProgress(uuid));
    }

    public static List<Delivery> getDeliveries(UUID uuid) {
        return deliveries.computeIfAbsent(uuid, u -> FishingDatabase.loadDeliveries(uuid));
    }

    public static List<Delivery> getQueue(UUID uuid) {
        return queue.computeIfAbsent(uuid, u -> new ArrayList<>());
    }





    public static void onFishCatch(UUID uuid) {

        DeliveryProgress p = getProgress(uuid);

        p.fishCaught++;


        int threshold = getGenerationThreshold(uuid);

        while (p.fishCaught >= threshold) {

            p.fishCaught -= threshold;

            generateDelivery(uuid);
        }

        FishingDatabase.saveDeliveryProgress(uuid, p);

    }

    public static void onSquidKill(UUID uuid) {

        DeliveryProgress p = getProgress(uuid);

        p.squidKilled++;

        int threshold = getGenerationThreshold(uuid);

        while (p.squidKilled >= threshold) {

            p.squidKilled -= threshold;

            generateDelivery(uuid);
        }
        FishingDatabase.saveDeliveryProgress(uuid, p);

    }

    public static void onDolphinKill(UUID uuid) {

        DeliveryProgress p = getProgress(uuid);

        p.dolphinKilled++;


        int threshold = getGenerationThreshold(uuid);

        while (p.dolphinKilled >= threshold) {

            p.dolphinKilled -= threshold;

            generateDelivery(uuid);
        }

        FishingDatabase.saveDeliveryProgress(uuid, p);
    }

    private static int getGenerationThreshold(UUID uuid) {

        int lucky = FishingDatabase.getDeliveryUpgrade(uuid, "delivery_lucky");

        int base = 100;


        return Math.max(10, base - lucky);
    }





    private static int getRequiredFish(UUID uuid) {

        int expert = FishingDatabase.getDeliveryUpgrade(uuid, "delivery_expert");
        int lucky = FishingDatabase.getDeliveryUpgrade(uuid, "delivery_lucky");

        int base = 100;

        int reduction = expert + lucky;

        return Math.max(10, base - reduction);
    }





    public static void generateDelivery(UUID uuid) {

        int tier = rollTier(uuid);

        Delivery delivery = new Delivery(-1, tier);

        generateRequirements(uuid, delivery);
        DeliveryRewardManager.rollRewards(delivery);

        delivery.npcName = getRandomNPC();


        if (!assignToSlot(uuid, delivery)) {
            getQueue(uuid).add(delivery);
        }

        FishingDatabase.saveDeliveries(uuid, deliveries.get(uuid));
    }

    public static boolean startDelivery(ServerPlayer player, int slot) {

        UUID uuid = player.getUUID();

        List<Delivery> list = getDeliveries(uuid);

        if (slot < 0 || slot >= list.size()) return false;

        Delivery delivery = list.get(slot);

        if (delivery.started) return false;


        if (!DeliveryUtils.hasRequiredFish(player, delivery)) {
            player.sendSystemMessage(TextFormatter.parse("&a&lFISHING &7&l➤ <red>Missing required fish</red>"));
            return false;
        }


        DeliveryUtils.removeRequiredFish(player, delivery);

        delivery.started = true;
        delivery.startTime = System.currentTimeMillis();

        long baseTime = getDeliveryTime(delivery.tier);

        int jetboat = FishingDatabase.getDeliveryUpgrade(uuid, "delivery_jetboat");

        double reduction = jetboat * 0.01;

        long finalTime = (long) (baseTime * (1.0 - reduction));

        delivery.endTime = delivery.startTime + finalTime;

        player.sendSystemMessage(TextFormatter.parse(
                "&a&lFISHING &7&l➤ <green>Started delivery for</green> <yellow>" + delivery.npcName + "</yellow>"
        ));

        FishingDatabase.saveDeliveries(uuid, deliveries.get(uuid));

        return true;
    }

    public static boolean completeDelivery(ServerPlayer player, int slot) {

        UUID uuid = player.getUUID();

        List<Delivery> list = getDeliveries(uuid);

        if (slot < 0 || slot >= list.size()) return false;

        Delivery delivery = list.get(slot);

        if (!delivery.started) return false;

        if (System.currentTimeMillis() < delivery.endTime) {
            player.sendSystemMessage(TextFormatter.parse("&a&lFISHING &7&l➤ <red>Delivery not ready yet</red>"));
            return false;
        }


        giveRewards(player, delivery);


        list.remove(slot);


        refillFromQueue(uuid);

        player.sendSystemMessage(TextFormatter.parse(
                "&a&lFISHING &7&l➤ <green>Delivery completed!</green>"
        ));

        FishingDatabase.saveDeliveries(uuid, deliveries.get(uuid));

        return true;
    }

    private static void refillFromQueue(UUID uuid) {

        List<Delivery> list = getDeliveries(uuid);
        List<Delivery> queueList = getQueue(uuid);

        int maxSlots = getMaxSlots(uuid);

        while (list.size() < maxSlots && !queueList.isEmpty()) {

            Delivery next = queueList.remove(0);

            next.slot = list.size();

            list.add(next);
        }
    }

    private static long getDeliveryTime(int tier) {
        return switch (tier) {
            case 1 -> minutes(15);
            case 2 -> minutes(30);
            case 3 -> minutes(45);
            case 4 -> minutes(60);
            case 5 -> minutes(90);
            case 6 -> minutes(120);
            case 7 -> minutes(150);
            case 8 -> minutes(180);
            case 9 -> minutes(210);
            case 10 -> minutes(240);
            default -> minutes(30);
        };
    }

    private static long minutes(int m) {
        return m * 60L * 1000L;
    }

    private static void giveRewards(ServerPlayer player, Delivery delivery) {

        UUID uuid = player.getUUID();


        int entropy = delivery.entropyReward;

        int payRise = FishingDatabase.getDeliveryUpgrade(uuid, "delivery_payrise");
        entropy = (int) Math.round(entropy * (1.0 + (payRise * 0.01)));

        FishingDatabase.addEntropy(uuid, entropy);

        player.sendSystemMessage(TextFormatter.parse(
                "&b " + entropy + " Entropy"
        ));


        for (DeliveryReward r : delivery.rolledRewards) {
            DeliveryRewardManager.apply(player, r);
        }
    }

    public static String getPreview(int tier) {

        List<DeliveryReward> list = DeliveryRewardManager.getRewardsForTier(tier);

        if (list == null || list.isEmpty()) return "";

        int totalWeight = 0;

        for (DeliveryReward r : list) {
            totalWeight += r.weight;
        }

        StringBuilder sb = new StringBuilder();

        for (DeliveryReward r : list) {

            if (!r.type.equals("command")) continue;

            double chance = (r.weight / (double) totalWeight) * 100.0;

            String name;

            if (r.display != null) {
                name = r.display;
            } else {

                name = r.value.replace("%player%", "")
                        .replace("give ", "")
                        .trim();
            }

            sb.append(String.format("%s (%.1f%%), ", name, chance));
        }

        if (sb.length() > 2) {
            sb.setLength(sb.length() - 2);
        }

        return sb.toString();
    }





    private static boolean assignToSlot(UUID uuid, Delivery delivery) {

        List<Delivery> list = getDeliveries(uuid);

        int maxSlots = getMaxSlots(uuid);

        if (list.size() >= maxSlots) return false;

        delivery.slot = list.size();
        list.add(delivery);

        return true;
    }

    private static int getMaxSlots(UUID uuid) {

        int level = FishingDatabase.getDeliveryUpgrade(uuid, "delivery_capacity");

        return 3 + level;
    }





    private static int rollTier(UUID uuid) {

        int lucky = FishingDatabase.getDeliveryUpgrade(uuid, "delivery_lucky");

        int roll = R.nextInt(100) + 1;

        int adjusted = roll - lucky;


        adjusted = Math.max(1, adjusted);

        if (adjusted <= 30) return 1;
        if (adjusted <= 49) return 2;
        if (adjusted <= 59) return 3;
        if (adjusted <= 68) return 4;
        if (adjusted <= 76) return 5;
        if (adjusted <= 83) return 6;
        if (adjusted <= 89) return 7;
        if (adjusted <= 94) return 8;
        if (adjusted <= 98) return 9;


        return 10;
    }





    private static void generateRequirements(UUID uuid, Delivery delivery) {

        List<FishData> pool = getPoolForTier(delivery.tier);
        if (pool.isEmpty()) return;

        int total = getAdjustedRequirement(uuid, delivery.tier);

        List<FishData> available = new ArrayList<>(pool);
        Collections.shuffle(available);

        int maxTypes = getMaxTypes(delivery.tier);

        List<FishData> selected = available.subList(0, Math.min(maxTypes, available.size()));


        List<Integer> pattern = generatePattern(total, selected.size(), delivery.tier);

        for (int i = 0; i < selected.size(); i++) {
            int amount = pattern.get(i);
            delivery.requiredFish.put(selected.get(i).id, amount);
        }
    }

    private static List<Integer> generatePattern(int total, int types, int tier) {

        List<List<Integer>> patterns = getPatternsForTier(tier);

        if (patterns.isEmpty()) {

            List<Integer> fallback = new ArrayList<>();
            int split = total / types;

            for (int i = 0; i < types; i++) {
                fallback.add(split);
            }

            return fallback;
        }


        List<Integer> base = patterns.get(R.nextInt(patterns.size()));


        List<Integer> trimmed = new ArrayList<>(base.subList(0, Math.min(types, base.size())));


        int baseSum = trimmed.stream().mapToInt(i -> i).sum();

        List<Integer> scaled = new ArrayList<>();

        for (int value : trimmed) {
            int scaledValue = Math.max(1, (int) Math.round((value / (double) baseSum) * total));
            scaled.add(scaledValue);
        }


        int diff = total - scaled.stream().mapToInt(i -> i).sum();

        int index = 0;
        while (diff != 0 && !scaled.isEmpty()) {
            int i = index % scaled.size();

            if (diff > 0) {
                scaled.set(i, scaled.get(i) + 1);
                diff--;
            } else if (scaled.get(i) > 1) {
                scaled.set(i, scaled.get(i) - 1);
                diff++;
            }

            index++;
        }

        Collections.shuffle(scaled);

        return scaled;
    }

    private static List<List<Integer>> getPatternsForTier(int tier) {

        List<List<Integer>> list = new ArrayList<>();

        switch (tier) {


            case 1, 2, 3 -> {
                list.add(List.of(6, 6, 12));
                list.add(List.of(4, 4, 4, 4));
                list.add(List.of(8, 8));
                list.add(List.of(5, 5, 10));
            }


            case 4, 5, 6 -> {
                list.add(List.of(6, 6, 6, 12));
                list.add(List.of(8, 8, 8));
                list.add(List.of(10, 10));
                list.add(List.of(4, 4, 4, 4, 8));
            }


            case 7, 8, 9 -> {
                list.add(List.of(12, 12));
                list.add(List.of(8, 8, 8, 8));
                list.add(List.of(6, 6, 6, 6, 12));
                list.add(List.of(10, 10, 10));
            }


            case 10 -> {
                list.add(List.of(1, 1));
                list.add(List.of(2, 2));
                list.add(List.of(1, 2, 1));
                list.add(List.of(2, 1, 1));
            }
        }

        return list;
    }

    private static int getAdjustedRequirement(UUID uuid, int tier) {

        int base = getTotalAmount(tier);

        int expert = FishingDatabase.getDeliveryUpgrade(uuid, "delivery_expert");


        return Math.max(1, base - expert);
    }

    private static List<FishData> getPoolForTier(int tier) {

        List<FishData> pool = new ArrayList<>();

        for (FishData fish : FishingManager.FISH) {

            switch (tier) {


                case 1, 2, 3 -> {
                    if (fish.tier == FishTier.BRONZE || fish.tier == FishTier.SILVER) {
                        pool.add(fish);
                    }
                }


                case 4, 5, 6 -> {
                    if (fish.tier == FishTier.GOLD || fish.tier == FishTier.DIAMOND) {
                        pool.add(fish);
                    }
                }


                case 7, 8, 9 -> {
                    if (fish.tier == FishTier.DIAMOND || fish.tier == FishTier.PLATINUM) {
                        pool.add(fish);
                    }
                }


                case 10 -> {
                    if (fish.tier == FishTier.MYTHICAL) {
                        pool.add(fish);
                    }
                }
            }
        }

        return pool;
    }

    private static int getMaxTypes(int tier) {
        return switch (tier) {
            case 1,4,7 -> 3;
            case 2,5,8 -> 4;
            case 3,6,9 -> 5;
            case 10 -> 4;
            default -> 3;
        };
    }

    private static int getTotalAmount(int tier) {
        return switch (tier) {
            case 1 -> rand(12,24);
            case 2 -> rand(16,32);
            case 3 -> rand(24,40);
            case 4 -> rand(12,24);
            case 5 -> rand(16,32);
            case 6 -> rand(24,40);
            case 7 -> rand(12,24);
            case 8 -> rand(16,32);
            case 9 -> rand(24,40);
            case 10 -> rand(2,8);
            default -> 10;
        };
    }

    private static int rand(int min, int max) {
        return min + R.nextInt(max - min + 1);
    }





    private static final String[] NPCS = {
            "Adam", "Howie", "Saint", "Venver", "Arceus"
    };

    private static String getRandomNPC() {
        return NPCS[R.nextInt(NPCS.length)];
    }
}