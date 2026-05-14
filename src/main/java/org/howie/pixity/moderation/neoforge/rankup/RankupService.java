package org.howie.pixity.moderation.neoforge.rankup;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;

import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.shop.EconomyBridge;

import java.util.List;

public class RankupService {

    private static final List<String> RANKS = List.of(
            "default",
            "trainer",
            "adventurer",
            "ace",
            "challenger",
            "champion"
    );


    private static final List<Double> COSTS = List.of(
            0.0,
            75000.0,
            150000.0,
            300000.0,
            500000.0,
            1000000.0
    );

    public static String getNextRank(String current) {
        int index = RANKS.indexOf(current);
        if (index == -1 || index >= RANKS.size() - 1) return null;
        return RANKS.get(index + 1);
    }

    public static double getNextCost(String current) {
        int index = RANKS.indexOf(current);
        if (index == -1 || index >= COSTS.size() - 1) return 0;
        return COSTS.get(index + 1);
    }


    public static void rankup(ServerPlayer player,
                              RankService rankService,
                              EconomyBridge econ) {

        String current = getCurrentRank(player, rankService);
        int index = RANKS.indexOf(current);

        if (index == -1) {
            player.sendSystemMessage(Component.literal("§cUnknown rank."));
            return;
        }

        if (index >= RANKS.size() - 1) {
            player.sendSystemMessage(Component.literal("§aYou are already max rank!"));
            return;
        }

        String next = RANKS.get(index + 1);
        double cost = COSTS.get(index + 1);


        if (!econ.has(player, cost, "money")) {
            player.sendSystemMessage(
                    Component.literal("§cYou need $" + (int) cost + " to rank up!")
            );
            return;
        }


        econ.take(player, cost, "money");


        player.getServer().getCommands().performPrefixedCommand(
                player.getServer().createCommandSourceStack(),
                "lp user " + player.getGameProfile().getName() + " parent remove " + current
        );


        player.getServer().getCommands().performPrefixedCommand(
                player.getServer().createCommandSourceStack(),
                "lp user " + player.getGameProfile().getName() + " parent add " + next
        );

        player.sendSystemMessage(
                Component.literal("§aRanked up to §e" + next + " §afor $" + (int) cost + "!")
        );
    }

    public static String getCurrentRank(ServerPlayer player, RankService rankService) {

        for (int i = RANKS.size() - 1; i >= 0; i--) {
            String rank = RANKS.get(i);

            if (rankService.hasPerm(player, "group." + rank)) {
                return rank;
            }
        }

        return "default";
    }
}