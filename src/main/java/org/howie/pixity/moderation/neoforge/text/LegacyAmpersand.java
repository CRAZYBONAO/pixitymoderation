package org.howie.pixity.moderation.neoforge.text;

import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.*;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Component;

import net.minecraft.server.level.ServerPlayer;
import org.howie.pixity.moderation.chat.TextFormatter;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class LegacyAmpersand {


    private LegacyAmpersand() {}



    private static final Map<String, Component> tempComponents = new HashMap<>();



    public static Component parse(String s) {
        if (s == null || s.isEmpty()) return Component.empty();

        MutableComponent base = Component.empty();
        parseAdvanced(base, s);
        return base;
    }


    public static Component parse(HolderLookup.Provider provider, String s) {
        return parse(s);
    }



    private static String applyPlaceholders(String input, Map<String, String> placeholders) {
        if (placeholders == null) return input;

        String out = input;
        for (var e : placeholders.entrySet()) {
            out = out.replace("{" + e.getKey() + "}", e.getValue());
        }
        return out;
    }

    private static Component multiGradient(String text, List<Integer> colors) {

        if (colors.size() < 2) return Component.literal(text);

        MutableComponent out = Component.empty();

        int len = text.length();

        for (int i = 0; i < len; i++) {

            float progress = (float) i / (len - 1);

            float scaled = progress * (colors.size() - 1);

            int index = (int) Math.floor(scaled);
            float t = scaled - index;

            int c1 = colors.get(index);
            int c2 = colors.get(Math.min(index + 1, colors.size() - 1));

            int r = lerp((c1 >> 16) & 255, (c2 >> 16) & 255, t);
            int g = lerp((c1 >> 8) & 255, (c2 >> 8) & 255, t);
            int b = lerp(c1 & 255, c2 & 255, t);

            int rgb = (r << 16) | (g << 8) | b;

            out.append(
                    Component.literal(String.valueOf(text.charAt(i)))
                            .withStyle(s -> s.withColor(TextColor.fromRgb(rgb)))
            );
        }

        return out;
    }


    private static void parseAdvanced(MutableComponent out, String input) {

        int lastEnd = 0;


        Pattern clickPattern = Pattern.compile("<click:(\\w+):(.*?)>(.*?)</click>", Pattern.DOTALL);
        Matcher clickMatcher = clickPattern.matcher(input);

        while (clickMatcher.find()) {
            out.append(parseAdvancedNested(input.substring(lastEnd, clickMatcher.start())));

            String action = clickMatcher.group(1);
            String value = clickMatcher.group(2);
            String text = clickMatcher.group(3);

            MutableComponent comp = parseAdvancedNested(text);

            ClickEvent.Action act = switch (action.toLowerCase()) {
                case "run" -> ClickEvent.Action.RUN_COMMAND;
                case "suggest" -> ClickEvent.Action.SUGGEST_COMMAND;
                case "url" -> ClickEvent.Action.OPEN_URL;
                default -> null;
            };

            if (act != null) {
                comp = comp.withStyle(s -> s.withClickEvent(new ClickEvent(act, value)));
            }

            out.append(comp);
            lastEnd = clickMatcher.end();
        }

        if (lastEnd > 0 && lastEnd <= input.length()) {
            input = input.substring(lastEnd);
        }
        lastEnd = 0;


        Pattern shadowPattern = Pattern.compile("<shadow:#(\\w{6})(?::(\\d+))?>(.*?)</shadow>", Pattern.DOTALL);
        Matcher shadowMatcher = shadowPattern.matcher(input);

        while (shadowMatcher.find()) {
            out.append(parseAdvancedNested(input.substring(lastEnd, shadowMatcher.start())));

            String hex = shadowMatcher.group(1);
            String strengthStr = shadowMatcher.group(2);
            String text = shadowMatcher.group(3);

            int rgb = Integer.parseInt(hex, 16);
            TextColor shadowColor = TextColor.fromRgb(rgb);

            int strength = 1;
            if (strengthStr != null) {
                try { strength = Integer.parseInt(strengthStr); } catch (Exception ignored) {}
            }

            MutableComponent comp = parseAdvancedNested(text);

            comp = comp.withStyle(style ->
                    style.withColor(style.getColor())
                            .withUnderlined(false)
            );

            out.append(comp);

            lastEnd = shadowMatcher.end();
        }

        if (lastEnd > 0 && lastEnd <= input.length()) {
            input = input.substring(lastEnd);
        }
        lastEnd = 0;


        int index = 0;

        while ((index = input.indexOf("<gradient:", index)) != -1) {

            int startTagEnd = input.indexOf(">", index);
            if (startTagEnd == -1) break;

            String header = input.substring(index + 10, startTagEnd);
            String[] stops = header.split(":");

            int closeIndex = findClosingTag(input, "gradient", startTagEnd);
            if (closeIndex == -1) break;

            out.append(parseLegacy(safeSub(input, lastEnd, index)));

            String innerText = input.substring(startTagEnd + 1, closeIndex);

            List<Integer> colors = new ArrayList<>();
            for (String stop : stops) {
                try {
                    colors.add(Integer.parseInt(stop.replace("#", ""), 16));
                } catch (Exception ignored) {}
            }

            MutableComponent inner = Component.empty();
            parseAdvanced(inner, innerText);

            MutableComponent result = Component.empty();

            final int[] charIndex = {0};
            final int total = Math.max(countCharacters(inner), 1);

            inner.visit((style, content) -> {

                for (int i = 0; i < content.length(); i++) {

                    float progress = (total <= 1) ? 0f : (float) charIndex[0] / (total - 1);

                    float scaled = progress * (colors.size() - 1);

                    int ci = (int) Math.floor(scaled);
                    float t = scaled - ci;

                    int c1 = colors.get(ci);
                    int c2 = colors.get(Math.min(ci + 1, colors.size() - 1));

                    int r = lerp((c1 >> 16) & 255, (c2 >> 16) & 255, t);
                    int g = lerp((c1 >> 8) & 255, (c2 >> 8) & 255, t);
                    int b = lerp(c1 & 255, c2 & 255, t);

                    int rgb = (r << 16) | (g << 8) | b;

                    result.append(
                            Component.literal(String.valueOf(content.charAt(i)))
                                    .withStyle(style.withColor(TextColor.fromRgb(rgb)))
                    );

                    charIndex[0]++;
                }

                return Optional.empty();
            }, Style.EMPTY);

            out.append(result);

            lastEnd = closeIndex + "</gradient>".length();
            index = lastEnd;
        }

        if (lastEnd > 0 && lastEnd <= input.length()) {
            input = input.substring(lastEnd);
        }
        lastEnd = 0;







        Pattern rainbowPattern = Pattern.compile("<rainbow>(.*?)</rainbow>", Pattern.DOTALL);
        Matcher rainbowMatcher = rainbowPattern.matcher(input);

        while (rainbowMatcher.find()) {
            out.append(parseAdvancedNested(input.substring(lastEnd, rainbowMatcher.start())));

            String text = rainbowMatcher.group(1);

            out.append(TextFormatter.rainbow(text));

            lastEnd = rainbowMatcher.end();
        }

        if (lastEnd > 0 && lastEnd <= input.length()) {
            input = input.substring(lastEnd);
        }
        lastEnd = 0;

        Pattern colorPattern = Pattern.compile("<(#?[a-zA-Z0-9_]+)>(.*?)</\\1>", Pattern.DOTALL);
        Matcher colorMatcher = colorPattern.matcher(input);

        while (colorMatcher.find()) {
            out.append(parseAdvancedNested(input.substring(lastEnd, colorMatcher.start())));

            String tag = colorMatcher.group(1).toLowerCase();
            String inner = colorMatcher.group(2);

            MutableComponent comp = parseAdvancedNested(inner);

            ChatFormatting fmt = switch (tag) {
                case "gold" -> ChatFormatting.GOLD;
                case "gray" -> ChatFormatting.GRAY;
                case "yellow" -> ChatFormatting.YELLOW;
                case "red" -> ChatFormatting.RED;
                case "green" -> ChatFormatting.GREEN;
                case "blue" -> ChatFormatting.BLUE;
                case "white" -> ChatFormatting.WHITE;
                default -> null;
            };

            if (tag.startsWith("#") && tag.length() == 7) {
                try {
                    int rgb = Integer.parseInt(tag.substring(1), 16);
                    comp = comp.withStyle(s -> s.withColor(TextColor.fromRgb(rgb)));
                } catch (Exception ignored) {}
            }

            if (fmt != null) {
                comp = comp.withStyle(fmt);
            }

            out.append(comp);

            lastEnd = colorMatcher.end();
        }

        if (lastEnd > 0 && lastEnd <= input.length()) {
            input = input.substring(lastEnd);
        }
        lastEnd = 0;


        Pattern stylePattern = Pattern.compile("<(bold|italic)>(.*?)</\\1>", Pattern.DOTALL);
        Matcher styleMatcher = stylePattern.matcher(input);

        while (styleMatcher.find()) {
            out.append(parseAdvancedNested(input.substring(lastEnd, styleMatcher.start())));

            String tag = styleMatcher.group(1);
            String inner = styleMatcher.group(2);

            MutableComponent comp = parseAdvancedNested(inner);

            if (tag.equalsIgnoreCase("bold")) {
                comp = comp.withStyle(ChatFormatting.BOLD);
            } else if (tag.equalsIgnoreCase("italic")) {
                comp = comp.withStyle(ChatFormatting.ITALIC);
            }

            out.append(comp);

            lastEnd = styleMatcher.end();
        }

        input = input.substring(lastEnd);
        lastEnd = 0;


        out.append(parseLegacy(input));
    }

    private static String safeSub(String input, int start, int end) {
        if (start < 0) start = 0;
        if (end > input.length()) end = input.length();
        if (start >= end) return "";
        return input.substring(start, end);
    }

    private static int countCharacters(Component comp) {
        final int[] count = {0};

        comp.visit((style, text) -> {
            count[0] += text.length();
            return Optional.empty();
        }, Style.EMPTY);

        return count[0];
    }

    private static int findClosingTag(String input, String tag, int start) {

        int depth = 1;
        int index = start;

        while (index < input.length()) {

            int open = input.indexOf("<" + tag, index);
            int close = input.indexOf("</" + tag + ">", index);

            if (close == -1) return -1;

            if (open != -1 && open < close) {
                depth++;
                index = open + 1;
            } else {
                depth--;
                if (depth == 0) return close;
                index = close + 1;
            }
        }

        return -1;
    }

    private static int getTextWidth(String text) {
        int width = 0;

        for (char c : text.toCharArray()) {
            width += getCharWidth(c);
        }

        return width;
    }

    private static int getCharWidth(char c) {
        return switch (c) {
            case 'i', 'l', '.', ',', '!' -> 2;
            case ' ' -> 4;
            case 't', 'f', 'k' -> 3;
            case 'I' -> 4;
            case 'W', 'M' -> 6;
            default -> 5;
        };
    }

    private static Component replaceComponent(Component base, String key, Component replacement) {

        MutableComponent result = Component.empty();

        base.visit((style, text) -> {

            if (text.contains(key)) {

                String[] parts = text.split(Pattern.quote(key), -1);

                for (int i = 0; i < parts.length; i++) {

                    if (!parts[i].isEmpty()) {
                        result.append(Component.literal(parts[i]).withStyle(style));
                    }

                    if (i < parts.length - 1) {
                        result.append(replacement.copy());
                    }
                }

            } else {
                result.append(Component.literal(text).withStyle(style));
            }

            return Optional.empty();
        }, Style.EMPTY);

        return result;
    }

    private static MutableComponent parseAdvancedNested(String text) {
        return parseLegacy(text);
    }



    private static MutableComponent parseLegacy(String s) {
        MutableComponent out = Component.empty();
        StringBuilder buf = new StringBuilder();

        ChatFormatting color = null;
        TextColor hexColor = null;

        boolean bold=false, italic=false, under=false, strike=false, obf=false;

        int i=0;
        while (i < s.length()) {
            char c = s.charAt(i);

            if (c == '&' && i+7 < s.length() && s.charAt(i+1) == '#') {
                String hex = s.substring(i+2, i+8);
                TextColor tc = parseHex(hex);
                if (tc != null) {
                    flush(out, buf, color, hexColor, bold, italic, under, strike, obf);
                    hexColor = tc;
                    color = null;
                    i += 8;
                    continue;
                }
            }

            if (c == '&' && i+1 < s.length()) {
                char code = Character.toLowerCase(s.charAt(i+1));
                ChatFormatting fmt = fromCode(code);
                if (fmt != null) {
                    flush(out, buf, color, hexColor, bold, italic, under, strike, obf);

                    if (fmt.isColor()) {
                        color = fmt;
                        hexColor = null;
                        bold=false; italic=false; under=false; strike=false; obf=false;
                    } else {
                        switch (fmt) {
                            case BOLD -> bold = true;
                            case ITALIC -> italic = true;
                            case UNDERLINE -> under = true;
                            case STRIKETHROUGH -> strike = true;
                            case OBFUSCATED -> obf = true;
                            case RESET -> {
                                color = null; hexColor = null;
                                bold=false; italic=false; under=false; strike=false; obf=false;
                            }
                        }
                    }
                    i += 2;
                    continue;
                }
            }

            buf.append(c);
            i++;
        }

        flush(out, buf, color, hexColor, bold, italic, under, strike, obf);
        return out;
    }

    private static Component rainbow(String text) {

        MutableComponent out = Component.empty();

        int len = text.codePointCount(0, text.length());
        int index = 0;

        for (int i = 0; i < text.length(); ) {

            int codePoint = text.codePointAt(i);
            String ch = new String(Character.toChars(codePoint));

            float hue = (len == 1) ? 0f : (float) index / len;

            int rgb = java.awt.Color.HSBtoRGB(hue, 1f, 1f) & 0xFFFFFF;

            out.append(
                    Component.literal(ch)
                            .withStyle(Style.EMPTY.withColor(rgb))
            );

            i += Character.charCount(codePoint);
            index++;
        }

        return out;
    }

    private static void flush(MutableComponent out, StringBuilder buf,
                              ChatFormatting color, TextColor hexColor,
                              boolean bold, boolean italic, boolean under, boolean strike, boolean obf) {

        if (buf.isEmpty()) return;

        MutableComponent part = Component.literal(buf.toString());

        if (hexColor != null) part = part.withStyle(s -> s.withColor(hexColor));
        else if (color != null) part = part.withStyle(color);

        if (bold) part = part.withStyle(ChatFormatting.BOLD);
        if (italic) part = part.withStyle(ChatFormatting.ITALIC);
        if (under) part = part.withStyle(ChatFormatting.UNDERLINE);
        if (strike) part = part.withStyle(ChatFormatting.STRIKETHROUGH);
        if (obf) part = part.withStyle(ChatFormatting.OBFUSCATED);

        out.append(part.withStyle(s -> s.withItalic(false)));
        buf.setLength(0);
    }



    private static Component gradient(String text, String startHex, String endHex) {

        int start = Integer.parseInt(startHex, 16);
        int end = Integer.parseInt(endHex, 16);

        boolean bold = text.contains("&l");
        boolean obf = text.contains("&k");
        boolean underline = text.contains("&n");
        boolean strike = text.contains("&m");

        String clean = text
                .replace("&l", "")
                .replace("&k", "")
                .replace("&n", "")
                .replace("&m", "")
                .replace("&o", "")
                .replace("&r", "");

        int len = Math.max(clean.length(), 1);
        MutableComponent out = Component.empty();

        for (int i = 0; i < clean.length(); i++) {

            float ratio = (len == 1) ? 0f : i / (float)(len - 1);

            int r = lerp((start >> 16) & 0xFF, (end >> 16) & 0xFF, ratio);
            int g = lerp((start >> 8) & 0xFF, (end >> 8) & 0xFF, ratio);
            int b = lerp(start & 0xFF, end & 0xFF, ratio);

            int rgb = (r << 16) | (g << 8) | b;

            MutableComponent ch = Component.literal(String.valueOf(clean.charAt(i)))
                    .withStyle(s -> s.withColor(TextColor.fromRgb(rgb)));

            if (bold) ch = ch.withStyle(ChatFormatting.BOLD);
            if (obf) ch = ch.withStyle(ChatFormatting.OBFUSCATED);
            if (underline) ch = ch.withStyle(ChatFormatting.UNDERLINE);
            if (strike) ch = ch.withStyle(ChatFormatting.STRIKETHROUGH);

            ch = ch.withStyle(s -> s.withItalic(false));

            out.append(ch);
        }

        return out;
    }

    private static int lerp(int a, int b, float t) {
        return (int)(a + (b - a) * t);
    }

    private static TextColor parseHex(String hex) {
        try {
            return TextColor.fromRgb(Integer.parseInt(hex, 16));
        } catch (Exception e) {
            return null;
        }
    }

    private static ChatFormatting fromCode(char code) {
        return switch (code) {
            case '0' -> ChatFormatting.BLACK;
            case '1' -> ChatFormatting.DARK_BLUE;
            case '2' -> ChatFormatting.DARK_GREEN;
            case '3' -> ChatFormatting.DARK_AQUA;
            case '4' -> ChatFormatting.DARK_RED;
            case '5' -> ChatFormatting.DARK_PURPLE;
            case '6' -> ChatFormatting.GOLD;
            case '7' -> ChatFormatting.GRAY;
            case '8' -> ChatFormatting.DARK_GRAY;
            case '9' -> ChatFormatting.BLUE;
            case 'a' -> ChatFormatting.GREEN;
            case 'b' -> ChatFormatting.AQUA;
            case 'c' -> ChatFormatting.RED;
            case 'd' -> ChatFormatting.LIGHT_PURPLE;
            case 'e' -> ChatFormatting.YELLOW;
            case 'f' -> ChatFormatting.WHITE;
            case 'k' -> ChatFormatting.OBFUSCATED;
            case 'l' -> ChatFormatting.BOLD;
            case 'm' -> ChatFormatting.STRIKETHROUGH;
            case 'n' -> ChatFormatting.UNDERLINE;
            case 'o' -> ChatFormatting.ITALIC;
            case 'r' -> ChatFormatting.RESET;
            default -> null;
        };
    }


}
