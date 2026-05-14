package org.howie.pixity.moderation.neoforge.worldboss;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.chat.TextFormatter;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WorldBossRewards {





    public static void distribute(
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





        List<Map.Entry<UUID, Long>> rankings =
                damageMap.entrySet()
                        .stream()

                        .sorted(
                                Map.Entry.comparingByValue(
                                        Comparator.reverseOrder()
                                )
                        )

                        .toList();





        int placement = 1;

        for (Map.Entry<UUID, Long> entry
                : rankings) {

            ServerPlayer player =
                    server.getPlayerList()
                            .getPlayer(
                                    entry.getKey()
                            );

            if (player == null) {
                continue;
            }

            long damage =
                    entry.getValue();





            WorldBossLootTable.giveLoot(

                    server,

                    player,

                    damage,

                    WorldBossManager.getCurrent()
            );





            int tokens =
                    25;

            int money =
                    5000;





            if (placement == 1) {

                tokens += 250;

                money += 100000;

            } else if (placement == 2) {

                tokens += 150;

                money += 50000;

            } else if (placement == 3) {

                tokens += 75;

                money += 25000;
            }





            tokens +=
                    (int) (
                            damage / 50_000L
                    );

            money +=
                    (int) (
                            damage / 10L
                    );





            try {





                player.server
                        .getCommands()
                        .performPrefixedCommand(

                                player.server
                                        .createCommandSourceStack(),

                                "tokens add "
                                        + player.getGameProfile()
                                        .getName()
                                        + " "
                                        + tokens
                        );





                player.server
                        .getCommands()
                        .performPrefixedCommand(

                                player.server
                                        .createCommandSourceStack(),

                                "eco give "
                                        + player.getGameProfile()
                                        .getName()
                                        + " "
                                        + money
                        );

            } catch (Exception e) {

                e.printStackTrace();
            }





            player.sendSystemMessage(

                    TextFormatter.parse(
                            "<gold>&l👑 WORLD BOSS REWARDS</gold>\n\n"

                                    + "<yellow>Placement:</yellow> "

                                    + "<green>#"
                                    + placement
                                    + "</green>\n\n"

                                    + "<yellow>Damage:</yellow> "

                                    + "<red>"
                                    + String.format(
                                    "%,d",
                                    damage
                            )
                                    + "</red>\n\n"

                                    + "<aqua>+"
                                    + tokens
                                    + " Tokens</aqua>\n"

                                    + "<green>+$"
                                    + String.format(
                                    "%,d",
                                    money
                            )
                                    + "</green>"
                    )
            );

            placement++;
        }





        if (!rankings.isEmpty()) {

            var winner =
                    rankings.getFirst();

            ServerPlayer top =
                    server.getPlayerList()
                            .getPlayer(
                                    winner.getKey()
                            );

            if (top != null) {

                server.getPlayerList()
                        .broadcastSystemMessage(

                                TextFormatter.parse(
                                        "<gold>&l👑 RAID MVP</gold>\n\n"

                                                + "<yellow>"
                                                + top.getGameProfile()
                                                .getName()
                                                + "</yellow>"

                                                + "<gray> dealt </gray>"

                                                + "<red>"
                                                + String.format(
                                                "%,d",
                                                winner.getValue()
                                        )
                                                + "</red>"

                                                + "<gray> damage!</gray>"
                                ),

                                false
                        );
            }
        }
    }
}