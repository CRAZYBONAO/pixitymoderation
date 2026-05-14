package org.howie.pixity.moderation.neoforge.chatgames;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.ServerChatEvent;
import net.neoforged.bus.api.SubscribeEvent;

public class ChatGamesListener {

    @SubscribeEvent
    public void onChat(ServerChatEvent event) {

        if (!ChatGameManager.isActive()) return;

        ServerPlayer player = event.getPlayer();
        String message = event.getRawText();

        boolean correct = ChatGameManager.tryAnswer(player, message);

        if (correct) {
            event.setCanceled(true);
        }
    }
}