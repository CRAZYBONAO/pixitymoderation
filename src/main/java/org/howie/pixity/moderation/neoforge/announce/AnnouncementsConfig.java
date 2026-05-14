package org.howie.pixity.moderation.neoforge.announce;

import java.util.ArrayList;
import java.util.List;

public final class AnnouncementsConfig {


    public boolean enabled = true;


    public int intervalSeconds = 600;


    public String mode = "CHAT";


    public int titleFadeInTicks = 10;
    public int titleStayTicks = 60;
    public int titleFadeOutTicks = 10;


    public List<String> messages = new ArrayList<>();

    public AnnouncementsConfig() {
        messages.add("&bWelcome to &fPixity&b!");
        messages.add("&eJoin our Discord: &fdiscord.gg/yourlink");
    }
}
