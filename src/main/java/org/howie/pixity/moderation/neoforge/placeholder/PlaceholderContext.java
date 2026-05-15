package org.howie.pixity.moderation.neoforge.placeholder;

import net.minecraft.server.level.ServerPlayer;

public class PlaceholderContext {

    private final ServerPlayer player;

    public PlaceholderContext(
            ServerPlayer player
    ) {

        this.player = player;
    }

    public ServerPlayer player() {
        return player;
    }
}