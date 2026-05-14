package org.howie.pixity.moderation.neoforge.skills;

import net.minecraft.server.level.ServerPlayer;
import org.howie.pixity.moderation.PixityModerationNeoForge;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AbilityEngine {

    private final SkillService skills;


    private final Map<UUID, Map<AbilityType, Long>> cooldowns = new HashMap<>();

    public AbilityEngine(SkillService skills) {
        this.skills = skills;
    }




    public boolean isEnabled(ServerPlayer player, AbilityType ability) {
        return skills.isEnabled(player.getUUID(), ability.name().toLowerCase());
    }





    public boolean roll(ServerPlayer player, double baseChance) {

        double luck = PixityModerationNeoForge.STAT_ENGINE.getLuck(player);

        double finalChance = baseChance + (luck * 0.05);

        return Math.random() * 100 <= finalChance;
    }






    public boolean isOnCooldown(ServerPlayer player, AbilityType ability) {

        long now = System.currentTimeMillis();

        return cooldowns
                .getOrDefault(player.getUUID(), Map.of())
                .getOrDefault(ability, 0L) > now;
    }

    public void setCooldown(ServerPlayer player, AbilityType ability, long ms) {

        cooldowns
                .computeIfAbsent(player.getUUID(), k -> new HashMap<>())
                .put(ability, System.currentTimeMillis() + ms);
    }
}
