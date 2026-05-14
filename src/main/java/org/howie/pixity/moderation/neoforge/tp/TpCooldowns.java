package org.howie.pixity.moderation.neoforge.tp;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;
import org.howie.pixity.moderation.neoforge.util.PermissionNumber;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class TpCooldowns {

    private final RankService perms;
    private final Map<String, Long> lastUse = new ConcurrentHashMap<>();

    public TpCooldowns(final RankService perms) {
        this.perms = perms;
    }

    private static String key(final UUID u, final String type) {
        return u + ":" + type;
    }


    public boolean checkOrWarn(final ServerPlayer p, final String type, final String permBase, final int scanMax) {
        if (p == null) return false;
        int cd = PermissionNumber.highest(perms, p, permBase, scanMax);
        if (cd <= 0) return true;
        long now = System.currentTimeMillis();
        String k = key(p.getUUID(), type);
        Long last = lastUse.get(k);
        if (last == null) {
            lastUse.put(k, now);
            return true;
        }
        long elapsed = now - last;
        long needMs = cd * 1000L;
        if (elapsed >= needMs) {
            lastUse.put(k, now);
            return true;
        }
        long left = (needMs - elapsed + 999) / 1000;
        p.sendSystemMessage(LegacyAmpersand.parse("&e&lTeleports &7&l➤ &eYou must wait &c" + left + "s &ebefore using that again."));
        return false;
    }

    public void markUsed(final ServerPlayer p, final String type) {
        if (p == null) return;
        lastUse.put(key(p.getUUID(), type), System.currentTimeMillis());
    }
}
