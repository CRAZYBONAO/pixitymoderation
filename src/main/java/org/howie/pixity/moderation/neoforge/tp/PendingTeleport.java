package org.howie.pixity.moderation.neoforge.tp;

public final class PendingTeleport {
    public WarpPos target;
    public long executeAtMs;

    public double startX;
    public double startY;
    public double startZ;

    public String label;

    public PendingTeleport() {}
}
