package org.howie.pixity.moderation.neoforge.playtime;

import com.google.gson.*;

import org.howie.pixity.moderation.neoforge.playtime.gui.PlaytimeLevelsMenu;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class PlaytimeLevelsConfig {

    private final Path file;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private List<PlaytimeLevelsMenu.Level> levels = new ArrayList<>();

    public PlaytimeLevelsConfig(Path configDir) {

        this.file = configDir.resolve("playtime_levels.json");

        load();
    }

    public List<PlaytimeLevelsMenu.Level> getLevels() {
        return levels;
    }

    private void load() {

        try {

            if (!Files.exists(file)) {
                createDefault();
            }

            JsonObject root = gson.fromJson(
                    Files.newBufferedReader(file),
                    JsonObject.class
            );

            JsonArray arr = root.getAsJsonArray("levels");

            List<PlaytimeLevelsMenu.Level> list = new ArrayList<>();

            for (JsonElement el : arr) {

                JsonObject o = el.getAsJsonObject();

                int level = o.get("level").getAsInt();
                long seconds = o.get("seconds").getAsLong();

                List<String> rewards = new ArrayList<>();

                JsonArray rewardsArr = o.getAsJsonArray("rewards");

                for (JsonElement r : rewardsArr) {
                    rewards.add(r.getAsString());
                }

                list.add(new PlaytimeLevelsMenu.Level(
                        level,
                        seconds,
                        rewards
                ));
            }

            list.sort(Comparator.comparingInt(l -> l.level));

            this.levels = list;

        } catch (Exception e) {
            throw new RuntimeException("Failed loading playtime levels", e);
        }
    }

    private void createDefault() {

        try {

            JsonObject root = new JsonObject();
            JsonArray levels = new JsonArray();

            for (int i = 1; i <= 19; i++) {

                JsonObject lvl = new JsonObject();

                lvl.addProperty("level", i);
                lvl.addProperty("seconds", i * 600);

                JsonArray rewards = new JsonArray();

                rewards.add("tokens:" + (i * 100));
                rewards.add("money:" + (i * 50));

                lvl.add("rewards", rewards);

                levels.add(lvl);
            }

            root.add("levels", levels);

            Files.createDirectories(file.getParent());

            Files.writeString(
                    file,
                    gson.toJson(root),
                    StandardOpenOption.CREATE
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed creating default playtime config", e);
        }
    }
}