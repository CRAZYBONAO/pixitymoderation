package org.howie.pixity.moderation.neoforge.queue;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;
import org.howie.pixity.moderation.neoforge.rank.RankService;

import java.util.*;

public final class QueueService {


    public static final String PERM_BYPASS = "pixity.queue.bypass";
    public static final String PERM_PRIORITY_PREFIX = "pixity.queue.priority.";
    public static final String PERM_GRACE = "pixity.queue.grace";

    private final QueueConfig cfg;
    private final RankService ranks;

    private final Map<String, Long> graceMap = new HashMap<>();

    public QueueService(final QueueConfig cfg, final RankService ranks) {
        this.cfg = cfg;
        this.ranks = ranks;
    }

    private boolean has(ServerPlayer p, String perm) {
        return ranks != null && ranks.hasPerm(p, perm);
    }

    public QueueConfig config() { return cfg; }



    public void recordDisconnect(final ServerPlayer p) {
        if (p == null || cfg == null || !cfg.enabled) return;

        long until = System.currentTimeMillis() + (Math.max(0, cfg.graceSeconds) * 1000L);
        graceMap.put(p.getUUID().toString(), until);
    }

    private boolean hasGrace(final ServerPlayer p) {
        if (p == null || cfg == null || cfg.graceSeconds <= 0) return false;

        if (!cfg.graceAppliesToAll && !has(p, PERM_GRACE)) return false;

        Long until = graceMap.get(p.getUUID().toString());
        if (until == null) return false;

        if (System.currentTimeMillis() > until) {
            graceMap.remove(p.getUUID().toString());
            return false;
        }

        return true;
    }



    public void onJoin(final MinecraftServer server, final ServerPlayer joiner) {
        if (server == null || joiner == null || cfg == null || !cfg.enabled) return;

        if (hasBypass(joiner)) return;

        int online = server.getPlayerList().getPlayerCount();
        int soft = Math.max(1, cfg.softMaxPlayers);

        if (online <= soft) return;

        int joinPri = priority(joiner);
        boolean isPriority = joinPri > 0;

        if (!isPriority) {
            if (hasGrace(joiner) && online <= soft + cfg.reservedSlots) return;

            joiner.connection.disconnect(LegacyAmpersand.parse(cfg.fullKickMessage));
            return;
        }

        if (!cfg.kickForPriority) return;

        int maxWithReserved = soft + Math.max(0, cfg.reservedSlots);
        if (online <= maxWithReserved) return;

        ServerPlayer victim = pickVictim(server, joiner, joinPri);
        if (victim != null) {
            victim.connection.disconnect(LegacyAmpersand.parse(cfg.displacedKickMessage));
        }
    }





    private ServerPlayer pickVictim(final MinecraftServer server, final ServerPlayer joiner, final int joinPri) {
        List<ServerPlayer> online = new ArrayList<>(server.getPlayerList().getPlayers());

        online.sort(Comparator
                .comparingInt(this::priority)
                .thenComparing(p -> p.getGameProfile().getName())
        );

        for (ServerPlayer p : online) {
            if (p == joiner) continue;
            if (hasBypass(p)) continue;

            if (priority(p) < joinPri) return p;
        }

        return null;
    }





    private boolean hasBypass(ServerPlayer p) {
        return has(p, PERM_BYPASS);
    }

    public int priority(final ServerPlayer p) {
        int best = 0;

        for (int i = 1; i <= 50; i++) {
            if (has(p, PERM_PRIORITY_PREFIX + i)) best = i;
        }

        return best;
    }


}
