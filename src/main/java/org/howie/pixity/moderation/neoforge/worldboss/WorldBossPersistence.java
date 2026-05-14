package org.howie.pixity.moderation.neoforge.worldboss;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.server.MinecraftServer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import java.util.Map;
import java.util.UUID;

public class WorldBossPersistence {

    private static final Gson GSON =
            new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

    private static File getFile(
            MinecraftServer server
    ) {

        return new File(

                "config/pixity",

                "pixity_worldboss_data.json"
        );
    }





    public static void save(
            MinecraftServer server
    ) {

        try {

            JsonObject json =
                    new JsonObject();

            json.addProperty(
                    "active",
                    WorldBossManager.isActive()
            );

            if (!WorldBossManager.isActive()) {

                File file =
                        getFile(server);





                if (file.getParentFile() != null) {

                    file.getParentFile().mkdirs();
                }

                FileWriter writer =
                        new FileWriter(file);

                GSON.toJson(
                        json,
                        writer
                );

                writer.close();

                return;
            }

            WorldBossDefinition boss =
                    WorldBossManager.getCurrent();

            json.addProperty(
                    "species",
                    boss.species
            );

            json.addProperty(
                    "display",
                    boss.display
            );

            json.addProperty(
                    "maxHealth",
                    boss.maxHealth
            );

            json.addProperty(
                    "scale",
                    boss.scale
            );

            json.addProperty(
                    "rewardTokens",
                    boss.rewardTokens
            );

            json.addProperty(
                    "rewardMoney",
                    boss.rewardMoney
            );

            json.addProperty(
                    "durationMinutes",
                    boss.durationMinutes
            );

            json.addProperty(
                    "health",
                    WorldBossManager.getHealth()
            );

            json.addProperty(
                    "endTime",
                    WorldBossManager.getEndTime()
            );





            JsonArray damageArray =
                    new JsonArray();

            for (Map.Entry<UUID, Long> entry
                    : WorldBossDamageTracker
                    .getAll()
                    .entrySet()) {

                JsonObject obj =
                        new JsonObject();

                obj.addProperty(
                        "uuid",
                        entry.getKey()
                                .toString()
                );

                obj.addProperty(
                        "damage",
                        entry.getValue()
                );

                damageArray.add(obj);
            }

            json.add(
                    "damageTracker",
                    damageArray
            );





            JsonArray attemptArray =
                    new JsonArray();

            for (Map.Entry<UUID, Integer> entry
                    : WorldBossBattleAttempts
                    .getAll()
                    .entrySet()) {

                JsonObject obj =
                        new JsonObject();

                obj.addProperty(
                        "uuid",
                        entry.getKey()
                                .toString()
                );

                obj.addProperty(
                        "attempts",
                        entry.getValue()
                );

                attemptArray.add(obj);
            }

            json.add(
                    "attempts",
                    attemptArray
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
                    "[WorldBoss] Saved persistence data."
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
                return;
            }

            JsonObject json =
                    GSON.fromJson(

                            new FileReader(file),

                            JsonObject.class
                    );

            boolean active =
                    json.get("active")
                            .getAsBoolean();

            if (!active) {
                return;
            }

            WorldBossDefinition definition =
                    new WorldBossDefinition(

                            json.get("species")
                                    .getAsString(),

                            json.get("display")
                                    .getAsString(),

                            json.get("maxHealth")
                                    .getAsLong(),

                            json.get("scale")
                                    .getAsFloat(),

                            json.get("rewardTokens")
                                    .getAsInt(),

                            json.get("rewardMoney")
                                    .getAsInt(),

                            json.get("durationMinutes")
                                    .getAsInt()
                    );

            WorldBossManager.restore(

                    server,

                    definition,

                    json.get("health")
                            .getAsLong(),

                    json.get("endTime")
                            .getAsLong()
            );

            WorldBossBossBar.create(server);

            JsonArray damageArray =
                    json.getAsJsonArray(
                            "damageTracker"
                    );

            for (var element : damageArray) {

                JsonObject obj =
                        element.getAsJsonObject();

                UUID uuid =
                        UUID.fromString(
                                obj.get("uuid")
                                        .getAsString()
                        );

                long damage =
                        obj.get("damage")
                                .getAsLong();

                WorldBossDamageTracker
                        .setDamage(
                                uuid,
                                damage
                        );
            }

            JsonArray attemptsArray =
                    json.getAsJsonArray(
                            "attempts"
                    );

            for (var element : attemptsArray) {

                JsonObject obj =
                        element.getAsJsonObject();

                UUID uuid =
                        UUID.fromString(
                                obj.get("uuid")
                                        .getAsString()
                        );

                int attempts =
                        obj.get("attempts")
                                .getAsInt();

                WorldBossBattleAttempts
                        .setAttempts(
                                uuid,
                                attempts
                        );
            }

            System.out.println(
                    "[WorldBoss] Loaded persistence data."
            );

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}