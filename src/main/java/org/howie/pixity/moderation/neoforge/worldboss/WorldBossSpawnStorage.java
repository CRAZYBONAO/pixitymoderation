package org.howie.pixity.moderation.neoforge.worldboss;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.google.gson.JsonObject;

import net.minecraft.core.BlockPos;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.server.MinecraftServer;

import net.minecraft.world.level.Level;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class WorldBossSpawnStorage {





    private static final Gson GSON =
            new GsonBuilder()
                    .setPrettyPrinting()
                    .create();





    private static File getFile(
            MinecraftServer server
    ) {

        return new File(

                "config/pixity",

                "pixity_worldboss_spawn.json"
        );
    }





    public static void save(
            MinecraftServer server,

            ResourceKey<Level> dimension,

            BlockPos pos
    ) {

        try {

            JsonObject json =
                    new JsonObject();

            json.addProperty(
                    "dimension",
                    dimension.location()
                            .toString()
            );

            json.addProperty(
                    "x",
                    pos.getX()
            );

            json.addProperty(
                    "y",
                    pos.getY()
            );

            json.addProperty(
                    "z",
                    pos.getZ()
            );

            File file =
                    getFile(server);

            file.getParentFile()
                    .mkdirs();

            FileWriter writer =
                    new FileWriter(file);

            GSON.toJson(
                    json,
                    writer
            );

            writer.close();

            System.out.println(
                    "[WorldBoss] Saved spawn location."
            );

        } catch (Exception e) {

            e.printStackTrace();
        }
    }





    public static void load(
            MinecraftServer server
    ) {

        try {

            File file =
                    getFile(server);

            if (!file.exists()) {

                System.out.println(
                        "[WorldBoss] No saved spawn location found."
                );

                return;
            }

            JsonObject json =
                    GSON.fromJson(

                            new FileReader(file),

                            JsonObject.class
                    );

            ResourceKey<Level> dimension =
                    ResourceKey.create(

                            net.minecraft.core.registries.Registries.DIMENSION,

                            ResourceLocation.parse(
                                    json.get("dimension")
                                            .getAsString()
                            )
                    );

            BlockPos pos =
                    new BlockPos(

                            json.get("x")
                                    .getAsInt(),

                            json.get("y")
                                    .getAsInt(),

                            json.get("z")
                                    .getAsInt()
                    );

            WorldBossSpawnPoint.load(
                    dimension,
                    pos
            );

            System.out.println(
                    "[WorldBoss] Loaded saved spawn location."
            );

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}