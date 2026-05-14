package org.howie.pixity.moderation.neoforge.freeze;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityMountEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.UUID;

public final class FreezeListener {

    private final FreezeService freeze;

    public FreezeListener(FreezeService freeze) {
        this.freeze = freeze;
    }



    private final Map<UUID, Vec3> locked = new java.util.concurrent.ConcurrentHashMap<>();

    @SubscribeEvent
    public void onTick(PlayerTickEvent.Post e) {

        if (!(e.getEntity() instanceof ServerPlayer p)) return;

        UUID u = p.getUUID();

        if (!freeze.isFrozen(u)) {
            locked.remove(u);
            return;
        }

        Vec3 lock = locked.computeIfAbsent(u, id -> p.position());

        p.setDeltaMovement(0, 0, 0);
        p.setPos(lock.x, lock.y, lock.z);
        p.setOnGround(true);
        p.hurtMarked = true;
    }



    @SubscribeEvent
    public void onInteract(PlayerInteractEvent.RightClickItem e) {
        if (e.getEntity() instanceof ServerPlayer p) {
            if (freeze.isFrozen(p.getUUID())) {
                e.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent.RightClickBlock e) {
        if (e.getEntity() instanceof ServerPlayer p) {
            if (freeze.isFrozen(p.getUUID())) {
                e.setCanceled(true);
            }
        }
    }



    @SubscribeEvent
    public void onBreak(PlayerEvent.BreakSpeed e) {
        if (e.getEntity() instanceof ServerPlayer p) {
            if (freeze.isFrozen(p.getUUID())) {
                e.setNewSpeed(0f);
            }
        }
    }

    @SubscribeEvent
    public void onMount(EntityMountEvent event) {

        if (!(event.getEntityMounting() instanceof ServerPlayer player))
            return;

        if (!freeze.isFrozen(player.getUUID()))
            return;

        event.setCanceled(true);
    }
}