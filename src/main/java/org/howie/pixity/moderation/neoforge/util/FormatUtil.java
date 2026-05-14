package org.howie.pixity.moderation.neoforge.util;

import java.text.DecimalFormat;

public final class FormatUtil {

    private static final DecimalFormat ONE_DECIMAL = new DecimalFormat("#.#");
    private static final DecimalFormat COMMA = new DecimalFormat("#,###");

    private FormatUtil() {}



    public static String formatMoney(double value) {
        if (value >= 1_000_000_000) {
            return ONE_DECIMAL.format(value / 1_000_000_000D) + "B";
        }
        if (value >= 1_000_000) {
            return ONE_DECIMAL.format(value / 1_000_000D) + "M";
        }
        if (value >= 1_000) {
            return ONE_DECIMAL.format(value / 1_000D) + "K";
        }
        return String.valueOf((int) value);
    }



    public static String formatComma(double value) {
        return COMMA.format((long) value);
    }



    public static String formatPlaytime(long seconds) {

        long h = seconds / 3600;
        long m = (seconds % 3600) / 60;
        long s = seconds % 60;

        if (h > 0) {
            return h + "h " + m + "m";
        }
        if (m > 0) {
            return m + "m " + s + "s";
        }
        return s + "s";
    }
}