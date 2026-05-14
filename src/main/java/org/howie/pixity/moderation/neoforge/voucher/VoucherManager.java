package org.howie.pixity.moderation.neoforge.voucher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.*;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

public class VoucherManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final Path FILE = Paths.get("config/pixity/vouchers.json");

    private static final Map<String, VoucherData> vouchers = new HashMap<>();


    private static MinecraftServer SERVER;

    public static void init(MinecraftServer server) {
        SERVER = server;
    }

    public static class VoucherData {
        public String display;
        public String command;
        public String item;
    }




    public static void load() {
        try {
            if (!Files.exists(FILE)) {
                Files.createDirectories(FILE.getParent());
                save();
                return;
            }

            Reader reader = Files.newBufferedReader(FILE);

            Type type = new TypeToken<Map<String, VoucherData>>() {}.getType();

            Map<String, VoucherData> data = GSON.fromJson(reader, type);

            if (data != null) {
                vouchers.clear();
                vouchers.putAll(data);
            }

            reader.close();

            System.out.println("[Pixity] Loaded " + vouchers.size() + " vouchers.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public static void save() {
        try {
            Writer writer = Files.newBufferedWriter(FILE);

            GSON.toJson(vouchers, writer);

            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public static void create(String id, String display, String command, ItemStack itemStack) {

        if (SERVER == null) {
            throw new IllegalStateException("VoucherManager not initialized with server!");
        }

        VoucherData data = new VoucherData();
        data.display = display;
        data.command = command;

        try {

            CompoundTag tag = (CompoundTag) itemStack.save(SERVER.registryAccess());
            data.item = tag.toString();

        } catch (Exception e) {
            e.printStackTrace();
            data.item = new CompoundTag().toString();
        }

        vouchers.put(id.toLowerCase(), data);

        save();
    }




    public static boolean delete(String id) {

        if (!vouchers.containsKey(id.toLowerCase())) return false;

        vouchers.remove(id.toLowerCase());

        save();

        return true;
    }




    public static VoucherData get(String id) {
        return vouchers.get(id.toLowerCase());
    }

    public static boolean exists(String id) {
        return vouchers.containsKey(id.toLowerCase());
    }

    public static Set<String> getAll() {
        return vouchers.keySet();
    }
}