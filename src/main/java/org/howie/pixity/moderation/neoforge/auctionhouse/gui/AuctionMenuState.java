package org.howie.pixity.moderation.neoforge.auctionhouse.gui;

import java.util.UUID;

public class AuctionMenuState {

    public UUID player;

    public int page = 0;

    public SortType sort = SortType.NEWEST;


    public String search = "";
    public String currencyFilter = null;

    public double minPrice = -1;
    public double maxPrice = -1;

    public AuctionMenuState(UUID player) {
        this.player = player;
        this.sort = SortType.NEWEST;
    }

    public enum SortType {
        NEWEST,
        OLDEST,
        PRICE_LOW,
        PRICE_HIGH
    }
}