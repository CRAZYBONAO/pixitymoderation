package org.howie.pixity.moderation.neoforge.jail;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public final class JailConfigStore {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Logger logger;
    private final Path file;

    public JailConfigStore(final Logger logger, final Path file) {
        this.logger = logger;
        this.file = file;
    }

    public JailConfig loadOrCreateDefault() {
        try {
            if (file.getParent() != null) {
                Files.createDirectories(file.getParent());
            }

            if (Files.exists(file) && Files.isDirectory(file)) {
                Files.delete(file);
            }

            if (!Files.exists(file)) {
                JailConfig cfg = defaultConfig();
                save(cfg);
                return cfg;
            }

            try (BufferedReader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                JailConfig cfg = GSON.fromJson(r, JailConfig.class);
                if (cfg == null) cfg = defaultConfig();

                if (cfg.allowCommands == null)
                    cfg.allowCommands = defaultConfig().allowCommands;

                if (cfg.maxDistance <= 0)
                    cfg.maxDistance = defaultConfig().maxDistance;

                return cfg;
            }

        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to load jail_config.json; using defaults.", e);
            return defaultConfig();
        }
    }

    public void save(final JailConfig cfg) {
        try {
            Files.createDirectories(file.getParent());
            try (BufferedWriter w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                GSON.toJson(cfg, w);
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to save jail_config.json", e);
        }
    }

    public static JailConfig defaultConfig() {
        JailConfig cfg = new JailConfig();
        cfg.allowCommands.addAll(Arrays.asList(
                "msg", "tell", "w", "r", "reply",
                "help", "rules",
                "ticket", "report",
                "mail",
                "jailcheck"
        ));
        cfg.maxDistance = 3.0;
        cfg.notifyStaff = true;
        return cfg;
    }
}
