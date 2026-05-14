package org.howie.pixity.moderation.neoforge.tp;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.minecraft.server.level.ServerPlayer;

public final class TeleportWarmupListener {

    private final TeleportWarmupManager warmup;

    public TeleportWarmupListener(final TeleportWarmupManager warmup) {
        this.warmup = warmup;
    }

    @SubscribeEvent
    public void onTick(final PlayerTickEvent.Post e) {
        if (e.getEntity() instanceof ServerPlayer p) {
            warmup.tick(p.getServer(), p);
        }
    }

    @SubscribeEvent
    public void onLogout(final PlayerEvent.PlayerLoggedOutEvent e) {
        if (e.getEntity() instanceof ServerPlayer p) {
            warmup.onLogout(p.getUUID());
        }
    }
}