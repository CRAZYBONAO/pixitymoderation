package org.howie.pixity.moderation.neoforge.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TimeUtil {

    private static final Pattern PATTERN = Pattern.compile("(\\d+)([smhd])");

    private TimeUtil() {}

    public static long parse(String input) {
        if (input == null || input.isBlank()) return 0;

        long total = 0;

        Matcher m = PATTERN.matcher(input.toLowerCase());

        while (m.find()) {
            int value = Integer.parseInt(m.group(1));
            char unit = m.group(2).charAt(0);

            switch (unit) {
                case 's' -> total += value;
                case 'm' -> total += value * 60L;
                case 'h' -> total += value * 3600L;
                case 'd' -> total += value * 86400L;
            }
        }

        return total;
    }

    public static String formatDuration(long seconds) {

        long days = seconds / 86400;
        seconds %= 86400;

        long hours = seconds / 3600;
        seconds %= 3600;

        long minutes = seconds / 60;
        seconds %= 60;

        StringBuilder sb = new StringBuilder();

        if (days > 0) sb.append(days).append("d ");
        if (hours > 0) sb.append(hours).append("h ");
        if (minutes > 0) sb.append(minutes).append("m ");
        if (seconds > 0 || sb.isEmpty()) sb.append(seconds).append("s");

        return sb.toString().trim();
    }
}