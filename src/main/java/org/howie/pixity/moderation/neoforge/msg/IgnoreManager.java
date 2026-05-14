package org.howie.pixity.moderation.neoforge.msg;

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
import java.util.concurrent.ConcurrentHashMap;

public final class IgnoreManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type TYPE = new TypeToken<Map<UUID, Set<UUID>>>(){}.getType();

    private final Logger logger;
    private final Path file;

    private final Map<UUID, Set<UUID>> ignores = new ConcurrentHashMap<>();

    public IgnoreManager(final Logger logger, final Path file) {
        this.logger = logger;
        this.file = file;
        load();
    }

    public boolean isIgnoring(final UUID owner, final UUID target) {
        if (owner == null || target == null) return false;
        Set<UUID> set = ignores.get(owner);
        return set != null && set.contains(target);
    }

    public boolean toggleIgnore(final UUID owner, final UUID target) {
        if (owner == null || target == null) return false;
        Set<UUID> set = ignores.computeIfAbsent(owner, k -> ConcurrentHashMap.newKeySet());
        boolean nowIgnored;
        if (set.contains(target)) {
            set.remove(target);
            nowIgnored = false;
        } else {
            set.add(target);
            nowIgnored = true;
        }
        save();
        return nowIgnored;
    }

    public Set<UUID> ignoredBy(final UUID owner) {
        Set<UUID> set = ignores.get(owner);
        return set == null ? Set.of() : Set.copyOf(set);
    }

    private void load() {
        try {
            if (!Files.exists(file)) return;
            try (BufferedReader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                Map<UUID, Set<UUID>> data = GSON.fromJson(r, TYPE);
                if (data != null) {
                    ignores.clear();
                    for (Map.Entry<UUID, Set<UUID>> e : data.entrySet()) {
                        ignores.put(e.getKey(), ConcurrentHashMap.newKeySet(e.getValue() == null ? 0 : e.getValue().size()));
                        if (e.getValue() != null) ignores.get(e.getKey()).addAll(e.getValue());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to load ignores.json", e);
        }
    }

    private void save() {
        try {
            Files.createDirectories(file.getParent());
            Map<UUID, Set<UUID>> snap = new LinkedHashMap<>();
            for (Map.Entry<UUID, Set<UUID>> e : ignores.entrySet()) {
                snap.put(e.getKey(), new LinkedHashSet<>(e.getValue()));
            }
            try (BufferedWriter w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                GSON.toJson(snap, TYPE, w);
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to save ignores.json", e);
        }
    }
}
