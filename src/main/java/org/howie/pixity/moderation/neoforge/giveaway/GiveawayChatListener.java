package org.howie.pixity.moderation.neoforge.giveaway;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.event.ServerChatEvent;

import net.minecraft.server.level.ServerPlayer;

public final class GiveawayChatListener {

    private final GiveawayChatPromptService prompts;

    public GiveawayChatListener(GiveawayChatPromptService prompts) {
        this.prompts = prompts;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChat(ServerChatEvent e) {

        ServerPlayer p = e.getPlayer();
        String msg = e.getMessage().getString();

        if (prompts.tryConsume(p.server, p, msg)) {
            e.setCanceled(true);
        }
    }
}