package org.howie.pixity.moderation.neoforge.auctionhouse.gui;

import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AuctionInputHandler {

    private static final Map<UUID, InputType> WAITING = new ConcurrentHashMap<>();

    public enum InputType {
        SEARCH,
        MIN_PRICE,
        MAX_PRICE
    }

    public static void awaitSearch(ServerPlayer player) {
        WAITING.put(player.getUUID(), InputType.SEARCH);
    }

    public static void awaitMinPrice(ServerPlayer player) {
        WAITING.put(player.getUUID(), InputType.MIN_PRICE);
    }

    public static void awaitMaxPrice(ServerPlayer player) {
        WAITING.put(player.getUUID(), InputType.MAX_PRICE);
    }

    public static boolean handle(ServerPlayer player, String message) {

        InputType type = WAITING.remove(player.getUUID());
        if (type == null) return false;

        AuctionMenuState state =
                AuctionMenuRegistry.get(player.getUUID());

        try {
            switch (type) {

                case SEARCH -> state.search = message;

                case MIN_PRICE -> state.minPrice = Double.parseDouble(message);

                case MAX_PRICE -> state.maxPrice = Double.parseDouble(message);
            }

        } catch (Exception e) {
            player.sendSystemMessage(
                    org.howie.pixity.moderation.chat.TextFormatter.parse(
                            "&e&lAUCTIONHOUSE&7&l➤ &cError! Invalid input"
                    )
            );
        }

        AuctionBrowserGui.open(player, new AuctionMenuState(player.getUUID()));
        return true;
    }
}