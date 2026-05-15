package org.howie.pixity.moderation.neoforge.placeholder;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.neoforge.fishing.FishingDatabase;
import org.howie.pixity.moderation.neoforge.fishing.gui.FishingLeaderboardGui;

import java.sql.ResultSet;
import java.util.UUID;

public class FishingPlaceholders {

    public static void register() {

        registerTopPositions();

        registerTopEntries();

        registerXp();
    }

    /*
     * %fishing_top_position_bronze%
     */

    private static void registerTopPositions() {

        PlaceholderRegistry.registerRegex(

                "%fishing_top_position_(bronze|silver|gold|diamond|platinum|mythical|total)%",

                (context, match) -> {

                    ServerPlayer player =
                            context.player();

                    if (player == null)
                        return "#0";

                    FishingLeaderboardGui.Mode mode =
                            mode(
                                    match.group(1)
                            );

                    if (mode == null)
                        return "#0";

                    int rank =
                            FishingDatabase.getPlayerRank(

                                    player.getUUID(),

                                    mode
                            );

                    return "#" + rank;
                }
        );
    }

    /*
     * %fishing_top_bronze_1%
     */

    private static void registerTopEntries() {

        PlaceholderRegistry.registerRegex(

                "%fishing_top_(bronze|silver|gold|diamond|platinum|mythical|total)_(\\d+)%",

                (context, match) -> {

                    FishingLeaderboardGui.Mode mode =
                            mode(
                                    match.group(1)
                            );

                    if (mode == null)
                        return "Unknown";

                    int position;

                    try {

                        position =
                                Integer.parseInt(
                                        match.group(2)
                                );

                    } catch (Exception e) {

                        return "Unknown";
                    }

                    if (position <= 0)
                        return "Unknown";

                    try {

                        ResultSet rs =
                                FishingDatabase.getLeaderboard(
                                        mode,
                                        position
                                );

                        int current = 0;

                        while (rs.next()) {

                            current++;

                            if (current != position)
                                continue;

                            UUID uuid =
                                    UUID.fromString(
                                            rs.getString("uuid")
                                    );

                            int value =
                                    rs.getInt("value");

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
                                    + value;
                        }

                    } catch (Exception e) {

                        e.printStackTrace();
                    }

                    return "Unknown";
                }
        );
    }

    /*
     * %fishing_xp%
     * %fishing_xp_required%
     */

    private static void registerXp() {

        PlaceholderRegistry.registerPlayer(

                "%fishing_xp%",

                context -> {

                    ServerPlayer player =
                            context.player();

                    if (player == null)
                        return "0";

                    return String.valueOf(

                            FishingDatabase.getXP(
                                    player.getUUID()
                            )
                    );
                }
        );

        PlaceholderRegistry.registerPlayer(

                "%fishing_xp_required%",

                context -> {

                    ServerPlayer player =
                            context.player();

                    if (player == null)
                        return "0";

                    int level =
                            FishingDatabase.getLevel(
                                    player.getUUID()
                            );

                    return String.valueOf(

                            FishingDatabase.getXPRequired(
                                    level
                            )
                    );
                }
        );
    }

    private static FishingLeaderboardGui.Mode mode(
            String input
    ) {

        return switch (
                input.toLowerCase()
                ) {

            case "bronze" ->
                    FishingLeaderboardGui.Mode.BRONZE;

            case "silver" ->
                    FishingLeaderboardGui.Mode.SILVER;

            case "gold" ->
                    FishingLeaderboardGui.Mode.GOLD;

            case "diamond" ->
                    FishingLeaderboardGui.Mode.DIAMOND;

            case "platinum" ->
                    FishingLeaderboardGui.Mode.PLATINUM;

            case "mythical" ->
                    FishingLeaderboardGui.Mode.MYTHICAL;

            case "total" ->
                    FishingLeaderboardGui.Mode.TOTAL_FISH;

            default -> null;
        };
    }
}