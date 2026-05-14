package org.howie.pixity.moderation.neoforge.fishing.events;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.BossEvent;

import org.howie.pixity.moderation.chat.TextFormatter;
import org.howie.pixity.moderation.neoforge.economy.CurrencyType;
import org.howie.pixity.moderation.neoforge.economy.EconomyService;
import org.howie.pixity.moderation.neoforge.fishing.FishData;
import org.howie.pixity.moderation.neoforge.fishing.FishTier;
import org.howie.pixity.moderation.neoforge.fishing.FishingDatabase;

import java.util.*;

public class FishingEventManager {

    private static FishingEventType currentEvent;
    private static long endTime;
    private static long nextAnnouncement;
    private static long cooldownEnd = 0;
    private static long nextAutoEvent = 0;
    private static final long AUTO_INTERVAL = 8 * 60 * 60 * 1000L;

    private static final Map<UUID, Integer> scores = new HashMap<>();
    private static final Map<UUID, Set<String>> biomeMap = new HashMap<>();

    private static final Map<UUID, ServerBossEvent> bossBars = new HashMap<>();
    private static MinecraftServer server;
    private static EconomyService economy;





    public static void init(MinecraftServer srv, EconomyService econ) {
        economy = econ;
        server = srv;

        if (economy == null) {
            System.out.println("[FishingEvent] Economy not initialized!");
            return;
        }

        nextAutoEvent = System.currentTimeMillis() + AUTO_INTERVAL;
    }

    public static void setCooldown(long millis) {
        cooldownEnd = System.currentTimeMillis() + millis;
    }



    public static boolean isActive() {
        return currentEvent != null && System.currentTimeMillis() < endTime;
    }

    public static FishingEventType getEvent() {
        return currentEvent;
    }




    public static void start(FishingEventType type) {

        if (System.currentTimeMillis() < cooldownEnd) {
            return;
        }

        currentEvent = type;
        endTime = System.currentTimeMillis() + (30 * 60 * 1000);
        nextAnnouncement = System.currentTimeMillis() + (5 * 60 * 1000);

        scores.clear();
        biomeMap.clear();

        createBossBars();
        broadcastStart(type);
    }




    public static void end() {

        if (currentEvent == null) return;

        if (System.currentTimeMillis() > nextAutoEvent) {
            nextAutoEvent = System.currentTimeMillis() + AUTO_INTERVAL;
        }

        announceWinners();

        for (ServerBossEvent bar : bossBars.values()) {
            bar.removeAllPlayers();
        }
        bossBars.clear();

        currentEvent = null;
        scores.clear();
        biomeMap.clear();
    }




    public static void tick() {

        long now = System.currentTimeMillis();




        if (currentEvent != null && now >= endTime) {
            end();
            return;
        }




        if (!isActive() && now >= nextAutoEvent) {

            FishingEventType type = FishingEventType.random();
            start(type);

            nextAutoEvent = now + AUTO_INTERVAL;
            return;
        }




        if (!isActive()) return;




        if (!bossBars.isEmpty()){

            long remaining = Math.max(0, endTime - now);

            float progress = Math.max(0f,
                    (float) remaining / (30f * 60f * 1000f)
            );


            for (ServerPlayer player : server.getPlayerList().getPlayers()) {

                ServerBossEvent bar = bossBars.get(player.getUUID());


                if (bar == null) {
                    bar = new ServerBossEvent(
                            Component.literal("Fishing Event"),
                            BossEvent.BossBarColor.YELLOW,
                            BossEvent.BossBarOverlay.PROGRESS
                    );

                    bar.setDarkenScreen(false);
                    bar.setVisible(true);
                    bar.addPlayer(player);

                    bossBars.put(player.getUUID(), bar);
                }

                bar.setProgress(progress);




                String time = getTimeColor(remaining);




                int rank = getPlayerRank(player.getUUID());

                String rankText;

                if (rank == -1) {
                    rankText = "&7#-";
                } else if (rank == 1) {
                    rankText = "&6#1";
                } else if (rank == 2) {
                    rankText = "&7#2";
                } else if (rank == 3) {
                    rankText = "&#cd7f32#3";
                } else {
                    rankText = "&b#" + rank;
                }




                bar.setName(
                        TextFormatter.parse(
                                "<rainbow>&lFISHING EVENT</rainbow> <gray>("
                                        + time +
                                        ")</gray> &8| "
                                        + rankText
                        )
                );
            }
        }





        if (now >= nextAnnouncement) {
            announceTop();
            nextAnnouncement = now + (5 * 60 * 1000);
        }
    }




