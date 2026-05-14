package org.howie.pixity.moderation.neoforge.globalhunt;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.chat.TextFormatter;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class GlobalHuntManager {





    private static GlobalHuntDefinition current;

    private static int progress = 0;





    private static final Set<UUID> contributors =
            new HashSet<>();





    private static long nextReset =
            System.currentTimeMillis()
                    + (1000L * 60L * 60L * 24L);





    private static final String[] SPECIES = {

            "pikachu",
            "eevee",
            "riolu",
            "gastly",
            "dratini",
            "magikarp",
            "ralts",
            "zorua",
            "shinx",
            "growlithe",
            "vulpix",
            "abra"
    };





    public static void generate(
            MinecraftServer server
    ) {

        Random r =
                new Random();

        current =
                new GlobalHuntDefinition(

                        GlobalHuntType.SPECIES,

                        SPECIES[
                                r.nextInt(
                                        SPECIES.length
                                )
                                ],

                        250,

                        500,

                        50000
                );

        progress = 0;

        contributors.clear();

        nextReset =
                System.currentTimeMillis()
                        + (1000L * 60L * 60L * 24L);

        GlobalHuntBossBar.remove();

        GlobalHuntBossBar.create(server);





        server.getPlayerList()
                .broadcastSystemMessage(

                        TextFormatter.parse(
                                "<gradient:#00ffff:#0066ff>&lGLOBAL HUNT</gradient>\n\n"

                                        + "<yellow>"
                                        + current.getDisplay()
                                        + "</yellow>\n\n"

                                        + "<green>&lCommunity Rewards:</green>\n"

                                        + "&b"
                                        + current.tokens
                                        + " Tokens\n"

                                        + "<green>$"
                                        + String.format(
                                        "%,d",
                                        current.money
                                )
                                        + "</green>"
                        ),
                        false
                );
    }





    public static void addProgress(
            ServerPlayer player
    ) {

        if (current == null) {
            return;
        }

        if (progress >= current.required) {
            return;
        }

        progress++;

        contributors.add(
                player.getUUID()
        );

        GlobalHuntBossBar.update(
                player.server
        );





        if (progress >= current.required) {

            complete(
                    player.server
            );
        }
    }





    private static void complete(
            MinecraftServer server
    ) {

        server.getPlayerList()
                .broadcastSystemMessage(

                        TextFormatter.parse(
                                "<green>&l🌎 GLOBAL HUNT COMPLETED!</green>\n\n"

                                        + "<yellow>"
                                        + current.getDisplay()
                                        + "</yellow>\n\n"

                                        + "&bRewards unlocked for all contributors!"
                        ),
                        false
                );





        for (UUID uuid : contributors) {

            ServerPlayer player =
                    server.getPlayerList()
                            .getPlayer(uuid);

            if (player == null) {
                continue;
            }

            reward(player);
        }

        GlobalHuntBossBar.remove();
    }





    private static void reward(
            ServerPlayer player
    ) {

        player.server.getCommands()
                .performPrefixedCommand(
                        player.server.createCommandSourceStack(),

                        "eco give "
                                + player.getGameProfile().getName()
                                + " "
                                + current.money
                );
    }





    public static GlobalHuntDefinition getCurrent() {
        return current;
    }

    public static int getProgress() {
        return progress;
    }

    public static long getNextReset() {
        return nextReset;
    }
}