package org.howie.pixity.moderation.neoforge.worldboss;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.chat.TextFormatter;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WorldBossLeaderboard {





    public static void announce(
            MinecraftServer server
    ) {





        if (!WorldBossManager.isActive()) {
            return;
        }





        Map<UUID, Long> damageMap =
                WorldBossDamageTracker.getAll();

        if (damageMap.isEmpty()) {
            return;
        }





        List<Map.Entry<UUID, Long>> top =
                damageMap.entrySet()
                        .stream()

                        .sorted(
                                Map.Entry.comparingByValue(
                                        Comparator.reverseOrder()
                                )
                        )

                        .limit(3)

                        .toList();





        StringBuilder msg =
                new StringBuilder();

        msg.append(
                "<red>&l👑 WORLD BOSS LEADERBOARD</red>\n\n"
        );

        int placement = 1;

        for (Map.Entry<UUID, Long> entry : top) {

            ServerPlayer player =
                    server.getPlayerList()
                            .getPlayer(
                                    entry.getKey()
                            );

            if (player == null) {
                continue;
            }

            String color =
                    switch (placement) {

                        case 1 -> "<gold>";

                        case 2 -> "<gray>";

                        case 3 -> "<yellow>";

                        default -> "<white>";
                    };

            msg.append(color)
                    .append("#")
                    .append(placement)
                    .append(" </")
                    .append(
                            color.replace(
                                    "<",
                                    ""
                            ).replace(
                                    ">",
                                    ""
                            )
                    )
                    .append(">")

                    .append("<white>")
                    .append(
                            player.getGameProfile()
                                    .getName()
                    )
                    .append("</white>")

                    .append("<gray> • </gray>")

                    .append("<green>")
                    .append(
                            String.format(
                                    "%,d",
                                    entry.getValue()
                            )
                    )
                    .append("</green>")

                    .append("<gray> damage</gray>\n");

            placement++;
        }





        msg.append("\n")

                .append("<gray>Boss HP Remaining:</gray>\n")

                .append("<red>")
                .append(
                        String.format(
                                "%,d",
                                WorldBossManager.getHealth()
                        )
                )
                .append("</red>");





        server.getPlayerList()
                .broadcastSystemMessage(

                        TextFormatter.parse(
                                msg.toString()
                        ),

                        false
                );
    }
}