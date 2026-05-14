package org.howie.pixity.moderation.neoforge.kits.firstjoin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class FirstJoinStore {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type TYPE = new TypeToken<Set<UUID>>(){}.getType();

    private final Logger logger;
    private final Path file;

    public FirstJoinStore(Logger logger, Path dir) {
        this.logger = logger;
        this.file = dir.resolve("firstjoin.json");
    }

    public Set<UUID> load() {
        try {
            if (!Files.exists(file)) return new HashSet<>();

            try (BufferedReader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                Set<UUID> set = GSON.fromJson(r, TYPE);
                return set != null ? set : new HashSet<>();
            }
        } catch (Exception e) {
            logger.error("Failed to load firstjoin.json", e);
            return new HashSet<>();
        }
    }

    public void save(Set<UUID> data) {
        try {
            Files.createDirectories(file.getParent());
            try (BufferedWriter w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                GSON.toJson(data, TYPE, w);
            }
        } catch (Exception e) {
            logger.error("Failed to save firstjoin.json", e);
        }
    }
}