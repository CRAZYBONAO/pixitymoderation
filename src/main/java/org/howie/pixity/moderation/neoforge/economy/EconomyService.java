package org.howie.pixity.moderation.neoforge.economy;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;

public final class EconomyService {

    private final Logger logger;
    private final SQLiteEconomyStore store;


    private final Map<UUID, EconomyAccount> accounts = new ConcurrentHashMap<>();


    private volatile boolean dirty = false;

    public EconomyService(Logger logger, SQLiteEconomyStore store) {
        this.logger = logger;
        this.store = store;

        this.accounts.putAll(store.load());
    }




    private EconomyAccount getAccount(UUID uuid) {
        return accounts.computeIfAbsent(uuid, k -> {
            dirty = true;
            return new EconomyAccount();
        });
    }




    public double get(UUID uuid, CurrencyType type) {
        return getAccount(uuid).get(type);
    }

    public double get(ServerPlayer p, CurrencyType type) {
        return get(p.getUUID(), type);
    }




    public void add(UUID uuid, CurrencyType type, double amount) {

        if (amount <= 0) return;

        EconomyAccount acc = getAccount(uuid);

        acc.set(type, acc.get(type) + amount);

        dirty = true;
    }

    public void add(ServerPlayer p, CurrencyType type, double amount) {
        add(p.getUUID(), type, amount);
    }




    public boolean remove(UUID uuid, CurrencyType type, double amount) {

        if (amount <= 0) return false;

        EconomyAccount acc = getAccount(uuid);
        double bal = acc.get(type);

        if (bal < amount) return false;

        acc.set(type, bal - amount);

        dirty = true;
        return true;
    }

    public boolean remove(ServerPlayer p, CurrencyType type, double amount) {
        return remove(p.getUUID(), type, amount);
    }




    public void set(UUID uuid, CurrencyType type, double amount) {

        if (amount < 0) amount = 0;

        getAccount(uuid).set(type, amount);

        dirty = true;
    }

    public void set(ServerPlayer p, CurrencyType type, double amount) {
        set(p.getUUID(), type, amount);
    }

    public Set<UUID> getAllUsers() {
        return new java.util.HashSet<>(accounts.keySet());
    }




    public void saveAll() {

        if (!dirty) return;

        try {
            store.save(new HashMap<>(accounts));
            dirty = false;

            logger.info("[Economy] Saved " + accounts.size() + " accounts");

        } catch (Exception e) {
            logger.error("Failed to save economy data", e);
        }
    }




    @SubscribeEvent
    public void onServerTick(ServerTickEvent.Post e) {

        if (e.getServer().getTickCount() % 6000 != 0) return;

        saveAll();
    }




    @SubscribeEvent
    public void onShutdown(ServerStoppingEvent e) {
        logger.info("[Economy] Server stopping, saving data...");
        saveAll();
    }
}