package org.howie.pixity.moderation.neoforge.queue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class QueueConfigStore {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Logger logger;
    private final Path file;

    public QueueConfigStore(final Logger logger, final Path dataDir) {
        this.logger = logger;
        this.file = dataDir.resolve("queue_config.json");
    }

    public QueueConfig loadOrCreate() {
        try {
            if (!Files.exists(file)) {
                QueueConfig cfg = new QueueConfig();
                save(cfg);
                return cfg;
            }
            try (BufferedReader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                QueueConfig cfg = GSON.fromJson(r, QueueConfig.class);
                if (cfg == null) cfg = new QueueConfig();
                if (cfg.softMaxPlayers < 1) cfg.softMaxPlayers = 80;
                if (cfg.reservedSlots < 0) cfg.reservedSlots = 0;
                if (cfg.fullKickMessage == null) cfg.fullKickMessage = "&cServer is full.";
                if (cfg.displacedKickMessage == null) cfg.displacedKickMessage = "&eA higher-priority player joined.";
                if (cfg.graceSeconds < 0) cfg.graceSeconds = 120;
                return cfg;
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to load queue_config.json; using defaults.", e);
            return new QueueConfig();
        }
    }

    private void save(final QueueConfig cfg) {
        try {
            Files.createDirectories(file.getParent());
            try (BufferedWriter w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                GSON.toJson(cfg, w);
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to save queue_config.json", e);
        }
    }
}
