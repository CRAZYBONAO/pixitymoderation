package org.howie.pixity.moderation.neoforge.punish.gui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.neoforged.fml.loading.FMLPaths;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;


public final class RecentReasonsStore {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type TYPE = new TypeToken<Map<String, Map<String, List<String>>>>(){}.getType();

    private static volatile Map<String, Map<String, List<String>>> cache;

    private RecentReasonsStore() {}

    private static Path filePath() throws Exception {
        Path cfgDir = FMLPaths.CONFIGDIR.get().resolve("pixitymoderation");
        Files.createDirectories(cfgDir);
        return cfgDir.resolve("punish_recent_reasons.json");
    }

    public static Map<String, List<String>> getFor(final UUID staff) {
        if (staff == null) return new HashMap<>();
        ensureLoaded();
        return cache.getOrDefault(staff.toString(), new HashMap<>());
    }

    public static List<String> getForAction(final UUID staff, final String action) {
        if (staff == null || action == null) return List.of();
        Map<String, List<String>> m = getFor(staff);
        List<String> l = m.get(action.toUpperCase(Locale.ROOT));
        return l == null ? List.of() : l;
    }

    public static void add(final UUID staff, final String action, final String reason, final int rememberMax) {
        if (staff == null || action == null || reason == null) return;
        String a = action.toUpperCase(Locale.ROOT);
        String r = reason.trim();
        if (r.isEmpty()) return;

        ensureLoaded();

        cache.computeIfAbsent(staff.toString(), k -> new HashMap<>());
        Map<String, List<String>> m = cache.get(staff.toString());
        m.computeIfAbsent(a, k -> new ArrayList<>());
        List<String> list = m.get(a);


        list.removeIf(x -> x != null && x.equalsIgnoreCase(r));
        list.add(0, r);

        while (list.size() > Math.max(1, rememberMax)) {
            list.remove(list.size() - 1);
        }

        save();
    }

    private static void ensureLoaded() {
        if (cache != null) return;
        synchronized (RecentReasonsStore.class) {
            if (cache != null) return;
            try {
                Path f = filePath();
                if (Files.exists(f)) {
                    String json = Files.readString(f, StandardCharsets.UTF_8);
                    Map<String, Map<String, List<String>>> parsed = GSON.fromJson(json, TYPE);
                    cache = (parsed == null) ? new HashMap<>() : parsed;
                    return;
                }
            } catch (Throwable ignored) {}
            cache = new HashMap<>();
            save();
        }
    }

    private static void save() {
        try {
            Path f = filePath();
            Files.writeString(f, GSON.toJson(cache), StandardCharsets.UTF_8);
        } catch (Throwable ignored) {}
    }
}
