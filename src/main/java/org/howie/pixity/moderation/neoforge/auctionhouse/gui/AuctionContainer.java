package org.howie.pixity.moderation.neoforge.auctionhouse.gui;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import org.howie.pixity.moderation.neoforge.auctionhouse.AuctionListing;

import java.util.Map;

public class AuctionContainer extends SimpleContainer {

    private final Map<Integer, AuctionListing> slotMap;

    public AuctionContainer(Map<Integer, AuctionListing> slotMap) {
        super(54);
        this.slotMap = slotMap;
    }

    public void setItem(int slot, ItemStack stack) {
        super.setItem(slot, stack);
    }
}