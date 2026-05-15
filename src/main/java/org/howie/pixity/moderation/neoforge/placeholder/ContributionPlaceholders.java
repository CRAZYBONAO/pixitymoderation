package org.howie.pixity.moderation.neoforge.placeholder;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.PixityModerationNeoForge;

import org.howie.pixity.moderation.neoforge.contribution.ContributionData;
import org.howie.pixity.moderation.neoforge.contribution.ContributionService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ContributionPlaceholders {

    public static void register() {

        registerCurrent();

        registerLifetime();

        registerTopPosition();

        registerTopEntries();
    }

    /*
     * %contribution%
     */

    private static void registerCurrent() {

        PlaceholderRegistry.registerPlayer(

                "%contribution%",

                context -> {

                    ServerPlayer player =
                            context.player();

                    if (player == null)
                        return "$0";

                    double amount =
                            PixityModerationNeoForge
                                    .CONTRIBUTION_SERVICE
                                    .current(
                                            player.getUUID()
                                    );

                    return "$" + format(amount);
                }
        );
    }

    /*
     * %contribution_lifetime%
     */

    private static void registerLifetime() {

        PlaceholderRegistry.registerPlayer(

                "%contribution_lifetime%",

                context -> {

                    ServerPlayer player =
                            context.player();

                    if (player == null)
                        return "$0";

                    double amount =
                            PixityModerationNeoForge
                                    .CONTRIBUTION_SERVICE
                                    .lifetime(
                                            player.getUUID()
                                    );

                    return "$" + format(amount);
                }
        );
    }

    /*
     * %contribution_top_position%
     */

    private static void registerTopPosition() {

        PlaceholderRegistry.registerPlayer(

                "%contribution_top_position%",

                context -> {

                    ServerPlayer player =
                            context.player();

                    if (player == null)
                        return "#0";

                    List<Map.Entry<UUID, ContributionData>> top =
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
     * %contribution_top_1%
     */

    private static void registerTopEntries() {

        PlaceholderRegistry.registerRegex(

                "%contribution_top_(\\d+)%",

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

                    List<Map.Entry<UUID, ContributionData>> top =
                            sorted();

                    if (position >= top.size())
                        return "Unknown";

                    Map.Entry<UUID, ContributionData> entry =
                            top.get(position);

                    UUID uuid =
                            entry.getKey();

                    ContributionData data =
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
                            + " - $"
                            + format(
                            data.getCurrent()
                    );
                }
        );
    }

    private static List<
            Map.Entry<
                    UUID,
                    ContributionData
                    >
            > sorted() {

        ContributionService contributions =
                PixityModerationNeoForge
                        .CONTRIBUTION_SERVICE;

        List<
                Map.Entry<
                        UUID,
                        ContributionData
                        >
                > list =
                new ArrayList<>(

                        contributions.getAll()
                                .entrySet()
                );

        list.sort(

                Comparator.comparingDouble(

                        (Map.Entry<
                                UUID,
                                ContributionData
                                > entry)

                                -> entry.getValue()
                                .getCurrent()

                ).reversed()
        );

        return list;
    }

    private static String format(
            double value
    ) {

        return String.format(
                "%,.2f",
                value
        );
    }
}