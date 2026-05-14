package org.howie.pixity.moderation.neoforge.tp.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.ServerChatEvent;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;


public final class ChatPromptService {

    private static final Map<UUID, Consumer<String>> WAITING = new ConcurrentHashMap<>();
    private static volatile boolean REGISTERED = false;

    private ChatPromptService() {}

    public static void prompt(final ServerPlayer p, final String prompt, final Consumer<String> onInput) {
        if (p == null || onInput == null) return;

        ensureRegistered();
        WAITING.put(p.getUUID(), onInput);
        if (prompt != null && !prompt.isBlank()) {
            p.sendSystemMessage(LegacyAmpersand.parse(prompt));
        }
    }

    private static void ensureRegistered() {
        if (REGISTERED) return;
        REGISTERED = true;
        NeoForge.EVENT_BUS.register(ChatPromptService.class);
    }

    @SubscribeEvent
    public static void onChat(final ServerChatEvent e) {
        final ServerPlayer p = e.getPlayer();
        if (p == null) return;

        final Consumer<String> c = WAITING.remove(p.getUUID());
        if (c == null) return;

        e.setCanceled(true);
        c.accept(e.getMessage().getString());
    }
}
