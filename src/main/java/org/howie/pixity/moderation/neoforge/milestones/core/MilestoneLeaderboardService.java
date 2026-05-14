package org.howie.pixity.moderation.neoforge.milestones.core;

import org.howie.pixity.moderation.neoforge.stats.PlayerStatsDatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MilestoneLeaderboardService {





    private static final String URL =
            "jdbc:sqlite:config/pixity/player_stats.db";





    public record LeaderboardEntry(
            UUID uuid,
            String username,
            long value
    ) {}





    public static List<LeaderboardEntry> getTop(
            String column,
            int limit
    ) {

        List<LeaderboardEntry> list =
                new ArrayList<>();

        try (
                Connection conn =
                        DriverManager.getConnection(URL)
        ) {

            PreparedStatement ps =
                    conn.prepareStatement(
                            "SELECT uuid, username, " + column +
                                    " FROM player_stats " +
                                    "ORDER BY " + column +
                                    " DESC LIMIT ?"
                    );

            ps.setInt(1, limit);

            ResultSet rs =
                    ps.executeQuery();

            while (rs.next()) {

                String uuidString =
                        rs.getString("uuid");

                UUID uuid;

                try {

                    uuid =
                            UUID.fromString(uuidString);

                } catch (Exception e) {

                    uuid =
                            new UUID(0, 0);
                }

                list.add(
                        new LeaderboardEntry(
                                uuid,
                                rs.getString("username"),
                                rs.getLong(column)
                        )
                );
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return list;
    }





    public static int getPlace(
            UUID uuid,
            String column
    ) {

        long playerValue =
                PlayerStatsDatabase.get(
                        uuid,
                        column
                );

        try (
                Connection conn =
                        DriverManager.getConnection(URL)
        ) {

            PreparedStatement ps =
                    conn.prepareStatement(
                            "SELECT COUNT(*) + 1 AS place " +
                                    "FROM player_stats " +
                                    "WHERE " + column + " > ?"
                    );

            ps.setLong(1, playerValue);

            ResultSet rs =
                    ps.executeQuery();

            if (rs.next()) {

                return rs.getInt("place");
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return -1;
    }





    public static long getValue(
            UUID uuid,
            String column
    ) {

        return PlayerStatsDatabase.get(
                uuid,
                column
        );
    }





    public static LeaderboardEntry getTopPlayer(
            String column
    ) {

        List<LeaderboardEntry> top =
                getTop(column, 1);

        if (top.isEmpty()) {
            return null;
        }

        return top.getFirst();
    }





    public static String formatPlace(
            int place
    ) {

        if (place <= 0) {
            return "#?";
        }

        return "#" + place;
    }





    public static String safeName(
            LeaderboardEntry entry
    ) {

        if (entry == null) {
            return "None";
        }

        return entry.username();
    }

    public static long safeValue(
            LeaderboardEntry entry
    ) {

        if (entry == null) {
            return 0;
        }

        return entry.value();
    }
}