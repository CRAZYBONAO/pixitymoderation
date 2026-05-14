package org.howie.pixity.moderation.neoforge.rollback;

public final class RollbackConfig {

    public boolean enabled = true;


    public int retentionDays = 14;


    public int maxActionsPerCommand = 500;


    public boolean containerRollbackEnabled = true;


    public boolean capabilityRollbackEnabled = true;


    public boolean nbtRollbackFallbackEnabled = true;


    public boolean networkTransactionLoggingEnabled = true;


    public boolean networkFluidLoggingEnabled = true;


    public boolean networkRollbackEnabled = true;


    public boolean networkFluidRollbackEnabled = true;


    public boolean logPlayerFacingTitle = true;


    public String permissionUse = "pixity.rollback";

    public RollbackConfig() {}
}
