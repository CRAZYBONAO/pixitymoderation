package org.howie.pixity.moderation.neoforge.chatgames;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.howie.pixity.moderation.chat.TextFormatter;

import java.util.*;
import java.util.stream.Collectors;

public class ChatGameManager {

    private static List<ChatGameQuestion> QUESTIONS = new ArrayList<>();
    private static List<JsonObject> REWARDS = new ArrayList<>();

    private static ChatGameType currentType;
    private static String answer;
    private static boolean active = false;
    private static ChatGameQuestion current;
    private static final long TIME_LIMIT_MS = 120_000;
    private static long startTime;
    private static long endTime;
    private static Set<String> validAnswers = new HashSet<>();
    private static final Map<UUID, Integer> PLAYER_WINS = new HashMap<>();
    private static final String HEADER = "<rainbow>&l✦ CHATGAMES ✦</rainbow>";

    private static final Map<UUID, Integer> STREAKS = new HashMap<>();



    public static void tick(ServerLevel level) {

        if (!active) return;

        if (System.currentTimeMillis() >= endTime) {

            active = false;

            broadcast(level,
                    "",
                    HEADER,
                    "<red>No one answered in time!</red>",
                    "<gray>The answer was:</gray> &b" + getAnswerDisplay(),
                    ""
            );

            for (ServerPlayer p : level.getServer().getPlayerList().getPlayers()) {

                int streak = STREAKS.getOrDefault(p.getUUID(), 0);

                if (streak > 0) {
                    p.sendSystemMessage(TextFormatter.parse(
                            "<red>Your streak of <gold>" + streak + "x <red>has been broken!"
                    ));
                }

                STREAKS.put(p.getUUID(), 0);
                ChatGamesDatabase.resetStreak(p.getUUID());
            }

        }
    }

    private static String getAnswerDisplay() {
        if (currentType == ChatGameType.TRIVIA && current != null) {
            return current.answers.get(0);
        }
        return answer;
    }

    public static void startRound(ServerLevel level) {

        if (active) return;

        Random r = new Random();

        ChatGameType[] types = ChatGameType.values();
        currentType = types[r.nextInt(types.length)];

        boolean started = false;

        switch (currentType) {

            case NUMBER -> started = startNumber(level, r);
            case UNSCRAMBLE -> started = startUnscramble(level, r);
            case FILL -> started = startFill(level, r);
            case MATH -> started = startMath(level, r);
            case TYPING -> started = startTyping(level, r);
            case TRIVIA -> started = startTrivia(level, r);
        }

        if (!started) return;

        startTime = System.currentTimeMillis();
        endTime = startTime + TIME_LIMIT_MS;
        active = true;
    }

    private static boolean startNumber(ServerLevel level, Random r) {

        int num = r.nextInt(20) + 1;

        validAnswers.clear();
        validAnswers.add(String.valueOf(num));

        current = null;

        broadcast(level,
                "",
                HEADER,
                "<yellow>Guess the number between 1 and 20!</yellow>",
                ""
        );

        return true;
    }


    private static boolean startUnscramble(ServerLevel level, Random r) {

        if (ChatGamesConfig.UNSCRAMBLE.isEmpty()) return false;

        String word = ChatGamesConfig.UNSCRAMBLE
                .get(r.nextInt(ChatGamesConfig.UNSCRAMBLE.size()));

        validAnswers.clear();
        validAnswers.add(word.toLowerCase());

        current = null;


        List<Character> chars = new ArrayList<>();

        for (char c : word.toCharArray()) {
            chars.add(c);
        }

        Collections.shuffle(chars);

        StringBuilder scrambled = new StringBuilder();

        for (char c : chars) {
            scrambled.append(c);
        }

        broadcast(level,
                "",
                HEADER,
                "<yellow>Unscramble this word!</yellow>",
                "&b" + scrambled,
                ""
        );

        return true;
    }

