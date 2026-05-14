package org.howie.pixity.moderation.neoforge.skills.gui;

import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GuiManager {

    public enum GuiType {
        MAIN_MENU
    }

    private static final Map<UUID, GuiType> open = new HashMap<>();

    public static void set(ServerPlayer player, GuiType type) {
        open.put(player.getUUID(), type);
    }

    public static GuiType get(ServerPlayer player) {
        return open.get(player.getUUID());
    }

    public static void clear(ServerPlayer player) {
        open.remove(player.getUUID());
    }
}