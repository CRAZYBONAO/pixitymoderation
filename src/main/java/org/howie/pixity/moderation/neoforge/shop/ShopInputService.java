package org.howie.pixity.moderation.neoforge.shop;

import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ShopInputService {

    public static class InputContext {
        public ShopItem item;
        public String shopId;
    }

    private static final Map<UUID, InputContext> waiting = new ConcurrentHashMap<>();

    public static void start(ServerPlayer player, ShopItem item, String shopId) {
        InputContext ctx = new InputContext();
        ctx.item = item;
        ctx.shopId = shopId;

        waiting.put(player.getUUID(), ctx);
    }

    public static InputContext get(ServerPlayer player) {
        return waiting.get(player.getUUID());
    }

    public static void clear(ServerPlayer player) {
        waiting.remove(player.getUUID());
    }

    public static boolean isWaiting(ServerPlayer player) {
        return waiting.containsKey(player.getUUID());
    }
}