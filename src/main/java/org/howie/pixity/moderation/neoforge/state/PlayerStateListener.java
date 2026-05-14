package org.howie.pixity.moderation.neoforge.state;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.neoforge.rank.RankService;

public final class PlayerStateListener {


    private final PlayerStateManager states;
    private final RankService ranks;

    public PlayerStateListener(PlayerStateManager states, RankService ranks) {
        this.states = states;
        this.ranks = ranks;
    }

    private boolean has(ServerPlayer p, String perm) {
        return ranks != null && ranks.hasPerm(p, perm);
    }

    @SubscribeEvent
    public void onJoin(PlayerEvent.PlayerLoggedInEvent e) {
        if (e.getEntity() instanceof ServerPlayer p) {
            states.applyAllOnJoin(p.server, p);
        }
    }

    @SubscribeEvent
    public void onStartTracking(PlayerEvent.StartTracking e) {
        if (!(e.getEntity() instanceof ServerPlayer viewer)) return;
        if (!(e.getTarget() instanceof ServerPlayer target)) return;

        if (states.isVanished(target.getUUID())) {
            if (!has(viewer, PlayerStateManager.PERM_VANISH_SEE)) {
                viewer.connection.send(
                        new net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket(target.getId())
                );
            }
        }
    }


}
