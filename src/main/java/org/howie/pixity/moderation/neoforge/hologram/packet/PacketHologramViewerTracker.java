package org.howie.pixity.moderation.neoforge.hologram.packet;

import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PacketHologramViewerTracker {

    private static final Map<
            String,
            Set<ServerPlayer>
            > VIEWERS =
            new HashMap<>();

    public static boolean isViewing(

            String hologramId,

            ServerPlayer player
    ) {

        return VIEWERS
                .getOrDefault(
                        hologramId,
                        Set.of()
                )
                .contains(player);
    }

    public static void addViewer(

            String hologramId,

            ServerPlayer player
    ) {

        VIEWERS.computeIfAbsent(

                hologramId,

                k -> new HashSet<>()
        ).add(player);
    }

    public static void removeViewer(

            String hologramId,

            ServerPlayer player
    ) {

        Set<ServerPlayer> viewers =
                VIEWERS.get(hologramId);

        if (viewers == null)
            return;

        viewers.remove(player);
    }

    public static void clearViewers(
            String hologramId
    ) {

        VIEWERS.remove(hologramId);
    }
}