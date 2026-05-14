package org.howie.pixity.moderation.neoforge.alts.smart;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import net.neoforged.fml.loading.FMLPaths;

public final class SmartAltConfigStore {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static volatile SmartAltConfig cached;

    private SmartAltConfigStore() {}

    public static SmartAltConfig get() {
        SmartAltConfig c = cached;
        if (c != null) return c;

        synchronized (SmartAltConfigStore.class) {
            if (cached != null) return cached;

            try {
                Path dir = FMLPaths.CONFIGDIR.get().resolve("pixitymoderation");
                Files.createDirectories(dir);

                Path file = dir.resolve("smart_alts.json");

                if (Files.exists(file)) {
                    String json = Files.readString(file, StandardCharsets.UTF_8);
                    SmartAltConfig parsed = GSON.fromJson(json, SmartAltConfig.class);
                    cached = (parsed != null) ? parsed : new SmartAltConfig();
                } else {
                    SmartAltConfig def = new SmartAltConfig();
                    Files.writeString(file, GSON.toJson(def), StandardCharsets.UTF_8);
                    cached = def;
                }

                return cached;

            } catch (Exception e) {
                cached = new SmartAltConfig();
                return cached;
            }
        }
    }

    public static void invalidate() {
        cached = null;
    }
}