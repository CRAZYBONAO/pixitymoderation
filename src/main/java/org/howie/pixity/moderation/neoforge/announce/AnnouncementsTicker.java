package org.howie.pixity.moderation.neoforge.announce;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public final class AnnouncementsTicker {

    private final AnnouncementsService svc;
    private int tick = 0;

    public AnnouncementsTicker(final AnnouncementsService svc) {
        this.svc = svc;
    }

    @SubscribeEvent
    public void onServerTick(final ServerTickEvent.Post e) {

        if (svc == null || svc.config() == null || !svc.config().enabled) return;

        tick++;

        int every = Math.max(1, svc.config().intervalSeconds) * 20;
        if (tick % every != 0) return;

        svc.broadcastNext(e.getServer());
    }
}