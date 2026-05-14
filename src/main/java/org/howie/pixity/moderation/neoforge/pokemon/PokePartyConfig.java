package org.howie.pixity.moderation.neoforge.pokemon;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class PokePartyConfig {

    private static final File FILE = new File("config/pokeparty.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public int requiredVotes = 10;
    public double shinyBoost = 0.10;
    public double legendaryChance = 0.15;
    public double rankBoostPerWeight = 0.01;
    public static int radius = 30;
    public int spawnsPerPlayer = 6;
    public int spawnRadiusPerPlayer = 12;

    private static PokePartyConfig INSTANCE;

    public static PokePartyConfig get() {
        if (INSTANCE == null) load();
        return INSTANCE;
    }

    public static void load() {
        try {
            if (!FILE.exists()) {
                INSTANCE = new PokePartyConfig();
                save();
                return;
            }

            INSTANCE = GSON.fromJson(new FileReader(FILE), PokePartyConfig.class);

        } catch (Exception e) {
            e.printStackTrace();
            INSTANCE = new PokePartyConfig();
        }
    }

    public static void save() {
        try {
            FILE.getParentFile().mkdirs();
            FileWriter writer = new FileWriter(FILE);
            GSON.toJson(INSTANCE, writer);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}