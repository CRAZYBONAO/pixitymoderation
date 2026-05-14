package org.howie.pixity.moderation.chat;

import net.minecraft.network.chat.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class CachedText {

    private static final Map<String, Component> CACHE = new ConcurrentHashMap<>();

    private CachedText() {}

    public static Component of(String text) {
        if (text == null || text.isEmpty()) return Component.empty();

        return CACHE.computeIfAbsent(text, TextFormatter::parse);
    }

    public static void clear() {
        CACHE.clear();
    }
}