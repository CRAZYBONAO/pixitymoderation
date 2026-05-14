package org.howie.pixity.moderation.neoforge.skills;

import net.minecraft.server.level.ServerPlayer;
import org.howie.pixity.moderation.chat.TextFormatter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SkillXpBuffer {

    private static final Map<UUID, Map<SkillType, Double>> buffer = new ConcurrentHashMap<>();
    private static final Map<UUID, Long> lastUpdate = new ConcurrentHashMap<>();




    public static void add(ServerPlayer player, SkillType type, double amount) {

        UUID uuid = player.getUUID();

        buffer.computeIfAbsent(uuid, k -> new HashMap<>())
                .merge(type, amount, Double::sum);

        lastUpdate.put(uuid, System.currentTimeMillis());
    }




    public static void flush(ServerPlayer player) {

        UUID uuid = player.getUUID();

        Map<SkillType, Double> data = buffer.get(uuid);

        if (data == null || data.isEmpty()) return;

        StringBuilder msg = new StringBuilder();

        for (Map.Entry<SkillType, Double> entry : data.entrySet()) {

            msg.append("<green>+")
                    .append(String.format("%.0f", entry.getValue()))
                    .append("</green> <yellow>")
                    .append(format(entry.getKey()))
                    .append(" XP</yellow>  ");
        }

        player.displayClientMessage(
                TextFormatter.parse(msg.toString()),
                true
        );

        buffer.remove(uuid);
        lastUpdate.remove(uuid);
    }




    public static void tick(ServerPlayer player) {

        UUID uuid = player.getUUID();

        Long last = lastUpdate.get(uuid);

        if (last == null) return;

        if (System.currentTimeMillis() - last > 1000) {
            flush(player);
        }
    }

    private static String format(SkillType type) {
        return type.name().substring(0, 1) + type.name().substring(1).toLowerCase();
    }
}