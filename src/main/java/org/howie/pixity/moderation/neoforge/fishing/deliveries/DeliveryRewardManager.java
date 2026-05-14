package org.howie.pixity.moderation.neoforge.fishing.deliveries;

import com.google.gson.*;
import net.minecraft.server.level.ServerPlayer;
import org.howie.pixity.moderation.chat.TextFormatter;
import org.howie.pixity.moderation.neoforge.fishing.FishingDatabase;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class DeliveryRewardManager {

    private static final Map<Integer, List<DeliveryReward>> rewards = new HashMap<>();
    private static final Map<Integer, int[]> rolls = new HashMap<>();
    private static final Map<String, String> rarityColors = new HashMap<>();
    private static final Map<Integer, Map<String, Double>> rarityBoost = new HashMap<>();
    private static final Map<Integer, Map<String, Double>> rarityPenalty = new HashMap<>();
    private static final Random R = new Random();

    private static final Path FILE = Paths.get("config/pixity/deliveries_rewards.json");

    public static void init() {
        try {
            if (!Files.exists(FILE)) {
                generateDefault();
            }

            load();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public static void load() throws Exception {

        rewards.clear();
        rolls.clear();
        rarityColors.clear();
        rarityBoost.clear();
        rarityPenalty.clear();

        JsonObject root = JsonParser.parseReader(new FileReader(FILE.toFile())).getAsJsonObject();
        JsonElement tiersElement = root.get("tiers");

        System.out.println("[Fishing DEBUG] JSON: " + root);

        if (tiersElement == null || !tiersElement.isJsonObject()) {
            throw new IllegalStateException("Invalid config: 'tiers' must be an object, not array");
        }

        JsonObject tiers = tiersElement.getAsJsonObject();

        if (root.has("rolls")) {

            JsonElement rollElement = root.get("rolls");

            if (!rollElement.isJsonObject()) {
                System.out.println("[Fishing] WARNING: 'rolls' is not an object. Skipping.");
            } else {

                JsonObject rollObj = rollElement.getAsJsonObject();

                for (String key : rollObj.keySet()) {

                    int tier = Integer.parseInt(key);

                    JsonElement arrEl = rollObj.get(key);

                    if (!arrEl.isJsonArray()) continue;

                    JsonArray arr = arrEl.getAsJsonArray();

                    int min = arr.get(0).getAsInt();
                    int max = arr.get(1).getAsInt();

                    rolls.putIfAbsent(tier, new int[]{min, max});
                }
            }
        }

        for (String key : tiers.keySet()) {

            int tier = Integer.parseInt(key);


            JsonArray arr = tiers.getAsJsonArray(key);

            List<DeliveryReward> list = new ArrayList<>();

            for (JsonElement el : arr) {

                JsonObject obj = el.getAsJsonObject();

                DeliveryReward r = new DeliveryReward();

                r.type = obj.get("type").getAsString();

                if (obj.has("value"))
                    r.value = obj.get("value").getAsString();

                if (obj.has("min"))
                    r.min = obj.get("min").getAsInt();

                if (obj.has("max"))
                    r.max = obj.get("max").getAsInt();

                r.weight = obj.has("weight") ? obj.get("weight").getAsInt() : 1;

                if (obj.has("rarity"))
                    r.rarity = obj.get("rarity").getAsString();

                if (obj.has("display"))
                    r.display = obj.get("display").getAsString();

                list.add(r);
            }

            rewards.put(tier, list);


            rolls.putIfAbsent(tier, new int[]{1, 1});
        }


        if (root.has("rarities")) {
            JsonObject rarities = root.getAsJsonObject("rarities");

            for (String key : rarities.keySet()) {
                rarityColors.put(key,
                        rarities.getAsJsonObject(key).get("color").getAsString());
            }
        }
    }




    private static void generateDefault() throws Exception {

        Files.createDirectories(FILE.getParent());

        JsonObject root = new JsonObject();
        JsonObject tiers = new JsonObject();

        for (int i = 1; i <= 10; i++) {

            JsonArray arr = new JsonArray();

            JsonObject entropy = new JsonObject();
            entropy.addProperty("type", "entropy");
            entropy.addProperty("min", i * 5000);
            entropy.addProperty("max", i * 8000);
            entropy.addProperty("weight", 100);

            arr.add(entropy);

            JsonObject cmd = new JsonObject();
            cmd.addProperty("type", "command");
            cmd.addProperty("value", "give %player% minecraft:iron_ingot " + (i * 2));
            cmd.addProperty("weight", 20);

            arr.add(cmd);

            tiers.add(String.valueOf(i), arr);
        }

        root.add("tiers", tiers);

        try (FileWriter writer = new FileWriter(FILE.toFile())) {
            new GsonBuilder().setPrettyPrinting().create().toJson(root, writer);
        }
    }




    public static void give(ServerPlayer player, int tier) {

        List<DeliveryReward> list = rewards.get(tier);

        if (list == null || list.isEmpty()) return;

        DeliveryReward reward = roll(list, tier, player.getUUID());

        apply(player, reward);
    }

    public static List<DeliveryReward> getRewardsForTier(int tier) {
        return rewards.getOrDefault(tier, Collections.emptyList());
    }

    private static DeliveryReward roll(List<DeliveryReward> list, int tier, UUID uuid) {

        Map<String, Double> boost = rarityBoost.getOrDefault(tier, Map.of());
        Map<String, Double> penalty = rarityPenalty.getOrDefault(tier, Map.of());

        int payRise = FishingDatabase.getDeliveryUpgrade(uuid, "delivery_payrise");


        double payRiseMultiplier = 1.0 + (payRise * 0.002);

        double total = 0;

        List<Double> weights = new ArrayList<>();

        for (DeliveryReward r : list) {

            double w = r.weight;

            if (r.rarity != null) {


                if (boost.containsKey(r.rarity)) {
                    w *= boost.get(r.rarity);
                }

                if (penalty.containsKey(r.rarity)) {
                    w *= penalty.get(r.rarity);
                }


                if (r.rarity.equalsIgnoreCase("rare")
                        || r.rarity.equalsIgnoreCase("epic")
                        || r.rarity.equalsIgnoreCase("legendary")) {

                    w *= payRiseMultiplier;
                }
            }

            weights.add(w);
            total += w;
        }

        double roll = R.nextDouble() * total;

        double current = 0;

        for (int i = 0; i < list.size(); i++) {

            current += weights.get(i);

            if (roll <= current) {
                return list.get(i);
            }
        }

        return list.get(0);
    }

    public static void apply(ServerPlayer player, DeliveryReward r) {

        String name = player.getName().getString();

        switch (r.type) {

            case "entropy" -> {
                int amount = r.min + R.nextInt(r.max - r.min + 1);

                int level = FishingDatabase.getDeliveryUpgrade(player.getUUID(), "delivery_payrise");
                amount = (int) (amount * (1 + (level * 0.01)));

                FishingDatabase.addEntropy(player.getUUID(), amount);

                player.sendSystemMessage(TextFormatter.parse(
                        "&b+ " + amount + " Entropy"
                ));
            }

            case "command" -> {
                String cmd = r.value.replace("%player%", name);

                player.server.getCommands().performPrefixedCommand(
                        player.server.createCommandSourceStack(),
                        cmd
                );
            }

            case "item" -> {

                try {
                    String[] split = r.value.split(" ");
                    String id = split[0];
                    int amount = split.length > 1 ? Integer.parseInt(split[1]) : 1;

                    var item = net.minecraft.core.registries.BuiltInRegistries.ITEM
                            .get(net.minecraft.resources.ResourceLocation.tryParse(id));

                    if (item == null) return;

                    net.minecraft.world.item.ItemStack stack =
                            new net.minecraft.world.item.ItemStack(item, amount);

                    if (!player.getInventory().add(stack)) {
                        player.drop(stack, false);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getPreview(int tier) {

        List<DeliveryReward> list = rewards.get(tier);

        if (list == null || list.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();

        for (DeliveryReward r : list) {

            String name = (r.display != null) ? r.display : r.value;

            String color = (r.rarity != null)
                    ? getRarityColor(r.rarity)
                    : "&7";

            sb.append(color).append(name).append("&7, ");


        }

        int[] range = rolls.getOrDefault(tier, new int[]{1,1});
        sb.append("<yellow>")
                .append(range[0])
                .append("-")
                .append(range[1])
                .append(" Rewards:</yellow> &7");

        if (sb.length() > 2) {
            sb.setLength(sb.length() - 8);
        }

        return sb.toString();
    }

    public static String getExactPreview(Delivery delivery) {

        StringBuilder sb = new StringBuilder();


        if (delivery.entropyReward > 0) {
            sb.append("&b+ ")
                    .append(delivery.entropyReward)
                    .append(" Entropy<gray>,</gray> ");
        }

        for (DeliveryReward r : delivery.rolledRewards) {

            String name = (r.display != null) ? r.display : r.value;

            String color = (r.rarity != null)
                    ? getRarityColor(r.rarity)
                    : "&7";

            sb.append(color).append(name).append("&7, ");
        }

        if (sb.length() > 2) {
            sb.setLength(sb.length() - 8);
        }

        return sb.toString();
    }

    public static void rollRewards(Delivery delivery) {

        List<DeliveryReward> pool = rewards.get(delivery.tier);

        if (pool == null || pool.isEmpty()) return;

        int[] range = rolls.getOrDefault(delivery.tier, new int[]{1, 1});

        int rollCount = range[0] + R.nextInt(range[1] - range[0] + 1);


        for (DeliveryReward r : pool) {
            if (r.type.equals("entropy")) {
                delivery.entropyReward = r.min + R.nextInt(r.max - r.min + 1);
                break;
            }
        }


        List<DeliveryReward> available = new ArrayList<>(pool);


        available.removeIf(r -> r.type.equals("entropy"));

        for (int i = 0; i < rollCount && !available.isEmpty(); i++) {

            DeliveryReward selected = roll(available, delivery.tier, delivery.owner);

            delivery.rolledRewards.add(selected);


            available.remove(selected);
        }
    }

    public static String getRarityColor(String rarity) {
        return rarityColors.getOrDefault(rarity, "&7");
    }
}