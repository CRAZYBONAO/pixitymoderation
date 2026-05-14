package org.howie.pixity.moderation.neoforge.jail;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import org.howie.pixity.moderation.neoforge.tp.WarpPos;

public final class JailEnforcementListener {

    private final JailService jail;

    public JailEnforcementListener(final JailService jail) {
        this.jail = jail;
    }

    @SubscribeEvent
    public void onTick(final ServerTickEvent.Post e) {

        for (ServerPlayer p : e.getServer().getPlayerList().getPlayers()) {

            if (!jail.isJailed(p.getUUID())) continue;

            JailRecord rec = jail.getActive(p.getUUID());
            if (rec == null) continue;

            WarpPos pos = jail.getJailPos(rec.jailName);
            if (pos == null) continue;

            Vec3 jailPos = new Vec3(pos.x, pos.y, pos.z);

            double maxDistance = jail.getConfig().maxDistance;

            if (p.position().distanceTo(jailPos) > maxDistance) {
                p.teleportTo(pos.x, pos.y, pos.z);
            }
        }
    }
}