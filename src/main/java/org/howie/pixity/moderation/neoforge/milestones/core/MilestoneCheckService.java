package org.howie.pixity.moderation.neoforge.milestones.core;

import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.chat.TextFormatter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MilestoneCheckService {





    private static final Map<UUID, Map<String, Integer>> CACHE =
            new HashMap<>();





    public static void check(
            ServerPlayer player,
            String milestoneId
    ) {

        MilestoneDefinition definition =
                MilestoneDefinitionRegistry.get(
                        milestoneId
                );

        if (definition == null) {
            return;
        }

        var progress =
                MilestoneProgressService.getProgress(
                        player,
                        definition
                );

        int currentLevel =
                progress.level();

        UUID uuid =
                player.getUUID();

        Map<String, Integer> playerCache =
                CACHE.computeIfAbsent(
                        uuid,
                        k -> new HashMap<>()
                );

        int previousLevel =
                playerCache.getOrDefault(
                        milestoneId,
                        0
                );





        if (currentLevel <= previousLevel) {
            return;
        }





        for (int i = previousLevel + 1;
             i <= currentLevel;
             i++) {






            if (
                    i == 10 ||
                            i == 25 ||
                            i == 50 ||
                            i == 75 ||
                            i == 100
            ) {

                broadcastMilestone(
                        player,
                        definition,
                        i
                );
            }
        }





        playerCache.put(
                milestoneId,
                currentLevel
        );
    }





    private static void broadcastMilestone(
            ServerPlayer player,
            MilestoneDefinition definition,
            int level
    ) {

        if (player.getServer() == null) {
            return;
        }

        player.getServer().getPlayerList().broadcastSystemMessage(

                TextFormatter.parse(

                        "<gradient:#FFD700:#FFF8DC:#FFD700>&lMILESTONES</gradient> &7&l➤ " +

                                "<white>" +
                                player.getGameProfile().getName() +
                                "</white> " +

                                "<gray>reached</gray> " +

                                "<green>Level " + level + "</green> " +

                                "<gray>in</gray> " +

                                "<yellow>" + definition.id + "</yellow>"
                ),

                false
        );
    }
}