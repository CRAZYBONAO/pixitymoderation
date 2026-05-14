package org.howie.pixity.moderation.neoforge.combat;

import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class CombatTagService {


    private final Map<UUID, Long> tagged = new ConcurrentHashMap<>();

    private final int durationSeconds;

    public CombatTagService(int durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public void tag(ServerPlayer p) {
        tagged.put(p.getUUID(), System.currentTimeMillis() + (durationSeconds * 1000L));
    }

    public boolean isTagged(ServerPlayer p) {
        Long until = tagged.get(p.getUUID());
        if (until == null) return false;

        if (System.currentTimeMillis() > until) {
            tagged.remove(p.getUUID());
            return false;
        }

        return true;
    }

    public int getRemaining(ServerPlayer p) {
        Long until = tagged.get(p.getUUID());
        if (until == null) return 0;

        long left = until - System.currentTimeMillis();
        return (int) Math.max(0, (left + 999) / 1000);
    }


}