    private static void createBossBars() {

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {

            ServerBossEvent bar = new ServerBossEvent(
                    Component.literal("Fishing Event"),
                    BossEvent.BossBarColor.YELLOW,
                    BossEvent.BossBarOverlay.PROGRESS
            );

            bar.setDarkenScreen(false);
            bar.setVisible(true);
            bar.addPlayer(player);

            bossBars.put(player.getUUID(), bar);
        }
    }




    private static void broadcastStart(FishingEventType type) {

        String goal = switch (type) {
            case LONGEST -> "catch the &clongest fish";
            case SHORTEST -> "catch the &cshortest fish";
            case BIOMES -> "fish in the most &abiomes";
            case BRONZE -> "catch the most &#cd7f32&lBRONZE";
            case SILVER -> "catch the most &#bdbdbd&lSILVER FISH";
            case GOLD -> "catch the most &#f5f788&lGOLD FISH";
            case SQUID -> "kill the most &8squid";
            case DOLPHIN -> "kill the most &bdolphins";
        };

        server.getPlayerList().broadcastSystemMessage(
                TextFormatter.parse(
                        "\n" +
                                "🏆 <rainbow>&lFISHING EVENT</rainbow> 🏆\n" +
                                "&7The goal of this event is &e" + goal + "\n\n" +

                                "&b&lTime Limit: 30 minutes\n\n" +

                                "&aRewards:\n" +
                                "&61st: &b10,000 Entropy&7, &e100 Coins&7, &b200 Tokens&7, &a$7,500\n" +
                                "&72nd: &b5,000 Entropy&7, &e50 Coins&7, &b100 Tokens&7, &a$3,250\n" +
                                "&#c99a733rd: &b2,500 Entropy&7, &e25 Coins&7, &b50 Tokens&7, &a$1,625\n"
                ),
                false
        );
    }




    public static void onFishCatch(ServerPlayer player, FishData fish, int size) {

        if (!isActive()) return;

        UUID uuid = player.getUUID();

        switch (currentEvent) {

            case LONGEST -> scores.put(uuid, Math.max(scores.getOrDefault(uuid, 0), size));

            case SHORTEST -> {
                if (!scores.containsKey(uuid)) {
                    scores.put(uuid, size);
                } else {
                    scores.put(uuid, Math.min(scores.get(uuid), size));
                }
            }

            case BRONZE -> {
                if (fish.tier == FishTier.BRONZE)
                    scores.merge(uuid, 1, Integer::sum);
            }

            case SILVER -> {
                if (fish.tier == FishTier.SILVER)
                    scores.merge(uuid, 1, Integer::sum);
            }

            case GOLD -> {
                if (fish.tier == FishTier.GOLD)
                    scores.merge(uuid, 1, Integer::sum);
            }

            case BIOMES -> {
                String biome = player.level().getBiome(player.blockPosition()).toString();
                biomeMap.computeIfAbsent(uuid, k -> new HashSet<>()).add(biome);
                scores.put(uuid, biomeMap.get(uuid).size());
            }
        }
    }

    public static void onSquidKill(ServerPlayer player) {

        if (!isActive() || currentEvent != FishingEventType.SQUID) return;

        scores.merge(player.getUUID(), 1, Integer::sum);
    }

    public static void onDolphinKill(ServerPlayer player) {

        if (!isActive() || currentEvent != FishingEventType.DOLPHIN) return;

        scores.merge(player.getUUID(), 1, Integer::sum);
    }




    private static void announceTop() {

        var top = scores.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .limit(3)
                .toList();

        server.getPlayerList().broadcastSystemMessage(
                TextFormatter.parse("<rainbow>&lFISHING EVENT STANDINGS</rainbow>"),
                false
        );

        int place = 1;

        for (var entry : top) {

            String name = server.getProfileCache()
                    .get(entry.getKey())
                    .map(p -> p.getName())
                    .orElse("Unknown");

            server.getPlayerList().broadcastSystemMessage(
                    TextFormatter.parse(
                            "&e" + place + ". &f" + name + " - &a" + entry.getValue()
                    ),
                    false
            );

            place++;
        }
    }




