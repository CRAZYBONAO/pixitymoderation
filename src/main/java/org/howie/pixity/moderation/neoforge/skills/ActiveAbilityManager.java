package org.howie.pixity.moderation.neoforge.skills;

import net.minecraft.server.level.ServerPlayer;
import org.howie.pixity.moderation.chat.TextFormatter;

import java.util.*;

public class ActiveAbilityManager {

    private final Map<UUID, Map<AbilityType, Long>> active = new HashMap<>();
    private final Map<UUID, Map<AbilityType, Long>> cooldown = new HashMap<>();


    private final Map<UUID, Set<AbilityType>> expired = new HashMap<>();

    private static final String PREFIX =
            "<gradient:#FF0000:#FFFFFF:#FF0000:#FFFFFF:#FF0000>&lPIXITY SKILLS</gradient> &7&l➤ ";




    public static String formatTime(long ms) {

        long s = ms / 1000;

        long h = s / 3600;
        long m = (s % 3600) / 60;
        long sec = s % 60;

        if (h > 0) return h + "h " + m + "m";
        if (m > 0) return m + "m " + sec + "s";
        return sec + "s";
    }




    public boolean isActive(ServerPlayer player, AbilityType ability) {
        return getRemainingActive(player, ability) > 0;
    }

    public boolean isOnCooldown(ServerPlayer player, AbilityType ability) {
        return getRemainingCooldown(player, ability) > 0;
    }

    public long getRemainingActive(ServerPlayer player, AbilityType ability) {
        return active
                .getOrDefault(player.getUUID(), Map.of())
                .getOrDefault(ability, 0L) - System.currentTimeMillis();
    }

    public long getRemainingCooldown(ServerPlayer player, AbilityType ability) {
        return cooldown
                .getOrDefault(player.getUUID(), Map.of())
                .getOrDefault(ability, 0L) - System.currentTimeMillis();
    }




    public void activate(ServerPlayer player, AbilityType ability, long durationMs, long cooldownMs) {

        long now = System.currentTimeMillis();

        active.computeIfAbsent(player.getUUID(), k -> new HashMap<>())
                .put(ability, now + durationMs);

        cooldown.computeIfAbsent(player.getUUID(), k -> new HashMap<>())
                .put(ability, now + cooldownMs);

        expired.computeIfAbsent(player.getUUID(), k -> new HashSet<>()).remove(ability);
    }




    public void sendCooldownMessage(ServerPlayer player, AbilityType ability) {

        long remaining = getRemainingCooldown(player, ability);

        if (remaining <= 0) return;

        player.sendSystemMessage(TextFormatter.parse(
                PREFIX +
                        AbilityColor.get(ability) + formatName(ability) + "</gradient>" +
                        " &7is on cooldown for &e" + formatTime(remaining)
        ));
    }




    public void tick(ServerPlayer player) {

        UUID uuid = player.getUUID();

        var playerActive = active.getOrDefault(uuid, Map.of());

        for (var entry : playerActive.entrySet()) {

            AbilityType ability = entry.getKey();
            long remaining = entry.getValue() - System.currentTimeMillis();




            if (remaining > 0) {

                player.displayClientMessage(
                        TextFormatter.parse(
                                "<gradient:#FF0000:#FFFFFF:#FF0000:#FFFFFF:#FF0000>&lPIXITY SKILLS</gradient> " +
                                        "&7➤ " +
                                        AbilityColor.get(ability) + formatName(ability) + "</gradient>" +
                                        " &7(" + formatTime(remaining) + ")"
                        ),
                        true
                );

                continue;
            }




            if (!expired.computeIfAbsent(uuid, k -> new HashSet<>()).contains(ability)) {

                expired.get(uuid).add(ability);

                player.sendSystemMessage(TextFormatter.parse(
                        PREFIX +
                                AbilityColor.get(ability) + formatName(ability) + "</gradient>" +
                                " &7has expired."
                ));
            }
        }
    }




    private String formatName(AbilityType ability) {

        String[] parts = ability.name().toLowerCase().split("_");

        StringBuilder result = new StringBuilder();

        for (String part : parts) {
            result.append(Character.toUpperCase(part.charAt(0)))
                    .append(part.substring(1))
                    .append(" ");
        }

        return result.toString().trim();
    }
}