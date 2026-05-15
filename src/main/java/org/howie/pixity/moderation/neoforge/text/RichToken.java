package org.howie.pixity.moderation.neoforge.text;

public class RichToken {

    private final RichTokenType type;

    private final String value;

    public RichToken(
            RichTokenType type,
            String value
    ) {

        this.type = type;
        this.value = value;
    }

    public RichTokenType type() {
        return type;
    }

    public String value() {
        return value;
    }
}