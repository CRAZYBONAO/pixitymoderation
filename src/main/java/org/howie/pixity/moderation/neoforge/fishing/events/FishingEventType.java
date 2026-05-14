package org.howie.pixity.moderation.neoforge.fishing.events;

public enum FishingEventType {

    LONGEST,
    SHORTEST,
    BIOMES,
    BRONZE,
    SILVER,
    GOLD,
    SQUID,
    DOLPHIN;

    public static FishingEventType random() {
        FishingEventType[] values = values();
        return values[new java.util.Random().nextInt(values.length)];
    }
}
