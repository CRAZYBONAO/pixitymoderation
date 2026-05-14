package org.howie.pixity.moderation.neoforge.mail;

public final class MailMessage {

    public String id;
    public long ts;
    public String fromUuid;
    public String fromName;
    public String toUuid;
    public String toName;
    public String message;
    public boolean read;

    public MailMessage() {}
}
