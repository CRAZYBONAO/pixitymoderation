package org.howie.pixity.moderation.neoforge.placeholder;

import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ComponentPlaceholderRegistry {

    private static final Map<
            String,
            Supplier<Component>
            > PLACEHOLDERS =
            new HashMap<>();

    public static void register(

            String placeholder,

            Supplier<Component> supplier
    ) {

        PLACEHOLDERS.put(
                normalize(placeholder),
                supplier
        );
    }

    public static Component apply(
            String text
    ) {

        Component result =
                Component.empty();

        int index = 0;

        while (index < text.length()) {

            boolean matched =
                    false;

            for (var entry :
                    PLACEHOLDERS.entrySet()) {

                String placeholder =
                        entry.getKey();

                if (!text.regionMatches(
                        true,
                        index,
                        placeholder,
                        0,
                        placeholder.length()
                )) {
                    continue;
                }

                Component glyph =
                        entry.getValue()
                                .get();

                result =
                        result.copy()
                                .append(glyph);

                index +=
                        placeholder.length();

                matched = true;

                break;
            }

            if (matched)
                continue;

            result =
                    result.copy()
                            .append(
                                    Component.literal(
                                            String.valueOf(
                                                    text.charAt(index)
                                            )
                                    )
                            );

            index++;
        }

        return result;
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
}