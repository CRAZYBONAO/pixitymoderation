



package org.howie.pixity.moderation.neoforge.skills;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.howie.pixity.moderation.PixityModerationNeoForge;

public class CombatListener {

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent.Pre event) {

        var source = event.getSource().getEntity();
        var target = event.getEntity();

        float damage = event.getNewDamage();

        if (source instanceof ServerPlayer attacker) {

            var skills = PixityModerationNeoForge.SKILL_SERVICE.get(attacker.getUUID());

            int hunter = skills.getLevel(SkillType.HUNTER);
            int killer = skills.getLevel(SkillType.KILLER);

            double strength = PixityModerationNeoForge.STAT_ENGINE.getStrength(attacker);
            double critChance = PixityModerationNeoForge.STAT_ENGINE.getCritChance(attacker);
            double critDamage = PixityModerationNeoForge.STAT_ENGINE.getCritDamage(attacker);

            damage *= (1 + (strength * 0.001));

            if (Math.random() < (critChance / 100.0)) {
                damage *= (1 + (critDamage * 0.005));
            }


            if (PixityModerationNeoForge.ABILITY_ENGINE.isEnabled(attacker, AbilityType.DOUBLE_STRIKE)
                    && PixityModerationNeoForge.ABILITY_ENGINE.roll(attacker, hunter * 0.5)) {

                damage *= 2;
            }


            if (PixityModerationNeoForge.ABILITY_ENGINE.isEnabled(attacker, AbilityType.VAMPIRE)
                    && PixityModerationNeoForge.ABILITY_ENGINE.roll(attacker, killer * 0.25)) {

                attacker.heal(damage * 0.1f);
            }


            if (PixityModerationNeoForge.ABILITY_ENGINE.isEnabled(attacker, AbilityType.TRUE_STRIKE)
                    && PixityModerationNeoForge.ABILITY_ENGINE.roll(attacker, killer * 0.05)) {

                event.setNewDamage(damage * 1.5f);
                return;
            }
        }

        if (target instanceof ServerPlayer defender) {

            double defense = PixityModerationNeoForge.STAT_ENGINE.getDefense(defender);
            damage *= (1 - (defense * 0.001));
        }

        event.setNewDamage(damage);
    }

    @SubscribeEvent
    public static void onFall(LivingFallEvent event) {

        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        double agility = PixityModerationNeoForge.STAT_ENGINE.getAgility(player);
        float dmg = event.getDamageMultiplier();

        dmg *= (1 - (agility * 0.01));

        event.setDamageMultiplier(dmg);
    }

    @SubscribeEvent
    public static void onTick(ServerTickEvent.Post event) {

        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {

            double health = PixityModerationNeoForge.STAT_ENGINE.getHealth(player);
            double speed = PixityModerationNeoForge.STAT_ENGINE.getSpeed(player);

            double max = 20 * (1 + (health * 0.001));

            player.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH)
                    .setBaseValue(max);

            player.getAbilities().setWalkingSpeed((float) (0.1 * (1 + (speed * 0.005))));
        }
    }
}