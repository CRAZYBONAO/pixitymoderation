package org.howie.pixity.moderation.neoforge.util;

import net.minecraft.server.level.ServerPlayer;
import org.howie.pixity.moderation.neoforge.rank.RankService;


public final class PermissionNumber {

    private PermissionNumber() {}

    public static int highest(final RankService perms, final ServerPlayer p, final String base, final int maxScan) {
        int best = -1;
        if (perms == null || p == null || base == null || base.isBlank()) return best;


        int max = Math.max(0, maxScan);

        for (int i = 0; i <= max; i++) {
            String node = base + "." + i;

            if (perms.hasPerm(p, node) || perms.hasPerm(p, "pixity.admin")) {
                best = i;
            }
        }

        return best;


    }
}
