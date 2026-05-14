package org.howie.pixity.moderation.neoforge.afk;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.event.CommandEvent;
import net.neoforged.neoforge.event.ServerChatEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class AfkListener {

    private final AfkService afk;
    private int tick = 0;

    private final Map<UUID, double[]> lastPos = new ConcurrentHashMap<>();
    private final Map<UUID, float[]> lastRot = new ConcurrentHashMap<>();

    public AfkListener(final AfkService afk) {
        this.afk = afk;
    }

    @SubscribeEvent
    public void onServerTick(final ServerTickEvent.Post e) {

        tick++;
        if (tick % 20 != 0) return;

        MinecraftServer server = e.getServer();

        long now = System.currentTimeMillis();
        long threshold = Math.max(1, afk.config().autoAfkMinutes) * 60_000L;

        for (ServerPlayer p : server.getPlayerList().getPlayers()) {

            if (p.hasPermissions(2)) continue;

            UUID u = p.getUUID();



            double x = p.getX();
            double y = p.getY();
            double z = p.getZ();

            double[] last = lastPos.get(u);
            boolean moved = false;

            if (last != null) {
                double dx = x - last[0];
                double dy = y - last[1];
                double dz = z - last[2];
                moved = (dx * dx + dy * dy + dz * dz) > 0.0005;
            }

            lastPos.put(u, new double[]{x, y, z});


            float yaw = p.getYRot();
            float pitch = p.getXRot();

            float[] rot = lastRot.get(u);
            boolean rotated = false;

            if (rot != null) {
                float dyaw = Math.abs(yaw - rot[0]);
                float dpitch = Math.abs(pitch - rot[1]);
                rotated = (dyaw > 1f || dpitch > 1f);
            }

            lastRot.put(u, new float[]{yaw, pitch});



            boolean inWater = p.isInWater() || p.isInWaterRainOrBubble();


            if (moved || rotated || inWater) {


                if (afk.isAfk(u)) {
                    afk.setAfk(server, p, false, false);
                }

                afk.touch(p);
                continue;
            }


            long lastActive = afk.lastActive(u);

            if (!afk.isAfk(u) && (now - lastActive) >= threshold) {
                afk.setAfk(server, p, true, false);
            }
        }
    }


    @SubscribeEvent
    public void onChat(ServerChatEvent e) {
        if (e.getPlayer() instanceof ServerPlayer p) {
            if (afk.isAfk(p.getUUID())) {
                afk.setAfk(p.getServer(), p, false, false);
            }
            afk.touch(p);
        }
    }


    @SubscribeEvent
    public void onLogin(PlayerEvent.PlayerLoggedInEvent e) {
        if (e.getEntity() instanceof ServerPlayer p) afk.touch(p);
    }

    @SubscribeEvent
    public void onRespawn(PlayerEvent.PlayerRespawnEvent e) {
        if (e.getEntity() instanceof ServerPlayer p) afk.touch(p);
    }

    @SubscribeEvent
    public void onInteract(net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickItem e) {
        if (e.getEntity() instanceof ServerPlayer p) afk.touch(p);
    }

    @SubscribeEvent
    public void onInteractBlock(net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock e) {
        if (e.getEntity() instanceof ServerPlayer p) afk.touch(p);
    }

    @SubscribeEvent
    public void onLeftClickBlock(net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.LeftClickBlock e) {
        if (e.getEntity() instanceof ServerPlayer p) afk.touch(p);
    }

    @SubscribeEvent
    public void onContainer(PlayerContainerEvent.Open e) {
        if (e.getEntity() instanceof ServerPlayer p) afk.touch(p);
    }

    @SubscribeEvent
    public void onCommand(CommandEvent e) {
        Object ent = e.getParseResults().getContext().getSource().getEntity();
        if (ent instanceof ServerPlayer p) afk.touch(p);
    }
}