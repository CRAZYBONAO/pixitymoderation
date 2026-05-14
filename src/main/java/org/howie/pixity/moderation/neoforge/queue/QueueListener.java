package org.howie.pixity.moderation.neoforge.queue;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.minecraft.server.level.ServerPlayer;

public final class QueueListener {

    private final QueueService svc;

    public QueueListener(final QueueService svc) {
        this.svc = svc;
    }

    @SubscribeEvent
    public void onJoin(final PlayerEvent.PlayerLoggedInEvent e) {
        if (e.getEntity() instanceof ServerPlayer p) {
            svc.onJoin(p.server, p);
        }
    }
}
