package org.howie.pixity.moderation.neoforge.tp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class WarmupConfigManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Logger logger;
    private final Path file;
    private volatile WarmupConfig cfg;

    public WarmupConfigManager(final Logger logger, final Path file) {
        this.logger = logger;
        this.file = file;
        this.cfg = loadOrCreate();
    }

    public WarmupConfig get() {
        return cfg;
    }

    public void reload() {
        this.cfg = loadOrCreate();
    }

    private WarmupConfig loadOrCreate() {
        try {
            if (file.getParent() != null) {
                Files.createDirectories(file.getParent());
            }

            if (Files.exists(file) && Files.isDirectory(file)) {
                Files.delete(file);
            }

            if (Files.exists(file)) {
                try (BufferedReader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                    WarmupConfig c = GSON.fromJson(r, WarmupConfig.class);
                    if (c != null) return c;
                }
            }

        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to load warmup.json", e);
        }

        WarmupConfig def = new WarmupConfig();
        save(def);
        return def;
    }

    private void save(final WarmupConfig cfg) {
        try {
            Files.createDirectories(file.getParent());
            try (BufferedWriter w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                GSON.toJson(cfg, w);
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to save warmup.json", e);
        }
    }
}
