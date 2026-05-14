package org.howie.pixity.moderation.neoforge.rollback;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class RollbackConfigStore {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Logger logger;
    private final Path file;

    public RollbackConfigStore(final Logger logger, final Path dataDir) {
        this.logger = logger;
        this.file = dataDir.resolve("rollback_config.json");
    }

    public RollbackConfig loadOrCreate() {
        try {
            if (!Files.exists(file)) {
                RollbackConfig cfg = new RollbackConfig();
                save(cfg);
                return cfg;
            }
            try (BufferedReader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                RollbackConfig cfg = GSON.fromJson(r, RollbackConfig.class);
                if (cfg == null) cfg = new RollbackConfig();
                if (cfg.retentionDays < 0) cfg.retentionDays = 14;
                if (cfg.maxActionsPerCommand < 1) cfg.maxActionsPerCommand = 500;
                if (cfg.permissionUse == null) cfg.permissionUse = "pixity.rollback";
                return cfg;
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to load rollback_config.json; using defaults.", e);
            return new RollbackConfig();
        }
    }

    private void save(final RollbackConfig cfg) {
        try {
            Files.createDirectories(file.getParent());
            try (BufferedWriter w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                GSON.toJson(cfg, w);
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to save rollback_config.json", e);
        }
    }
}
