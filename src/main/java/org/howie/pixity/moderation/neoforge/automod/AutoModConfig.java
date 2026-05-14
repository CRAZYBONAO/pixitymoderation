package org.howie.pixity.moderation.neoforge.automod;

import java.util.ArrayList;
import java.util.List;

public final class AutoModConfig {

    public boolean enabled = true;


    public boolean blockLinks = true;
    public String linkBlockedMessage = "&cLinks are not allowed.";


    public boolean capsFilter = true;
    public int capsMinLength = 12;
    public double capsRatio = 0.70;
    public String capsBlockedMessage = "&ePlease don't spam caps.";


    public boolean repeatFilter = true;
    public int repeatWindowSeconds = 10;
    public String repeatBlockedMessage = "&ePlease don't repeat messages.";


    public boolean rateFilter = true;
    public int rateWindowSeconds = 6;
    public int rateMaxMessages = 4;
    public String rateBlockedMessage = "&eYou're chatting too fast.";


    public List<String> bannedWords = new ArrayList<>();
    public String bannedWordMessage = "&cThat message is not allowed.";


    public boolean escalationEnabled = true;
    public int strikesWindowSeconds = 120;
    public int strikesToMute = 3;
    public int muteSeconds = 120;
    public String muteReason = "AutoMod: chat spam/filter";


    public String bypassPermission = "pixity.automod.bypass";

    public AutoModConfig() {

    }
}
