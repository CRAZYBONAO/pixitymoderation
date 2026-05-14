package org.howie.pixity.moderation.neoforge.chat.cosmetics;

import net.minecraft.ChatFormatting;

public class GlowUtil {

    public static ChatFormatting hexToChat(String hex) {

        hex = hex.replace("#", "");

        int r = Integer.valueOf(hex.substring(0, 2), 16);
        int g = Integer.valueOf(hex.substring(2, 4), 16);
        int b = Integer.valueOf(hex.substring(4, 6), 16);

        ChatFormatting best = ChatFormatting.WHITE;
        double bestDist = Double.MAX_VALUE;

        for (ChatFormatting c : ChatFormatting.values()) {

            if (!c.isColor()) continue;
            if (c == ChatFormatting.RESET) continue;

            Integer col = c.getColor();
            if (col == null) continue;

            int cr = (col >> 16) & 255;
            int cg = (col >> 8) & 255;
            int cb = col & 255;

            double dist =
                    Math.pow(r - cr, 2) +
                            Math.pow(g - cg, 2) +
                            Math.pow(b - cb, 2);

            if (dist < bestDist) {
                bestDist = dist;
                best = c;
            }
        }

        return best;
    }
}