package org.howie.pixity.moderation.neoforge.joinleave;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public final class JoinLeaveListener {

    private final JoinLeaveService svc;

    public JoinLeaveListener(final JoinLeaveService svc) {
        this.svc = svc;
    }

    @SubscribeEvent
    public void onJoin(final PlayerEvent.PlayerLoggedInEvent e) {

        if (!(e.getEntity() instanceof ServerPlayer p))
            return;


        MinecraftServer server = p.server;

        server.execute(() -> {
            svc.onJoin(server, p);
        });
    }

    @SubscribeEvent
    public void onLeave(final PlayerEvent.PlayerLoggedOutEvent e) {

        if (!(e.getEntity() instanceof ServerPlayer p))
            return;

        MinecraftServer server = p.server;

        server.execute(() -> {
            svc.onLeave(server, p);
        });
    }
}