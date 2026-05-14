package org.howie.pixity.moderation.neoforge.tp;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.BossEvent;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.neoforge.combat.CombatTagService;
import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class TeleportWarmupManager {

    private final WarmupConfigManager cfg;
    private final RankService perms;
    private final TpService tp;

    private final Map<UUID, PendingTeleport> pending = new ConcurrentHashMap<>();
    private final Map<UUID, ServerBossEvent> bossbars = new ConcurrentHashMap<>();
    private final Map<UUID, Long> lastActionbarSecond = new ConcurrentHashMap<>();
    private final CombatTagService combat;

    public TeleportWarmupManager(final WarmupConfigManager cfg, final RankService perms, final TpService tp, CombatTagService combat) {
        this.cfg = cfg;
        this.perms = perms;
        this.tp = tp;
        this.combat = combat;
    }

    public void reload() {
        cfg.reload();
    }

    public void cancel(final UUID u, final String msg, final MinecraftServer server) {
        if (u == null) return;
        PendingTeleport pt = pending.remove(u);
        if (pt == null) return;

        removeBossbar(u);
        lastActionbarSecond.remove(u);

        ServerPlayer p = server == null ? null : server.getPlayerList().getPlayer(u);
        if (p != null && msg != null) {
            p.sendSystemMessage(LegacyAmpersand.parse(msg));
            p.displayClientMessage(Component.empty(), true);
        }
    }

    public boolean request(final MinecraftServer server, final ServerPlayer p, final WarpPos target, final String label) {
        if (server == null || p == null || target == null) return false;

        WarmupConfig c = cfg.get();
        int secs = c.warmupSeconds;

        String bypass = "pixity.tp.instant";
        boolean instant = secs <= 0 || perms.hasPerm(p, bypass);

        if (combat != null && combat.isTagged(p)) {
            int left = combat.getRemaining(p);
            p.sendSystemMessage(LegacyAmpersand.parse("&4&lCOMBAT &7&l➤ &cYou cannot teleport while in combat &e(" + left + "s)&c."));
            return false;
        }



        if (instant) {
            BackService.record(p);

            return tp.teleportNow(server, p, target);
        }

        PendingTeleport pt = new PendingTeleport();
        pt.target = target;
        pt.executeAtMs = System.currentTimeMillis() + (secs * 1000L);
        pt.startX = p.getX();
        pt.startY = p.getY();
        pt.startZ = p.getZ();
        pt.label = label == null ? "" : label;

        pending.put(p.getUUID(), pt);
        lastActionbarSecond.remove(p.getUUID());

        p.sendSystemMessage(LegacyAmpersand.parse("&e&lTELEPORTS &7&l➤ &eTeleporting " + pt.label + " &ein &a" + secs + "s. &eDon't move."));

        if (c.bossbarCountdown) {
            setupBossbar(server, p, pt, secs);
        }
        return true;
    }

    public void tick(final MinecraftServer server, final ServerPlayer p) {
        if (server == null || p == null) return;
        UUID u = p.getUUID();
        PendingTeleport pt = pending.get(u);
        if (pt == null) return;

        WarmupConfig c = cfg.get();
        double max = c.cancelMoveDistance;

        double dx = p.getX() - pt.startX;
        double dz = p.getZ() - pt.startZ;
        double distSq = dx*dx + dz*dz;

        if (combat != null && combat.isTagged(p)) {
            pending.remove(u);
            removeBossbar(u);
            lastActionbarSecond.remove(u);


            p.sendSystemMessage(LegacyAmpersand.parse("&e&lTELEPORTS &7&l➤ &cTeleport cancelled (combat)."));
            p.displayClientMessage(Component.empty(), true);
            return;


        }


        if (distSq > (max * max)) {
            pending.remove(u);
            removeBossbar(u);
            lastActionbarSecond.remove(u);
            p.sendSystemMessage(LegacyAmpersand.parse("&e&lTELEPORTS &7&l➤ &cTeleport cancelled (you moved)."));
            p.displayClientMessage(Component.empty(), true);
            return;
        }

        long now = System.currentTimeMillis();
        long msLeft = Math.max(0, pt.executeAtMs - now);
        long secLeft = (msLeft + 999) / 1000;

        if (c.actionbarCountdown) {
            Long last = lastActionbarSecond.get(u);
            if (last == null || last.longValue() != secLeft) {
                lastActionbarSecond.put(u, secLeft);
                String lbl = pt.label == null || pt.label.isBlank() ? "teleport" : pt.label;
                p.displayClientMessage(
                        org.howie.pixity.moderation.neoforge.text.LegacyAmpersand.parse(
                                "&e&lTELEPORTS &7&l➤ &eTeleporting to &c" + lbl + " &ein &a" + secLeft + "s"
                        ),
                        true
                );
            }
        }

        if (c.bossbarCountdown) {
            updateBossbar(u, c, pt, secLeft);
        }

        if (now >= pt.executeAtMs) {
            pending.remove(u);
            removeBossbar(u);
            lastActionbarSecond.remove(u);
            p.displayClientMessage(Component.empty(), true);
            BackService.record(p);
            tp.teleportNow(server, p, pt.target);
        }
    }

    public void onLogout(final UUID u) {
        if (u != null) {
            pending.remove(u);
            removeBossbar(u);
            lastActionbarSecond.remove(u);
        }
    }

    private void setupBossbar(final MinecraftServer server, final ServerPlayer p, final PendingTeleport pt, final int totalSeconds) {
        UUID u = p.getUUID();
        removeBossbar(u);

        ServerBossEvent bar = new ServerBossEvent(
                LegacyAmpersand.parse(formatBossTitle(cfg.get().bossbarTitle, pt.label, totalSeconds)),
                BossEvent.BossBarColor.YELLOW,
                BossEvent.BossBarOverlay.PROGRESS
        );
        bar.addPlayer(p);
        bar.setProgress(0.0f);
        bossbars.put(u, bar);
    }

    private void updateBossbar(final UUID u, final WarmupConfig c, final PendingTeleport pt, final long secLeft) {
        ServerBossEvent bar = bossbars.get(u);
        if (bar == null) return;

        int total = Math.max(1, c.warmupSeconds);
        float progress = 1.0f - (Math.min(total, Math.max(0, secLeft)) / (float) total);
        if (progress < 0f) progress = 0f;
        if (progress > 1f) progress = 1f;

        String title = formatBossTitle(c.bossbarTitle, pt.label, (int) secLeft);
        bar.setName(LegacyAmpersand.parse(title));
        bar.setProgress(progress);
    }

    private void removeBossbar(final UUID u) {
        ServerBossEvent bar = bossbars.remove(u);
        if (bar != null) {
            bar.removeAllPlayers();
        }
    }

    private static String formatBossTitle(final String template, final String label, final int seconds) {
        String t = template == null || template.isBlank() ? "Teleporting {label} in {seconds}s" : template;
        String lbl = (label == null || label.isBlank()) ? "teleport" : label;
        return t.replace("{label}", lbl).replace("{seconds}", String.valueOf(seconds));
    }
}
