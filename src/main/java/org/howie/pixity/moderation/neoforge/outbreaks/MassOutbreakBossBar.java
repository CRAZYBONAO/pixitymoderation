package org.howie.pixity.moderation.neoforge.outbreaks;

import net.minecraft.network.chat.Component;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;

import net.minecraft.world.BossEvent;

import org.howie.pixity.moderation.chat.TextFormatter;

public class MassOutbreakBossBar {





    private static ServerBossEvent bossBar;





    public static void create(
            MinecraftServer server
    ) {

        bossBar =
                new ServerBossEvent(

                        Component.literal(
                                "Mass Outbreak"
                        ),

                        BossEvent.BossBarColor.PURPLE,

                        BossEvent.BossBarOverlay.PROGRESS
                );

        bossBar.setVisible(true);

        for (ServerPlayer player
                : server.getPlayerList().getPlayers()) {

            bossBar.addPlayer(player);
        }
    }





    public static void remove() {

        if (bossBar == null) {
            return;
        }

        bossBar.removeAllPlayers();

        bossBar = null;
    }





    public static void update(
            MinecraftServer server
    ) {

        if (bossBar == null) {
            return;
        }

        MassOutbreakDefinition outbreak =
                MassOutbreakManager.getCurrent();

        if (outbreak == null) {
            return;
        }

        long remaining =
                MassOutbreakManager.getEndTime()
                        - System.currentTimeMillis();

        if (remaining < 0) {
            remaining = 0;
        }

        float percent =
                remaining
                        / (float) MassOutbreakManager.getDurationMillis();

        percent =
                Math.max(
                        0F,
                        Math.min(
                                1F,
                                percent
                        )
                );

        long minutes =
                remaining / (1000L * 60L);

        long seconds =
                (remaining / 1000L) % 60L;

        bossBar.setProgress(percent);

        bossBar.setName(

                TextFormatter.parse(
                        "<gradient:#ff8800:#ff0000><bold>MASS OUTBREAK</bold></gradient>"
                                + "<gray> • </gray>"

                                + "<gold>"
                                + outbreak.getDisplayName()
                                + "</gold>"

                                + "<gray> • </gray>"

                                + "<yellow>"
                                + minutes
                                + "m "
                                + seconds
                                + "s</yellow>"
                )
        );





        for (ServerPlayer player
                : server.getPlayerList().getPlayers()) {

            if (!bossBar.getPlayers().contains(player)) {

                bossBar.addPlayer(player);
            }
        }
    }
}