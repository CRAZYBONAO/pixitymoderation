package org.howie.pixity.moderation.neoforge.state;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.CommandEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;


public final class StateEnforcementListener {

    private final PlayerStateService state;

    public StateEnforcementListener(PlayerStateService state) {
        this.state = state;
    }

    @SubscribeEvent
    public void onMove(PlayerTickEvent.Post e) {
        if (!(e.getEntity() instanceof ServerPlayer p)) return;

        if (!state.canMove(p.getUUID())) {
            p.setDeltaMovement(0, 0, 0);
            p.teleportTo(p.getX(), p.getY(), p.getZ());
        }
    }

    @SubscribeEvent
    public void onInteractBlock(PlayerInteractEvent.RightClickBlock e) {
        if (!(e.getEntity() instanceof ServerPlayer p)) return;

        if (!state.canInteract(p.getUUID())) {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onInteractItem(PlayerInteractEvent.RightClickItem e) {
        if (!(e.getEntity() instanceof ServerPlayer p)) return;

        if (!state.canInteract(p.getUUID())) {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock e) {
        if (!(e.getEntity() instanceof ServerPlayer p)) return;

        if (!state.canInteract(p.getUUID())) {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onCommand(CommandEvent e) {
        if (!(e.getParseResults().getContext().getSource().getEntity() instanceof ServerPlayer p)) return;

        if (!state.canCommand(p.getUUID())) {
            e.setCanceled(true);
            p.sendSystemMessage(LegacyAmpersand.parse("&c&lPUNISHMENTS &7&l➤ &cError! You cannot use commands right now."));
        }
    }


    @SubscribeEvent
    public void onDamage(LivingIncomingDamageEvent e) {
        if (!(e.getEntity() instanceof ServerPlayer p)) return;

        if (state.isGod(p.getUUID())) {
            e.setCanceled(true);
        }
    }
}