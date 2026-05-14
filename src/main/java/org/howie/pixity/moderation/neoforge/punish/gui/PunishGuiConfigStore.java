package org.howie.pixity.moderation.neoforge.punish.gui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import net.neoforged.fml.loading.FMLPaths;


public final class PunishGuiConfigStore {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static volatile PunishGuiConfig cached;

    private PunishGuiConfigStore() {}

    public static PunishGuiConfig get() {
        PunishGuiConfig c = cached;
        if (c != null) return c;

        synchronized (PunishGuiConfigStore.class) {
            if (cached != null) return cached;

            try {
                Path cfgDir = FMLPaths.CONFIGDIR.get().resolve("pixitymoderation");
                Files.createDirectories(cfgDir);
                Path file = cfgDir.resolve("punish_gui.json");

                if (Files.exists(file)) {
                    String json = Files.readString(file, StandardCharsets.UTF_8);
                    PunishGuiConfig parsed = GSON.fromJson(json, PunishGuiConfig.class);
                    if (parsed != null) {
                        cached = mergeWithDefaults(parsed);
                        return cached;
                    }
                }

                PunishGuiConfig def = PunishGuiConfig.defaults();
                Files.writeString(file, GSON.toJson(def), StandardCharsets.UTF_8);
                cached = def;
                return cached;

            } catch (Throwable t) {
                cached = PunishGuiConfig.defaults();
                return cached;
            }
        }
    }

    public static void reload() {
        cached = null;
        get();
    }

    private static PunishGuiConfig mergeWithDefaults(final PunishGuiConfig in) {
        PunishGuiConfig def = PunishGuiConfig.defaults();
        if (in == null) return def;


if (in.presetReasons == null) in.presetReasons = def.presetReasons;
for (String k : def.presetReasons.keySet()) {
    in.presetReasons.putIfAbsent(k, def.presetReasons.get(k));
}

if (in.presetDurations == null) in.presetDurations = def.presetDurations;
for (String k : def.presetDurations.keySet()) {
    in.presetDurations.putIfAbsent(k, def.presetDurations.get(k));
}

if (in.perGroupDurations == null) in.perGroupDurations = new java.util.HashMap<>();
if (in.recentReasonsMax <= 0) in.recentReasonsMax = def.recentReasonsMax;
if (in.recentReasonsRemember <= 0) in.recentReasonsRemember = def.recentReasonsRemember;

return in;
    }
}
