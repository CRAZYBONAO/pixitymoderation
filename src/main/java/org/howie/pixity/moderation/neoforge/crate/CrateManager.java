package org.howie.pixity.moderation.neoforge.crate;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.*;

public class CrateManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = Paths.get("config/pixity/crates.json");
    private static final Map<String, Rarity> rarities = new HashMap<>();

    private static final Map<String, Crate> crates = new HashMap<>();

    public static class Crate {
        public String display;

        public KeyItem keyItem;

        public List<Reward> rewards;
    }

    public static class KeyItem {

        public String item;

        public String name;

        public List<String> lore;
    }

    public static class Reward {
        public String type;
        public List<String> commands;
        public String item;
        public int amount = 1;
        public int weight;
        public String rarity;
        public String displayItem;
        public String pokemonData;
        public Integer customModelData;

        public String displayName;
        public Map<String, Integer> enchants;


    }

    public static void load() {
        try {

            if (!Files.exists(FILE)) {
                Files.createDirectories(FILE.getParent());
                Files.writeString(FILE, "{}");
            }

            Reader reader = Files.newBufferedReader(FILE);

            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();


            rarities.clear();

            if (root.has("rarities")) {

                JsonObject rarityObj = root.getAsJsonObject("rarities");

                for (var entry : rarityObj.entrySet()) {

                    Rarity r = GSON.fromJson(entry.getValue(), Rarity.class);

                    rarities.put(entry.getKey().toLowerCase(), r);
                }
            }


            crates.clear();

            for (var entry : root.entrySet()) {

                if (entry.getKey().equalsIgnoreCase("rarities")) continue;

                Crate crate = GSON.fromJson(entry.getValue(), Crate.class);

                crates.put(entry.getKey().toLowerCase(), crate);
            }

            reader.close();

            System.out.println("[Pixity] Loaded " + crates.size() + " crates.");
            System.out.println("[Pixity] Loaded " + rarities.size() + " rarities.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class Rarity {
        public String color;
        public boolean broadcast = false;
    }

    public static Rarity getRarity(String id) {

        if (id == null) return rarities.get("common");

        return rarities.getOrDefault(id.toLowerCase(), rarities.get("common"));
    }

    public static Crate get(String id) {
        return crates.get(id.toLowerCase());
    }

    public static Set<String> getAll() {
        return crates.keySet();
    }
}