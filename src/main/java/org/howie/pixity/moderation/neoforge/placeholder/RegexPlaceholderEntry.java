package org.howie.pixity.moderation.neoforge.placeholder;

import java.util.regex.Pattern;

public class RegexPlaceholderEntry {

    private final Pattern pattern;

    private final RegexPlaceholder resolver;

    public RegexPlaceholderEntry(

            Pattern pattern,

            RegexPlaceholder resolver
    ) {

        this.pattern = pattern;
        this.resolver = resolver;
    }

    public Pattern pattern() {
        return pattern;
    }

    public RegexPlaceholder resolver() {
        return resolver;
    }
}