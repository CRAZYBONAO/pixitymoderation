package org.howie.pixity.moderation.neoforge.tp;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.Logger;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public final class BackService {

    public static final String PERM_BACK = "pixity.back";

    private static volatile boolean INIT = false;
    private static BackStore store;
    private static Logger logger;

    private static final Map<UUID, WarpPos> last = new ConcurrentHashMap<>();

    private BackService() {}

    public static void init(final Logger log, final BackStore s) {
        if (INIT) return;
        INIT = true;
        logger = log;
        store = s;
        last.putAll(store.load());
    }

    public static void record(final ServerPlayer p) {
        if (!INIT || p == null) return;

        WarpPos wp = new WarpPos();
        ResourceLocation rl = p.level().dimension().location();
        wp.dimension = rl.toString();
        wp.x = p.getX();
        wp.y = p.getY();
        wp.z = p.getZ();
        wp.yaw = p.getYRot();
        wp.pitch = p.getXRot();

        last.put(p.getUUID(), wp);
        store.save(new java.util.HashMap<>(last));
    }



    public static WarpPos get(final UUID u) {
        return u == null ? null : last.get(u);
    }

    public static boolean back(final MinecraftServer server, final ServerPlayer p, final TpService tp) {
        if (server == null || p == null || tp == null) return false;
        WarpPos wp = last.get(p.getUUID());
        if (wp == null) {
            p.sendSystemMessage(LegacyAmpersand.parse("&e&lBACK &7&l➤ &cError! No /back location recorded yet."));
            return false;
        }
        record(p);
        return tp.teleportWithBack(server, p, wp);
    }
}
