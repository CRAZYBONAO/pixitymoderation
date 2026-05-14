package org.howie.pixity.moderation.neoforge.kits;

import java.util.List;
import java.util.UUID;

public final class Kit {
    public String name;
    public String displayNameRaw;
    public long cooldownSeconds;
    public UUID createdBy;
    public long createdAtEpochMs;

    public String category;
    public double price;
    public String currency;

    public List<String> itemsSnbt;
    public String iconSnbt;

    public Kit() {}
}