package org.howie.pixity.moderation.neoforge.chat.cosmetics;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public class AnimatedChatListener {

    private final AnimatedChatManager animatedChat;

    public AnimatedChatListener(AnimatedChatManager animatedChat) {
        this.animatedChat = animatedChat;
    }

    @SubscribeEvent
    public void onTick(ServerTickEvent.Post e) {
        animatedChat.tick();
    }
}