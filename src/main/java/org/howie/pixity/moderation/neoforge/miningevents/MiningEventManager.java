package org.howie.pixity.moderation.neoforge.miningevents;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;

import net.minecraft.world.BossEvent;

import org.howie.pixity.moderation.chat.TextFormatter;

import java.util.*;
import java.util.stream.Collectors;

public class MiningEventManager {





    private static boolean active = false;

    private static MiningEventOre currentOre;

    private static long endTime = 0L;

    private static long nextEventTime =
            System.currentTimeMillis()
                    + (1000L * 60L * 60L * 8L);





    private static ServerBossEvent bossBar;





    private static final Map<UUID, Integer> progress =
            new HashMap<>();





    public static final long EVENT_DURATION =
            1000L * 60L * 30L;

    public static final long EVENT_INTERVAL =
            1000L * 60L * 60L * 8L;





    public static boolean isActive() {
        return active;
    }

    public static MiningEventOre getCurrentOre() {
        return currentOre;
    }

    public static long getEndTime() {
        return endTime;
    }

    public static long getNextEventTime() {
        return nextEventTime;
    }





    public static void startEvent(
            MinecraftServer server,
            MiningEventOre ore
    ) {

        if (active) {
            return;
        }

        active = true;

        currentOre = ore;

        progress.clear();





        bossBar =
                new ServerBossEvent(

                        Component.literal(
                                "Mining Event"
                        ),

                        BossEvent.BossBarColor.BLUE,

                        BossEvent.BossBarOverlay.PROGRESS
                );

        bossBar.setVisible(true);

        for (ServerPlayer p : server.getPlayerList().getPlayers()) {

            bossBar.addPlayer(p);
        }

        endTime =
                System.currentTimeMillis()
                        + EVENT_DURATION;

        nextEventTime =
                System.currentTimeMillis()
                        + EVENT_INTERVAL;





        server.getPlayerList()
                .broadcastSystemMessage(

                        TextFormatter.parse(
                                ""
                                        + "<gold>🏆<rainbow>MINING EVENT</rainbow>🏆</gold>\n"
                                        + "<gray>The goal of this event is to mine the most "
                                        + ore.color
                                        + ore.display
                                        + "</gray>\n\n"

                                        + "<aqua><bold>Time Limit:</bold></aqua> "
                                        + "<white>30 Minutes</white>\n\n"

                                        + "<green><bold>Rewards:</bold></green>\n"

                                        + "<gold>1st:</gold> "
                                        + "<#371461>1x Mystic Crate Key</#371461><gray>, </gray>"
                                        + "<yellow>100 Coins</yellow><gray>, </gray>"
                                        + "<aqua>200 Tokens</aqua><gray>, </gray>"
                                        + "<green>$7,500</green>\n"

                                        + "<gray>2nd:</gray> "
                                        + "<#5e1dab>1x Elite Crate Key</#5e1dab><gray>, </gray>"
                                        + "<yellow>50 Coins</yellow><gray>, </gray>"
                                        + "<aqua>200 Tokens</aqua><gray>, </gray>"
                                        + "<green>$7,500</green>\n"

                                        + "<#cd7f32>3rd:</#cd7f32> "
                                        + "<green>1x Vote Crate Key</green><gray>, </gray>"
                                        + "<yellow>25 Coins</yellow><gray>, </gray>"
                                        + "<aqua>200 Tokens</aqua><gray>, </gray>"
                                        + "<green>$7,500</green>"
                        ),
                        false
                );
    }





    public static void forceEnd(
            MinecraftServer server
    ) {

        if (!active) {
            return;
        }

        endEvent(server);
    }





