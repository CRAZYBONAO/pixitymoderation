package org.howie.pixity.moderation.neoforge.punish;

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

public final class PunishStore {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final Type BANS_TYPE = new TypeToken<Map<UUID, ActiveBan>>(){}.getType();
    private static final Type HIST_TYPE = new TypeToken<Map<UUID, List<PunishEntry>>>(){}.getType();

    private final Logger logger;
    private final Path bansFile;
    private final Path historyFile;
    private final Path auditJsonl;

    public PunishStore(final Logger logger, final Path dataDir) {
        this.logger = logger;
        this.bansFile = dataDir.resolve("bans.json");
        this.historyFile = dataDir.resolve("history.json");
        this.auditJsonl = dataDir.resolve("audit.jsonl");
    }

    public Map<UUID, ActiveBan> loadBans() {
        try {
            if (!Files.exists(bansFile)) return new HashMap<>();
            try (BufferedReader r = Files.newBufferedReader(bansFile, StandardCharsets.UTF_8)) {
                Map<UUID, ActiveBan> m = GSON.fromJson(r, BANS_TYPE);
                return m != null ? new HashMap<>(m) : new HashMap<>();
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to load bans.json", e);
            return new HashMap<>();
        }
    }

    public void saveBans(final Map<UUID, ActiveBan> bans) {
        try {
            Files.createDirectories(bansFile.getParent());
            try (BufferedWriter w = Files.newBufferedWriter(bansFile, StandardCharsets.UTF_8)) {
                GSON.toJson(bans, BANS_TYPE, w);
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to save bans.json", e);
        }
    }

    public Map<UUID, List<PunishEntry>> loadHistory() {
        try {
            if (!Files.exists(historyFile)) return new HashMap<>();
            try (BufferedReader r = Files.newBufferedReader(historyFile, StandardCharsets.UTF_8)) {
                Map<UUID, List<PunishEntry>> m = GSON.fromJson(r, HIST_TYPE);
                return m != null ? new HashMap<>(m) : new HashMap<>();
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to load history.json", e);
            return new HashMap<>();
        }
    }

    public void saveHistory(final Map<UUID, List<PunishEntry>> history) {
        try {
            Files.createDirectories(historyFile.getParent());
            try (BufferedWriter w = Files.newBufferedWriter(historyFile, StandardCharsets.UTF_8)) {
                GSON.toJson(history, HIST_TYPE, w);
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to save history.json", e);
        }
    }

    public void appendAudit(final PunishEntry entry) {
        try {
            Files.createDirectories(auditJsonl.getParent());
            String json = GSON.toJson(entry);
            try (BufferedWriter w = Files.newBufferedWriter(auditJsonl, StandardCharsets.UTF_8,
                    Files.exists(auditJsonl) ? java.nio.file.StandardOpenOption.APPEND : java.nio.file.StandardOpenOption.CREATE)) {
                w.write(json);
                w.newLine();
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to append audit.jsonl", e);
        }
    }
}
