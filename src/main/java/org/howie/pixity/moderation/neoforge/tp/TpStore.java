package org.howie.pixity.moderation.neoforge.tp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public final class TpStore {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final Type HOMES_TYPE = new TypeToken<Map<UUID, Map<String, WarpPos>>>(){}.getType();
    private static final Type WARPS_TYPE = new TypeToken<Map<String, WarpPos>>(){}.getType();
    private static final Type PWARPS_TYPE = new TypeToken<Map<String, PlayerWarp>>(){}.getType();
    private static final Type SPAWN_TYPE = new TypeToken<WarpPos>(){}.getType();

    private final Logger logger;
    private final Path homesFile;
    private final Path warpsFile;
    private final Path pwarpsFile;
    private final Path spawnFile;

    public TpStore(final Logger logger, final Path dataDir) {
        this.logger = logger;
        this.homesFile = dataDir.resolve("homes.json");
        this.warpsFile = dataDir.resolve("warps.json");
        this.pwarpsFile = dataDir.resolve("playerwarps.json");
        this.spawnFile = dataDir.resolve("spawn.json");
    }

    public Map<UUID, Map<String, WarpPos>> loadHomes() {
        try {
            if (!Files.exists(homesFile)) return new HashMap<>();
            try (BufferedReader r = Files.newBufferedReader(homesFile, StandardCharsets.UTF_8)) {
                Map<UUID, Map<String, WarpPos>> m = GSON.fromJson(r, HOMES_TYPE);
                return m != null ? new HashMap<>(m) : new HashMap<>();
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to load homes.json", e);
            return new HashMap<>();
        }
    }

    public void saveHomes(final Map<UUID, Map<String, WarpPos>> homes) {
        try {
            Files.createDirectories(homesFile.getParent());
            try (BufferedWriter w = Files.newBufferedWriter(homesFile, StandardCharsets.UTF_8)) {
                GSON.toJson(homes, HOMES_TYPE, w);
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to save homes.json", e);
        }
    }

    public Map<String, WarpPos> loadWarps() {
        try {
            if (!Files.exists(warpsFile)) return new HashMap<>();
            try (BufferedReader r = Files.newBufferedReader(warpsFile, StandardCharsets.UTF_8)) {
                Map<String, WarpPos> m = GSON.fromJson(r, WARPS_TYPE);
                return m != null ? new HashMap<>(m) : new HashMap<>();
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to load warps.json", e);
            return new HashMap<>();
        }
    }

    public void saveWarps(final Map<String, WarpPos> warps) {
        try {
            Files.createDirectories(warpsFile.getParent());
            try (BufferedWriter w = Files.newBufferedWriter(warpsFile, StandardCharsets.UTF_8)) {
                GSON.toJson(warps, WARPS_TYPE, w);
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to save warps.json", e);
        }
    }

    public Map<String, PlayerWarp> loadPlayerWarps() {
        try {
            if (!Files.exists(pwarpsFile)) return new HashMap<>();
            try (BufferedReader r = Files.newBufferedReader(pwarpsFile, StandardCharsets.UTF_8)) {
                Map<String, PlayerWarp> m = GSON.fromJson(r, PWARPS_TYPE);
                return m != null ? new HashMap<>(m) : new HashMap<>();
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to load playerwarps.json", e);
            return new HashMap<>();
        }
    }

    public void savePlayerWarps(final Map<String, PlayerWarp> pwarps) {
        try {
            Files.createDirectories(pwarpsFile.getParent());
            try (BufferedWriter w = Files.newBufferedWriter(pwarpsFile, StandardCharsets.UTF_8)) {
                GSON.toJson(pwarps, PWARPS_TYPE, w);
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to save playerwarps.json", e);
        }
    }

    public WarpPos loadSpawn() {
        try {
            if (!Files.exists(spawnFile)) return null;
            try (BufferedReader r = Files.newBufferedReader(spawnFile, StandardCharsets.UTF_8)) {
                return GSON.fromJson(r, SPAWN_TYPE);
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to load spawn.json", e);
            return null;
        }
    }

    public void saveSpawn(final WarpPos spawn) {
        try {
            Files.createDirectories(spawnFile.getParent());
            try (BufferedWriter w = Files.newBufferedWriter(spawnFile, StandardCharsets.UTF_8)) {
                GSON.toJson(spawn, SPAWN_TYPE, w);
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to save spawn.json", e);
        }
    }
}
