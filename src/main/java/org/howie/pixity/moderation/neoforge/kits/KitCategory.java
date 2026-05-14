package org.howie.pixity.moderation.neoforge.kits;

public enum KitCategory {
    FREE,
    DONATOR,
    PURCHASE,
    EVENT,
    VOTE;

    public static KitCategory from(String s) {
        if (s == null) return FREE;
        try {
            return KitCategory.valueOf(s.toUpperCase());
        } catch (Exception e) {
            return FREE;
        }
    }
}