package org.howie.pixity.moderation.chat;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

public final class TextFormatter {

    private TextFormatter() {}

    public static Component parse(String input) {
        return LegacyAmpersand.parse(input);
    }

    public static Component color(String text, String hex) {
        if (text == null) return Component.empty();

        try {
            int color = Integer.parseInt(hex.replace("#", ""), 16);

            return Component.literal(text)
                    .withStyle(Style.EMPTY.withColor(color));

        } catch (Exception e) {
            return Component.literal(text);
        }
    }

    public static Component gradient(String text, int c1, int c2) {
        MutableComponent out = Component.empty();

        int len = text.codePointCount(0, text.length());
        int index = 0;

        for (int i = 0; i < text.length(); ) {

            int codePoint = text.codePointAt(i);
            String ch = new String(Character.toChars(codePoint));

            float t = (len == 1) ? 0f : (float) index / (len - 1);

            int r = lerp((c1 >> 16) & 255, (c2 >> 16) & 255, t);
            int g = lerp((c1 >> 8) & 255, (c2 >> 8) & 255, t);
            int b = lerp(c1 & 255, c2 & 255, t);

            int color = (r << 16) | (g << 8) | b;

            out.append(
                    Component.literal(ch)
                            .withStyle(Style.EMPTY.withColor(color))
            );

            i += Character.charCount(codePoint);
            index++;
        }

        return out;
    }

    public static Component rainbow(String text) {

        boolean bold = text.contains("&l");

        String clean = text.replace("&l", "");

        MutableComponent out = Component.empty();

        int len = clean.codePointCount(0, clean.length());
        int index = 0;

        for (int i = 0; i < clean.length(); ) {

            int codePoint = clean.codePointAt(i);
            String ch = new String(Character.toChars(codePoint));

            float hue = (len == 1) ? 0f : (float) index / len;

            int rgb = java.awt.Color.HSBtoRGB(hue, 1f, 1f) & 0xFFFFFF;

            MutableComponent part = Component.literal(ch)
                    .withStyle(s -> s.withColor(rgb));

            if (bold) part = part.withStyle(ChatFormatting.BOLD);

            out.append(part);

            i += Character.charCount(codePoint);
            index++;
        }

        return out;
    }

    private static int lerp(int a, int b, float t) {
        return (int) (a + (b - a) * t);
    }

    public static Component gradient(String text, String startHex, String endHex) {
        try {
            int c1 = Integer.parseInt(startHex.replace("#", ""), 16);
            int c2 = Integer.parseInt(endHex.replace("#", ""), 16);
            return gradient(text, c1, c2);
        } catch (Exception e) {
            return Component.literal(text);
        }
    }
}