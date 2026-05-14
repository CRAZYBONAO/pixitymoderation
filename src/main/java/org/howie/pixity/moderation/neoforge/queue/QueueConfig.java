package org.howie.pixity.moderation.neoforge.queue;

public final class QueueConfig {


    public boolean enabled = true;


    public int softMaxPlayers = 80;


    public int reservedSlots = 5;


    public boolean kickForPriority = true;


    public String fullKickMessage = "&cServer is full.&r\n&7Try again soon.";


    public String displacedKickMessage = "&eA higher-priority player joined.&r\n&7You were moved out to make room. Try rejoining.";


    public int graceSeconds = 120;


    public boolean graceAppliesToAll = true;

    public QueueConfig() {}
}