    public static void endEvent(
            MinecraftServer server
    ) {

        if (!active) {
            return;
        }

        active = false;





        List<Map.Entry<UUID, Integer>> sorted =
                progress.entrySet()
                        .stream()
                        .sorted((a, b) ->
                                Integer.compare(
                                        b.getValue(),
                                        a.getValue()
                                )
                        )
                        .collect(Collectors.toList());





        Map.Entry<UUID, Integer> first =
                sorted.size() > 0
                        ? sorted.get(0)
                        : null;

        Map.Entry<UUID, Integer> second =
                sorted.size() > 1
                        ? sorted.get(1)
                        : null;

        Map.Entry<UUID, Integer> third =
                sorted.size() > 2
                        ? sorted.get(2)
                        : null;





        StringBuilder msg =
                new StringBuilder();

        msg.append(
                "<gold>🏆<rainbow>MINING EVENT ENDED</rainbow>🏆</gold>\n\n"
        );

        if (first != null) {

            String name =
                    getName(server, first.getKey());

            msg.append(
                    "<gold>1st:</gold> "
                            + "<white>"
                            + name
                            + "</white>"
                            + "<gray> - </gray>"
                            + "<yellow>"
                            + first.getValue()
                            + "</yellow>\n"
            );

            rewardFirst(server, first.getKey());
        }

        if (second != null) {

            String name =
                    getName(server, second.getKey());

            msg.append(
                    "<gray>2nd:</gray> "
                            + "<white>"
                            + name
                            + "</white>"
                            + "<gray> - </gray>"
                            + "<yellow>"
                            + second.getValue()
                            + "</yellow>\n"
            );

            rewardSecond(server, second.getKey());
        }

        if (third != null) {

            String name =
                    getName(server, third.getKey());

            msg.append(
                    "<#cd7f32>3rd:</#cd7f32> "
                            + "<white>"
                            + name
                            + "</white>"
                            + "<gray> - </gray>"
                            + "<yellow>"
                            + third.getValue()
                            + "</yellow>\n"
            );

            rewardThird(server, third.getKey());
        }

        server.getPlayerList()
                .broadcastSystemMessage(
                        TextFormatter.parse(
                                msg.toString()
                        ),
                        false
                );





        if (bossBar != null) {

            bossBar.removeAllPlayers();

            bossBar = null;
        }

        progress.clear();

        currentOre = null;
    }





    public static void handleMine(
            ServerPlayer player,
            String statColumn
    ) {

        if (!active) {
            return;
        }

        if (currentOre == null) {
            return;
        }

        if (
                !currentOre.statColumn
                        .equalsIgnoreCase(statColumn)
        ) {
            return;
        }

        progress.merge(
                player.getUUID(),
                1,
                Integer::sum
        );
    }





    public static void announceStandings(
            MinecraftServer server
    ) {

        if (!active) {
            return;
        }

        List<Map.Entry<UUID, Integer>> sorted =
                progress.entrySet()
                        .stream()
                        .sorted((a, b) ->
                                Integer.compare(
                                        b.getValue(),
                                        a.getValue()
                                )
                        )
                        .limit(3)
                        .toList();

        StringBuilder msg =
                new StringBuilder();

        long remaining =
                endTime
                        - System.currentTimeMillis();

        long minutes =
                remaining / (1000L * 60L);

        msg.append(
                "<gold>🏆<rainbow>MINING EVENT</rainbow>🏆</gold>\n"

                        + "<gray>Mine the most "
                        + currentOre.color
                        + currentOre.display
                        + "</gold>"
                        + "<gray>!</gray>\n\n"

                        + "<aqua><bold>Time Remaining:</bold></aqua> "
                        + "<yellow>"
                        + minutes
                        + " Minutes</yellow>\n\n"

                        + "<green><bold>Rewards:</bold></green>\n"

                        + "<gold>1st:</gold> "
                        + "<#371461>1x Mystic Crate Key</#371461><gray>, </gray>"
                        + "<yellow>100 Coins</yellow><gray>, </gray>"
                        + "<aqua>200 Tokens</aqua><gray>, </gray>"
                        + "<green>$7,500</green>\n"

                        + "<gray>2nd:</gray> "
                        + "<#5e1dab>1x Elite Crate Key</#5e1dab><gray>, </gray>"
                        + "<yellow>50 Coins</yellow><gray>, </gray>"
                        + "<aqua>200 Tokens</aqua><gray>, </gray>"
                        + "<green>$7,500</green>\n"

                        + "<#cd7f32>3rd:</#cd7f32> "
                        + "<green>1x Vote Crate Key</green><gray>, </gray>"
                        + "<yellow>25 Coins</yellow><gray>, </gray>"
                        + "<aqua>200 Tokens</aqua><gray>, </gray>"
                        + "<green>$7,500</green>\n\n"

                        + "<aqua><bold>Current Standings:</bold></aqua>\n"
        );

        for (int i = 0; i < sorted.size(); i++) {

            Map.Entry<UUID, Integer> e =
                    sorted.get(i);

            String prefix =
                    switch (i) {

                        case 0 -> "<gold>1st:</gold> ";
                        case 1 -> "<gray>2nd:</gray> ";
                        default -> "<#cd7f32>3rd:</#cd7f32> ";
                    };

            msg.append(
                    prefix
                            + "<white>"
                            + getName(server, e.getKey())
                            + "</white>"
                            + "<gray> - </gray>"
                            + "<yellow>"
                            + e.getValue()
                            + "</yellow>\n"
            );
        }

        server.getPlayerList()
                .broadcastSystemMessage(
                        TextFormatter.parse(
                                msg.toString()
                        ),
                        false
                );
    }





