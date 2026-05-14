package org.howie.pixity.moderation.neoforge.tp;


public final class WarmupConfig {


    public int warmupSeconds = 3;


    public double cancelMoveDistance = 0.35;


    public String bypassPermission = "pixity.tp.instant";


    public boolean actionbarCountdown = true;


    public boolean bossbarCountdown = true;


    public String bossbarTitle = "Teleporting {label} in {seconds}s";

    public WarmupConfig() {}
}
