package org.howie.pixity.moderation.neoforge.chatcontrol;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ChatControlConfigStore {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Logger logger;
    private final Path file;

    public ChatControlConfigStore(final Logger logger, final Path dataDir) {
        this.logger = logger;
        this.file = dataDir.resolve("chatcontrol_config.json");
    }

    public ChatControlConfig loadOrCreate() {
        try {
            if (!Files.exists(file)) {
                ChatControlConfig cfg = new ChatControlConfig();
                save(cfg);
                return cfg;
            }
            try (BufferedReader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                ChatControlConfig cfg = GSON.fromJson(r, ChatControlConfig.class);
                if (cfg == null) cfg = new ChatControlConfig();
                if (cfg.defaultSlowchatSeconds <= 0) cfg.defaultSlowchatSeconds = 5;
                if (cfg.chatMutedMessage == null) cfg.chatMutedMessage = "&cChat is currently muted.";
                if (cfg.slowchatWaitMessage == null) cfg.slowchatWaitMessage = "&cPlease wait &f{SECONDS}&c seconds before chatting again.";
                return cfg;
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to load chatcontrol_config.json; using defaults.", e);
            return new ChatControlConfig();
        }
    }

    private void save(final ChatControlConfig cfg) {
        try {
            Files.createDirectories(file.getParent());
            try (BufferedWriter w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                GSON.toJson(cfg, w);
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to save chatcontrol_config.json", e);
        }
    }
}
