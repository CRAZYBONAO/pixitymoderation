package org.howie.pixity.moderation.neoforge.alts;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.Logger;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;




public final class AltsService {

    public static final String PERM_ALTS_VIEW = "pixity.alts.view";
    public static final String PERM_ALTS_LINK = "pixity.alts.link";

    private final Map<UUID, Set<UUID>> map = new ConcurrentHashMap<>();


    private final Logger logger;
    private final SQLiteAltsStore store;


    private final Map<UUID, Set<UUID>> links = new ConcurrentHashMap<>();

    public AltsService(final Logger logger, final SQLiteAltsStore store) {
        this.logger = logger;
        this.store = store;
        this.map.putAll(store.load());
    }


    public Set<UUID> altsOf(final UUID who) {
        if (who == null) return Collections.emptySet();
        Set<UUID> comp = component(who);
        if (comp.isEmpty()) return Collections.emptySet();
        comp.remove(who);
        return comp;
    }


    public Set<UUID> component(final UUID who) {
        if (who == null) return new LinkedHashSet<>();
        Set<UUID> out = new LinkedHashSet<>();
        Deque<UUID> q = new ArrayDeque<>();
        q.add(who);
        out.add(who);

        while (!q.isEmpty()) {
            UUID cur = q.removeFirst();
            Set<UUID> adj = links.get(cur);
            if (adj == null) continue;
            for (UUID n : adj) {
                if (n == null) continue;
                if (out.add(n)) q.addLast(n);
            }
        }
        return out;
    }

    public synchronized boolean link(final MinecraftServer server, final ServerPlayer staff, final UUID a, final UUID b) {
        if (a == null || b == null) return false;
        if (a.equals(b)) return false;

        links.computeIfAbsent(a, k -> ConcurrentHashMap.newKeySet()).add(b);
        links.computeIfAbsent(b, k -> ConcurrentHashMap.newKeySet()).add(a);

        normalize();
        persist();

        if (server != null && staff != null) {
            staff.sendSystemMessage(LegacyAmpersand.parse("&c&lALTS &7&l➤ &aLinked alts."));
        }
        return true;
    }

    public synchronized boolean unlink(final MinecraftServer server, final ServerPlayer staff, final UUID a, final UUID b) {
        if (a == null || b == null) return false;
        boolean changed = false;

        Set<UUID> sa = links.get(a);
        if (sa != null) changed |= sa.remove(b);
        Set<UUID> sb = links.get(b);
        if (sb != null) changed |= sb.remove(a);

        cleanup();
        persist();

        if (server != null && staff != null) {
            staff.sendSystemMessage(LegacyAmpersand.parse("&c&lALTS &7&l➤ &cUnlinked alts."));
        }
        return changed;
    }

    private void normalize() {
        for (Map.Entry<UUID, Set<UUID>> e : new HashMap<>(links).entrySet()) {
            UUID k = e.getKey();
            Set<UUID> set = e.getValue();
            if (k == null || set == null) continue;
            for (UUID v : new HashSet<>(set)) {
                if (v == null || v.equals(k)) {
                    set.remove(v);
                    continue;
                }
                links.computeIfAbsent(v, kk -> ConcurrentHashMap.newKeySet()).add(k);
            }
        }
        cleanup();
    }

    private void cleanup() {
        for (UUID k : new HashSet<>(links.keySet())) {
            Set<UUID> set = links.get(k);
            if (set == null) { links.remove(k); continue; }
            set.remove(k);
            if (set.isEmpty()) links.remove(k);
        }
    }

    private void persist() {
        try {
            store.save(new LinkedHashMap<>(links));
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed saving alts", e);
        }
    }
}
