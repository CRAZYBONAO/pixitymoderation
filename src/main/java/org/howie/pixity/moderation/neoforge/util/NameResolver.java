package org.howie.pixity.moderation.neoforge.util;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;
import java.util.UUID;

public final class NameResolver {

    private NameResolver(){}

    public static String nameOrUuid(final MinecraftServer server, final UUID uuid) {
        if (uuid == null) return "unknown";

        try {
            ServerPlayer online = server.getPlayerList().getPlayer(uuid);
            if (online != null) return online.getGameProfile().getName();
        } catch (Throwable ignored) {}

        try {
            var cache = server.getProfileCache();
            if (cache != null) {
                var opt = cache.get(uuid);
                if (opt.isPresent() && opt.get().getName() != null) {
                    return opt.get().getName();
                }
            }
        } catch (Throwable ignored) {}

        return uuid.toString();
    }

    public static UUID uuid(final MinecraftServer server, final String input) {
        if (input == null || input.isEmpty()) return null;

        String s = input.trim();

        try {
            return UUID.fromString(s);
        } catch (IllegalArgumentException ignored) {}

        try {
            ServerPlayer online = server.getPlayerList().getPlayerByName(s);
            if (online != null) return online.getUUID();
        } catch (Throwable ignored) {}

        try {
            var cache = server.getProfileCache();
            if (cache != null) {
                Optional<com.mojang.authlib.GameProfile> opt = cache.get(s);
                if (opt.isPresent()) return opt.get().getId();
            }
        } catch (Throwable ignored) {}

        return null;
    }
}