package org.howie.pixity.moderation.neoforge.announce;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class AnnouncementsConfigStore {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Logger logger;
    private final Path file;

    public AnnouncementsConfigStore(final Logger logger, final Path dataDir) {
        this.logger = logger;
        this.file = dataDir.resolve("announcements_config.json");
    }

    public AnnouncementsConfig loadOrCreate() {
        try {
            if (!Files.exists(file)) {
                AnnouncementsConfig cfg = new AnnouncementsConfig();
                save(cfg);
                return cfg;
            }
            try (BufferedReader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                AnnouncementsConfig cfg = GSON.fromJson(r, AnnouncementsConfig.class);
                if (cfg == null) cfg = new AnnouncementsConfig();
                if (cfg.intervalSeconds <= 0) cfg.intervalSeconds = 600;
                if (cfg.mode == null || cfg.mode.isBlank()) cfg.mode = "CHAT";
                if (cfg.messages == null) cfg.messages = new java.util.ArrayList<>();
                return cfg;
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to load announcements_config.json; using defaults.", e);
            return new AnnouncementsConfig();
        }
    }

    private void save(final AnnouncementsConfig cfg) {
        try {
            Files.createDirectories(file.getParent());
            try (BufferedWriter w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                GSON.toJson(cfg, w);
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to save announcements_config.json", e);
        }
    }
}
