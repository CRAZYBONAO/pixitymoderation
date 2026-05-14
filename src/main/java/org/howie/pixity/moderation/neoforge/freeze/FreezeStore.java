package org.howie.pixity.moderation.neoforge.freeze;

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

public final class FreezeStore {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type TYPE = new TypeToken<Map<UUID, FreezeRecord>>(){}.getType();

    private final Logger logger;
    private final Path file;

    public FreezeStore(final Logger logger, final Path dataDir) {
        this.logger = logger;
        this.file = dataDir.resolve("frozen.json");
    }

    public Map<UUID, FreezeRecord> load() {
        try {
            if (!Files.exists(file)) return new HashMap<>();
            try (BufferedReader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                Map<UUID, FreezeRecord> m = GSON.fromJson(r, TYPE);
                return m != null ? new HashMap<>(m) : new HashMap<>();
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to load frozen.json", e);
            return new HashMap<>();
        }
    }

    public void save(final Map<UUID, FreezeRecord> map) {
        try {
            Files.createDirectories(file.getParent());
            try (BufferedWriter w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                GSON.toJson(map, TYPE, w);
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to save frozen.json", e);
        }
    }
}
