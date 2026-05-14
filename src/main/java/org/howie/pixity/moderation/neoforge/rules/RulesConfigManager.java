package org.howie.pixity.moderation.neoforge.rules;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class RulesConfigManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Logger logger;
    private final Path file;
    private volatile RulesConfig cfg;

    public RulesConfigManager(final Logger logger, final Path file) {
        this.logger = logger;
        this.file = file;
        this.cfg = loadOrCreate();
    }

    public RulesConfig get() {
        return cfg;
    }

    public void reload() {
        this.cfg = loadOrCreate();
    }

    private RulesConfig loadOrCreate() {
        try {
            if (Files.exists(file)) {
                try (BufferedReader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                    RulesConfig c = GSON.fromJson(r, RulesConfig.class);
                    if (c != null) return c;
                }
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to load rules.json", e);
        }

        RulesConfig def = new RulesConfig();
        save(def);
        return def;
    }

    private void save(final RulesConfig cfg) {
        try {
            Files.createDirectories(file.getParent());
            try (BufferedWriter w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                GSON.toJson(cfg, w);
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to save rules.json", e);
        }
    }
}
