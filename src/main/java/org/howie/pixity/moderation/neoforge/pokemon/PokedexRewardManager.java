package org.howie.pixity.moderation.neoforge.pokemon;

import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.chat.CachedText;
import org.howie.pixity.moderation.chat.TextFormatter;

public class PokedexRewardManager {

    public static void checkRewards(ServerPlayer player, int count) {

        for (var reward : PokedexRewardConfig.getRewards()) {

            if (count >= reward.milestone &&
                    !PokedexDatabase.hasReward(player.getUUID(), reward.milestone)) {

                PokedexDatabase.addReward(player.getUUID(), reward.milestone);

                applyReward(player, reward);

                player.sendSystemMessage(CachedText.of(
                        "<rainbow>&l🎉 POKEDEX REWARD 🎉</rainbow> <gold>"
                                + reward.milestone + " Pokémon!"
                ));
            }
        }
    }

    private static void applyReward(ServerPlayer player, PokedexRewardConfig.Reward reward) {

        switch (reward.type) {

            case "shiny_boost":
                ShinyBoostManager.enablePlayer(player, reward.multiplier, reward.duration);
                break;

            case "item":
                giveItem(player, reward.item);
                break;
        }
    }

    private static void giveItem(ServerPlayer player, String itemString) {
        try {
            String cmd = "give " + player.getName().getString() + " " + itemString;

            player.getServer().getCommands().performPrefixedCommand(
                    player.getServer().createCommandSourceStack(),
                    cmd
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}