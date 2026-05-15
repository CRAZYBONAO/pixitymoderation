package org.howie.pixity.moderation.neoforge.hologram;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.neoforge.placeholder.PlaceholderRegistry;

import org.howie.pixity.moderation.neoforge.text.RichTextParser;

public class HologramPlaceholderService {

    public static Component parse(
            String text
    ) {

        text =
                PlaceholderRegistry.apply(
                        null,
                        text
                );

        return RichTextParser.parse(
                text
        );
    }

    public static Component parse(
            ServerPlayer player,
            String text
    ) {

        text =
                PlaceholderRegistry.apply(
                        player,
                        text
                );

        return RichTextParser.parse(
                text
        );
    }
}