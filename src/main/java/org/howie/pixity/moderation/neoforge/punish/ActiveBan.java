package org.howie.pixity.moderation.neoforge.punish;

import java.util.UUID;

public final class ActiveBan {
    public UUID targetUuid;
    public String targetNameLower;


    public long expiresAtMs;

    public String reason;
    public UUID staffUuid;
    public String staffName;
    public long createdAtMs;

    public ActiveBan() {}

    public boolean isPermanent() { return expiresAtMs < 0; }

    public boolean isExpired(final long nowMs) {
        return !isPermanent() && nowMs >= expiresAtMs;
    }
}
