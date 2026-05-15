package org.howie.pixity.moderation.neoforge.contribution;

import net.minecraft.server.level.ServerPlayer;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ContributionService {

    private final Logger logger;

    private final SQLiteContributionStore store;

    private final Map<UUID, ContributionData>
            cache =
            new ConcurrentHashMap<>();

    private volatile boolean dirty = false;

    public ContributionService(
            Logger logger,
            SQLiteContributionStore store
    ) {

        this.logger = logger;
        this.store = store;

        cache.putAll(store.load());
    }

    private ContributionData getData(
            UUID uuid
    ) {

        return cache.computeIfAbsent(

                uuid,

                id -> {

                    dirty = true;

                    return new ContributionData();
                }
        );
    }

    public ContributionData get(
            UUID uuid
    ) {

        return getData(uuid);
    }

    public double current(
            UUID uuid
    ) {

        return getData(uuid)
                .getCurrent();
    }

    public double lifetime(
            UUID uuid
    ) {

        return getData(uuid)
                .getLifetime();
    }

    public void add(
            UUID uuid,
            double amount
    ) {

        if (amount <= 0)
            return;

        getData(uuid)
                .add(amount);

        dirty = true;
    }

    public void add(
            ServerPlayer player,
            double amount
    ) {

        add(
                player.getUUID(),
                amount
        );
    }

    public void remove(
            UUID uuid,
            double amount
    ) {

        if (amount <= 0)
            return;

        getData(uuid)
                .remove(amount);

        dirty = true;
    }

    public void set(
            UUID uuid,
            double amount
    ) {

        if (amount < 0)
            amount = 0;

        getData(uuid)
                .setCurrent(amount);

        dirty = true;
    }

    public Map<UUID, ContributionData> getAll() {
        return cache;
    }

    public void saveAll() {

        if (!dirty)
            return;

        try {

            store.save(
                    new HashMap<>(cache)
            );

            dirty = false;

            logger.info(
                    "[Contribution] Saved "
                            + cache.size()
                            + " profiles"
            );

        } catch (Exception e) {

            logger.error(
                    "Failed saving contributions",
                    e
            );
        }
    }

    @SubscribeEvent
    public void onTick(
            ServerTickEvent.Post e
    ) {

        if (e.getServer().getTickCount() % 6000 != 0)
            return;

        saveAll();
    }

    @SubscribeEvent
    public void onShutdown(
            ServerStoppingEvent e
    ) {

        saveAll();
    }
}