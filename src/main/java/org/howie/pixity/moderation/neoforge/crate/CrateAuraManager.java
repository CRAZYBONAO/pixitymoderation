package org.howie.pixity.moderation.neoforge.crate;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.*;

public class CrateAuraManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = Paths.get("config/pixity/crate_auras.json");

    public static class Aura {
        public double radius = 0.8;
        public double height = 1.2;
        public double speed = 0.1;

        public int r = 255;
        public int g = 255;
        public int b = 255;

        public float size = 1.0f;

        public boolean doubleRing = true;
        public boolean rainbow = false;

        public double verticalSpeed = 0.02;
        public double verticalRange = 0.5;
    }

    private static final Map<String, Aura> auras = new HashMap<>();

    public static void load() {
        try {

            if (!Files.exists(FILE)) {
                Files.createDirectories(FILE.getParent());

                generateDefaults();
                save();

                System.out.println("[Pixity] Generated default crate auras.");
                return;
            }

            Reader reader = Files.newBufferedReader(FILE);
            Type type = new TypeToken<Map<String, Aura>>(){}.getType();

            Map<String, Aura> data = GSON.fromJson(reader, type);

            auras.clear();
            if (data != null) auras.putAll(data);

            reader.close();

            System.out.println("[Pixity] Loaded " + auras.size() + " crate auras.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void generateDefaults() {


        Aura helix = new Aura();
        helix.radius = 0.9;
        helix.height = 1.2;
        helix.speed = 0.1;
        helix.doubleRing = true;
        helix.verticalRange = 0.6;
        helix.r = 0;
        helix.g = 255;
        helix.b = 255;
        auras.put("helix", helix);


        Aura single = new Aura();
        single.radius = 0.7;
        single.height = 1.2;
        single.speed = 0.08;
        single.doubleRing = false;
        single.r = 0;
        single.g = 150;
        single.b = 255;
        auras.put("single_ring", single);

        Aura doubleRing = new Aura();
        doubleRing.radius = 0.8;
        doubleRing.height = 1.3;
        doubleRing.speed = 0.1;
        doubleRing.doubleRing = true;
        doubleRing.r = 255;
        doubleRing.g = 100;
        doubleRing.b = 50;
        auras.put("double_ring", doubleRing);

        Aura rainbow = new Aura();
        rainbow.radius = 1.0;
        rainbow.height = 1.4;
        rainbow.speed = 0.08;
        rainbow.doubleRing = true;
        rainbow.rainbow = true;
        rainbow.verticalRange = 0.5;
        auras.put("rainbow", rainbow);


        Aura purple = new Aura();
        purple.radius = 0.75;
        purple.height = 1.2;
        purple.speed = 0.09;
        purple.doubleRing = true;
        purple.r = 180;
        purple.g = 0;
        purple.b = 255;
        purple.size = 1.2f;
        auras.put("purple_dust", purple);
    }

    public static void save() {
        try {
            Writer writer = Files.newBufferedWriter(FILE);
            GSON.toJson(auras, writer);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void set(String crateId, Aura aura) {
        auras.put(crateId.toLowerCase(), aura);
        save();
    }

    public static Aura get(String crateId) {
        return auras.get(crateId.toLowerCase());
    }
}