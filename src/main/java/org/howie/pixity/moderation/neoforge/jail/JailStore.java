package org.howie.pixity.moderation.neoforge.jail;

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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class JailStore {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final Type JAILS_TYPE = new TypeToken<Map<String, org.howie.pixity.moderation.neoforge.tp.WarpPos>>(){}.getType();
    private static final Type ACTIVE_TYPE = new TypeToken<Map<UUID, JailRecord>>(){}.getType();

    private final Logger logger;
    private final Path jailsFile;
    private final Path activeFile;

    public JailStore(final Logger logger, final Path dir) {
        this.logger = logger;
        this.jailsFile = dir.resolve("jails.json");
        this.activeFile = dir.resolve("jailed.json");
    }

    public Map<String, org.howie.pixity.moderation.neoforge.tp.WarpPos> loadJails() {
        try {
            if (!Files.exists(jailsFile)) return new HashMap<>();
            try (BufferedReader r = Files.newBufferedReader(jailsFile, StandardCharsets.UTF_8)) {
                Map<String, org.howie.pixity.moderation.neoforge.tp.WarpPos> m = GSON.fromJson(r, JAILS_TYPE);
                return m != null ? new HashMap<>(m) : new HashMap<>();
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to load jails.json", e);
            return new HashMap<>();
        }
    }

    public void saveJails(final Map<String, org.howie.pixity.moderation.neoforge.tp.WarpPos> jails) {
        try {
            Files.createDirectories(jailsFile.getParent());
            try (BufferedWriter w = Files.newBufferedWriter(jailsFile, StandardCharsets.UTF_8)) {
                GSON.toJson(jails, JAILS_TYPE, w);
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to save jails.json", e);
        }
    }

    public Map<UUID, JailRecord> loadActive() {
        try {
            if (!Files.exists(activeFile)) return new HashMap<>();
            try (BufferedReader r = Files.newBufferedReader(activeFile, StandardCharsets.UTF_8)) {
                Map<UUID, JailRecord> m = GSON.fromJson(r, ACTIVE_TYPE);
                return m != null ? new HashMap<>(m) : new HashMap<>();
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to load jailed.json", e);
            return new HashMap<>();
        }
    }

    public void saveActive(final Map<UUID, JailRecord> active) {
        try {
            Files.createDirectories(activeFile.getParent());
            try (BufferedWriter w = Files.newBufferedWriter(activeFile, StandardCharsets.UTF_8)) {
                GSON.toJson(active, ACTIVE_TYPE, w);
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to save jailed.json", e);
        }
    }
}
