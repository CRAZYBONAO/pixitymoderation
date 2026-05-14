package org.howie.pixity.moderation.neoforge.economy;

public final class EconomyAccount {

    public double money;
    public double coins;
    public double tokens;

    public EconomyAccount() {}

    public double get(CurrencyType type) {
        return switch (type) {
            case MONEY -> money;
            case COINS -> coins;
            case TOKENS -> tokens;
        };
    }

    public void set(CurrencyType type, double value) {
        switch (type) {
            case MONEY -> money = value;
            case COINS -> coins = value;
            case TOKENS -> tokens = value;
        }
    }
}