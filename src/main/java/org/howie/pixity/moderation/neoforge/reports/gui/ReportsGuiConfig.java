package org.howie.pixity.moderation.neoforge.reports.gui;

import java.util.ArrayList;
import java.util.List;


public final class ReportsGuiConfig {


    public List<String> presetCloseReasons = new ArrayList<>();

    public ReportsGuiConfig() {

        presetCloseReasons.add("Forwarded To Management");
        presetCloseReasons.add("No evidence");
        presetCloseReasons.add("False report");
        presetCloseReasons.add("Duplicate");
        presetCloseReasons.add("Resolved");
        presetCloseReasons.add("Not actionable");
        presetCloseReasons.add("Warned");
    }
}
