package org.howie.pixity.moderation.chat;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import org.howie.pixity.moderation.neoforge.punish.PunishmentManager;

public class MuteTicker {

    private final MuteManager mutes;
    private final PunishmentManager punish;

    public MuteTicker(MuteManager mutes, PunishmentManager punish) {
        this.mutes = mutes;
        this.punish = punish;
    }

    @SubscribeEvent
    public void tick(ServerTickEvent.Post e) {
        mutes.cleanupExpired(punish);
    }
}