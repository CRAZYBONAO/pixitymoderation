package org.howie.pixity.moderation.neoforge.mail;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class MailConfigStore {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Logger logger;
    private final Path file;

    public MailConfigStore(final Logger logger, final Path dataDir) {
        this.logger = logger;
        this.file = dataDir.resolve("mail_config.json");
    }

    public MailConfig loadOrCreate() {
        try {
            if (!Files.exists(file)) {
                MailConfig cfg = new MailConfig();
                save(cfg);
                return cfg;
            }
            try (BufferedReader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                MailConfig cfg = GSON.fromJson(r, MailConfig.class);
                if (cfg == null) cfg = new MailConfig();
                if (cfg.expireDays < 0) cfg.expireDays = 14;
                if (cfg.notifyMessage == null) cfg.notifyMessage = "&6You have &e{COUNT}&6 unread mail. &7Use &f/mail inbox&7.";
                return cfg;
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to load mail_config.json; using defaults.", e);
            return new MailConfig();
        }
    }

    private void save(final MailConfig cfg) {
        try {
            Files.createDirectories(file.getParent());
            try (BufferedWriter w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                GSON.toJson(cfg, w);
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to save mail_config.json", e);
        }
    }
}
