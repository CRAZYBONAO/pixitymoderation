package org.howie.pixity.moderation.neoforge.outbreaks;

import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.chat.TextFormatter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OutbreakChainManager {





    private static final Map<UUID, OutbreakChainData> CHAINS =
            new HashMap<>();





    public static void onCatch(
            ServerPlayer player,
            String species
    ) {

        UUID uuid =
                player.getUUID();

        OutbreakChainData data =
                CHAINS.get(uuid);





        if (data == null) {

            data =
                    new OutbreakChainData(
                            species,
                            1
                    );

            CHAINS.put(
                    uuid,
                    data
            );

            sendChainMessage(
                    player,
                    data
            );

            return;
        }





        if (
                data.species.equalsIgnoreCase(
                        species
                )
        ) {

            data.chain++;

            sendChainMessage(
                    player,
                    data
            );

            return;
        }





        data.species =
                species;

        data.chain = 1;

        sendChainMessage(
                player,
                data
        );
    }





    private static void sendChainMessage(
            ServerPlayer player,
            OutbreakChainData data
    ) {

        String bonus =
                getBonusText(
                        data.chain
                );

        player.sendSystemMessage(

                TextFormatter.parse(
                        "<gold><bold>🔥 OUTBREAK CHAIN</bold></gold>\n\n"

                                + "<yellow>"
                                + capitalize(data.species)
                                + "</yellow>\n"

                                + "<aqua>Chain: "
                                + data.chain
                                + "</aqua>\n\n"

                                + bonus
                )
        );
    }





    private static String getBonusText(
            int chain
    ) {

        if (chain >= 100) {

            return "<gold><bold>MASSIVE shiny boost active!</bold></gold>";
        }

        if (chain >= 50) {

            return "<light_purple>⭐ Alpha boost active!</light_purple>";
        }

        if (chain >= 30) {

            return "<aqua>✨ Increased shiny odds!</aqua>";
        }

        if (chain >= 20) {

            return "<green>🧬 Increased hidden ability odds!</green>";
        }

        if (chain >= 10) {

            return "<yellow>📈 Improved IV odds!</yellow>";
        }

        return "<gray>Keep catching for bonuses...</gray>";
    }





    public static int getShinyBonus(
            ServerPlayer player
    ) {

        OutbreakChainData data =
                CHAINS.get(
                        player.getUUID()
                );

        if (data == null) {
            return 0;
        }

        if (data.chain >= 100) {
            return 256;
        }

        if (data.chain >= 50) {
            return 512;
        }

        if (data.chain >= 30) {
            return 1024;
        }

        return 0;
    }





    public static int getHiddenAbilityBonus(
            ServerPlayer player
    ) {

        OutbreakChainData data =
                CHAINS.get(
                        player.getUUID()
                );

        if (data == null) {
            return 0;
        }

        if (data.chain >= 50) {
            return 4;
        }

        if (data.chain >= 20) {
            return 8;
        }

        return 0;
    }





    public static int getAlphaBonus(
            ServerPlayer player
    ) {

        OutbreakChainData data =
                CHAINS.get(
                        player.getUUID()
                );

        if (data == null) {
            return 0;
        }

        if (data.chain >= 100) {
            return 10;
        }

        if (data.chain >= 50) {
            return 5;
        }

        return 0;
    }





    private static String capitalize(
            String s
    ) {

        if (s == null || s.isEmpty()) {
            return s;
        }

        return s.substring(0, 1)
                .toUpperCase()
                + s.substring(1)
                .toLowerCase();
    }
}