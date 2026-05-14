package org.howie.pixity.moderation.neoforge.auctionhouse.gui;

import net.minecraft.world.SimpleContainer;
import org.howie.pixity.moderation.neoforge.auctionhouse.AuctionListing;
import org.howie.pixity.moderation.neoforge.economy.EconomyService;

public class ConfirmContainer extends SimpleContainer {

    public EconomyService econ;
    public AuctionListing listing;

    public ConfirmContainer(EconomyService econ, AuctionListing listing) {
        super(27);
        this.econ = econ;
        this.listing = listing;
    }
}