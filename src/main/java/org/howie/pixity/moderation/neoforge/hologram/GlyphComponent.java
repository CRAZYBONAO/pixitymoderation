package org.howie.pixity.moderation.neoforge.hologram;

import net.minecraft.network.chat.Component;

public class GlyphComponent {

    public static Component glyph(
            String glyph
    ) {

        return Component.literal(
                glyph
        );
    }
}