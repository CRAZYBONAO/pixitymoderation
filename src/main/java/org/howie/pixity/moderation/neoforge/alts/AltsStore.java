package org.howie.pixity.moderation.neoforge.alts;

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


public final class AltsStore {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type TYPE = new TypeToken<Map<String, List<String>>>(){}.getType();

    private final Logger logger;
    private final Path file;

    public AltsStore(final Logger logger, final Path dataDir) {
        this.logger = logger;
        this.file = dataDir.resolve("alts.json");
    }

    public synchronized Map<UUID, Set<UUID>> load() {
        try {
            if (!Files.exists(file)) {
                save(Collections.emptyMap());
                return new HashMap<>();
            }
            try (BufferedReader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                Map<String, List<String>> raw = GSON.fromJson(r, TYPE);
                Map<UUID, Set<UUID>> out = new HashMap<>();
                if (raw != null) {
                    for (Map.Entry<String, List<String>> e : raw.entrySet()) {
                        try {
                            UUID k = UUID.fromString(e.getKey());
                            Set<UUID> set = new LinkedHashSet<>();
                            if (e.getValue() != null) {
                                for (String v : e.getValue()) {
                                    try { set.add(UUID.fromString(v)); } catch (Exception ignored) {}
                                }
                            }
                            if (!set.isEmpty()) out.put(k, set);
                        } catch (Exception ignored) {}
                    }
                }
                return out;
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed loading alts", e);
            return new HashMap<>();
        }
    }

    public synchronized void save(final Map<UUID, Set<UUID>> map) {
        try {
            Map<String, List<String>> raw = new LinkedHashMap<>();
            for (Map.Entry<UUID, Set<UUID>> e : map.entrySet()) {
                if (e.getKey() == null || e.getValue() == null || e.getValue().isEmpty()) continue;
                List<String> vals = new ArrayList<>();
                for (UUID u : e.getValue()) if (u != null) vals.add(u.toString());
                raw.put(e.getKey().toString(), vals);
            }
            Files.createDirectories(file.getParent());
            try (BufferedWriter w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                GSON.toJson(raw, w);
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed saving alts", e);
        }
    }
}
