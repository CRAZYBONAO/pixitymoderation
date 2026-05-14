package org.howie.pixity.moderation.neoforge.economy;

public final class CurrencyFormatter {

    private CurrencyFormatter() {}

    public static String symbol(CurrencyType type) {
        return switch (type) {
            case MONEY -> "$";
            case COINS -> "⛁ ";
            case TOKENS -> "✦ ";
        };
    }

    public static String name(CurrencyType type) {
        return switch (type) {
            case MONEY -> "Money";
            case COINS -> "Coins";
            case TOKENS -> "Tokens";
        };
    }

    public static String format(CurrencyType type, double amount) {
        return symbol(type) + amount;
    }
}