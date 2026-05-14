package org.howie.pixity.moderation.neoforge.queue;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.minecraft.server.level.ServerPlayer;

public final class QueueDisconnectListener {

    private final QueueService svc;

    public QueueDisconnectListener(final QueueService svc) {
        this.svc = svc;
    }

    @SubscribeEvent
    public void onLogout(final PlayerEvent.PlayerLoggedOutEvent e) {
        if (e.getEntity() instanceof ServerPlayer p) {
            svc.recordDisconnect(p);
        }
    }
}
