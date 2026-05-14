package org.howie.pixity.moderation.neoforge.crate;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import net.minecraft.server.level.ServerLevel;

public class CrateAuraTicker {

    @SubscribeEvent
    public void onTick(LevelTickEvent.Post e) {

        if (!(e.getLevel() instanceof ServerLevel level)) return;

        CrateAuraService.tick(level);
    }
}