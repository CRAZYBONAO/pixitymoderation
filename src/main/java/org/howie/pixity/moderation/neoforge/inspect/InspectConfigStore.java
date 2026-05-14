package org.howie.pixity.moderation.neoforge.inspect;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class InspectConfigStore {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Logger logger;
    private final Path file;

    public InspectConfigStore(final Logger logger, final Path dataDir) {
        this.logger = logger;
        this.file = dataDir.resolve("inspect_config.json");
    }

    public InspectConfig loadOrCreate() {
        try {
            if (!Files.exists(file)) {
                InspectConfig cfg = new InspectConfig();
                save(cfg);
                return cfg;
            }
            try (BufferedReader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                InspectConfig cfg = GSON.fromJson(r, InspectConfig.class);
                if (cfg == null) cfg = new InspectConfig();
                if (cfg.maxEventsPerBlock < 1) cfg.maxEventsPerBlock = 10;
                if (cfg.retentionDays < 0) cfg.retentionDays = 30;
                if (cfg.permissionUse == null) cfg.permissionUse = "pixity.inspect";
                return cfg;
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to load inspect_config.json; using defaults.", e);
            return new InspectConfig();
        }
    }

    private void save(final InspectConfig cfg) {
        try {
            Files.createDirectories(file.getParent());
            try (BufferedWriter w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                GSON.toJson(cfg, w);
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to save inspect_config.json", e);
        }
    }
}
