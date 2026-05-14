package org.howie.pixity.moderation.neoforge.milestones.core;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import org.howie.pixity.moderation.PixityModerationNeoForge;
import org.howie.pixity.moderation.chat.TextFormatter;

public class MilestoneClaimService {





    public static boolean claim(
            ServerPlayer player,
            String milestoneId,
            int level
    ) {





        var milestonePlayers =
                PixityModerationNeoForge.MILESTONE_PLAYERS;

        var economy =
                PixityModerationNeoForge.ECONOMY_SERVICE;





        MilestoneDefinition definition =
                MilestoneDefinitionRegistry.get(
                        milestoneId
                );

        if (definition == null) {

            if (level <= 0 || level > definition.maxLevel) {

                player.sendSystemMessage(
                        TextFormatter.parse(
                                "<gradient:#FFD700:#FFF8DC:#FFD700>&lMILESTONES</gradient> &7&l➤ <red>Error! Invalid milestone level.</red>"
                        )
                );

                return false;
            }

            player.sendSystemMessage(
                    TextFormatter.parse(
                            "<gradient:#FFD700:#FFF8DC:#FFD700>&lMILESTONES</gradient> &7&l➤ <red>Error! Invalid milestone.</red>"
                    )
            );

            return false;
        }





        if (milestonePlayers.hasClaimed(
                player,
                milestoneId,
                level
        )) {

            player.sendSystemMessage(
                    TextFormatter.parse(
                            "<gradient:#FFD700:#FFF8DC:#FFD700>&lMILESTONES</gradient> &7&l➤ <red>Error! You already claimed this milestone.</red>"
                    )
            );

            return false;
        }





        var progress =
                MilestoneProgressService.getProgress(
                        player,
                        definition
                );





        if (progress.level() < level) {

            player.sendSystemMessage(
                    TextFormatter.parse(
                            "<gradient:#FFD700:#FFF8DC:#FFD700>&lMILESTONES</gradient> &7&l➤ <red>Error! You have not unlocked this milestone yet.</red>"
                    )
            );

            return false;
        }






        double money =
                definition.getMoney(level);

        if (money > 0) {

            economy.add(
                    player,
                    org.howie.pixity.moderation.neoforge.economy.CurrencyType.MONEY,
                    money
            );
        }





        double tokens =
                definition.getTokens(level);

        if (tokens > 0) {

            economy.add(
                    player,
                    org.howie.pixity.moderation.neoforge.economy.CurrencyType.TOKENS,
                    tokens
            );
        }





        MinecraftServer server =
                player.getServer();

        if (server != null) {

            CommandSourceStack source =
                    server.createCommandSourceStack()
                            .withSuppressedOutput();

            for (MilestoneCommandReward reward :
                    definition.commandRewards) {

                if (!reward.shouldGive(level)) {
                    continue;
                }

                String command =
                        reward.command
                                .replace(
                                        "%player%",
                                        player.getGameProfile().getName()
                                )
                                .replace(
                                        "%level%",
                                        String.valueOf(level)
                                );

                server.getCommands().performPrefixedCommand(
                        source,
                        command
                );
            }
        }





        milestonePlayers.claim(
                player,
                milestoneId,
                level
        );





        player.sendSystemMessage(
                TextFormatter.parse(
                        "<gradient:#FFD700:#FFF8DC:#FFD700>&lMILESTONES</gradient> &7&l➤ " +
                                "<green>You claimed milestone level </green>" +
                                "<yellow>" + level + "</yellow><green>!</green>"
                )
        );

        player.playNotifySound(
                SoundEvents.PLAYER_LEVELUP,
                SoundSource.PLAYERS,
                1f,
                1.2f
        );

        return true;
    }
}