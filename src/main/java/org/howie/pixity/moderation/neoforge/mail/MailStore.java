package org.howie.pixity.moderation.neoforge.mail;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class MailStore {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type LIST_TYPE = new TypeToken<List<MailMessage>>(){}.getType();

    private final Logger logger;
    private final Path file;

    public MailStore(final Logger logger, final Path dataDir) {
        this.logger = logger;
        this.file = dataDir.resolve("mail.json");
    }

    public synchronized List<MailMessage> load() {
        try {
            if (!Files.exists(file)) return new ArrayList<>();
            try (BufferedReader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                List<MailMessage> list = GSON.fromJson(r, LIST_TYPE);
                if (list == null) list = new ArrayList<>();
                return list;
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to load mail.json", e);
            return new ArrayList<>();
        }
    }

    public synchronized void save(final List<MailMessage> list) {
        try {
            Files.createDirectories(file.getParent());
            try (BufferedWriter w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                GSON.toJson(list, LIST_TYPE, w);
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to save mail.json", e);
        }
    }
}
