package org.howie.pixity.moderation.neoforge.pokemon;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class PokedexRewardConfig {

    private static final File FILE = new File("config/pokedex_rewards.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static class Reward {
        public int milestone;
        public String type;
        public double multiplier;
        public int duration;
        public String item;
    }

    private static List<Reward> rewards = new ArrayList<>();

    public static void load() {
        try {
            if (!FILE.exists()) {
                createDefault();
                return;
            }

            Reader reader = new FileReader(FILE);
            Type type = new TypeToken<List<Reward>>(){}.getType();

            rewards = GSON.fromJson(reader, type);
            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createDefault() {

        rewards = new ArrayList<>();

        Reward r1 = new Reward();
        r1.milestone = 10;
        r1.type = "item";
        r1.item = "minecraft:diamond 3";

        Reward r2 = new Reward();
        r2.milestone = 25;
        r2.type = "shiny_boost";
        r2.multiplier = 2;
        r2.duration = 120;

        rewards.add(r1);
        rewards.add(r2);

        save();
    }

    public static void save() {
        try {
            FILE.getParentFile().mkdirs();
            Writer writer = new FileWriter(FILE);
            GSON.toJson(rewards, writer);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Reward> getRewards() {
        return rewards;
    }
}