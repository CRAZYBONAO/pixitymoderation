
package org.howie.pixity.moderation.neoforge.alts.smart;

import java.util.ArrayList;
import java.util.List;


public final class SmartAltConfig {


    public boolean enabled = true;


    public int keepDays = 30;


    public int maxEntriesPerPlayer = 50;


    public int minSharedHits = 1;


    public int maxSuggestions = 12;


    public boolean maskIp = true;


    public boolean ignoreLocal = true;

    public SmartAltConfig() {}
}
