package org.howie.pixity.moderation.neoforge.rules;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.minecraft.server.level.ServerPlayer;

public final class RulesJoinListener {

    private final RulesService rules;

    public RulesJoinListener(final RulesService rules) {
        this.rules = rules;
    }

    @SubscribeEvent
    public void onJoin(final PlayerEvent.PlayerLoggedInEvent e) {
        if (e.getEntity() instanceof ServerPlayer p) {
            rules.maybeShowOnFirstJoin(p);
        }
    }
}
