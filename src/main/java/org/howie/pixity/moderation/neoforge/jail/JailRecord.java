package org.howie.pixity.moderation.neoforge.jail;

import java.util.UUID;

public final class JailRecord {
    public UUID player;
    public String playerName;

    public String jailName;


    public long expiresAtMs;

    public UUID staffUuid;
    public String staffName;

    public String reason;

    public JailRecord() {}
}
