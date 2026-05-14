package org.howie.pixity.moderation.neoforge.pokemon;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.network.chat.Component;

import java.util.*;

public class HologramManager {

    private static final Map<Integer, List<ArmorStand>> ACTIVE = new HashMap<>();
    private static final Set<Integer> DIRTY = new HashSet<>();
    private static List<HologramData> HOLOGRAM_CACHE = new ArrayList<>();

    private static long nextAllowedRefresh = 0;

    private static final Map<String, Component> TEXT_CACHE = new HashMap<>();

    private static List<Map.Entry<UUID, Integer>> LAST_NORMAL = new ArrayList<>();
    private static List<Map.Entry<UUID, Integer>> LAST_SHINY = new ArrayList<>();

    private static Component cached(String text) {
        return TEXT_CACHE.computeIfAbsent(text, Component::literal);
    }


    public static void loadCache() {
        HOLOGRAM_CACHE = PokedexDatabase.getHolograms();
    }


    public static void loadAll(ServerLevel level) {

        for (var data : HOLOGRAM_CACHE) {
            if (!level.dimension().location().toString().equals(data.world)) continue;
            createIfMissing(level, data);
        }

        markDirty("normal");
        markDirty("shiny");
    }


    public static void markDirty(String type) {
        for (var data : HOLOGRAM_CACHE) {
            if (data.type.equalsIgnoreCase(type)) {
                DIRTY.add(data.id);
            }
        }
    }


    public static void queueRefresh(MinecraftServer server) {

        long now = System.currentTimeMillis();

        if (now < nextAllowedRefresh) return;

        nextAllowedRefresh = now + 3000;

        server.tell(new net.minecraft.server.TickTask(0, () -> {
            refreshDirty(server);
        }));
    }


    public static void refreshDirty(MinecraftServer server) {

        if (DIRTY.isEmpty()) return;

        Set<Integer> toUpdate = new HashSet<>(DIRTY);
        DIRTY.clear();

        java.util.concurrent.CompletableFuture
                .supplyAsync(() -> {

                    Map<String, List<Map.Entry<UUID, Integer>>> result = new HashMap<>();

                    var normal = PokedexDatabase.getTopDex();
                    var shiny = PokedexDatabase.getTopShinyDex();

                    if (isDifferent(normal, LAST_NORMAL)) {
                        LAST_NORMAL = normal;
                        result.put("normal", normal);
                    }

                    if (isDifferent(shiny, LAST_SHINY)) {
                        LAST_SHINY = shiny;
                        result.put("shiny", shiny);
                    }

                    return result;
                })

                .thenAccept(data -> server.tell(new net.minecraft.server.TickTask(0, () -> {


                    if (!data.containsKey("normal") && !data.containsKey("shiny")) {
                        return;
                    }

                    for (var level : server.getAllLevels()) {

                        for (var holo : HOLOGRAM_CACHE) {

                            if (!toUpdate.contains(holo.id)) continue;
                            if (!level.dimension().location().toString().equals(holo.world)) continue;

                            var list = holo.type.equals("shiny")
                                    ? data.get("shiny")
                                    : data.get("normal");

                            if (list != null) {
                                updateFromList(level, holo, list);
                            }
                        }
                    }

                    cleanupInvalid();

                })));
    }


    private static boolean isDifferent(List<Map.Entry<UUID, Integer>> a,
                                       List<Map.Entry<UUID, Integer>> b) {

        if (a.size() != b.size()) return true;

        for (int i = 0; i < a.size(); i++) {
            var e1 = a.get(i);
            var e2 = b.get(i);

            if (!e1.getKey().equals(e2.getKey())) return true;
            if (!e1.getValue().equals(e2.getValue())) return true;
        }

        return false;
    }


    private static void createIfMissing(ServerLevel level, HologramData data) {

        if (ACTIVE.containsKey(data.id)) return;

        List<ArmorStand> stands = new ArrayList<>();

        double y = data.y + 2;

        for (int i = 0; i < 12; i++) {
            stands.add(create(level, data.x, y, data.z));
            y -= 0.25;
        }

        ACTIVE.put(data.id, stands);
    }


    private static void updateFromList(ServerLevel level,
                                       HologramData data,
                                       List<Map.Entry<UUID, Integer>> list) {

        List<ArmorStand> stands = ACTIVE.get(data.id);
        if (stands == null) return;

        int index = 0;

        stands.get(index++).setCustomName(cached(
                data.type.equals("shiny")
                        ? "§d§l✨ SHINY DEX ✨"
                        : "§6§l🏆 POKEDEX 🏆"
        ));

        int rank = 1;

        for (var entry : list) {

            if (index >= stands.size()) break;

            String name = PokedexDatabase.getName(entry.getKey());

            stands.get(index++).setCustomName(cached(
                    "§7#" + rank + " §f" + name + " §7- §e" + entry.getValue()
            ));

            rank++;
        }

        while (index < stands.size()) {
            stands.get(index++).setCustomName(cached(""));
        }
    }


    private static void cleanupInvalid() {

        Iterator<Map.Entry<Integer, List<ArmorStand>>> it = ACTIVE.entrySet().iterator();

        while (it.hasNext()) {

            var entry = it.next();
            List<ArmorStand> stands = entry.getValue();

            stands.removeIf(s -> s == null || !s.isAlive());

            if (stands.isEmpty()) {
                it.remove();
            }
        }
    }


    public static void remove(int id) {

        var list = ACTIVE.remove(id);

        if (list != null) {
            for (var s : list) {
                if (s != null && s.isAlive()) {
                    s.discard();
                }
            }
        }
    }


    private static ArmorStand create(ServerLevel level, int x, double y, int z) {

        ArmorStand stand = new ArmorStand(EntityType.ARMOR_STAND, level);

        stand.setPos(x + 0.5, y, z + 0.5);
        stand.setInvisible(true);
        stand.setNoGravity(true);
        stand.setInvulnerable(true);
        stand.setCustomNameVisible(true);

        byte flags = stand.getEntityData().get(ArmorStand.DATA_CLIENT_FLAGS);

        flags |= 0x01;
        flags |= 0x08;
        flags |= 0x10;

        stand.getEntityData().set(ArmorStand.DATA_CLIENT_FLAGS, flags);

        level.addFreshEntity(stand);
        return stand;
    }
}