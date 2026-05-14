package org.howie.pixity.moderation.neoforge.kits;

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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class KitStore {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type KIT_MAP_TYPE = new TypeToken<Map<String, Kit>>(){}.getType();
    private static final Type COOLDOWN_MAP_TYPE = new TypeToken<Map<UUID, Map<String, Long>>>(){}.getType();

    private final Logger logger;
    private final Path kitsFile;
    private final Path cooldownsFile;

    public KitStore(final Logger logger, final Path configDir) {
        this.logger = logger;
        this.kitsFile = configDir.resolve("kits.json");
        this.cooldownsFile = configDir.resolve("kits_cooldowns.json");
    }

    public Map<String, Kit> loadKits() {
        try {
            if (!Files.exists(this.kitsFile)) return new HashMap<>();
            try (BufferedReader r = Files.newBufferedReader(this.kitsFile, StandardCharsets.UTF_8)) {
                Map<String, Kit> map = GSON.fromJson(r, KIT_MAP_TYPE);
                return map != null ? new HashMap<>(map) : new HashMap<>();
            }
        } catch (Exception e) {
            this.logger.error("[PixityModeration] Failed to load kits.json", e);
            return new HashMap<>();
        }
    }

    public void saveKits(final Map<String, Kit> kits) {
        try {
            Files.createDirectories(this.kitsFile.getParent());
            try (BufferedWriter w = Files.newBufferedWriter(this.kitsFile, StandardCharsets.UTF_8)) {
                GSON.toJson(kits, KIT_MAP_TYPE, w);
            }
        } catch (Exception e) {
            this.logger.error("[PixityModeration] Failed to save kits.json", e);
        }
    }

    public Map<UUID, Map<String, Long>> loadCooldowns() {
        try {
            if (!Files.exists(this.cooldownsFile)) return new HashMap<>();
            try (BufferedReader r = Files.newBufferedReader(this.cooldownsFile, StandardCharsets.UTF_8)) {
                Map<UUID, Map<String, Long>> map = GSON.fromJson(r, COOLDOWN_MAP_TYPE);
                return map != null ? new HashMap<>(map) : new HashMap<>();
            }
        } catch (Exception e) {
            this.logger.error("[PixityModeration] Failed to load kits_cooldowns.json", e);
            return new HashMap<>();
        }
    }

    public void saveCooldowns(final Map<UUID, Map<String, Long>> cooldowns) {
        try {
            Files.createDirectories(this.cooldownsFile.getParent());
            try (BufferedWriter w = Files.newBufferedWriter(this.cooldownsFile, StandardCharsets.UTF_8)) {
                GSON.toJson(cooldowns, COOLDOWN_MAP_TYPE, w);
            }
        } catch (Exception e) {
            this.logger.error("[PixityModeration] Failed to save kits_cooldowns.json", e);
        }
    }
}
