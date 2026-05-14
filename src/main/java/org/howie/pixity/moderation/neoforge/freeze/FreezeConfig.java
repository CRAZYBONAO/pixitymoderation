package org.howie.pixity.moderation.neoforge.freeze;

import java.util.ArrayList;
import java.util.List;

public final class FreezeConfig {


    public boolean blockChat = false;


    public boolean unfreezeOnLogout = false;


    public boolean showActionbar = true;


    public boolean showBossbar = true;


    public List<String> allowCommands = new ArrayList<>();

    public FreezeConfig() {}
}
