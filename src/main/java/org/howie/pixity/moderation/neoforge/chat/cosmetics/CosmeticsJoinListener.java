package org.howie.pixity.moderation.neoforge.chat.cosmetics;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class CosmeticsJoinListener {

    private final ChatCosmeticsService cosmetics;

    public CosmeticsJoinListener(ChatCosmeticsService cosmetics) {
        this.cosmetics = cosmetics;
    }

    @SubscribeEvent
    public void onJoin(PlayerEvent.PlayerLoggedInEvent e) {
        if (!(e.getEntity() instanceof ServerPlayer player)) return;

        cosmetics.load(player.getUUID());
    }
}