    private static void announceWinners() {

        var top = scores.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .limit(3)
                .toList();

        server.getPlayerList().broadcastSystemMessage(
                TextFormatter.parse("🏆 <rainbow>&lFISHING EVENT FINISHED</rainbow> 🏆"),
                false
        );

        int place = 1;

        for (var entry : top) {

            UUID uuid = entry.getKey();
            int value = entry.getValue();

            String name = server.getProfileCache()
                    .get(uuid)
                    .map(p -> p.getName())
                    .orElse("Unknown");

            String color = switch (place) {
                case 1 -> "&6";
                case 2 -> "&7";
                case 3 -> "&#c99a73";
                default -> "&f";
            };

            server.getPlayerList().broadcastSystemMessage(
                    TextFormatter.parse(
                            color + place + ". &f" + name + " - &a" + value
                    ),
                    false
            );

            giveRewards(uuid, place);

            place++;
        }
    }

    private static void giveRewards(UUID uuid, int place) {

        if (place == 1) {

            FishingDatabase.incrementStat(
                    uuid,
                    "total_event_wins"
            );





            org.howie.pixity.moderation.neoforge.stats.PlayerStatsDatabase.add(
                    uuid,
                    "total_event_wins",
                    1
            );





            switch (currentEvent) {

                case LONGEST,
                     SHORTEST,
                     BIOMES,
                     BRONZE,
                     SILVER,
                     GOLD,
                     SQUID,
                     DOLPHIN -> {

                    org.howie.pixity.moderation.neoforge.stats.PlayerStatsDatabase.add(
                            uuid,
                            "fishing_event_wins",
                            1
                    );
                }
            }





            ServerPlayer player =
                    server.getPlayerList()
                            .getPlayer(uuid);

            if (player != null) {

                org.howie.pixity.moderation.neoforge.milestones.core.MilestoneCheckService.check(
                        player,
                        "total_event_wins"
                );

                org.howie.pixity.moderation.neoforge.milestones.core.MilestoneCheckService.check(
                        player,
                        "events_fishing"
                );
            }
        }

        switch (place) {
            case 1 -> {
                FishingDatabase.addEntropy(uuid, 10000);
                economy.add(uuid, CurrencyType.COINS, 100);
                economy.add(uuid, CurrencyType.TOKENS, 200);
                economy.add(uuid, CurrencyType.MONEY, 7500);
            }
            case 2 -> {
                FishingDatabase.addEntropy(uuid, 5000);
                economy.add(uuid, CurrencyType.COINS, 50);
                economy.add(uuid, CurrencyType.TOKENS, 100);
                economy.add(uuid, CurrencyType.MONEY, 3250);
            }
            case 3 -> {
                FishingDatabase.addEntropy(uuid, 2500);
                economy.add(uuid, CurrencyType.COINS, 25);
                economy.add(uuid, CurrencyType.TOKENS, 50);
                economy.add(uuid, CurrencyType.MONEY, 1625);
            }
        }
    }


    public static FishingEventType getCurrentEvent() {
        return currentEvent;
    }

    public static Map<UUID, Integer> getScores() {
        return scores;
    }

    public static long getTimeRemaining() {
        return Math.max(0, endTime - System.currentTimeMillis());
    }

    public static long getTimeUntilNextEvent() {
        return Math.max(0, nextAutoEvent - System.currentTimeMillis());
    }

    private static String formatTime(long millis) {

        long totalSeconds = millis / 1000;

        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;

        return minutes + "m " + seconds + "s";
    }

    private static String getTimeColor(long millis) {

        long seconds = millis / 1000;

        if (seconds > 300) {
            return "&a" + formatTime(millis) + "&7";
        } else if (seconds > 60) {
            return "&e" + formatTime(millis) + "&7";
        } else {
            return "&c" + formatTime(millis) + "&7";
        }
    }

    private static int getPlayerRank(UUID uuid) {

        if (scores.isEmpty()) return -1;

        List<Map.Entry<UUID, Integer>> sorted = scores.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .toList();

        for (int i = 0; i < sorted.size(); i++) {
            if (sorted.get(i).getKey().equals(uuid)) {
                return i + 1;
            }
        }

        return -1;
    }

}