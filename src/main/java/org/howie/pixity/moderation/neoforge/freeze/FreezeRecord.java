package org.howie.pixity.moderation.neoforge.freeze;

import java.util.UUID;

public final class FreezeRecord {
    public UUID player;
    public String playerName;

    public long createdAtMs;

    public UUID staffUuid;
    public String staffName;

    public String reason;

    public FreezeRecord() {}
}
