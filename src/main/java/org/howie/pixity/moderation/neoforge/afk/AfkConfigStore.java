package org.howie.pixity.moderation.neoforge.afk;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class AfkConfigStore {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final Logger logger;
    private final Path file;

    public AfkConfigStore(final Logger logger, final Path dataDir) {
        this.logger = logger;
        this.file = dataDir.resolve("afk_config.json");
    }

    public AfkConfig loadOrCreate() {
        try {
            if (!Files.exists(file)) {
                AfkConfig cfg = new AfkConfig();
                save(cfg);
                return cfg;
            }
            try (BufferedReader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                AfkConfig cfg = GSON.fromJson(r, AfkConfig.class);
                if (cfg == null) cfg = new AfkConfig();
                if (cfg.autoAfkMinutes <= 0) cfg.autoAfkMinutes = 10;
                return cfg;
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to load afk_config.json; using defaults.", e);
            return new AfkConfig();
        }
    }

    private void save(final AfkConfig cfg) {
        try {
            Files.createDirectories(file.getParent());
            try (BufferedWriter w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                GSON.toJson(cfg, w);
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to save afk_config.json", e);
        }
    }
}
