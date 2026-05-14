package org.howie.pixity.moderation.neoforge.joinleave;

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

public final class JoinLeaveStore {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type MSG_TYPE = new TypeToken<Map<UUID, PlayerJoinLeave>>(){}.getType();
    private static final Type SEEN_TYPE = new TypeToken<Set<UUID>>(){}.getType();

    private final Logger logger;
    private final Path messagesFile;
    private final Path seenFile;

    public JoinLeaveStore(final Logger logger, final Path dataDir) {
        this.logger = logger;
        this.messagesFile = dataDir.resolve("joinleave_players.json");
        this.seenFile = dataDir.resolve("firstjoin_seen.json");
    }

    public Map<UUID, PlayerJoinLeave> loadMessages() {
        try {
            if (!Files.exists(messagesFile)) return new HashMap<>();
            try (BufferedReader r = Files.newBufferedReader(messagesFile, StandardCharsets.UTF_8)) {
                Map<UUID, PlayerJoinLeave> m = GSON.fromJson(r, MSG_TYPE);
                return m != null ? new HashMap<>(m) : new HashMap<>();
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to load joinleave_players.json", e);
            return new HashMap<>();
        }
    }

    public void saveMessages(final Map<UUID, PlayerJoinLeave> m) {
        try {
            Files.createDirectories(messagesFile.getParent());
            try (BufferedWriter w = Files.newBufferedWriter(messagesFile, StandardCharsets.UTF_8)) {
                GSON.toJson(m, MSG_TYPE, w);
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to save joinleave_players.json", e);
        }
    }

    public Set<UUID> loadSeen() {
        try {
            if (!Files.exists(seenFile)) return new HashSet<>();
            try (BufferedReader r = Files.newBufferedReader(seenFile, StandardCharsets.UTF_8)) {
                Set<UUID> s = GSON.fromJson(r, SEEN_TYPE);
                return s != null ? new HashSet<>(s) : new HashSet<>();
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to load firstjoin_seen.json", e);
            return new HashSet<>();
        }
    }

    public void saveSeen(final Set<UUID> seen) {
        try {
            Files.createDirectories(seenFile.getParent());
            try (BufferedWriter w = Files.newBufferedWriter(seenFile, StandardCharsets.UTF_8)) {
                GSON.toJson(seen, SEEN_TYPE, w);
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to save firstjoin_seen.json", e);
        }
    }
}
