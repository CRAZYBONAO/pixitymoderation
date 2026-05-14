package org.howie.pixity.moderation.neoforge.rules;

import java.util.ArrayList;
import java.util.List;


public final class RulesConfig {
    public String header = "Server Rules";
    public List<String> lines = new ArrayList<>();


    public boolean showOnFirstJoin = true;


    public String bypassPermission = "pixity.rules.bypass";

    public RulesConfig() {
        lines.add("1) Be respectful.");
        lines.add("2) No cheating/exploits.");
        lines.add("3) No harassment.");
        lines.add("4) Use common sense.");
    }
}
