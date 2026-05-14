package org.howie.pixity.moderation.neoforge.inspect;

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
import java.util.List;
import java.util.Map;

public final class BlockLogStore {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type TYPE = new TypeToken<Map<String, List<BlockLogEntry>>>(){}.getType();

    private final Logger logger;
    private final Path file;

    public BlockLogStore(final Logger logger, final Path dataDir) {
        this.logger = logger;
        this.file = dataDir.resolve("blocklog.json");
    }

    public Map<String, List<BlockLogEntry>> load() {
        try {
            if (!Files.exists(file)) return new HashMap<>();
            try (BufferedReader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                Map<String, List<BlockLogEntry>> m = GSON.fromJson(r, TYPE);
                if (m == null) m = new HashMap<>();
                return m;
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to load blocklog.json", e);
            return new HashMap<>();
        }
    }

    public void save(final Map<String, List<BlockLogEntry>> data) {
        try {
            Files.createDirectories(file.getParent());
            try (BufferedWriter w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                GSON.toJson(data, TYPE, w);
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to save blocklog.json", e);
        }
    }
}
