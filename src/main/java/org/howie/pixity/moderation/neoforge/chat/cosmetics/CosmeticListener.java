package org.howie.pixity.moderation.neoforge.chat.cosmetics;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.minecraft.server.level.ServerPlayer;

public class CosmeticListener {

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event) {

        if (!(event.getEntity() instanceof ServerPlayer player)) return;


        if (player.level().isClientSide()) return;

        if (player.tickCount % 2 == 0) {
            org.howie.pixity.moderation.neoforge.chat.cosmetics.CosmeticService.tick(player);
        }


    }

    @SubscribeEvent
    public void onJoin(PlayerEvent.PlayerLoggedInEvent e) {
        if (e.getEntity() instanceof ServerPlayer sp) {
            org.howie.pixity.moderation.neoforge.chat.cosmetics.CosmeticService.load(sp);
        }
    }
}