    private static boolean startFill(ServerLevel level, Random r) {

        if (ChatGamesConfig.FILL.isEmpty()) return false;

        String word = ChatGamesConfig.FILL
                .get(r.nextInt(ChatGamesConfig.FILL.size()));

        validAnswers.clear();
        validAnswers.add(word.toLowerCase());

        current = null;

        StringBuilder hidden = new StringBuilder();

        for (char c : word.toCharArray()) {
            hidden.append(r.nextDouble() < 0.6 ? "_" : c);
        }

        if (!hidden.toString().contains("_")) {
            hidden.setCharAt(r.nextInt(hidden.length()), '_');
        }

        broadcast(level,
                HEADER,
                "<yellow>Fill in the missing letters!</yellow>",
                "&b" + hidden
        );

        return true;
    }

    private static boolean startMath(ServerLevel level, Random r) {

        int a = r.nextInt(20) + 1;
        int b = r.nextInt(20) + 1;

        int op = r.nextInt(4);


        String question;
        int result;

        switch (op) {
            case 0 -> {
                question = a + " + " + b;
                result = a + b;
            }
            case 1 -> {
                question = a + " - " + b;
                result = a - b;
            }
            case 2 -> {
                question = a + " * " + b;
                result = a * b;
            }
            default -> {

                result = a;
                int divisor = b;
                question = (a * divisor) + " / " + divisor;
            }
        }

        answer = String.valueOf(result);
        current = null;

        broadcast(level,
                "",
                HEADER,
                "<yellow>Solve this:</yellow>",
                "&b" + question,
                ""
        );

        validAnswers.clear();

        return true;
    }

    private static final String CHARS = "abcdefghijklmnopqrstuvwxyz0123456789";

