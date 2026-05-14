package org.howie.pixity.moderation.neoforge.punish.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public final class PunishGuiConfig {


    public Map<String, List<String>> presetReasons = new HashMap<>();


    public Map<String, List<String>> presetDurations = new HashMap<>();


    public Map<String, Map<String, List<String>>> perGroupDurations = new HashMap<>();


    public int recentReasonsMax = 5;


    public int recentReasonsRemember = 15;

    public PunishGuiConfig() {}

    public static PunishGuiConfig defaults() {
        PunishGuiConfig c = new PunishGuiConfig();

        c.presetReasons.put("WARN", list(
                "&eSpam",
                "&eInappropriate language",
                "&eHarassment / Toxicity",
                "&eAdvertising",
                "&eDisrespecting staff"
        ));

        c.presetReasons.put("KICK", list(
                "&cSpam",
                "&cInappropriate language",
                "&cHarassment / Toxicity",
                "&cAdvertising",
                "&cRefusing to comply"
        ));

        c.presetReasons.put("MUTE", list(
                "&6Chat spam",
                "&6Harassment / Toxicity",
                "&6Hate speech",
                "&6Advertising",
                "&6Mic spam / Soundboard"
        ));

        c.presetReasons.put("BAN", list(
                "&4Cheating / Hacked client",
                "&4Griefing",
                "&4Hate speech",
                "&4Doxxing / Threats",
                "&4Ban evasion"
        ));

        c.presetReasons.put("FREEZE", list(
                "&bStaff check",
                "&bSuspected cheating"
        ));

        c.presetReasons.put("JAIL", list(
                "&cGriefing",
                "&cTheft",
                "&cRule violation"
        ));

        c.presetReasons.put("UNBAN", list(
                "&aAppeal accepted",
                "&aMistaken ban"
        ));

        c.presetDurations.put("MUTE", list("5m","15m","1h","6h","1d"));
        c.presetDurations.put("BAN",  list("10m","1h","6h","1d","7d","perm"));



        return c;
    }

    private static List<String> list(final String... s) {
        List<String> out = new ArrayList<>();
        if (s != null) for (String v : s) out.add(v);
        return out;
    }
}
