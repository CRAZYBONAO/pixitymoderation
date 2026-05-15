package org.howie.pixity.moderation.neoforge.placeholder;

import java.util.regex.MatchResult;

public interface RegexPlaceholder {

    String resolve(

            PlaceholderContext context,

            MatchResult match
    );
}