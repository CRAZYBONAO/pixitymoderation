package org.howie.pixity.moderation.neoforge.skills;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.apache.logging.log4j.Logger;
import org.howie.pixity.moderation.PixityModerationNeoForge;
import org.howie.pixity.moderation.chat.TextFormatter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SkillService {

    private final Logger logger;
    private final SQLiteSkillsStore store;

    private final Map<UUID, SkillData> cache = new ConcurrentHashMap<>();

    private volatile boolean dirty = false;

    private static final int MAX_LEVEL = 75;

    public SkillService(Logger logger, SQLiteSkillsStore store) {
        this.logger = logger;
        this.store = store;

        this.cache.putAll(store.load());
    }




    private SkillData getData(UUID uuid) {
        return cache.computeIfAbsent(uuid, k -> {
            dirty = true;
            return new SkillData();
        });
    }

    public SkillData get(UUID uuid) {
        return getData(uuid);
    }




    public void addXp(ServerPlayer player, SkillType type, double amount){

        if (amount <= 0) return;

        double wisdom = PixityModerationNeoForge.STAT_ENGINE.getWisdom(player);
        amount *= (1 + (wisdom * 0.002));

        UUID uuid = player.getUUID();

        SkillData sd = getData(uuid);

        double xp = sd.getXp(type);
        int level = sd.getLevel(type);

        xp += amount;

        while (level < MAX_LEVEL) {

            double needed = getXpForLevel(level);

            if (xp < needed) break;

            xp -= needed;
            level++;

            onLevelUp(uuid, player, type, level);
        }

        sd.setXp(type, xp);
        sd.setLevel(type, level);
        SkillXpBuffer.add(player, type, amount);

        dirty = true;
    }

    public double getXpForLevel(int level) {
        return 500 * Math.pow(1.15, level);
    }

    private void onLevelUp(UUID uuid, ServerPlayer player, SkillType type, int level) {
        player.sendSystemMessage(TextFormatter.parse("<gradient:#FF0000:#FFFFFF:#FF0000:#FFFFFF:#FF0000>&lPIXITY SKILLS</gradient> &7&l➤ &cYou leveled up your " + SkillColor.getPlain(type)));
    }




    public void toggle(UUID uuid, String ability) {
        getData(uuid).toggle(ability);
        dirty = true;
    }

    public boolean isEnabled(UUID uuid, String ability) {
        return getData(uuid).isToggled(ability);
    }




    public void saveAll() {

        if (!dirty) return;

        try {
            store.save(new HashMap<>(cache));
            dirty = false;

            logger.info("[Skills] Saved " + cache.size() + " players");

        } catch (Exception e) {
            logger.error("Failed to save skills", e);
        }
    }

    public Map<UUID, SkillData> getAll() {
        return cache;
    }




    @SubscribeEvent
    public void onTick(ServerTickEvent.Post e) {

        if (e.getServer().getTickCount() % 6000 != 0) return;

        saveAll();
    }




    @SubscribeEvent
    public void onShutdown(ServerStoppingEvent e) {
        logger.info("[Skills] Saving on shutdown...");
        saveAll();
    }
}