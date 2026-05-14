package org.howie.pixity.moderation.neoforge.util;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.chat.DisplayFormatter;
import org.howie.pixity.moderation.chat.NickManager;
import org.howie.pixity.moderation.neoforge.economy.*;
import org.howie.pixity.moderation.neoforge.fly.FlyTimeService;

public final class PlayerHoverUtil {

    private PlayerHoverUtil() {}

    public static Component buildHover(ServerPlayer p,
                                       NickManager nick,
                                       EconomyService economy,
                                       FlyTimeService fly) {

        Component name = DisplayFormatter.formatPlayer(p);

        long playtimeTicks = p.getStats().getValue(
                net.minecraft.stats.Stats.CUSTOM,
                net.minecraft.stats.Stats.PLAY_TIME
        );

        long seconds = playtimeTicks / 20;

        double money = economy.get(p, CurrencyType.MONEY);
        double tokens = economy.get(p, CurrencyType.TOKENS);
        double coins = economy.get(p, CurrencyType.COINS);

        long flySeconds = fly.getTime(p.getUUID());

        return Component.literal("")
                .append(Component.literal("§eUsername: §f"))
                .append(name)
                .append(Component.literal("\n"))

                .append(Component.literal("§ePlaytime: §f" + formatTime(seconds) + "\n"))

                .append(Component.literal("§eMoney: §a" + formatMoney(money) + "\n"))
                .append(Component.literal("§eTokens: §b" + formatComma(tokens) + "\n"))
                .append(Component.literal("§eCoins: §6" + formatComma(coins) + "\n"))

                .append(Component.literal("§eFlight Time: §f" + formatTime(flySeconds)));
    }



    private static String formatTime(long seconds) {
        long m = seconds / 60;
        long h = m / 60;

        if (h > 0) return h + "h " + (m % 60) + "m";
        return m + "m";
    }

    private static String formatMoney(double n) {
        if (n >= 1_000_000) return "$" + String.format("%.1fM", n / 1_000_000);
        if (n >= 1_000) return "$" + (long)(n / 1_000) + "k";
        return "$" + (long)n;
    }

    private static String formatComma(double n) {
        return String.format("%,d", (long)n);
    }
}