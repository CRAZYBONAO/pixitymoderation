package org.howie.pixity.moderation.neoforge.inspect;

public final class InspectConfig {

    public boolean enabled = true;


    public int maxEventsPerBlock = 10;


    public int retentionDays = 30;


    public boolean logInteractions = true;


    public boolean logPlaces = true;


    public boolean logBreaks = true;


    public String permissionUse = "pixity.inspect";


    public String permissionBypass = "pixity.inspect.bypass";

    public InspectConfig() {}
}
