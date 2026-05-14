package org.howie.pixity.moderation.neoforge.skills;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;
import org.howie.pixity.moderation.PixityModerationNeoForge;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber
public class EnchanterListener {

    private static final Map<UUID, Integer> lastXp = new HashMap<>();

    @SubscribeEvent
    public static void onXpChange(PlayerXpEvent.XpChange event) {

        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        UUID uuid = player.getUUID();

        int current = player.experienceLevel;
        int previous = lastXp.getOrDefault(uuid, current);


        lastXp.put(uuid, current);




        if (current >= previous) return;

        int lost = previous - current;

        if (lost <= 0) return;

        var skills = PixityModerationNeoForge.SKILL_SERVICE;
        var abilities = PixityModerationNeoForge.ABILITY_ENGINE;

        int level = skills.get(uuid).getLevel(SkillType.ENCHANTER);




        if (!abilities.isEnabled(player, AbilityType.ENCHANTERS_KEEP)) return;

        double chance = level * 0.25;

        if (!abilities.roll(player, chance)) return;


        int refund = Math.max(1, (int) (lost * 0.75));

        player.giveExperienceLevels(refund);

        player.displayClientMessage(
                org.howie.pixity.moderation.chat.TextFormatter.parse(
                        "<gradient:#FF0000:#FFFFFF:#FF0000:#FFFFFF:#FF0000>&lPIXITY SKILLS</gradient> &7&l➤ <gradient:#9537E5:#E918FF:#8E37E5>Enchanter's Keep</gradient> &arefunded &e" + refund + " &alevels!"
                ),
                true
        );
    }
}