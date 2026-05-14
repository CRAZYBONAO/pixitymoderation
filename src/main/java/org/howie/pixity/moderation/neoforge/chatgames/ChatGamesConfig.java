package org.howie.pixity.moderation.neoforge.chatgames;

import com.google.gson.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ChatGamesConfig {

    private static final Path FILE = Paths.get("config/pixity_chatgames.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static int intervalSeconds = 300;

    public static final List<String> UNSCRAMBLE = new ArrayList<>();
    public static final List<String> FILL = new ArrayList<>();
    public static final List<ChatGameQuestion> TRIVIA = new ArrayList<>();
    public static final List<JsonObject> REWARDS = new ArrayList<>();
    public static final Map<Integer, JsonObject> STREAK_MILESTONES = new HashMap<>();
    public static final Map<Integer, JsonObject> MILESTONES = new HashMap<>();


    public static void load() {

        try {

            if (!Files.exists(FILE)) {
                saveDefault();
            }

            JsonObject json = JsonParser.parseReader(new FileReader(FILE.toFile()))
                    .getAsJsonObject();




            intervalSeconds = json.has("interval_seconds")
                    ? json.get("interval_seconds").getAsInt()
                    : 300;




            UNSCRAMBLE.clear();

            if (json.has("unscramble_words")) {
                JsonArray arr = json.getAsJsonArray("unscramble_words");

                for (JsonElement el : arr) {
                    UNSCRAMBLE.add(el.getAsString().toLowerCase());
                }
            }




            FILL.clear();

            if (json.has("fill_words")) {
                JsonArray arr = json.getAsJsonArray("fill_words");

                for (JsonElement el : arr) {
                    FILL.add(el.getAsString().toLowerCase());
                }
            }





            TRIVIA.clear();

            if (json.has("trivia")) {
                JsonArray arr = json.getAsJsonArray("trivia");

                for (JsonElement el : arr) {
                    JsonObject obj = el.getAsJsonObject();

                    ChatGameQuestion q = new ChatGameQuestion();
                    q.question = obj.get("question").getAsString();

                    q.answers = new ArrayList<>();
                    for (JsonElement a : obj.getAsJsonArray("answers")) {
                        q.answers.add(a.getAsString().toLowerCase());
                    }

                    TRIVIA.add(q);
                }
            }




            REWARDS.clear();

            if (json.has("rewards")) {
                JsonArray arr = json.getAsJsonArray("rewards");

                for (JsonElement el : arr) {
                    if (el.isJsonObject()) {
                        REWARDS.add(el.getAsJsonObject());
                    }
                }
            }

            STREAK_MILESTONES.clear();

            if (json.has("streak_milestones")) {
                JsonObject obj = json.getAsJsonObject("streak_milestones");

                for (String key : obj.keySet()) {
                    int streak = Integer.parseInt(key);
                    STREAK_MILESTONES.put(streak, obj.getAsJsonObject(key));
                }
            }

            MILESTONES.clear();

            if (json.has("milestone_rewards")) {
                JsonObject obj = json.getAsJsonObject("milestone_rewards");

                for (String key : obj.keySet()) {
                    int level = Integer.parseInt(key);
                    MILESTONES.put(level, obj.getAsJsonObject(key));
                }
            }

            System.out.println("[ChatGames] Loaded config");

        } catch (Exception e) {
            System.err.println("[ChatGames] Failed to load config!");
            e.printStackTrace();
        }
    }

    public static int getRequirement(int milestone) {

        if (milestone <= 10) return milestone * 50;
        if (milestone <= 20) return 500 + (milestone - 10) * 100;
        if (milestone <= 30) return 1500 + (milestone - 20) * 150;
        if (milestone <= 40) return 3000 + (milestone - 30) * 200;
        return 5000 + (milestone - 40) * 250;
    }


    private static void saveDefault() {

        try {
            Files.createDirectories(FILE.getParent());

            JsonObject json = new JsonObject();

            json.addProperty("interval_seconds", 300);




            JsonArray unscramble = new JsonArray();
            unscramble.add("pokemon");
            unscramble.add("pikachu");
            unscramble.add("charizard");
            json.add("unscramble_words", unscramble);




            JsonArray fill = new JsonArray();
            fill.add("pokeball");
            fill.add("netherite");
            fill.add("minecraft");
            json.add("fill_words", fill);




            JsonArray rewards = new JsonArray();

            JsonObject r1 = new JsonObject();
            r1.addProperty("type", "command");
            r1.addProperty("value", "eco give %player% 1000");

            JsonObject r2 = new JsonObject();
            r2.addProperty("type", "item");
            r2.addProperty("item", "minecraft:diamond");
            r2.addProperty("amount", 5);

            rewards.add(r1);
            rewards.add(r2);

            json.add("rewards", rewards);

            try (FileWriter writer = new FileWriter(FILE.toFile())) {
                GSON.toJson(json, writer);
            }

            System.out.println("[ChatGames] Created default config");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}