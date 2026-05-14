package org.howie.pixity.moderation.neoforge.fly;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public final class FlyTimeListener {

    private final FlyTimeService fly;
    private int tick = 0;

    public FlyTimeListener(FlyTimeService fly) {
        this.fly = fly;
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEvent.Post e) {

        tick++;
        if (tick % 20 != 0) return;

        for (ServerPlayer p : e.getServer().getPlayerList().getPlayers()) {


            fly.tick(p);
        }
    }

    private static String formatDuration(long seconds) {

        long days = seconds / 86400;
        seconds %= 86400;

        long hours = seconds / 3600;
        seconds %= 3600;

        long minutes = seconds / 60;
        long secs = seconds % 60;

        if (days > 0) {
            if (hours > 0) return days + "d " + hours + "h";
            return days + "d";
        }

        if (hours > 0) {
            if (minutes > 0) return hours + "h " + minutes + "m";
            return hours + "h";
        }

        if (minutes > 0) {
            if (secs > 0) return minutes + "m " + secs + "s";
            return minutes + "m";
        }

        return secs + "s";
    }
}