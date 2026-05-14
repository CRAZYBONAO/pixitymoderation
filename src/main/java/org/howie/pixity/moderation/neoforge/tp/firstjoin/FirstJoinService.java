package org.howie.pixity.moderation.neoforge.tp.firstjoin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.neoforge.tp.TpService;
import org.howie.pixity.moderation.neoforge.tp.SQLiteTpStore;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class FirstJoinService {


    private final SQLiteTpStore store;
    private final TpService tp;

    private final Set<UUID> seen = ConcurrentHashMap.newKeySet();

    public FirstJoinService(SQLiteTpStore store, TpService tp) {
        this.store = store;
        this.tp = tp;

        seen.addAll(store.loadFirstJoins());
    }

    public void handleJoin(MinecraftServer server, ServerPlayer p) {
        UUID u = p.getUUID();

        if (seen.contains(u)) return;

        seen.add(u);
        store.saveFirstJoin(u);

        tp.teleportSpawn(server, p);
    }


}
