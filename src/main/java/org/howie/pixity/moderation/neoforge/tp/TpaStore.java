package org.howie.pixity.moderation.neoforge.tp;

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

public final class TpaStore {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type TYPE = new TypeToken<Map<UUID, Boolean>>(){}.getType();

    private final Logger logger;
    private final Path file;

    public TpaStore(final Logger logger, final Path file) {
        this.logger = logger;
        this.file = file;
    }

    public Map<UUID, Boolean> loadToggles() {
        try {
            if (!Files.exists(file)) return new HashMap<>();
            try (BufferedReader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                Map<UUID, Boolean> m = GSON.fromJson(r, TYPE);
                return m != null ? new HashMap<>(m) : new HashMap<>();
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to load tptoggle.json", e);
            return new HashMap<>();
        }
    }

    public void saveToggles(final Map<UUID, Boolean> toggles) {
        try {
            Files.createDirectories(file.getParent());
            try (BufferedWriter w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                GSON.toJson(toggles, TYPE, w);
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to save tptoggle.json", e);
        }
    }
}
