package org.howie.pixity.moderation.neoforge.worldboss;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.chat.TextFormatter;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

public class WorldBossScheduler {





    private static int lastHour =
            -1;

    private static int lastMinute =
            -1;





    public static void tick(
            MinecraftServer server
    ) {





        if (WorldBossManager.isActive()) {
            return;
        }





        LocalDateTime now =
                LocalDateTime.now();

        int hour =
                now.getHour();

        int minute =
                now.getMinute();

        DayOfWeek day =
                now.getDayOfWeek();





        if (
                hour == lastHour
                        &&
                        minute == lastMinute
        ) {
            return;
        }





        if (
                day == DayOfWeek.SATURDAY
                        &&
                        hour == 12
                        &&
                        minute == 0
        ) {

            lastHour = hour;
            lastMinute = minute;

            spawnLegendary(server);

            return;
        }





        if (
                day == DayOfWeek.SUNDAY
                        &&
                        hour == 20
                        &&
                        minute == 0
        ) {

            lastHour = hour;
            lastMinute = minute;

            spawnLegendary(server);

            return;
        }






        if (
                minute == 0
                        &&
                        (
                                hour == 0
                                        ||
                                        hour == 8
                                        ||
                                        hour == 16
                        )
        ) {

            lastHour = hour;
            lastMinute = minute;

            spawnSingle(server);

            return;
        }






        if (
                minute == 0
                        &&
                        (
                                hour == 4
                                        ||
                                        hour == 12
                                        ||
                                        hour == 20
                        )
        ) {





            if (
                    (day == DayOfWeek.SATURDAY && hour == 12)

                            ||

                            (day == DayOfWeek.SUNDAY && hour == 20)
            ) {
                return;
            }

            lastHour = hour;
            lastMinute = minute;

            spawnDual(server);
        }
    }





    private static void spawnSingle(
            MinecraftServer server
    ) {

        ServerPlayer player =
                firstPlayer(server);

        if (player == null) {
            return;
        }

        WorldBossManager.startSpecific(

                player,

                WorldBossPool.randomSingle()
        );

        broadcast(

                server,

                "<red>&l👑 SINGLE-TYPE RAID</red>\n\n"

                        + "<yellow>A powerful single-type boss has appeared!</yellow>"
        );
    }





    private static void spawnDual(
            MinecraftServer server
    ) {

        ServerPlayer player =
                firstPlayer(server);

        if (player == null) {
            return;
        }

        WorldBossManager.startSpecific(

                player,

                WorldBossPool.randomDual()
        );

        broadcast(

                server,

                "<gold>&l⚔ DUAL-TYPE RAID</gold>\n\n"

                        + "<yellow>A dangerous dual-type boss has appeared!</yellow>"
        );
    }





    private static void spawnLegendary(
            MinecraftServer server
    ) {

        ServerPlayer player =
                firstPlayer(server);

        if (player == null) {
            return;
        }

        WorldBossManager.startSpecific(

                player,

                LegendaryWorldBossPool.randomLegendary()
        );

        broadcast(

                server,

                "<dark_purple>&l☄ LEGENDARY RAID</dark_purple>\n\n"

                        + "<light_purple>A legendary Pokémon has appeared!</light_purple>"
        );
    }





    private static ServerPlayer firstPlayer(
            MinecraftServer server
    ) {

        return server.getPlayerList()
                .getPlayers()
                .stream()
                .findFirst()
                .orElse(null);
    }





    private static void broadcast(
            MinecraftServer server,
            String msg
    ) {

        server.getPlayerList()
                .broadcastSystemMessage(

                        TextFormatter.parse(
                                msg
                        ),

                        false
                );
    }
}