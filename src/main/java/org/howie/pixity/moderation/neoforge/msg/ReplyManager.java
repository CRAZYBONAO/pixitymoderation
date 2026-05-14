package org.howie.pixity.moderation.neoforge.msg;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ReplyManager {

    private final Map<UUID, UUID> last = new ConcurrentHashMap<>();

    public void setLast(final UUID sender, final UUID target) {
        if (sender == null || target == null) return;
        last.put(sender, target);
    }

    public UUID getLast(final UUID sender) {
        return sender == null ? null : last.get(sender);
    }
}
