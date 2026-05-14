package org.howie.pixity.moderation.neoforge.rules;

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
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class RulesSeenStore {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type TYPE = new TypeToken<Set<UUID>>(){}.getType();

    private final Logger logger;
    private final Path file;

    public RulesSeenStore(final Logger logger, final Path file) {
        this.logger = logger;
        this.file = file;
    }

    public Set<UUID> load() {
        try {
            if (!Files.exists(file)) return new HashSet<>();
            try (BufferedReader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                Set<UUID> set = GSON.fromJson(r, TYPE);
                return set != null ? new HashSet<>(set) : new HashSet<>();
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to load rules_seen.json", e);
            return new HashSet<>();
        }
    }

    public void save(final Set<UUID> seen) {
        try {
            Files.createDirectories(file.getParent());
            try (BufferedWriter w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                GSON.toJson(seen, TYPE, w);
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to save rules_seen.json", e);
        }
    }
}
