package org.howie.pixity.moderation.neoforge.skills;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import org.howie.pixity.moderation.PixityModerationNeoForge;
import org.howie.pixity.moderation.chat.TextFormatter;

@EventBusSubscriber
public class PotionListener {

    private static final ThreadLocal<Boolean> APPLYING = ThreadLocal.withInitial(() -> false);

    @SubscribeEvent
    public static void onEffectApply(MobEffectEvent.Added event) {

        if (!(event.getEntity() instanceof ServerPlayer player)) return;


        if (APPLYING.get()) return;

        var abilities = PixityModerationNeoForge.ABILITY_ENGINE;

        if (!abilities.isEnabled(player, AbilityType.STEADY_HANDS)) return;

        var effect = event.getEffectInstance();




        if (!effect.isVisible()) return;

        double wisdom = PixityModerationNeoForge.STAT_ENGINE.getWisdom(player);

        double chance = Math.min(50, wisdom * 0.5);

        if (!abilities.roll(player, chance)) return;

        int duration = effect.getDuration();

        double bonusPercent = 0.20 + (wisdom * 0.005);
        int newDuration = (int) (duration * (1 + bonusPercent));


        if (newDuration <= duration) return;

        APPLYING.set(true);

        try {

            player.server.execute(() -> {


                player.removeEffect(effect.getEffect());

                player.addEffect(new MobEffectInstance(
                        effect.getEffect(),
                        newDuration,
                        effect.getAmplifier(),
                        effect.isAmbient(),
                        effect.isVisible(),
                        effect.showIcon()
                ));

                player.displayClientMessage(
                        TextFormatter.parse(
                                "<gradient:#FF0000:#FFFFFF:#FF0000>&lPIXITY SKILLS</gradient> &7➤ &dWisdom empowered your potion!"
                        ),
                        true
                );
            });

        } finally {
            APPLYING.set(false);
        }
    }
}