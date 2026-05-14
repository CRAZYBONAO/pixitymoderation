package org.howie.pixity.moderation.neoforge.reports;

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

public final class ReportsStore {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type TYPE = new TypeToken<List<ReportEntry>>(){}.getType();

    private final Logger logger;
    private final Path file;

    public ReportsStore(final Logger logger, final Path dataDir) {
        this.logger = logger;
        this.file = dataDir.resolve("reports.json");
    }

    public synchronized List<ReportEntry> load() {
        try {
            if (!Files.exists(file)) {
                save(new ArrayList<>());
                return new ArrayList<>();
            }
            try (BufferedReader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                List<ReportEntry> list = GSON.fromJson(r, TYPE);
                return (list == null) ? new ArrayList<>() : list;
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to load reports.json; using empty.", e);
            return new ArrayList<>();
        }
    }

    public synchronized void save(final List<ReportEntry> list) {
        try {
            Files.createDirectories(file.getParent());
            try (BufferedWriter w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                GSON.toJson(list, TYPE, w);
            }
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed to save reports.json", e);
        }
    }
}
