package org.howie.pixity.moderation.neoforge.kits;

import net.minecraft.network.chat.Component;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

import java.util.Locale;

final class KitText {

    private KitText() {}

    static Component renderDisplayName(final String raw) {
        if (raw == null || raw.isEmpty())
            return Component.empty();

        return LegacyAmpersand.parse(raw);
    }

    static String normalizeName(final String input) {

        if (input == null) return "kit";

        String s = input.trim().toLowerCase(Locale.ROOT);

        s = s.replaceAll("[^a-z0-9_-]", "");

        if (s.isBlank()) s = "kit";

        return s;
    }
}