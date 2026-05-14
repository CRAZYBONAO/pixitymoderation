package org.howie.pixity.moderation.tab;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;


public final class TabListConfigManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Logger logger;
    private final Path file;
    private TabListConfig cached;

    public TabListConfigManager(final Logger logger, final Path dataDir) {
        this.logger = logger;
        this.file = dataDir.resolve("tablist_config.json");
        this.cached = loadOrCreate();
    }

    public TabListConfig get() {
        return cached;
    }

    public void reload() {
        this.cached = loadOrCreate();
    }

    private TabListConfig loadOrCreate() {
        try {
            if (!Files.exists(file)) {
                TabListConfig cfg = defaults();
                save(cfg);
                return cfg;
            }
            try (BufferedReader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                TabListConfig cfg = GSON.fromJson(r, TabListConfig.class);
                if (cfg == null) cfg = defaults();
                if (cfg.titleLines == null) cfg.titleLines = defaults().titleLines;
                if (cfg.footerLines == null) cfg.footerLines = defaults().footerLines;
                if (cfg.playerFormat == null || cfg.playerFormat.isBlank()) cfg.playerFormat = defaults().playerFormat;
                if (cfg.sortOrder == null) cfg.sortOrder = defaults().sortOrder;
                if (cfg.updateEveryTicks <= 0) cfg.updateEveryTicks = defaults().updateEveryTicks;
                return cfg;
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to load tablist_config.json; using defaults.", e);
            return defaults();
        }
    }

    private void save(final TabListConfig cfg) {
        try {
            Files.createDirectories(file.getParent());
            try (BufferedWriter w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                GSON.toJson(cfg, w);
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to save tablist_config.json", e);
        }
    }

    private static TabListConfig defaults() {
        TabListConfig cfg = new TabListConfig();
        cfg.titleLines.addAll(Arrays.asList(
                "&b&lPixity Network",
                "&7TPS: &a{TPS} &8| &7MSPT: &a{MTPS} &8| &7CPU: &a{CPU}% &8| &7MEM: &a{MEMORY} &8| &7Players: &a{PLAYERCOUNT}"
        ));
        cfg.footerLines.addAll(Arrays.asList(
                "&7Have fun! &8- &bPixity"
        ));
        cfg.playerFormat = "{PREFIX}{NAME}{SUFFIX}";
        cfg.sortOrder.addAll(Arrays.asList("owner","admin","mod","vip","default"));
        cfg.updateEveryTicks = 20;
        return cfg;
    }
}
