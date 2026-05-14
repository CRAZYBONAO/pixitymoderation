package org.howie.pixity.moderation.neoforge.pokemonhunt;

import net.minecraft.network.chat.Component;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;

import net.minecraft.world.BossEvent;

import org.howie.pixity.moderation.chat.TextFormatter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PokemonHuntBossBarManager {





    private static final Map<UUID, ServerBossEvent> bars =
            new HashMap<>();





    private static final String PREFIX =
            "<gradient:#ff1a1a:#8b0000>&lHUNT</gradient><gray>&l ➤ </gray>";





    public static void create(
            ServerPlayer player
    ) {

        PokemonHuntDefinition hunt =
                PokemonHuntManager.getCurrent();

        if (hunt == null) {
            return;
        }

        ServerBossEvent bar =
                new ServerBossEvent(

                        Component.literal("Hunt"),

                        BossEvent.BossBarColor.RED,

                        BossEvent.BossBarOverlay.PROGRESS
                );

        bar.addPlayer(player);

        bars.put(
                player.getUUID(),
                bar
        );

        update(player);
    }





    public static void remove(
            UUID uuid
    ) {

        ServerBossEvent bar =
                bars.remove(uuid);

        if (bar == null) {
            return;
        }

        bar.removeAllPlayers();
    }





    public static void update(
            ServerPlayer player
    ) {

        PokemonHuntDefinition hunt =
                PokemonHuntManager.getCurrent();

        if (hunt == null) {
            return;
        }

        ServerBossEvent bar =
                bars.get(player.getUUID());

        if (bar == null) {

            create(player);

            return;
        }

        int progress =
                PokemonHuntDatabase.getProgress(
                        player.getUUID()
                );

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





        if (progress >= hunt.required) {

            bar.setColor(
                    BossEvent.BossBarColor.GREEN
            );

            bar.setProgress(1F);

            bar.setName(
                    TextFormatter.parse(
                            PREFIX
                                    + "<green>✔ Hunt Complete!</green>"
                    )
            );

            return;
        }





        bar.setColor(
                BossEvent.BossBarColor.RED
        );

        bar.setProgress(percent);

        bar.setName(

                TextFormatter.parse(
                        PREFIX

                                + "<yellow>"
                                + hunt.getDisplay()
                                + "</yellow>"

                                + "<gray> • </gray>"

                                + "<aqua>"
                                + progress
                                + "/"
                                + hunt.required
                                + "</aqua>"
                )
        );
    }





    public static void updateAll(
            MinecraftServer server
    ) {

        for (ServerPlayer player
                : server.getPlayerList().getPlayers()) {

            update(player);
        }
    }





    public static void resetAll(
            MinecraftServer server
    ) {

        for (ServerPlayer player
                : server.getPlayerList().getPlayers()) {

            remove(
                    player.getUUID()
            );

            create(player);
        }
    }
}