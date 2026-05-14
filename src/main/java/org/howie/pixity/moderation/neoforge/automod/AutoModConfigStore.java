package org.howie.pixity.moderation.neoforge.automod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class AutoModConfigStore {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Logger logger;
    private final Path file;

    public AutoModConfigStore(final Logger logger, final Path dataDir) {
        this.logger = logger;
        this.file = dataDir.resolve("automod_config.json");
    }

    public AutoModConfig loadOrCreate() {
        try {
            if (!Files.exists(file)) {
                AutoModConfig cfg = new AutoModConfig();
                save(cfg);
                return cfg;
            }
            try (BufferedReader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                AutoModConfig cfg = GSON.fromJson(r, AutoModConfig.class);
                if (cfg == null) cfg = new AutoModConfig();

                if (cfg.repeatWindowSeconds < 1) cfg.repeatWindowSeconds = 10;
                if (cfg.rateWindowSeconds < 1) cfg.rateWindowSeconds = 6;
                if (cfg.rateMaxMessages < 1) cfg.rateMaxMessages = 4;
                if (cfg.strikesWindowSeconds < 5) cfg.strikesWindowSeconds = 120;
                if (cfg.strikesToMute < 1) cfg.strikesToMute = 3;
                if (cfg.muteSeconds < 0) cfg.muteSeconds = 120;
                if (cfg.bypassPermission == null) cfg.bypassPermission = "pixity.automod.bypass";
                if (cfg.muteReason == null) cfg.muteReason = "AutoMod: chat spam/filter";

                return cfg;
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to load automod_config.json; using defaults.", e);
            return new AutoModConfig();
        }
    }

    private void save(final AutoModConfig cfg) {
        try {
            Files.createDirectories(file.getParent());
            try (BufferedWriter w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                GSON.toJson(cfg, w);
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to save automod_config.json", e);
        }
    }
}
