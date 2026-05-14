package org.howie.pixity.moderation.neoforge.giveaway;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.world.BossEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

import java.nio.file.Path;
import java.util.*;

public final class GiveawayService {

    private final List<ItemStack> rewardPool = new ArrayList<>();
    private final Set<UUID> entered = new HashSet<>();
    private final List<String> lastWinners = new ArrayList<>();
    private final Map<UUID, ServerBossEvent> bossbars = new HashMap<>();

    private final GiveawayDatabase db;
    private final Map<String, GiveawayPreset> presets;

    private GiveawayMode mode = GiveawayMode.SINGLE;
    private int entryTime = 30;

    private long endTime = 0;
    private boolean running = false;

    private int lastBroadcast = -1;

    public GiveawayService(Path folder) {
        this.db = new GiveawayDatabase(folder);
        this.presets = db.loadPresets();
    }

    public List<ItemStack> getRewardPool() {
        return rewardPool;
    }

    public GiveawayMode getMode() {
        return mode;
    }

    public int getTime() {
        return entryTime;
    }

    public void toggleMode() {
        mode = mode == GiveawayMode.SINGLE
                ? GiveawayMode.SHARED
                : GiveawayMode.SINGLE;
    }

    public void addTime(int seconds) {

        entryTime += seconds;

        if (entryTime < 5)
            entryTime = 5;

        if (entryTime > 3600)
            entryTime = 3600;
    }

    public void start(MinecraftServer server, int seconds) {

        entered.clear();
        running = true;

        endTime = System.currentTimeMillis() + (seconds * 1000L);

        server.getPlayerList().broadcastSystemMessage(
                LegacyAmpersand.parse("&4&lGIVEAWAYS &7&l➤ &aGiveaway &astarted! Use /enter"),
                false
        );
    }

    public void enter(ServerPlayer p) {
        entered.add(p.getUUID());
    }

    public void finish(MinecraftServer server) {

        if (!running) return;

        running = false;

        for (var bar : bossbars.values())
            bar.removeAllPlayers();

        bossbars.clear();

        List<ServerPlayer> pool = new ArrayList<>();

        for (UUID u : entered) {
            ServerPlayer p = server.getPlayerList().getPlayer(u);
            if (p != null) pool.add(p);
        }

        if (!pool.isEmpty()) {

            if (mode == GiveawayMode.SINGLE)
                giveSingle(pool);
            else
                giveShared(pool);
        }

        server.getPlayerList().broadcastSystemMessage(
                LegacyAmpersand.parse("&4&lGIVEAWAYS &7&l➤ §cThe &egiveaway has &cended!"),
                false
        );
    }

    private void giveSingle(List<ServerPlayer> pool) {

        Collections.shuffle(pool);
        ServerPlayer winner = pool.get(0);

        lastWinners.clear();
        lastWinners.add(winner.getGameProfile().getName());

        db.addWinner(winner.getGameProfile().getName());

        for (ItemStack item : rewardPool)
            winner.getInventory().add(item.copy());
    }

    private void giveShared(List<ServerPlayer> pool) {

        Random r = new Random();

        lastWinners.clear();

        for (ItemStack item : rewardPool) {

            ServerPlayer w = pool.get(r.nextInt(pool.size()));

            lastWinners.add(w.getGameProfile().getName());
            db.addWinner(w.getGameProfile().getName());

            w.getInventory().add(item.copy());
        }
    }





    public void updateBossbar(ServerPlayer p, long left) {

        if (!running) return;

        var bar = bossbars.computeIfAbsent(
                p.getUUID(),
                u -> new ServerBossEvent(
                        Component.literal("Giveaway"),
                        BossEvent.BossBarColor.RED,
                        BossEvent.BossBarOverlay.PROGRESS
                )
        );

        bar.setName(LegacyAmpersand.parse("&4&lGIVEAWAYS &7&l➤  &cAlert! Ends in §e" + left + "s &7&l<< &4&lGIVEAWAYS"));

        float progress = Math.min(1f, left / (float) entryTime);
        bar.setProgress(progress);

        if (!bar.getPlayers().contains(p))
            bar.addPlayer(p);
    }





    public void autoBroadcast(MinecraftServer server, long left) {

        int sec = (int) left;

        if (sec == lastBroadcast) return;

        if (sec == 30 || sec == 15 || sec == 10 || sec <= 5) {

            server.getPlayerList().broadcastSystemMessage(
                    LegacyAmpersand.parse("&4&lGIVEAWAYS &7&l➤  §eEnds in §c" + sec + "s §7- /enter"
                    ),
                    false
            );
        }

        lastBroadcast = sec;
    }





    public void savePreset(String name) {

        db.savePreset(
                name,
                entryTime,
                mode.name(),
                rewardPool
        );

        presets.put(
                name.toLowerCase(),
                new GiveawayPreset(
                        name,
                        entryTime,
                        mode.name(),
                        new ArrayList<>(rewardPool)
                )
        );
    }

    public void deletePreset(String name) {

        presets.remove(name.toLowerCase());

        db.deletePreset(name);
    }

    public Map<String, GiveawayPreset> getPresets() {
        return presets;
    }

    public void loadPreset(String name) {

        GiveawayPreset p = presets.get(name.toLowerCase());
        if (p == null) return;

        rewardPool.clear();
        rewardPool.addAll(p.items);

        entryTime = p.time;
        mode = GiveawayMode.valueOf(p.mode);
    }

    public String getPresetAt(int index) {

        if (index < 0 || index >= presets.size())
            return null;

        return new ArrayList<>(presets.keySet()).get(index);
    }

    public void renamePreset(String oldName, String newName) {

        GiveawayPreset p = presets.remove(oldName.toLowerCase());
        if (p == null) return;

        db.deletePreset(oldName);

        GiveawayPreset renamed = new GiveawayPreset(
                newName,
                p.time,
                p.mode,
                new ArrayList<>(p.items)
        );

        presets.put(newName.toLowerCase(), renamed);

        db.savePreset(
                newName,
                renamed.time,
                renamed.mode,
                renamed.items
        );
    }





    public List<String> getStoredWinners() {
        return db.loadWinners(10);
    }





    public boolean isRunning() {
        return running;
    }

    public long getTimeLeft() {
        return (endTime - System.currentTimeMillis()) / 1000L;
    }
}