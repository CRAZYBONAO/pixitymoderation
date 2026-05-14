package org.howie.pixity.moderation.neoforge.shop;

import net.minecraft.server.level.ServerPlayer;
import org.howie.pixity.moderation.neoforge.rank.RankService;

public class BoostService {

    public static double getTotalMultiplier(ServerPlayer player, double wandMultiplier, RankService rankService) {

        double rankBoost = getRankBoost(player, rankService);
        double personalBoost = getMoneyBoost(player, rankService);
        double globalBoost = GlobalBoostService.getMultiplier();

        return wandMultiplier * rankBoost * personalBoost * globalBoost;
    }


    public static double getRankBoost(ServerPlayer player, RankService rankService) {

        if (rankService.hasPerm(player, "group.legendary")) return 2.0;
        if (rankService.hasPerm(player, "group.master")) return 1.75;
        if (rankService.hasPerm(player, "group.mystic")) return 1.5;
        if (rankService.hasPerm(player, "group.elite")) return 1.25;
        if (rankService.hasPerm(player, "group.shiny")) return 1.1;

        return 1.0;
    }


    public static double getMoneyBoost(ServerPlayer player, RankService rankService) {

        if (rankService.hasPerm(player, "pixity.money.boost.2.5")) return 2.5;
        if (rankService.hasPerm(player, "pixity.money.boost.2")) return 2.0;
        if (rankService.hasPerm(player, "pixity.money.boost.1.75")) return 1.75;
        if (rankService.hasPerm(player, "pixity.money.boost.1.5")) return 1.5;
        if (rankService.hasPerm(player, "pixity.money.boost.1.25")) return 1.25;

        return 1.0;
    }
}