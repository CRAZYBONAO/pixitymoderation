package org.howie.pixity.moderation.neoforge.fishing;

public enum FishTier {
    BRONZE,
    SILVER,
    GOLD,
    DIAMOND,
    PLATINUM,
    MYTHICAL;

    public String getColor() {
        return switch (this) {
            case BRONZE -> "&#c99a73";
            case SILVER -> "&#bdbdbd";
            case GOLD -> "&#f5f788";
            case DIAMOND -> "&#57f2e8";
            case PLATINUM -> "&#2deb95";
            case MYTHICAL -> "&#db2deb";
        };
    }
}

