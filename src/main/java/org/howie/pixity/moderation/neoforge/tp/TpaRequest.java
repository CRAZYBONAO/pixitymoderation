package org.howie.pixity.moderation.neoforge.tp;

import java.util.UUID;

public final class TpaRequest {
    public enum Type { TO, HERE }

    public UUID from;
    public UUID to;
    public String fromName;
    public long expiresAtMs;
    public Type type;
}
