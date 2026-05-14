package org.howie.pixity.moderation.neoforge.auctionhouse.gui;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AuctionMenuRegistry {

    private static final Map<UUID, AuctionMenuState> STATES = new ConcurrentHashMap<>();

    public static AuctionMenuState get(UUID uuid) {
        return STATES.computeIfAbsent(uuid, AuctionMenuState::new);
    }

    public static void remove(UUID uuid) {
        STATES.remove(uuid);
    }
}