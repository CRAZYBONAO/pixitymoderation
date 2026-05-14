package org.howie.pixity.moderation.neoforge.giveaway;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.world.BossEvent;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public class GiveawayTicker {

    private final GiveawayService service;

    public GiveawayTicker(GiveawayService service) {
        this.service = service;
    }

    @SubscribeEvent
    public void tick(ServerTickEvent.Post e) {

        if (!service.isRunning()) return;

        long left = service.getTimeLeft();
        if (left <= 0) {
            service.finish(e.getServer());
            return;
        }

        for (ServerPlayer p : e.getServer().getPlayerList().getPlayers()) {
            service.updateBossbar(p, left);
        }

        service.autoBroadcast(e.getServer(), left);
    }
}