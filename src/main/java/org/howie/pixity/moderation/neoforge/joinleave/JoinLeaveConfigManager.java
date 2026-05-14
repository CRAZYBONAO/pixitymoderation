package org.howie.pixity.moderation.neoforge.joinleave;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class JoinLeaveConfigManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Logger logger;
    private final Path file;
    private volatile JoinLeaveConfig cfg;

    public JoinLeaveConfigManager(final Logger logger, final Path file) {
        this.logger = logger;
        this.file = file;
        this.cfg = loadOrCreate();
    }

    public JoinLeaveConfig get() {
        return cfg;
    }

    public void reload() {
        this.cfg = loadOrCreate();
    }

    private JoinLeaveConfig loadOrCreate() {
        try {
            if (Files.exists(file)) {
                try (BufferedReader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                    JoinLeaveConfig c = GSON.fromJson(r, JoinLeaveConfig.class);
                    if (c != null) return c;
                }
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to load joinleave.json", e);
        }

        JoinLeaveConfig def = new JoinLeaveConfig();
        save(def);
        return def;
    }

    private void save(final JoinLeaveConfig cfg) {
        try {
            Files.createDirectories(file.getParent());
            try (BufferedWriter w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                GSON.toJson(cfg, w);
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to save joinleave.json", e);
        }
    }
}
