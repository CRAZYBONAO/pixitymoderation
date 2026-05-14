package org.howie.pixity.moderation.neoforge.pokemon;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShinyBoostManager {

    private static class BoostData {
        double multiplier;
        long endTime;

        BoostData(double multiplier, long endTime) {
            this.multiplier = multiplier;
            this.endTime = endTime;
        }
    }




    private static double globalMultiplier = 1.0;
    private static long globalEndTime = 0;

    private static final Map<UUID, BoostData> playerBoosts = new HashMap<>();




    public static void enableGlobal(ServerLevel level, double multi, int durationSeconds) {

        globalMultiplier = multi;
        globalEndTime = System.currentTimeMillis() + (durationSeconds * 1000L);

        var msg = org.howie.pixity.moderation.chat.CachedText.of(
                "<rainbow>&l✨ GLOBAL SHINY BOOST ✨</rainbow> <light_purple>"
                        + multi + "x for " + durationSeconds + "s"
        );

        for (var p : level.getServer().getPlayerList().getPlayers()) {
            p.sendSystemMessage(msg);
        }
    }




    public static boolean isGlobalActive() {
        return System.currentTimeMillis() < globalEndTime;
    }




    public static double getGlobalMultiplier() {
        return isGlobalActive() ? globalMultiplier : 1.0;
    }




    public static void clearGlobal(ServerLevel level) {

        globalMultiplier = 1.0;
        globalEndTime = 0;

        var msg = org.howie.pixity.moderation.chat.CachedText.of(
                "<gray>Global shiny boost has ended.</gray>"
        );

        for (var p : level.getServer().getPlayerList().getPlayers()) {
            p.sendSystemMessage(msg);
        }
    }




    public static void enablePlayer(ServerPlayer player, double multi, int durationSeconds) {

        long now = System.currentTimeMillis();
        long newEnd = now + (durationSeconds * 1000L);

        BoostData existing = playerBoosts.get(player.getUUID());

        if (existing != null && existing.multiplier == multi) {

            existing.endTime += (durationSeconds * 1000L);

            player.sendSystemMessage(
                    org.howie.pixity.moderation.chat.CachedText.of(
                            "<yellow>Shiny boost extended! (" + multi + "x)</yellow>"
                    )
            );
        } else {

            playerBoosts.put(player.getUUID(), new BoostData(multi, newEnd));

            player.sendSystemMessage(
                    org.howie.pixity.moderation.chat.CachedText.of(
                            "<rainbow>&l✨ SHINY BOOST ✨</rainbow> <light_purple>"
                                    + multi + "x for " + durationSeconds + "s"
                    )
            );
        }
    }




    public static double getPlayerMultiplier(ServerPlayer player) {

        BoostData data = playerBoosts.get(player.getUUID());

        if (data == null) return 1.0;

        long now = System.currentTimeMillis();

        if (now >= data.endTime) {
            playerBoosts.remove(player.getUUID());
            return 1.0;
        }

        return data.multiplier;
    }




    public static long getRemaining(ServerPlayer player) {

        BoostData data = playerBoosts.get(player.getUUID());
        if (data == null) return 0;

        long remaining = data.endTime - System.currentTimeMillis();
        return Math.max(remaining, 0);
    }




    public static void clearPlayer(ServerPlayer player) {
        playerBoosts.remove(player.getUUID());

        player.sendSystemMessage(
                org.howie.pixity.moderation.chat.CachedText.of(
                        "<red>Your shiny boost has been cleared.</red>"
                )
        );
    }
}