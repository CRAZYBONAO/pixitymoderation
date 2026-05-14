package org.howie.pixity.moderation.neoforge.punish;

import java.util.UUID;

public final class PunishEntry {
    public long tsEpochMs;

    public PunishAction action;

    public UUID staffUuid;
    public String staffName;

    public UUID targetUuid;
    public String targetName;


    public Long durationSeconds;

    public String reason;

    public PunishEntry() {}
}
