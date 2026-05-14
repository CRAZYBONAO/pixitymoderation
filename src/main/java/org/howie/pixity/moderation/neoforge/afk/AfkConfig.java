package org.howie.pixity.moderation.neoforge.afk;

public final class AfkConfig {

    public int autoAfkMinutes = 15;

    public boolean tagInTab = true;
    public boolean tagInChat = true;
    public boolean denyTpaWhileAfk = true;
    public boolean broadcastMessages = true;

    public String afkMessage = "&e&lAFK &7&l➤ &7{DISPLAYNAME} &cis now AFK";
    public String returnMessage = "&e&lAFK &7&l➤ &7{DISPLAYNAME} &ahas returned";

    public AfkConfig() {}
}