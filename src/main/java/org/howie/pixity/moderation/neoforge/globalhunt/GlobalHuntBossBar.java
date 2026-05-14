package org.howie.pixity.moderation.neoforge.globalhunt;

import net.minecraft.network.chat.Component;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;

import net.minecraft.world.BossEvent;

import org.howie.pixity.moderation.chat.TextFormatter;

public class GlobalHuntBossBar {





    private static ServerBossEvent bossBar;





    public static void create(
            MinecraftServer server
    ) {

        bossBar =
                new ServerBossEvent(

                        Component.literal(
                                "Global Hunt"
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

        GlobalHuntDefinition hunt =
                GlobalHuntManager.getCurrent();

        if (hunt == null) {
            return;
        }

        int progress =
                GlobalHuntManager.getProgress();

        float percent =
                progress
                        / (float) hunt.required;

        percent =
                Math.max(
                        0F,
                        Math.min(
                                1F,
                                percent
                        )
                );

        bossBar.setProgress(percent);

        bossBar.setName(

                TextFormatter.parse(
                        "<gradient:#00ffff:#0066ff>&lGLOBAL HUNT</gradient>"
                                + "<gray> • </gray>"

                                + "<yellow>"
                                + hunt.getDisplay()
                                + "</yellow>"

                                + "<gray> • </gray>"

                                + "&b"
                                + progress
                                + "/"
                                + hunt.required

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