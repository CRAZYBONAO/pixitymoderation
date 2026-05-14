package org.howie.pixity.moderation.neoforge.chatcontrol;

import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ChatControlService {

    public static final String PERM_SLOWCHAT = "pixity.chat.slowchat";
    public static final String PERM_CHATMUTE = "pixity.chat.mute";
    public static final String PERM_CLEARCHAT = "pixity.chat.clearchat";

    public static final String PERM_BYPASS_SLOWCHAT = "pixity.chat.slowchat.bypass";
    public static final String PERM_BYPASS_MUTE = "pixity.chat.mute.bypass";

    private final ChatControlConfig cfg;


    private volatile boolean chatMuted = false;
    private volatile int slowchatSeconds = 0;


    private final Map<UUID, Long> lastChatMs = new ConcurrentHashMap<>();

    public ChatControlService(final ChatControlConfig cfg) {
        this.cfg = cfg;
    }

    public ChatControlConfig config() { return cfg; }

    public boolean isChatMuted() { return chatMuted; }
    public int slowchatSeconds() { return slowchatSeconds; }

    public void setChatMuted(final boolean v) { chatMuted = v; }
    public void setSlowchatSeconds(final int sec) { slowchatSeconds = Math.max(0, sec); }

    public boolean checkSlowchat(final ServerPlayer p) {
        if (p == null) return true;
        int sec = slowchatSeconds;
        if (sec <= 0) return true;

        long now = System.currentTimeMillis();
        long last = lastChatMs.getOrDefault(p.getUUID(), 0L);
        if (last == 0L) {
            lastChatMs.put(p.getUUID(), now);
            return true;
        }
        long diff = now - last;
        if (diff >= (sec * 1000L)) {
            lastChatMs.put(p.getUUID(), now);
            return true;
        }
        return false;
    }

    public long secondsRemaining(final ServerPlayer p) {
        int sec = slowchatSeconds;
        if (p == null || sec <= 0) return 0L;
        long now = System.currentTimeMillis();
        long last = lastChatMs.getOrDefault(p.getUUID(), 0L);
        long remainMs = (sec * 1000L) - (now - last);
        if (remainMs <= 0) return 0L;
        long s = (remainMs + 999) / 1000;
        return Math.max(0, s);
    }

    public void noteChat(final ServerPlayer p) {
        if (p == null) return;
        lastChatMs.put(p.getUUID(), System.currentTimeMillis());
    }
}
