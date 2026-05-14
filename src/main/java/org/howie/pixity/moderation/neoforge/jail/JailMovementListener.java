package org.howie.pixity.moderation.neoforge.jail;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityMountEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.howie.pixity.moderation.neoforge.tp.WarpPos;

import net.minecraft.server.level.ServerPlayer;

public final class JailMovementListener {

    private final JailService jail;

    public JailMovementListener(JailService jail) {
        this.jail = jail;
    }

    @SubscribeEvent
    public void onTick(PlayerTickEvent.Post e) {

        if (!(e.getEntity() instanceof ServerPlayer p)) return;
        if (!jail.isJailed(p.getUUID())) return;

        p.setDeltaMovement(0, p.getDeltaMovement().y, 0);
    }

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent.RightClickItem e) {
        if (e.getEntity() instanceof ServerPlayer p) {
            if (jail.isJailed(p.getUUID())) {
                e.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent.RightClickBlock e) {
        if (e.getEntity() instanceof ServerPlayer p) {
            if (jail.isJailed(p.getUUID())) {
                e.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event) {

        if (!(event.getEntity() instanceof ServerPlayer player))
            return;

        if (!jail.isJailed(player.getUUID()))
            return;

        JailRecord rec = jail.getActive(player.getUUID());
        if (rec == null) return;

        WarpPos pos = jail.getJailPos(rec.jailName);
        if (pos == null) return;

        double dx = Math.abs(player.getX() - pos.x);
        double dy = Math.abs(player.getY() - pos.y);
        double dz = Math.abs(player.getZ() - pos.z);

        if (dx > 3 || dy > 3 || dz > 3) {

            player.teleportTo(
                    pos.x + 0.5,
                    pos.y,
                    pos.z + 0.5
            );

            player.setDeltaMovement(0,0,0);
        }

        player.getAbilities().flying = false;
        player.getAbilities().mayfly = false;
    }

    @SubscribeEvent
    public void onMount(EntityMountEvent event) {

        if (!(event.getEntityMounting() instanceof ServerPlayer player))
            return;

        if (!jail.isJailed(player.getUUID()))
            return;

        event.setCanceled(true);
    }
}