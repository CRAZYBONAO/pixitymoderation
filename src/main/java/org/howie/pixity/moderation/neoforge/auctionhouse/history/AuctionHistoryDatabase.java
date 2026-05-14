package org.howie.pixity.moderation.neoforge.auctionhouse.history;

import org.howie.pixity.moderation.neoforge.auctionhouse.AuctionListing;

import java.util.*;

public class AuctionHistoryDatabase {

    private static final int MAX = 1000;

    private static final Deque<AuctionListing> HISTORY = new ArrayDeque<>();

    public static void add(AuctionListing listing) {


        HISTORY.addFirst(listing);


        while (HISTORY.size() > MAX) {
            HISTORY.removeLast();
        }
    }

    public static List<AuctionListing> getAll() {
        return new ArrayList<>(HISTORY);
    }

    public static List<AuctionListing> search(String query) {

        if (query == null || query.isEmpty()) {
            return getAll();
        }

        String q = query.toLowerCase();

        List<AuctionListing> result = new ArrayList<>();

        for (AuctionListing l : HISTORY) {

            if (l.sellerName.toLowerCase().contains(q)
                    || l.currency.toLowerCase().contains(q)
                    || l.itemData.toLowerCase().contains(q)) {

                result.add(l);
            }
        }

        return result;
    }
}