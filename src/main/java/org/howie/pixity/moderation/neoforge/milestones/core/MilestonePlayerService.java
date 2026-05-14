package org.howie.pixity.moderation.neoforge.milestones.core;

import net.minecraft.server.level.ServerPlayer;

import net.neoforged.bus.api.SubscribeEvent;

import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MilestonePlayerService {





    private final Logger logger;

    private final SQLiteMilestoneStore store;

    private final Map<UUID, MilestonePlayerData> cache =
            new ConcurrentHashMap<>();

    private volatile boolean dirty = false;





    public MilestonePlayerService(
            Logger logger,
            SQLiteMilestoneStore store
    ) {

        this.logger = logger;
        this.store = store;

        this.cache.putAll(
                store.load()
        );
    }





    private MilestonePlayerData getData(
            UUID uuid
    ) {

        return cache.computeIfAbsent(
                uuid,
                k -> {

                    dirty = true;

                    return new MilestonePlayerData();
                }
        );
    }





    public MilestonePlayerData get(
            UUID uuid
    ) {

        return getData(uuid);
    }

    public MilestonePlayerData get(
            ServerPlayer player
    ) {

        return getData(
                player.getUUID()
        );
    }





    public void claim(
            UUID uuid,
            String milestoneId,
            int level
    ) {

        getData(uuid)
                .claim(
                        milestoneId,
                        level
                );

        dirty = true;
    }

    public void claim(
            ServerPlayer player,
            String milestoneId,
            int level
    ) {

        claim(
                player.getUUID(),
                milestoneId,
                level
        );
    }





    public boolean hasClaimed(
            UUID uuid,
            String milestoneId,
            int level
    ) {

        return getData(uuid)
                .hasClaimed(
                        milestoneId,
                        level
                );
    }

    public boolean hasClaimed(
            ServerPlayer player,
            String milestoneId,
            int level
    ) {

        return hasClaimed(
                player.getUUID(),
                milestoneId,
                level
        );
    }





    public void reset(
            UUID uuid
    ) {

        cache.put(
                uuid,
                new MilestonePlayerData()
        );

        dirty = true;
    }





    public void saveAll() {

        if (!dirty) {
            return;
        }

        try {

            store.save(
                    new HashMap<>(cache)
            );

            dirty = false;

            logger.info(
                    "[Milestones] Saved " +
                            cache.size() +
                            " player milestone profiles"
            );

        } catch (Exception e) {

            logger.error(
                    "Failed to save milestone player data",
                    e
            );
        }
    }





    public void savePlayer(
            UUID uuid
    ) {

        try {

            Map<UUID, MilestonePlayerData> map =
                    new HashMap<>();

            map.put(
                    uuid,
                    getData(uuid)
            );

            store.save(map);

        } catch (Exception e) {

            logger.error(
                    "Failed to save player milestone data",
                    e
            );
        }
    }





    public Map<UUID, MilestonePlayerData> getAll() {
        return cache;
    }





    @SubscribeEvent
    public void onTick(
            ServerTickEvent.Post event
    ) {


        if (
                event.getServer()
                        .getTickCount() % 6000 != 0
        ) {
            return;
        }

        saveAll();
    }





    @SubscribeEvent
    public void onShutdown(
            ServerStoppingEvent event
    ) {

        logger.info(
                "[Milestones] Saving milestone data on shutdown..."
        );

        saveAll();
    }
}