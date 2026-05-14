package org.howie.pixity.moderation.neoforge.auctionhouse;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class AuctionListing {

    public UUID id;

    public UUID seller;
    public String sellerName;

    public String itemData;
    public int amount;

    public double price;
    public String currency;

    public long createdAt;
    public long expiresAt;

    public boolean sold = false;




    public AuctionListing(
            UUID seller,
            String sellerName,
            String itemData,
            int amount,
            double price,
            String currency,
            long durationMillis
    ) {
        this.seller = seller;
        this.sellerName = sellerName;
        this.itemData = itemData;
        this.amount = amount;
        this.price = price;
        this.currency = currency;

        this.createdAt = System.currentTimeMillis();
        this.expiresAt = this.createdAt + durationMillis;
        this.id = UUID.randomUUID();
    }


    public AuctionListing() {}


    public boolean isExpired() {
        return System.currentTimeMillis() > expiresAt;
    }


    public ItemStack getItem(RegistryAccess registry) {
        try {
            var tag = TagParser.parseTag(itemData);
            return ItemStack.parseOptional(registry, tag);
        } catch (Exception e) {
            return ItemStack.EMPTY;
        }
    }

    public String getSearchText() {
        return (sellerName + " " + currency).toLowerCase();
    }
}