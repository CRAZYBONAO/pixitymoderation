package org.howie.pixity.moderation.neoforge.reports.gui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import net.neoforged.fml.loading.FMLPaths;


public final class ReportsGuiConfigStore {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static volatile ReportsGuiConfig cached;

    private ReportsGuiConfigStore() {}

    public static ReportsGuiConfig get() {
        ReportsGuiConfig c = cached;
        if (c != null) return c;

        synchronized (ReportsGuiConfigStore.class) {
            if (cached != null) return cached;

            try {
                Path cfgDir = FMLPaths.CONFIGDIR.get().resolve("pixitymoderation");
                Files.createDirectories(cfgDir);
                Path file = cfgDir.resolve("reports_gui.json");

                if (Files.exists(file)) {
                    String json = Files.readString(file, StandardCharsets.UTF_8);
                    ReportsGuiConfig parsed = GSON.fromJson(json, ReportsGuiConfig.class);
                    if (parsed == null) parsed = new ReportsGuiConfig();
                    if (parsed.presetCloseReasons == null || parsed.presetCloseReasons.isEmpty()) {
                        parsed.presetCloseReasons = new ReportsGuiConfig().presetCloseReasons;
                    }
                    cached = parsed;
                    return cached;
                } else {
                    ReportsGuiConfig def = new ReportsGuiConfig();
                    Files.writeString(file, GSON.toJson(def), StandardCharsets.UTF_8);
                    cached = def;
                    return cached;
                }
            } catch (Exception e) {
                cached = new ReportsGuiConfig();
                return cached;
            }
        }
    }


    public static void invalidate() {
        cached = null;
    }
}
