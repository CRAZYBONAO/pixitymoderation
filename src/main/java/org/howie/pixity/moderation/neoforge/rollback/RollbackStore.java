package org.howie.pixity.moderation.neoforge.rollback;

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

public final class RollbackStore {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type TYPE = new TypeToken<List<RollbackEntry>>(){}.getType();

    private final Logger logger;
    private final Path file;

    public RollbackStore(final Logger logger, final Path dataDir) {
        this.logger = logger;
        this.file = dataDir.resolve("rollback.json");
    }

    public synchronized List<RollbackEntry> load() {
        try {
            if (!Files.exists(file)) return new ArrayList<>();
            try (BufferedReader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                List<RollbackEntry> list = GSON.fromJson(r, TYPE);
                if (list == null) list = new ArrayList<>();
                return list;
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to load rollback.json", e);
            return new ArrayList<>();
        }
    }

    public synchronized void save(final List<RollbackEntry> list) {
        try {
            Files.createDirectories(file.getParent());
            try (BufferedWriter w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                GSON.toJson(list, TYPE, w);
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to save rollback.json", e);
        }
    }
}
