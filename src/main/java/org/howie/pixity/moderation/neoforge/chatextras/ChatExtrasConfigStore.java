package org.howie.pixity.moderation.neoforge.chatextras;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ChatExtrasConfigStore {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Logger logger;
    private final Path file;

    public ChatExtrasConfigStore(final Logger logger, final Path dataDir) {
        this.logger = logger;
        this.file = dataDir.resolve("chat_extras_config.json");
    }

    public ChatExtrasConfig loadOrCreate() {
        try {
            if (!Files.exists(file)) {
                ChatExtrasConfig cfg = new ChatExtrasConfig();
                save(cfg);
                return cfg;
            }
            try (BufferedReader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                ChatExtrasConfig cfg = GSON.fromJson(r, ChatExtrasConfig.class);
                if (cfg == null) cfg = new ChatExtrasConfig();

                if (cfg.emojiMaxPerMessage < 0) cfg.emojiMaxPerMessage = 15;
                if (cfg.mentionColor == null) cfg.mentionColor = "&b";
                if (cfg.mentionActionbarText == null) cfg.mentionActionbarText = "&eYou were mentioned by &f{SENDER}&e!";
                if (cfg.mentionBypassPermission == null) cfg.mentionBypassPermission = "pixity.mention.bypass";


                if (cfg.emojis == null) cfg.emojis = new java.util.LinkedHashMap<>();
                return cfg;
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to load chat_extras_config.json; using defaults.", e);
            return new ChatExtrasConfig();
        }
    }

    private void save(final ChatExtrasConfig cfg) {
        try {
            Files.createDirectories(file.getParent());
            try (BufferedWriter w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                GSON.toJson(cfg, w);
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to save chat_extras_config.json", e);
        }
    }
}
