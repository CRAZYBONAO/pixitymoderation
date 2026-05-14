package org.howie.pixity.moderation.neoforge.freeze;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public final class FreezeConfigStore {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Logger logger;
    private final Path file;

    public FreezeConfigStore(final Logger logger, final Path dataDir) {
        this.logger = logger;
        this.file = dataDir.resolve("freeze_config.json");
    }

    public FreezeConfig loadOrCreate() {
        try {
            if (!Files.exists(file)) {
                FreezeConfig cfg = defaults();
                save(cfg);
                return cfg;
            }
            try (BufferedReader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                FreezeConfig cfg = GSON.fromJson(r, FreezeConfig.class);
                if (cfg == null) cfg = defaults();
                if (cfg.allowCommands == null) cfg.allowCommands = defaults().allowCommands;
                return cfg;
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to load freeze_config.json; using defaults.", e);
            return defaults();
        }
    }

    private void save(final FreezeConfig cfg) {
        try {
            Files.createDirectories(file.getParent());
            try (BufferedWriter w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                GSON.toJson(cfg, w);
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to save freeze_config.json", e);
        }
    }

    private static FreezeConfig defaults() {
        FreezeConfig cfg = new FreezeConfig();
        cfg.blockChat = false;
        cfg.unfreezeOnLogout = false;
        cfg.showActionbar = true;
        cfg.showBossbar = true;
        cfg.allowCommands.addAll(Arrays.asList(
                "r","reply",
                "ticket","report"
        ));
        return cfg;
    }
}
