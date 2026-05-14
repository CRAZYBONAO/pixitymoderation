package org.howie.pixity.moderation.neoforge.reports;

public final class ReportEntry {
    public int id;
    public long ts;

    public String reporterUuid;
    public String reporterName;

    public String targetUuid;
    public String targetName;

    public String reason;

    public ReportStatus status = ReportStatus.OPEN;

    public String assignedUuid;
    public String assignedName;


    public long assignedTs;

    public long closedTs;


    public String closedBy;

    public String closeNote;

    public String lastLocation;

    public ReportEntry() {}
}