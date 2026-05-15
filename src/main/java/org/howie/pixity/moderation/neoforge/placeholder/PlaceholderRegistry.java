package org.howie.pixity.moderation.neoforge.placeholder;

import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class PlaceholderRegistry {

    private static final Map<
            String,
            Function<PlaceholderContext, String>
            > PLAYER_PLACEHOLDERS =
            new HashMap<>();

    private static final Map<
            String,
            Function<Void, String>
            > GLOBAL_PLACEHOLDERS =
            new HashMap<>();

    private static final java.util.List<
            RegexPlaceholderEntry
            > REGEX_PLACEHOLDERS =
            new java.util.ArrayList<>();

    public static void registerPlayer(

            String placeholder,

            Function<PlaceholderContext, String> function
    ) {

        PLAYER_PLACEHOLDERS.put(
                normalize(placeholder),
                function
        );
    }

    public static void registerGlobal(

            String placeholder,

            Function<Void, String> function
    ) {

        GLOBAL_PLACEHOLDERS.put(
                normalize(placeholder),
                function
        );
    }

    public static String apply(

            ServerPlayer player,

            String text
    ) {

        if (text == null)
            return "";

        for (var entry :
                PLAYER_PLACEHOLDERS.entrySet()) {

            try {

                String placeholder =
                        entry.getKey();

                if (player == null)
                    continue;

                String value =
                        entry.getValue()
                                .apply(
                                        new PlaceholderContext(
                                                player
                                        )
                                );

                if (value == null)
                    value = "";

                text =
                        text.replace(
                                placeholder,
                                value
                        );

            } catch (Exception e) {

                System.out.println(
                        "[Pixity Placeholder] Failed player placeholder: "
                                + entry.getKey()
                );

                e.printStackTrace();
            }
        }

        for (var entry :
                GLOBAL_PLACEHOLDERS.entrySet()) {

            try {

                String placeholder =
                        entry.getKey();

                String value =
                        entry.getValue()
                                .apply(null);

                if (value == null)
                    value = "";

                text =
                        text.replace(
                                placeholder,
                                value
                        );

            } catch (Exception e) {

                System.out.println(
                        "[Pixity Placeholder] Failed global placeholder: "
                                + entry.getKey()
                );

                e.printStackTrace();
            }
        }

        PlaceholderContext context =
                new PlaceholderContext(
                        player
                );

        for (RegexPlaceholderEntry entry :
                REGEX_PLACEHOLDERS) {

            try {

                java.util.regex.Matcher matcher =
                        entry.pattern()
                                .matcher(text);

                StringBuffer buffer =
                        new StringBuffer();

                while (matcher.find()) {

                    String replacement =
                            entry.resolver()
                                    .resolve(
                                            context,
                                            matcher.toMatchResult()
                                    );

                    if (replacement == null)
                        replacement = "";

                    matcher.appendReplacement(

                            buffer,

                            java.util.regex.Matcher.quoteReplacement(
                                    replacement
                            )
                    );
                }

                matcher.appendTail(buffer);

                text = buffer.toString();

            } catch (Exception e) {

                System.out.println(
                        "[Pixity Placeholder] Failed regex placeholder: "
                                + entry.pattern()
                );

                e.printStackTrace();
            }
        }

        return text;
    }

    private static String normalize(
            String placeholder
    ) {

        if (!placeholder.startsWith("%")) {
            placeholder =
                    "%" + placeholder;
        }

        if (!placeholder.endsWith("%")) {
            placeholder =
                    placeholder + "%";
        }

        return placeholder.toLowerCase();
    }

    public static void registerRegex(

            String regex,

            RegexPlaceholder resolver
    ) {

        REGEX_PLACEHOLDERS.add(

                new RegexPlaceholderEntry(

                        java.util.regex.Pattern.compile(
                                regex,
                                java.util.regex.Pattern.CASE_INSENSITIVE
                        ),

                        resolver
                )
        );
    }
}