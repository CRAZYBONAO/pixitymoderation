package org.howie.pixity.moderation.neoforge.text;

import java.util.ArrayList;
import java.util.List;

public class RichTextTokenizer {

    public static List<RichToken> tokenize(
            String text
    ) {

        List<RichToken> tokens =
                new ArrayList<>();

        StringBuilder current =
                new StringBuilder();

        int index = 0;

        while (index < text.length()) {

            if (text.charAt(index) == '%') {

                int end =
                        text.indexOf(
                                '%',
                                index + 1
                        );

                if (end != -1) {

                    String placeholder =
                            text.substring(
                                    index,
                                    end + 1
                            );

                    if (placeholder.startsWith("%glyph_")) {

                        if (!current.isEmpty()) {

                            tokens.add(

                                    new RichToken(

                                            RichTokenType.TEXT,

                                            current.toString()
                                    )
                            );

                            current.setLength(0);
                        }

                        tokens.add(

                                new RichToken(

                                        RichTokenType.GLYPH,

                                        placeholder
                                )
                        );

                        index = end + 1;

                        continue;
                    }
                }
            }

            current.append(
                    text.charAt(index)
            );

            index++;
        }

        if (!current.isEmpty()) {

            tokens.add(

                    new RichToken(

                            RichTokenType.TEXT,

                            current.toString()
                    )
            );
        }

        return tokens;
    }
}