package org.howie.pixity.moderation.neoforge.skills;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class TreecapitatorHelper {

    private static final int MAX_BLOCKS = 40;
    private static final ThreadLocal<Boolean> BREAKING = ThreadLocal.withInitial(() -> false);

    public static void breakTree(ServerPlayer player, BlockPos start) {




        if (BREAKING.get()) return;
        BREAKING.set(true);

        try {

            var level = player.level();

            Set<BlockPos> visited = new HashSet<>();
            Queue<BlockPos> queue = new LinkedList<>();

            queue.add(start);

            int broken = 0;

            while (!queue.isEmpty() && broken < MAX_BLOCKS) {

                BlockPos pos = queue.poll();

                if (visited.contains(pos)) continue;
                visited.add(pos);

                var state = level.getBlockState(pos);


                if (!state.is(BlockTags.LOGS)) continue;


                if (!player.getMainHandItem().isCorrectToolForDrops(state)) continue;


                ((ServerLevel) level).destroyBlock(pos, true, player);
                broken++;


                for (int x = -1; x <= 1; x++) {
                    for (int y = 0; y <= 1; y++) {
                        for (int z = -1; z <= 1; z++) {

                            BlockPos next = pos.offset(x, y, z);

                            if (!visited.contains(next)) {
                                queue.add(next);
                            }
                        }
                    }
                }
            }

        } finally {
            BREAKING.set(false);
        }
    }

    public static boolean isBreaking() {
        return BREAKING.get();
    }

    public static void setBreaking(boolean value) {
        BREAKING.set(value);
    }
}