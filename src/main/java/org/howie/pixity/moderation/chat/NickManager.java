package org.howie.pixity.moderation.chat;

import net.minecraft.server.level.ServerPlayer;

import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class NickManager {

    private final Map<UUID, String> nicks =
            new ConcurrentHashMap<>();

    private final NickDatabase database;

    public NickManager(Path folder) {

        this.database = new NickDatabase(folder);

        nicks.putAll(database.loadAll());
    }

    public String getDisplayName(ServerPlayer player) {
        if (player == null) return "Unknown";

        String nick = nicks.get(player.getUUID());

        if (nick != null && !nick.isBlank())
            return nick;

        return player.getGameProfile().getName();
    }

    public void setNick(UUID uuid, String nick) {
        if (uuid == null) return;

        if (nick == null || nick.isBlank()) {
            clearNick(uuid);
            return;
        }

        nicks.put(uuid, nick);

        database.save(uuid, nick);
    }

    public void clearNick(UUID uuid) {
        if (uuid == null) return;

        nicks.remove(uuid);

        database.delete(uuid);
    }
}