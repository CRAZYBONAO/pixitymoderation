package org.howie.pixity.moderation.neoforge.fly;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.world.BossEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.howie.pixity.moderation.neoforge.util.TimeUtil.formatDuration;

public final class FlyTimeService {


    private int saveTick = 0;

    private final SQLiteFlyTimeStore store;

    private final Map<UUID, Long> timeLeft = new ConcurrentHashMap<>();
    private final Set<UUID> flying = ConcurrentHashMap.newKeySet();

    private final Map<UUID, ServerBossEvent> bossbars = new HashMap<>();

    private final RankService ranks;

    private static final long DISPLAY_MAX = 600;

    public FlyTimeService(SQLiteFlyTimeStore store, RankService ranks) {
        this.store = store;
        this.timeLeft.putAll(store.load());
        this.ranks = ranks;
    }


    public double getDiscount(ServerPlayer p) {
        double best = 0;

        if (ranks.hasPerm(p, "pixity.fly.discount.15")) best = 0.15;
        if (ranks.hasPerm(p, "pixity.fly.discount.10")) best = Math.max(best, 0.10);
        if (ranks.hasPerm(p, "pixity.fly.discount.5")) best = Math.max(best, 0.05);

        return best;
    }


    public long getTime(UUID uuid) {
        return timeLeft.getOrDefault(uuid, 0L);
    }

    public void give(UUID uuid, long seconds) {
        timeLeft.merge(uuid, seconds, Long::sum);
    }

    public void set(UUID uuid, long seconds) {
        timeLeft.put(uuid, Math.max(0, seconds));
    }


    public boolean toggleFlight(ServerPlayer p) {
        UUID u = p.getUUID();

        if (flying.contains(u)) {
            disableFlight(p);
            return false;
        }

        long time = getTime(u);
        if (time <= 0) {
            p.sendSystemMessage(LegacyAmpersand.parse("&6&lFLIGHT: &cError! No flight time remaining."));
            return false;
        }

        enableFlight(p);
        return true;
    }

    public void enableFlight(ServerPlayer p) {
        UUID u = p.getUUID();

        flying.add(u);

        p.getAbilities().mayfly = true;
        p.getAbilities().flying = true;
        p.onUpdateAbilities();
    }

    public void disableFlight(ServerPlayer p) {
        UUID u = p.getUUID();

        flying.remove(u);

        p.getAbilities().flying = false;
        p.getAbilities().mayfly = false;
        p.onUpdateAbilities();

        removeBossbar(p);
    }


    public void tick(ServerPlayer p) {

        UUID u = p.getUUID();

        if (!flying.contains(u)) return;

        if (!p.getAbilities().flying) return;

        if (p.onGround()) return;

        long time = getTime(u);

        if (time <= 0) {
            disableFlight(p);
            p.sendSystemMessage(LegacyAmpersand.parse(
                    "§6§lFLIGHT: §cYour flight time expired! Buy more in /fly shop"
            ));
            return;
        }

        long newTime = time - 1;
        timeLeft.put(u, newTime);

        p.displayClientMessage(
                LegacyAmpersand.parse("§e✈ Time Left: §f" + formatDuration(newTime)),
                true
        );

        updateBossbar(p, newTime);
    }


    public void updateBossbar(ServerPlayer p, long time) {

        UUID uuid = p.getUUID();

        if (time <= 0) {
            removeBossbar(p);
            return;
        }

        ServerBossEvent bar = bossbars.computeIfAbsent(uuid, u ->
                new ServerBossEvent(
                        Component.literal("Flight Time"),
                        BossEvent.BossBarColor.BLUE,
                        BossEvent.BossBarOverlay.PROGRESS
                )
        );

        float progress = Math.min(1f, time / (float) DISPLAY_MAX);

        bar.setProgress(progress);
        bar.setName(Component.literal("§eFlight Time: §f" + formatDuration(time)));

        if (!bar.getPlayers().contains(p)) {
            bar.addPlayer(p);
        }
    }

    public void removeBossbar(ServerPlayer p) {
        ServerBossEvent bar = bossbars.remove(p.getUUID());
        if (bar != null) bar.removePlayer(p);
    }


    public void save() {
        store.save(new HashMap<>(timeLeft));
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEvent.Post e) {

        saveTick++;

        if (saveTick < 6000) return;

        saveTick = 0;
        save();
    }


}
