package org.howie.pixity.moderation.neoforge.spawners;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class SpawnerConfig {

    private int defaultMax = 64;
    private Map<String, Integer> limits = new HashMap<>();

    public int getMax(String mob) {
        return limits.getOrDefault(mob, defaultMax);
    }

    public static SpawnerConfig load(Path file) {

        try {

            if (!Files.exists(file)) {

                SpawnerConfig cfg = new SpawnerConfig();

                Files.createDirectories(file.getParent());

                Gson gson =
                        new GsonBuilder().setPrettyPrinting().create();

                Files.writeString(
                        file,
                        gson.toJson(cfg)
                );

                return cfg;
            }

            Gson gson = new Gson();

            return gson.fromJson(
                    Files.readString(file),
                    SpawnerConfig.class
            );

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}