package org.howie.pixity.moderation.neoforge.msg;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class SocialSpyManager {

    private final Set<UUID> enabled = ConcurrentHashMap.newKeySet();

    public boolean toggle(final UUID uuid) {
        if (uuid == null) return false;
        if (enabled.contains(uuid)) {
            enabled.remove(uuid);
            return false;
        }
        enabled.add(uuid);
        return true;
    }

    public boolean isEnabled(final UUID uuid) {
        return uuid != null && enabled.contains(uuid);
    }
}
