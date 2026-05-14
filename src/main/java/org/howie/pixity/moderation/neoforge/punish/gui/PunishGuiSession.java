package org.howie.pixity.moderation.neoforge.punish.gui;

import java.util.UUID;

public final class PunishGuiSession {
    public UUID targetUuid;
    public String targetName;

    public String action;
    public Long durationSeconds;
    public String jailName;

    public String selectedCategory;

    public String reason;
}
