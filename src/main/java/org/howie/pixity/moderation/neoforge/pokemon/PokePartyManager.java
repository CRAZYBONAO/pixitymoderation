package org.howie.pixity.moderation.neoforge.pokemon;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PokePartyManager {

    private static BlockPos eventLocation = null;
    private static ServerLevel eventLevel = null;

    private static final Set<UUID> voters = new HashSet<>();

    public static void setEvent(ServerLevel level, BlockPos pos) {
        eventLocation = pos;
        eventLevel = level;
        voters.clear();
    }

    public static boolean hasEvent() {
        return eventLocation != null && eventLevel != null;
    }

    public static boolean addVote(UUID uuid) {
        return voters.add(uuid);
    }

    public static int getVotes() {
        return voters.size();
    }

    public static int getRequiredVotes() {
        return PokePartyConfig.get().requiredVotes;
    }

    public static boolean isReady() {
        return voters.size() >= getRequiredVotes();
    }

    public static BlockPos getLocation() {
        return eventLocation;
    }

    public static ServerLevel getLevel() {
        return eventLevel;
    }

    public static void reset() {
        voters.clear();
    }
}