    private static boolean startTyping(ServerLevel level, Random r) {

        int len = 10 + r.nextInt(15);

        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(r.nextInt(chars.length())));
        }

        String text = sb.toString();

        validAnswers.clear();
        validAnswers.add(text.toLowerCase());

        current = null;

        broadcast(level,
                "",
                HEADER,
                "<yellow>Type this exactly:</yellow>",
                "&b" + text,
                ""
        );

        return true;
    }

    private static boolean startTrivia(ServerLevel level, Random r) {

        if (ChatGamesConfig.TRIVIA.isEmpty()) return false;

        ChatGameQuestion q = ChatGamesConfig.TRIVIA
                .get(r.nextInt(ChatGamesConfig.TRIVIA.size()));

        validAnswers.clear();

        current = q;
        for (String a : current.answers) {
            validAnswers.add(a.toLowerCase());
        }

        broadcast(level,
                "",
                HEADER,
                "<yellow>" + q.question + "</yellow>",
                ""
        );

        return true;
    }

    public static boolean isActive() {
        return active;
    }

    private static final Map<UUID, Long> LAST_ATTEMPT = new HashMap<>();

    public static boolean tryAnswer(ServerPlayer player, String message) {

        if (!active) return false;

        long now = System.currentTimeMillis();
        UUID uuid = player.getUUID();

        long last = LAST_ATTEMPT.getOrDefault(uuid, 0L);
        if (now - last < 200) return false;

        LAST_ATTEMPT.put(uuid, now);

        String msg = message.toLowerCase(Locale.ROOT).trim();

        if (!validAnswers.contains(msg)) return false;

        win(player);
        return true;
    }

    private static void win(ServerPlayer player) {

        active = false;

        UUID uuid = player.getUUID();

        int streak = STREAKS.getOrDefault(uuid, 0) + 1;
        STREAKS.put(uuid, streak);


        reward(player, streak);
        ChatGamesDatabase.updateStats(player.getUUID(), streak);

        long timeMs = System.currentTimeMillis() - startTime;
        double seconds = timeMs / 1000.0;

        String timeFormatted = String.format("%.2f", seconds);

        broadcast(player.serverLevel(),
                HEADER,
                "<white>" + player.getName().getString()
                        + " </white><gray>answered in</gray> <green>" + timeFormatted + "s</green>",
                "<yellow>Streak:</yellow> <gold>" + streak + "x</gold>"
        );
    }

    private static void reward(ServerPlayer player, int streak) {

        if (ChatGamesConfig.REWARDS.isEmpty()) return;

        JsonObject reward = getRandomReward();

        String type = reward.get("type").getAsString();

        if (type.equals("command")) {

            String cmd = reward.get("value").getAsString()
                    .replace("%player%", player.getName().getString());

            player.server.getCommands().performPrefixedCommand(
                    player.server.createCommandSourceStack(),
                    cmd
            );

        } else if (type.equals("item")) {

            try {
                String itemId = reward.get("item").getAsString();
                int amount = reward.get("amount").getAsInt();


                if (streak >= 3) {
                    amount *= 2;
                }

                ResourceLocation rl = ResourceLocation.parse(itemId);

                ItemStack item = new ItemStack(
                        BuiltInRegistries.ITEM.get(rl),
                        amount
                );

                if (!player.getInventory().add(item)) {
                    player.drop(item, false);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        applyMilestone(player, streak);


        if (streak == 5) {
            player.server.getCommands().performPrefixedCommand(
                    player.server.createCommandSourceStack(),
                    "eco give " + player.getName().getString() + " money 1000"
            );

            player.sendSystemMessage(TextFormatter.parse("<gold>🔥 5 WIN STREAK BONUS!</gold>"));
        }
    }

    private static void applyMilestone(ServerPlayer player, int streak) {

        JsonObject milestone = ChatGamesConfig.STREAK_MILESTONES.get(streak);

        if (milestone == null) return;




        if (milestone.has("commands")) {

            for (JsonElement el : milestone.getAsJsonArray("commands")) {

                String cmd = el.getAsString()
                        .replace("%player%", player.getName().getString());

                player.server.getCommands().performPrefixedCommand(
                        player.server.createCommandSourceStack(),
                        cmd
                );
            }
        }




        if (milestone.has("items")) {

            for (JsonElement el : milestone.getAsJsonArray("items")) {

                try {
                    JsonObject itemObj = el.getAsJsonObject();

                    String itemId = itemObj.get("item").getAsString();
                    int amount = itemObj.get("amount").getAsInt();

                    ResourceLocation rl = ResourceLocation.parse(itemId);

                    ItemStack item = new ItemStack(
                            BuiltInRegistries.ITEM.get(rl),
                            amount
                    );

                    if (!player.getInventory().add(item)) {
                        player.drop(item, false);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }




        broadcast(player.serverLevel(),
                HEADER,
                "<gold>" + player.getName().getString()
                        + "</gold> reached a <yellow>" + streak + "x</yellow> <gold>streak!"
        );
    }

    public static int getNextMilestone(int streak) {

        return ChatGamesConfig.STREAK_MILESTONES.keySet().stream()
                .filter(k -> k > streak)
                .min(Integer::compareTo)
                .orElse(-1);
    }

    public static int getStreak(UUID uuid) {
        return STREAKS.getOrDefault(uuid, 0);
    }


    private static JsonObject getRandomReward() {

        List<JsonObject> rewards = ChatGamesConfig.REWARDS;

        int totalWeight = 0;

        for (JsonObject r : rewards) {
            totalWeight += r.has("weight") ? r.get("weight").getAsInt() : 1;
        }

        int roll = new Random().nextInt(totalWeight);

        for (JsonObject r : rewards) {

            int w = r.has("weight") ? r.get("weight").getAsInt() : 1;

            if (roll < w) return r;

            roll -= w;
        }

        return rewards.get(0);
    }

    private static void broadcast(ServerLevel level, String... lines) {

        List<Component> built = new ArrayList<>(lines.length);

        for (String s : lines) {
            built.add(TextFormatter.parse(s));
        }

        for (ServerPlayer p : level.getServer().getPlayerList().getPlayers()) {
            for (Component c : built) {
                p.sendSystemMessage(c);
            }
        }
    }
}