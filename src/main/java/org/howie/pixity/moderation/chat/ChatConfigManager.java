package org.howie.pixity.moderation.chat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public final class ChatConfigManager {

    private final Logger logger;
    private final Path file;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private ChatConfig config = new ChatConfig();

    public ChatConfigManager(final Logger logger, final Path file) {
        this.logger = logger;
        this.file = file;
        loadOrCreate();
    }

    public ChatConfig config() {
        return this.config;
    }

    public void reload() {
        loadOrCreate();
    }

    private void loadOrCreate() {


        try {
            if (!Files.exists(file)) {
                Files.createDirectories(file.getParent());
                config = defaultConfig();
                Files.writeString(file, gson.toJson(config));
                return;
            }

            ChatConfig loaded = gson.fromJson(Files.readString(file), ChatConfig.class);
            if (loaded == null) loaded = defaultConfig();


            if (loaded.rankColors == null) loaded.rankColors = new HashMap<>();
            if (loaded.rainbowGroups == null) loaded.rainbowGroups = new java.util.ArrayList<>();

            if (loaded.nameGradients == null) loaded.nameGradients = new HashMap<>();
            if (loaded.messageGradients == null) loaded.messageGradients = new HashMap<>();

            seedMissingDefaults(loaded);

            config = loaded;

        } catch (Throwable t) {
            try { logger.error("[PixityModeration] Failed to load chat config: " + file, t); } catch (Throwable ignored) {}
            config = defaultConfig();
        }
    }

    private void seedMissingDefaults(final ChatConfig c) {

        if (c.rainbowGroups.isEmpty()) c.rainbowGroups.add("god");


        for (var entry : c.rankColors.entrySet()) {
            String group = entry.getKey().toLowerCase();
            String color = entry.getValue();

            c.nameGradients.putIfAbsent(group, new ChatConfig.Gradient(color, color));
            c.messageGradients.putIfAbsent(group, new ChatConfig.Gradient("#ffffff", "#ffffff"));
        }

        c.nameGradients.putIfAbsent("owner", new ChatConfig.Gradient("#ff0000", "#ffaa00"));
        c.nameGradients.putIfAbsent("admin", new ChatConfig.Gradient("#ff5555", "#aa0000"));
        c.nameGradients.putIfAbsent("mod", new ChatConfig.Gradient("#ff55ff", "#aa00aa"));

        java.util.function.BiConsumer<String, String> putIfMissing = (k, v) -> {
            if (k == null || v == null) return;
            String key = k.trim().toLowerCase(java.util.Locale.ROOT);
            if (key.isEmpty()) return;
            if (!c.rankColors.containsKey(key)) c.rankColors.put(key, v);
        };


        putIfMissing.accept("owner", "#81F0E1");
        putIfMissing.accept("admin", "#FF5555");
        putIfMissing.accept("headadmin", "#AA0000");
        putIfMissing.accept("manager", "#E64545");
        putIfMissing.accept("developer", "#5555FF");
        putIfMissing.accept("srmod", "#AA00AA");
        putIfMissing.accept("mod", "#FF55FF");


        putIfMissing.accept("common", "#55FF55");
        putIfMissing.accept("uncommon", "#00AA00");
        putIfMissing.accept("rare", "#5555FF");
        putIfMissing.accept("mythical", "#AA00AA");
        putIfMissing.accept("legendary", "#FFAA00");


        putIfMissing.accept("elite1", "#55FFFF");
        putIfMissing.accept("elite2", "#00AAAA");
        putIfMissing.accept("elite3", "#FF55FF");
        putIfMissing.accept("elite4", "#AA00AA");
        putIfMissing.accept("champion", "#FFAA00");


        putIfMissing.accept("normalgymleader", "#AAAAAA");
        putIfMissing.accept("firegymleader", "#FF5555");
        putIfMissing.accept("watergymleader", "#5555FF");
        putIfMissing.accept("grassgymleader", "#55FF55");
        putIfMissing.accept("electricgymleader", "#FFFF55");
        putIfMissing.accept("icegymleader", "#55FFFF");
        putIfMissing.accept("fightinggymleader", "#AA0000");
        putIfMissing.accept("poisongymleader", "#AA00AA");
        putIfMissing.accept("groundgymleader", "#FFAA00");
        putIfMissing.accept("flyinggymleader", "#00AAAA");
        putIfMissing.accept("psychicgymleader", "#FF4FD8");


        putIfMissing.accept("buggymleader", "#7CCB2D");
        putIfMissing.accept("rockgymleader", "#8B7B4A");
        putIfMissing.accept("ghostgymleader", "#7A4FD6");
        putIfMissing.accept("dragongymleader", "#6A3DFF");
        putIfMissing.accept("darkgymleader", "#2B2B2B");
        putIfMissing.accept("steelgymleader", "#B0B7C6");
        putIfMissing.accept("fairygymleader", "#FF7AD9");


        putIfMissing.accept("default", "#C8C8C8");
    }

    private ChatConfig defaultConfig() {
        ChatConfig c = new ChatConfig();
        c.defaultNameHex = "#C8C8C8";
        c.defaultMessageHex = "#C8C8C8";
        c.rankFormat = "{LP_PREFIX} {DISPLAYNAME} {LP_SUFFIX} 》";
        c.useLuckPermsPrefixSuffix = true;
        c.useLuckPermsPrimaryGroupForRank = true;
        c.godRainbow = true;


        c.rainbowGroups.add("god");


        seedMissingDefaults(c);

        return c;
    }
}
