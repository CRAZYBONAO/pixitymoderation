package org.howie.pixity.moderation.neoforge.alts.smart;

import org.apache.logging.log4j.Logger;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class SmartAltService {

    public static final String PERM_ALTS_SUGGEST = "pixity.alts.suggest";

    private final Logger logger;
    private final SQLiteSmartAltStore store;


    private final Map<UUID, List<SQLiteSmartAltStore.IpEntry>> history = new ConcurrentHashMap<>();

    public SmartAltService(final Logger logger, final SQLiteSmartAltStore store) {
        this.logger = logger;
        this.store = store;

        Map<UUID, List<SQLiteSmartAltStore.IpEntry>> loaded = store.load();
        if (loaded != null) {
            this.history.putAll(loaded);
        }
    }


    public void record(final UUID uuid, final String maskedIp, final long ts) {
        if (uuid == null || maskedIp == null || maskedIp.isBlank()) return;

        SmartAltConfig cfg = SmartAltConfigStore.get();
        if (cfg == null || !cfg.enabled) return;

        List<SQLiteSmartAltStore.IpEntry> list =
                history.computeIfAbsent(uuid, k -> Collections.synchronizedList(new ArrayList<>()));

        synchronized (list) {
            list.add(new SQLiteSmartAltStore.IpEntry(maskedIp, ts));

            int max = Math.max(5, cfg.maxEntriesPerPlayer);

            if (list.size() > max) {
                list.sort(Comparator.comparingLong(e -> e.ts));
                while (list.size() > max) list.remove(0);
            }
        }

        cleanup();
        persist();
    }


    public List<UUID> suggest(final UUID who, final Set<UUID> alreadyLinked) {

        SmartAltConfig cfg = SmartAltConfigStore.get();
        if (cfg == null || !cfg.enabled) return Collections.emptyList();
        if (who == null) return Collections.emptyList();

        List<SQLiteSmartAltStore.IpEntry> myEntries = entriesOf(who);
        if (myEntries.isEmpty()) return Collections.emptyList();

        Map<String, Integer> myCounts = new HashMap<>();
        for (SQLiteSmartAltStore.IpEntry e : myEntries) {
            if (e == null || e.ip == null) continue;
            myCounts.merge(e.ip, 1, Integer::sum);
        }

        if (myCounts.isEmpty()) return Collections.emptyList();

        Map<UUID, Integer> score = new HashMap<>();

        for (Map.Entry<UUID, List<SQLiteSmartAltStore.IpEntry>> ent : history.entrySet()) {

            UUID other = ent.getKey();

            if (other == null || other.equals(who)) continue;
            if (alreadyLinked != null && alreadyLinked.contains(other)) continue;

            int hits = 0;

            for (SQLiteSmartAltStore.IpEntry e : ent.getValue()) {
                if (e == null || e.ip == null) continue;

                if (myCounts.containsKey(e.ip)) {
                    hits++;
                }
            }

            if (hits >= cfg.minSharedHits) {
                score.put(other, hits);
            }
        }

        List<Map.Entry<UUID, Integer>> sorted = new ArrayList<>(score.entrySet());
        sorted.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));

        int max = Math.max(3, cfg.maxSuggestions);

        List<UUID> result = new ArrayList<>();
        for (int i = 0; i < sorted.size() && result.size() < max; i++) {
            result.add(sorted.get(i).getKey());
        }

        return result;
    }


    public List<SQLiteSmartAltStore.IpEntry> entriesOf(final UUID uuid) {
        List<SQLiteSmartAltStore.IpEntry> list = history.get(uuid);

        if (list == null) return Collections.emptyList();

        synchronized (list) {
            return new ArrayList<>(list);
        }
    }



    private void cleanup() {
        SmartAltConfig cfg = SmartAltConfigStore.get();
        if (cfg == null) return;

        long cutoff = System.currentTimeMillis()
                - (long) cfg.keepDays * 24L * 60L * 60L * 1000L;

        for (UUID u : new HashSet<>(history.keySet())) {
            List<SQLiteSmartAltStore.IpEntry> list = history.get(u);

            if (list == null) continue;

            synchronized (list) {
                list.removeIf(e ->
                        e == null ||
                                e.ts < cutoff ||
                                e.ip == null ||
                                e.ip.isBlank()
                );

                if (list.isEmpty()) {
                    history.remove(u);
                }
            }
        }
    }



    private void persist() {
        try {
            store.save(new LinkedHashMap<>(history));
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed saving smart alt store", e);
        }
    }



    public static String maskAddress(final Object remoteAddr) {
        if (!(remoteAddr instanceof InetSocketAddress isa)) return null;

        InetAddress addr = isa.getAddress();
        if (addr == null) return null;

        byte[] b = addr.getAddress();
        if (b == null) return null;

        if (b.length == 4) {
            int a = b[0] & 0xFF;
            int c = b[1] & 0xFF;
            int d = b[2] & 0xFF;
            return a + "." + c + "." + d + ".*";
        }

        if (b.length == 16) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                sb.append(String.format("%02x", b[i]));
                if (i % 2 == 1 && i != 7) sb.append(":");
            }
            sb.append("::/64");
            return sb.toString();
        }

        return null;
    }
}