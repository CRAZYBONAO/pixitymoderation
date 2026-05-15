package org.howie.pixity.moderation.neoforge.placeholder;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.PixityModerationNeoForge;

import org.howie.pixity.moderation.neoforge.playtime.PlaytimeService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlaytimePlaceholders {

    public static void register() {

        registerPlaytime();

        registerTopPosition();

        registerTopEntries();
    }

    /*
     * %playtime%
     */

    private static void registerPlaytime() {

        PlaceholderRegistry.registerPlayer(

                "%playtime%",

                context -> {

                    ServerPlayer player =
                            context.player();

                    if (player == null)
                        return "0m";

                    long seconds =
                            PixityModerationNeoForge
                                    .PLAYTIME_SERVICE
                                    .getPlaytime(
                                            player.getUUID()
                                    );

                    return format(seconds);
                }
        );
    }

    /*
     * %playtime_top_position%
     */

    private static void registerTopPosition() {

        PlaceholderRegistry.registerPlayer(

                "%playtime_top_position%",

                context -> {

                    ServerPlayer player =
                            context.player();

                    if (player == null)
                        return "#0";

                    List<Map.Entry<UUID, Long>> top =
                            sorted();

                    UUID uuid =
                            player.getUUID();

                    for (int i = 0; i < top.size(); i++) {

                        if (top.get(i)
                                .getKey()
                                .equals(uuid)) {

                            return "#" + (i + 1);
                        }
                    }

                    return "#0";
                }
        );
    }

    /*
     * %playtime_top_1%
     */

    private static void registerTopEntries() {

        PlaceholderRegistry.registerRegex(

                "%playtime_top_(\\d+)%",

                (context, match) -> {

                    int position;

                    try {

                        position =
                                Integer.parseInt(
                                        match.group(1)
                                ) - 1;

                    } catch (Exception e) {

                        return "Unknown";
                    }

                    if (position < 0)
                        return "Unknown";

                    List<Map.Entry<UUID, Long>> top =
                            sorted();

                    if (position >= top.size())
                        return "Unknown";

                    Map.Entry<UUID, Long> entry =
                            top.get(position);

                    UUID uuid =
                            entry.getKey();

                    long seconds =
                            entry.getValue();

                    String name =
                            uuid.toString();

                    ServerPlayer viewer =
                            context.player();

                    MinecraftServer server =
                            viewer != null
                                    ? viewer.getServer()
                                    : null;

                    if (server != null
                            && server.getProfileCache() != null) {

                        name =
                                server.getProfileCache()
                                        .get(uuid)
                                        .map(p -> p.getName())
                                        .orElse(name);
                    }

                    return name
                            + " - "
                            + format(seconds);
                }
        );
    }

    private static List<Map.Entry<UUID, Long>> sorted() {

        PlaytimeService playtime =
                PixityModerationNeoForge
                        .PLAYTIME_SERVICE;

        List<Map.Entry<UUID, Long>> list =
                new ArrayList<>();

        list.addAll(
                playtime.getAll()
                        .entrySet()
        );

        list.sort(

                Comparator.comparingLong(
                        Map.Entry<UUID, Long>::getValue
                ).reversed()
        );

        return list;
    }

    private static String format(
            long seconds
    ) {

        long days =
                seconds / 86400;

        long hours =
                (seconds % 86400) / 3600;

        long minutes =
                (seconds % 3600) / 60;

        if (days > 0) {

            return days + "d "
                    + hours + "h";
        }

        if (hours > 0) {

            return hours + "h "
                    + minutes + "m";
        }

        return minutes + "m";
    }
}