package org.howie.pixity.moderation.neoforge.shop;

public enum ShopSortType {

    NONE,
    PRICE_HIGH,
    PRICE_LOW,
    A_Z,
    Z_A;

    public ShopSortType next() {
        return values()[(this.ordinal() + 1) % values().length];
    }

    public String getDisplay() {
        return switch (this) {
            case NONE -> "&7Sort: &fDefault";
            case PRICE_HIGH -> "&7Sort: &cHighest Price";
            case PRICE_LOW -> "&7Sort: &aLowest Price";
            case A_Z -> "&7Sort: &bA → Z";
            case Z_A -> "&7Sort: &dZ → A";
        };
    }
}