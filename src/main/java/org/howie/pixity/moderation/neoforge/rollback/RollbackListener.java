package org.howie.pixity.moderation.neoforge.rollback;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public final class RollbackListener {

    private final RollbackService svc;

    public RollbackListener(RollbackService svc) {
        this.svc = svc;
    }

    @SubscribeEvent
    public void onPlace(BlockEvent.EntityPlaceEvent e) {
        if (!(e.getEntity() instanceof ServerPlayer p)) return;
        if (!(e.getLevel() instanceof ServerLevel lvl)) return;

        BlockPos pos = e.getPos();
        BlockState before = lvl.getBlockState(pos);

        String id = e.getPlacedBlock().getBlock().builtInRegistryHolder().key().location().toString();
        svc.recordPlace(lvl, pos, p, before, id);
    }

    @SubscribeEvent
    public void onBreak(BlockEvent.BreakEvent e) {
        if (!(e.getPlayer() instanceof ServerPlayer p)) return;
        if (!(e.getLevel() instanceof ServerLevel lvl)) return;

        BlockPos pos = e.getPos();
        BlockState before = e.getState();

        String id = before.getBlock().builtInRegistryHolder().key().location().toString();
        svc.recordBreak(lvl, pos, p, before, id);
    }

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent.RightClickBlock e) {
        if (!(e.getEntity() instanceof ServerPlayer p)) return;
        if (!(e.getLevel() instanceof ServerLevel lvl)) return;

        BlockPos pos = e.getPos();
        String id = lvl.getBlockState(pos).getBlock().builtInRegistryHolder().key().location().toString();

        svc.onOpenContainer(lvl, pos, p, id);
    }

    @SubscribeEvent
    public void onLogout(PlayerEvent.PlayerLoggedOutEvent e) {
        if (e.getEntity() instanceof ServerPlayer p) {
            svc.onCloseContainer(p.server, p);
        }
    }
}