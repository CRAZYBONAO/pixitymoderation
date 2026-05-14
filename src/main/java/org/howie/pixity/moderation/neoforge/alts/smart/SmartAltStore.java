
package org.howie.pixity.moderation.neoforge.alts.smart;

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


public final class SmartAltStore {

    public static final class IpEntry {
        public String ip;
        public long ts;
        public IpEntry() {}
        public IpEntry(final String ip, final long ts) { this.ip = ip; this.ts = ts; }
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type TYPE = new TypeToken<Map<String, List<IpEntry>>>(){}.getType();

    private final Logger logger;
    private final Path file;

    public SmartAltStore(final Logger logger, final Path dataDir) {
        this.logger = logger;
        this.file = dataDir.resolve("smart_alts_ip.json");
    }

    public synchronized Map<UUID, List<IpEntry>> load() {
        try {
            if (!Files.exists(file)) {
                save(Collections.emptyMap());
                return new HashMap<>();
            }
            try (BufferedReader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                Map<String, List<IpEntry>> raw = GSON.fromJson(r, TYPE);
                Map<UUID, List<IpEntry>> out = new HashMap<>();
                if (raw != null) {
                    for (Map.Entry<String, List<IpEntry>> e : raw.entrySet()) {
                        try {
                            UUID u = UUID.fromString(e.getKey());
                            List<IpEntry> list = (e.getValue() == null) ? new ArrayList<>() : new ArrayList<>(e.getValue());
                            out.put(u, list);
                        } catch (Exception ignored) {}
                    }
                }
                return out;
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed loading smart alt store", e);
            return new HashMap<>();
        }
    }

    public synchronized void save(final Map<UUID, List<IpEntry>> map) {
        try {
            Map<String, List<IpEntry>> raw = new LinkedHashMap<>();
            for (Map.Entry<UUID, List<IpEntry>> e : map.entrySet()) {
                if (e.getKey() == null) continue;
                List<IpEntry> list = (e.getValue() == null) ? new ArrayList<>() : e.getValue();
                raw.put(e.getKey().toString(), list);
            }
            Files.createDirectories(file.getParent());
            try (BufferedWriter w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                GSON.toJson(raw, w);
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed saving smart alt store", e);
        }
    }
}
