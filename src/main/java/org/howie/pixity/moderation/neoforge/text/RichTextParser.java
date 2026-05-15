package org.howie.pixity.moderation.neoforge.text;

import net.minecraft.network.chat.Component;

import net.minecraft.network.chat.MutableComponent;
import org.howie.pixity.moderation.chat.TextFormatter;

import org.howie.pixity.moderation.neoforge.hologram.GlyphComponent;

import java.util.List;

public class RichTextParser {

    public static Component parse(
            String text
    ) {

        List<RichToken> tokens =
                RichTextTokenizer.tokenize(
                        text
                );

        MutableComponent result =
                Component.empty();

        for (RichToken token :
                tokens) {

            switch (token.type()) {

                case TEXT -> {

                    result =
                            result.append(

                                    TextFormatter.parse(
                                            token.value()
                                    )
                            );
                }

                case GLYPH -> {

                    result =
                            result.append(

                                    glyph(
                                            token.value()
                                    )
                            );
                }
            }
        }

        return result;
    }

    private static Component glyph(
            String placeholder
    ) {

        return switch (
                placeholder.toLowerCase()
                ) {

            case "%glyph_owner%" ->
                    GlyphComponent.glyph(
                            "\uE000"
                    );

            case "%glyph_manager%" ->
                    GlyphComponent.glyph(
                            "\uE001"
                    );

            case "%glyph_developer%" ->
                    GlyphComponent.glyph(
                            "\uE002"
                    );

            case "%glyph_headadmin%" ->
                    GlyphComponent.glyph(
                            "\uE003"
                    );

            case "%glyph_admin%" ->
                    GlyphComponent.glyph(
                            "\uE004"
                    );

            case "%glyph_trialadmin%" ->
                    GlyphComponent.glyph(
                            "\uE005"
                    );

            case "%glyph_mod%" ->
                    GlyphComponent.glyph(
                            "\uE006"
                    );

            case "%glyph_firegym%" ->
                    GlyphComponent.glyph(
                            "\uE00E"
                    );

            case "%glyph_watergym%" ->
                    GlyphComponent.glyph(
                            "\uE00F"
                    );

            case "%glyph_grassgym%" ->
                    GlyphComponent.glyph(
                            "\uE010"
                    );

            default ->
                    Component.empty();
        };
    }
}