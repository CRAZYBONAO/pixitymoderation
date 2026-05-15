package org.howie.pixity.moderation.neoforge.playtime;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PlaytimeService {

    private final SQLitePlaytimeStore store;
    private final Map<UUID, Long> playtime = new ConcurrentHashMap<>();

    public PlaytimeService(SQLitePlaytimeStore store) {
        this.store = store;
        this.playtime.putAll(store.load());
    }

    public long getPlaytime(UUID uuid) {
        return playtime.getOrDefault(uuid, 0L);
    }

    public void set(UUID uuid, long seconds) {
        playtime.put(uuid, seconds);
    }

    public void add(UUID uuid, long seconds) {
        playtime.merge(uuid, seconds, Long::sum);
    }

    public void remove(UUID uuid, long seconds) {
        playtime.computeIfPresent(uuid, (u, t) ->
                Math.max(0, t - seconds));
    }

    public void saveAll() {
        store.save(playtime);
    }

    public void save() {
        store.save(playtime);
    }

    @SubscribeEvent
    public void onTick(ServerTickEvent.Post e) {

        if (e.getServer().getTickCount() % 20 != 0) return;

        for (ServerPlayer p : e.getServer().getPlayerList().getPlayers()) {
            playtime.merge(p.getUUID(), 1L, Long::sum);
        }
    }

    @SubscribeEvent
    public void onShutdown(ServerStoppingEvent e) {
        store.save(playtime);
    }

    public Map<UUID, Long> getAll() {
        return playtime;
    }
}