package org.howie.pixity.moderation.neoforge.worldboss;

import net.minecraft.server.MinecraftServer;

import org.howie.pixity.moderation.chat.TextFormatter;

import java.util.HashSet;
import java.util.Set;

public class WorldBossHazards {





    private static final Set<String> ACTIVE =
            new HashSet<>();





    public static void reset() {

        ACTIVE.clear();
    }





    public static boolean has(
            String id
    ) {

        return ACTIVE.contains(id);
    }





    public static void add(

            MinecraftServer server,

            String id,

            String title,

            String description
    ) {

        if (ACTIVE.contains(id)) {
            return;
        }

        ACTIVE.add(id);





        server.getPlayerList()
                .broadcastSystemMessage(

                        TextFormatter.parse(

                                "<red>&l⚠ RAID HAZARD</red>\n\n"

                                        + "<gold>"
                                        + title
                                        + "</gold>\n\n"

                                        + "<gray>"
                                        + description
                                        + "</gray>"
                        ),

                        false
                );
    }





    public static double getModifier() {

        double modifier =
                1.0;





        if (has("stealth_rock")) {
            modifier *= 0.90;
        }





        if (has("spikes")) {
            modifier *= 0.92;
        }





        if (has("toxic_spikes")) {
            modifier *= 0.88;
        }





        if (has("sticky_web")) {
            modifier *= 0.85;
        }





        if (has("paralysis_field")) {
            modifier *= 0.80;
        }





        if (has("frozen_terrain")) {
            modifier *= 0.75;
        }

        return modifier;
    }
}