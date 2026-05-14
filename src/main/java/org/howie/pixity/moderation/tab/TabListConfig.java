package org.howie.pixity.moderation.tab;

import java.util.ArrayList;
import java.util.List;



public final class TabListConfig {


    public List<String> titleLines = new ArrayList<>();


    public List<String> footerLines = new ArrayList<>();


    public String playerFormat = "{PREFIX}{NAME}{SUFFIX}";


    public List<String> sortOrder = new ArrayList<>();


    public int updateEveryTicks = 20;

    public TabListConfig() {}
}
