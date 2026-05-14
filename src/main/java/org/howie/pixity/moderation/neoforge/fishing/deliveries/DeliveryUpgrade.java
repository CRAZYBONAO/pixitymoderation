package org.howie.pixity.moderation.neoforge.fishing.deliveries;

public enum DeliveryUpgrade {

    CAPACITY("delivery_capacity", "Capacity", 10, 5000),
    JETBOAT("delivery_jetboat", "Jetboat", 10, 5000),
    EXPERT("delivery_expert", "Expert Deliveryman", 25, 3000),
    PAYRISE("delivery_payrise", "Pay Rise", 50, 4000),
    LUCKY("delivery_lucky", "Lucky Charm", 25, 3000);

    public final String dbKey;
    public final String display;
    public final int maxLevel;
    public final int baseCost;

    DeliveryUpgrade(String dbKey, String display, int maxLevel, int baseCost) {
        this.dbKey = dbKey;
        this.display = display;
        this.maxLevel = maxLevel;
        this.baseCost = baseCost;
    }

    public int getCost(int level) {

        return (int) (baseCost * (1 + (level * 0.15)));
    }
}