package org.howie.pixity.moderation.neoforge.combat;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import net.minecraft.server.level.ServerPlayer;

public final class CombatTagListener {


    private final CombatTagService combat;

    public CombatTagListener(CombatTagService combat) {
        this.combat = combat;
    }

    @SubscribeEvent
    public void onDamage(LivingIncomingDamageEvent event) {

        if (event.getEntity() instanceof ServerPlayer player) {
            combat.tag(player);
        }

        if (event.getSource().getDirectEntity() instanceof ServerPlayer attacker) {
            combat.tag(attacker);
        }
    }


}
