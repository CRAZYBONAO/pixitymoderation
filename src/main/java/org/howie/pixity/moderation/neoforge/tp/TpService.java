package org.howie.pixity.moderation.neoforge.tp;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class TpService {




    public static final String PERM_SPAWN = "pixity.tp.spawn";
    public static final String PERM_SETSPAWN = "pixity.tp.setspawn";

    public static final String PERM_HOME = "pixity.home.use";
    public static final String PERM_SETHOME = "pixity.home.set";
    public static final String PERM_DELHOME = "pixity.home.delete";

    public static final String PERM_WARP = "pixity.tp.warp";
    public static final String PERM_SETWARP = "pixity.tp.setwarp";
    public static final String PERM_DELWARP = "pixity.tp.delwarp";

    public static final String PERM_PWARP = "pixity.pwarp.use";
    public static final String PERM_SETPWARP = "pixity.pwarp.set";
    public static final String PERM_DELPWARP = "pixity.pwarp.delete";
    public static final String PERM_DELPWARP_ANY = "pixity.pwarp.delete.any";

    private final SQLiteTpStore store;
    private final RankService perms;

    private final Map<UUID, Map<String, WarpPos>> homes = new ConcurrentHashMap<>();
    private final Map<String, WarpPos> warps = new ConcurrentHashMap<>();
    private final Map<String, PlayerWarp> pwarps = new ConcurrentHashMap<>();
    private volatile WarpPos spawn;

    public TpService(final SQLiteTpStore store, final RankService perms) {
        this.store = store;
        this.perms = perms;

        homes.putAll(store.loadHomes());
        warps.putAll(store.loadWarps());
        pwarps.putAll(store.loadPlayerWarps());
        spawn = store.loadSpawn();
    }



    public boolean hasPerm(final ServerPlayer p, final String node) {
        return perms != null && (perms.hasPerm(p, node) || perms.hasPerm(p, "pixity.admin"));
    }



    public boolean setSpawn(final ServerPlayer p) {
        if (!hasPerm(p, PERM_SETSPAWN)) {
            p.sendSystemMessage(LegacyAmpersand.parse("&c&lPERMISSIONS &7&l➤ &cNo permission to set spawn."));
            return false;
        }


        spawn = capture(p);
        store.saveSpawn(spawn);

        return true;


    }


    public boolean teleportSpawn(final MinecraftServer server, final ServerPlayer p) {
        if (spawn == null) {
            p.sendSystemMessage(LegacyAmpersand.parse("&e&lSPAWNS &7&l➤ Spawn is not set."));
            return false;
        }
        return teleportNow(server, p, spawn);
    }


    public void setHome(final ServerPlayer p, final String name) {
        String key = norm(name);
        homes.computeIfAbsent(p.getUUID(), k -> new ConcurrentHashMap<>()).put(key, capture(p));
        store.saveHomes(snapshotHomes());
    }

    public boolean delHome(final ServerPlayer p, final String name) {
        String key = norm(name);
        Map<String, WarpPos> m = homes.get(p.getUUID());
        if (m == null) return false;
        WarpPos removed = m.remove(key);
        store.saveHomes(snapshotHomes());
        return removed != null;
    }

    public boolean teleportHome(final MinecraftServer server, final ServerPlayer p, final String name) {
        Map<String, WarpPos> m = homes.get(p.getUUID());
        if (m == null) return false;

        WarpPos pos = m.get(norm(name));
        if (pos == null) return false;

        return teleportNow(server, p, pos);
    }




    public void setWarp(final ServerPlayer p, final String name) {
        warps.put(norm(name), capture(p));
        store.saveWarps(new HashMap<>(warps));
    }

    public boolean delWarp(final String name) {
        WarpPos removed = warps.remove(norm(name));
        store.saveWarps(new HashMap<>(warps));
        return removed != null;
    }

    public boolean teleportWarp(final MinecraftServer server, final ServerPlayer p, final String name) {
        WarpPos pos = warps.get(norm(name));
        if (pos == null) return false;
        return teleportNow(server, p, pos);
    }


    public void setPlayerWarp(final ServerPlayer p, final String name) {
        PlayerWarp w = new PlayerWarp();
        w.name = norm(name);
        w.owner = p.getUUID();
        w.ownerName = p.getGameProfile().getName();
        w.pos = capture(p);

        pwarps.put(w.name, w);
        store.savePlayerWarps(new HashMap<>(pwarps));
    }

    public boolean delPlayerWarp(final ServerPlayer actor, final String name) {
        PlayerWarp w = pwarps.get(norm(name));
        if (w == null) return false;

        boolean canAny = hasPerm(actor, PERM_DELPWARP_ANY);

        if (!canAny && (w.owner == null || !w.owner.equals(actor.getUUID()))) {
            actor.sendSystemMessage(LegacyAmpersand.parse("&d&lPLAYER WARPS &7&l➤ &cError! You don't own that player warp."));
            return false;
        }

        pwarps.remove(norm(name));
        store.savePlayerWarps(new HashMap<>(pwarps));
        return true;
    }

    public boolean teleportPlayerWarp(final MinecraftServer server, final ServerPlayer p, final String name) {
        PlayerWarp w = pwarps.get(norm(name));
        if (w == null || w.pos == null) return false;
        return teleportNow(server, p, w.pos);
    }


    public boolean teleportNow(final MinecraftServer server, final ServerPlayer p, final WarpPos pos) {
        if (server == null || p == null || pos == null) return false;

        ResourceLocation dimId = ResourceLocation.parse(pos.dimension);
        ResourceKey<net.minecraft.world.level.Level> key =
                ResourceKey.create(Registries.DIMENSION, dimId);

        ServerLevel level = server.getLevel(key);
        if (level == null) {
            p.sendSystemMessage(LegacyAmpersand.parse("&e&lTELEPORTS &7&l➤ &cError! Invalid dimension!"));
            return false;
        }


        p.teleportTo(level, pos.x + 0.5, pos.y, pos.z + 0.5, pos.yaw, pos.pitch);
        return true;
    }



    private WarpPos capture(final ServerPlayer p) {
        WarpPos wp = new WarpPos();
        wp.dimension = p.level().dimension().location().toString();
        wp.x = p.getX();
        wp.y = p.getY();
        wp.z = p.getZ();
        wp.yaw = p.getYRot();
        wp.pitch = p.getXRot();
        return wp;
    }

    private Map<UUID, Map<String, WarpPos>> snapshotHomes() {
        Map<UUID, Map<String, WarpPos>> out = new HashMap<>();
        for (var e : homes.entrySet()) {
            out.put(e.getKey(), new HashMap<>(e.getValue()));
        }
        return out;
    }

    private static String norm(final String s) {
        String x = (s == null ? "home" : s).trim().toLowerCase(Locale.ROOT);
        if (x.isBlank()) x = "home";
        return x;
    }



    public List<String> listHomes(final ServerPlayer p) {
        Map<String, WarpPos> m = homes.get(p.getUUID());
        if (m == null) return new ArrayList<>();
        return new ArrayList<>(m.keySet());
    }



    public List<String> listPlayerWarps() {
        return new ArrayList<>(pwarps.keySet());
    }

    public List<String> listWarps() {
        return new ArrayList<>(warps.keySet());
    }




    public boolean renamePlayerWarp(final ServerPlayer p, final String oldName, final String newName) {

        String oldKey = norm(oldName);
        String newKey = norm(newName);

        PlayerWarp w = pwarps.get(oldKey);
        if (w == null) return false;

        if (w.owner != null && !w.owner.equals(p.getUUID())) {
            p.sendSystemMessage(LegacyAmpersand.parse("&d&lPLAYER WARPS &7&l➤ &cError! You don't own that warp."));
            return false;
        }

        if (pwarps.containsKey(newKey)) {
            p.sendSystemMessage(LegacyAmpersand.parse("&d&lPLAYER WARPS &7&l➤ &cErrpr! A warp with that name already exists."));
            return false;
        }

        pwarps.remove(oldKey);

        w.name = newKey;
        pwarps.put(newKey, w);

        store.savePlayerWarps(new HashMap<>(pwarps));
        return true;
    }



    public boolean teleportWithBack(final MinecraftServer server, final ServerPlayer p, final WarpPos pos) {
        if (server == null || p == null || pos == null) return false;

        BackService.record(p);

        return teleportNow(server, p, pos);
    }



    public WarpPos getSpawnPos() {
        return spawn;
    }

    public WarpPos getWarpPos(String name) {
        if (name == null) return null;
        return warps.get(norm(name));
    }

    public WarpPos getHomePos(ServerPlayer p, String name) {
        if (p == null || name == null) return null;


        Map<String, WarpPos> m = homes.get(p.getUUID());
        if (m == null) return null;

        return m.get(norm(name));


    }

    public PlayerWarp getPlayerWarp(String name) {
        if (name == null) return null;
        return pwarps.get(norm(name));
    }



    public void savePlayerWarp(PlayerWarp warp) {
        if (warp == null || warp.name == null) return;

        String key = norm(warp.name);

        pwarps.put(key, warp);

        store.savePlayerWarps(new HashMap<>(pwarps));
    }





}
