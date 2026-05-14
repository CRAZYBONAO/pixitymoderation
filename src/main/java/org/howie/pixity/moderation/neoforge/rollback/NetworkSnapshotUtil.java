package org.howie.pixity.moderation.neoforge.rollback;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import java.util.*;

public final class NetworkSnapshotUtil {

    private NetworkSnapshotUtil() {}

    public static final class Snapshot {
        public final String kind;
        public final Map<String, Long> items;
        public final Map<String, Long> fluids;

        public Snapshot(String kind, Map<String, Long> items, Map<String, Long> fluids) {
            this.kind = kind;
            this.items = items;
            this.fluids = fluids;
        }
    }

    public static Snapshot snapshot(ServerLevel lvl, BlockPos pos, String blockIdHint) {
        return null;
    }

    public static Map<String, Long> diff(Map<String, Long> before, Map<String, Long> after) {
        return null;
    }

    public static List<String> encodeDelta(Map<String, Long> delta) {
        return null;
    }

    public static Map<String, Long> decodeDelta(List<String> lines) {
        return null;
    }

    public static boolean applyInverseDelta(ServerLevel lvl, BlockPos pos, String kind, Map<String, Long> delta) {
        return false;
    }

    public static boolean applyInverseDeltaFluids(ServerLevel lvl, BlockPos pos, String kind, Map<String, Long> delta) {
        return false;
    }

    public static String detectTerminalType(String blockIdHint, net.minecraft.world.level.block.entity.BlockEntity be) {
        return "NONE";
    }

    public static String getTerminalDisplayName(String blockIdHint, net.minecraft.world.level.block.entity.BlockEntity be) {
        return blockIdHint;
    }
}