    public static List<Map.Entry<UUID, Integer>> getStandings() {

        return progress.entrySet()
                .stream()
                .sorted((a, b) ->
                        Integer.compare(
                                b.getValue(),
                                a.getValue()
                        )
                )
                .toList();
    }





    public static void updateBossBar(
            MinecraftServer server
    ) {

        if (!active) {
            return;
        }

        if (bossBar == null) {
            return;
        }

        long remaining =
                endTime
                        - System.currentTimeMillis();

        if (remaining < 0) {
            remaining = 0;
        }

        float progressPercent =
                remaining
                        / (float) EVENT_DURATION;

        bossBar.setProgress(
                Math.max(
                        0F,
                        Math.min(
                                1F,
                                progressPercent
                        )
                )
        );

        long minutes =
                remaining / (1000L * 60L);

        long seconds =
                (remaining / 1000L) % 60L;

        bossBar.setName(

                TextFormatter.parse(
                        "<gold>🏆 Mining Event</gold>"
                                + "<gray> • </gray>"
                                + currentOre.color
                                + currentOre.display
                                + "</gold>"
                                + "<gray> • </gray>"
                                + "<yellow>"
                                + minutes
                                + "m "
                                + seconds
                                + "s</yellow>"
                )
        );





        for (ServerPlayer p : server.getPlayerList().getPlayers()) {

            if (!bossBar.getPlayers().contains(p)) {
                bossBar.addPlayer(p);
            }
        }
    }





    private static void rewardFirst(
            MinecraftServer server,
            UUID uuid
    ) {

        execute(
                server,
                "crate givekey " + getName(server, uuid) + " mystic 1"
        );

        execute(
                server,
                "eco give " + getName(server, uuid) + " 7500"
        );
    }

    private static void rewardSecond(
            MinecraftServer server,
            UUID uuid
    ) {

        execute(
                server,
                "crate givekey " + getName(server, uuid) + " elite 1"
        );

        execute(
                server,
                "eco give " + getName(server, uuid) + " 7500"
        );
    }

    private static void rewardThird(
            MinecraftServer server,
            UUID uuid
    ) {

        execute(
                server,
                "crate givekey " + getName(server, uuid) + " vote 1"
        );

        execute(
                server,
                "eco give " + getName(server, uuid) + " 7500"
        );
    }





    private static void execute(
            MinecraftServer server,
            String cmd
    ) {

        server.getCommands()
                .performPrefixedCommand(
                        server.createCommandSourceStack(),
                        cmd
                );
    }





    private static String getName(
            MinecraftServer server,
            UUID uuid
    ) {

        ServerPlayer player =
                server.getPlayerList()
                        .getPlayer(uuid);

        if (player != null) {
            return player.getGameProfile().getName();
        }

        return uuid.toString();
    }
}