package org.howie.pixity.moderation.neoforge.auctionhouse;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;

public class AuctionExpiryScheduler {

    private static boolean running = false;

    public static void start(MinecraftServer server) {
        running = true;
    }

    public static void schedule(MinecraftServer server, AuctionListing listing) {

        if (listing == null) return;

        long delayMs = listing.expiresAt - System.currentTimeMillis();

        if (delayMs < 0) delayMs = 0;

        int delayTicks = (int) (delayMs / 50);

        server.tell(new TickTask(delayTicks, () -> {

            if (listing.isExpired()) {
                AuctionDatabase.expire(listing);
            }

        }));
    }
}