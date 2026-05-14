package org.howie.pixity.moderation.neoforge.worldboss;

import net.minecraft.network.chat.Component;

import net.minecraft.server.MinecraftServer;

import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;

import net.minecraft.world.BossEvent;

import org.howie.pixity.moderation.chat.TextFormatter;

public class WorldBossBossBar {





    private static ServerBossEvent bossBar;





    public static void create(
            MinecraftServer server
    ) {

        bossBar =
                new ServerBossEvent(

                        Component.literal(
                                "World Boss"
                        ),

                        BossEvent.BossBarColor.RED,

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

        if (server == null) {
            return;
        }

        if (!WorldBossManager.isActive()) {
            return;
        }

        WorldBossDefinition boss =
                WorldBossManager.getCurrent();

        if (boss == null) {
            return;
        }

        float percent =
                WorldBossManager.getHealth()
                        / (float) boss.maxHealth;

        percent =
                Math.max(
                        0F,
                        Math.min(
                                1F,
                                percent
                        )
                );

        bossBar.setVisible(true);

        bossBar.setProgress(percent);

        bossBar.setName(

                TextFormatter.parse(
                        "<red>&l👑 WORLD BOSS</red>"

                                + "<gray> • </gray>"

                                + "<gold>"
                                + boss.display
                                + "</gold>"

                                + "<gray> • </gray>"

                                + "<green>"
                                + String.format(
                                "%,d",
                                WorldBossManager.getHealth()
                        )
                                + "</green>"

                                + "<gray>/</gray>"

                                + "<red>"
                                + String.format(
                                "%,d",
                                boss.maxHealth
                        )
                                + "</red>"
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