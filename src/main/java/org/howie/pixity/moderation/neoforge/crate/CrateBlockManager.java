package org.howie.pixity.moderation.neoforge.crate;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.*;

public class CrateBlockManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = Paths.get("config/pixity/crate_blocks.json");

    private static final Map<String, String> blocks = new HashMap<>();

    private static String key(ServerLevel level, BlockPos pos) {
        return level.dimension().location() + ":" +
                pos.getX() + "," + pos.getY() + "," + pos.getZ();
    }

    public static void set(ServerLevel level, BlockPos pos, String crateId) {
        blocks.put(key(level, pos), crateId.toLowerCase());
        save();
    }

    public static void remove(ServerLevel level, BlockPos pos) {
        blocks.remove(key(level, pos));
        save();
    }

    public static String get(ServerLevel level, BlockPos pos) {
        return blocks.get(key(level, pos));
    }

    public static void load() {
        try {
            if (!Files.exists(FILE)) {
                Files.createDirectories(FILE.getParent());
                save();
                return;
            }

            Reader reader = Files.newBufferedReader(FILE);

            Type type = new TypeToken<Map<String, String>>(){}.getType();
            Map<String, String> data = GSON.fromJson(reader, type);

            blocks.clear();
            if (data != null) blocks.putAll(data);

            reader.close();

            System.out.println("[Pixity] Loaded " + blocks.size() + " crate blocks.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Map<String, String> getAll() {
        return blocks;
    }

    public static void save() {
        try {
            Writer writer = Files.newBufferedWriter(FILE);
            GSON.toJson(blocks, writer);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}