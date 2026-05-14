package org.howie.pixity.moderation.neoforge.mail;

public final class MailConfig {

    public boolean enabled = true;


    public int expireDays = 14;


    public boolean notifyOnJoin = true;


    public boolean notifySound = true;


    public String notifyMessage = "&c&lMAIL &cYou got {COUNT} mail!";

    public MailConfig() {}
}