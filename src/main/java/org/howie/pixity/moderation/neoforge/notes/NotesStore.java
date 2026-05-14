package org.howie.pixity.moderation.neoforge.notes;

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
import java.util.*;

public final class NotesStore {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type TYPE = new TypeToken<Map<String, List<NoteEntry>>>(){}.getType();

    private final Logger logger;
    private final Path file;

    public NotesStore(final Logger logger, final Path dataDir) {
        this.logger = logger;
        this.file = dataDir.resolve("notes.json");
    }

    public synchronized Map<UUID, List<NoteEntry>> load() {
        try {
            if (!Files.exists(file)) {
                save(Collections.emptyMap());
                return new HashMap<>();
            }
            try (BufferedReader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                Map<String, List<NoteEntry>> raw = GSON.fromJson(r, TYPE);
                Map<UUID, List<NoteEntry>> out = new HashMap<>();
                if (raw != null) {
                    for (Map.Entry<String, List<NoteEntry>> e : raw.entrySet()) {
                        try {
                            UUID u = UUID.fromString(e.getKey());
                            List<NoteEntry> list = (e.getValue() == null) ? new ArrayList<>() : new ArrayList<>(e.getValue());
                            out.put(u, list);
                        } catch (Exception ignored) {}
                    }
                }
                return out;
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to load notes.json; using empty.", e);
            return new HashMap<>();
        }
    }

    public synchronized void save(final Map<UUID, List<NoteEntry>> data) {
        try {
            Files.createDirectories(file.getParent());
            Map<String, List<NoteEntry>> raw = new LinkedHashMap<>();
            for (Map.Entry<UUID, List<NoteEntry>> e : data.entrySet()) {
                raw.put(e.getKey().toString(), e.getValue());
            }
            try (BufferedWriter w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                GSON.toJson(raw, TYPE, w);
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to save notes.json", e);
        }
    }
}
