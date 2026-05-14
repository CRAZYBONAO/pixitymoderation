package org.howie.pixity.moderation.neoforge.reports;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import org.apache.logging.log4j.Logger;
import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

import java.util.*;

public final class ReportsService {


    public static final String PERM_REPORT_CREATE = "pixity.report.create";
    public static final String PERM_REPORT_VIEW = "pixity.report.view";
    public static final String PERM_REPORT_TP = "pixity.report.tp";
    public static final String PERM_REPORT_ASSIGN = "pixity.report.assign";
    public static final String PERM_REPORT_CLOSE = "pixity.report.close";
    public static final String PERM_REPORT_NOTIFY = "pixity.report.notify";
    public static final String PERM_REPORT_TELEPORT = "pixity.report.teleport";

    private final Logger logger;
    private final ReportsStore store;
    private final RankService ranks;

    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final List<ReportEntry> reports = new ArrayList<>();
    private int nextId = 1;

    public ReportsService(final Logger logger, final ReportsStore store, final RankService ranks) {
        this.logger = logger;
        this.store = store;
        this.ranks = ranks;

        reports.addAll(store.load());
        for (ReportEntry r : reports) nextId = Math.max(nextId, r.id + 1);
    }

    private boolean has(ServerPlayer p, String perm) {
        return ranks != null && ranks.hasPerm(p, perm);
    }





    public synchronized ReportEntry create(final MinecraftServer server, final ServerPlayer reporter, final ServerPlayer target, final String reason) {

        long now = System.currentTimeMillis();
        long last = cooldowns.getOrDefault(reporter.getUUID(), 0L);

        if (now - last < 300000L) {
            reporter.sendSystemMessage(LegacyAmpersand.parse("&c&lREPORTS &7&l➤ &cError! You must wait 5 minutes before creating another report."));
            return null;
        }

        cooldowns.put(reporter.getUUID(), now);

        ReportEntry r = new ReportEntry();
        r.id = nextId++;
        r.ts = now;

        r.reporterUuid = reporter.getUUID().toString();
        r.reporterName = reporter.getGameProfile().getName();

        r.targetUuid = target.getUUID().toString();
        r.targetName = target.getGameProfile().getName();

        r.reason = reason;
        r.status = ReportStatus.OPEN;
        r.lastLocation = locString(target);

        reports.add(r);
        persist();

        notifyStaff(server, r);
        return r;
    }





    public synchronized List<ReportEntry> list(final ReportStatus status) {
        List<ReportEntry> out = new ArrayList<>();
        for (ReportEntry r : reports) {
            if (status == null || r.status == status) out.add(r);
        }
        out.sort(Comparator.comparingInt(a -> a.id));
        return out;
    }

    public synchronized ReportEntry get(final int id) {
        for (ReportEntry r : reports) if (r.id == id) return r;
        return null;
    }





    public synchronized boolean assign(final MinecraftServer server, final int id, final ServerPlayer staff) {
        ReportEntry r = get(id);
        if (r == null) return false;

        r.status = ReportStatus.ASSIGNED;
        r.assignedUuid = staff.getUUID().toString();
        r.assignedName = staff.getGameProfile().getName();
        r.assignedTs = System.currentTimeMillis();

        persist();

        broadcast(server, LegacyAmpersand.parse("&c&lREPORTS &7&l➤ &cReport &e#" + id + " &cassigned to &e" + r.assignedName + "&c."));
        return true;
    }

    public synchronized boolean unassign(final MinecraftServer server, final int id, final ServerPlayer staff) {
        ReportEntry r = get(id);
        if (r == null) return false;
        if (r.status == ReportStatus.CLOSED) return false;

        r.status = ReportStatus.OPEN;
        r.assignedUuid = null;
        r.assignedName = null;

        persist();

        broadcast(server, LegacyAmpersand.parse("&c&lREPORTS &7&l➤ &cReport &e#" + id + " &cunassigned by &e" + staff.getGameProfile().getName() + "&c."));
        return true;
    }





    public synchronized boolean close(final MinecraftServer server, final int id, final ServerPlayer staff, final String note) {
        ReportEntry r = get(id);
        if (r == null) return false;

        r.status = ReportStatus.CLOSED;
        r.closedTs = System.currentTimeMillis();
        r.closeNote = note;
        r.closedBy = staff.getGameProfile().getName();

        persist();

        broadcast(server, LegacyAmpersand.parse("&c&lREPORTS &7&l➤ &cReport &e#" + id + " &cclosed by &e" + r.closedBy + "&c."));

        try {
            ServerPlayer reporter = server.getPlayerList().getPlayerByName(r.reporterName);
            if (reporter != null) {
                reporter.sendSystemMessage(LegacyAmpersand.parse("&c&lREPORTS &7&l➤ &cYour report &e#" + r.id + " &chas been resolved by &e" + r.closedBy + "&c."
                ));
            }
        } catch (Throwable ignored) {}

        return true;
    }





    private void notifyStaff(final MinecraftServer server, final ReportEntry r) {
        Component msg = LegacyAmpersand.parse("&c&lREPORTS &7&l➤ &e#" + r.id + " &c" + r.reporterName + " &a-> &c" + r.targetName + " &8| &e" + r.reason);
        Component hint = LegacyAmpersand.parse("&c&lREPORTS &7&l➤ &7Use &f/reports open &7or &f/report tp " + r.id);

        for (ServerPlayer p : server.getPlayerList().getPlayers()) {
            if (!has(p, PERM_REPORT_NOTIFY)) continue;

            p.sendSystemMessage(msg);
            p.sendSystemMessage(hint);

            try {
                p.playNotifySound(SoundEvents.NOTE_BLOCK_BELL.value(), SoundSource.MASTER, 1.0f, 1.0f);
            } catch (Throwable ignored) {}
        }
    }

    private void broadcast(final MinecraftServer server, final Component msg) {
        for (ServerPlayer p : server.getPlayerList().getPlayers()) {
            if (!has(p, PERM_REPORT_NOTIFY)) continue;
            p.sendSystemMessage(msg);
        }
    }

    private String locString(final ServerPlayer p) {
        try {
            String dim = p.level().dimension().location().toString();
            int x = (int)Math.floor(p.getX());
            int y = (int)Math.floor(p.getY());
            int z = (int)Math.floor(p.getZ());
            return dim + " " + x + " " + y + " " + z;
        } catch (Throwable t) {
            return "unknown";
        }
    }

    private void persist() {
        try {
            store.save(reports);
        } catch (Exception e) {
            logger.error("[PixityModeration] Failed saving reports", e);
        }
    }


}
