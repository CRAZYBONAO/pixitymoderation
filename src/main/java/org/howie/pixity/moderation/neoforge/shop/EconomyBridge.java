package org.howie.pixity.moderation.neoforge.shop;

import net.minecraft.server.level.ServerPlayer;
import org.howie.pixity.moderation.neoforge.economy.*;

public class EconomyBridge {

    private final EconomyService econ;

    public EconomyBridge(EconomyService econ) {
        this.econ = econ;
    }


    private CurrencyType parse(String id) {

        try {
            return CurrencyType.valueOf(id.toUpperCase());
        } catch (Exception e) {
            return CurrencyType.MONEY;
        }
    }


    public boolean has(ServerPlayer p, double amount, String currency) {

        CurrencyType type = parse(currency);

        return econ.get(p, type) >= amount;
    }


    public boolean take(ServerPlayer p, double amount, String currency) {

        CurrencyType type = parse(currency);

        return econ.remove(p, type, amount);
    }


    public void give(ServerPlayer p, double amount, String currency) {

        CurrencyType type = parse(currency);

        econ.add(p, type, amount);
    }


    public double get(ServerPlayer p, String currency) {

        CurrencyType type = parse(currency);

        return econ.get(p, type);
    }
}