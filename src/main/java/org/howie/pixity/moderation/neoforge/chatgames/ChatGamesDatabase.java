package org.howie.pixity.moderation.neoforge.chatgames;

import java.sql.*;
import java.util.*;

public class ChatGamesDatabase {

    private static Connection conn;


    public static void init() {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:config/pixity_chatgames.db");

            try (Statement st = conn.createStatement()) {
                st.execute("""
    CREATE TABLE IF NOT EXISTS leaderboard (
        uuid TEXT PRIMARY KEY,
        best_streak INTEGER NOT NULL,
        current_streak INTEGER NOT NULL,
        wins INTEGER NOT NULL
    );
""");
            }

            try (Statement st = conn.createStatement()) {
                st.execute("""
        CREATE TABLE IF NOT EXISTS milestone_claims (
            uuid TEXT NOT NULL,
            milestone INTEGER NOT NULL,
            first_claimed_by TEXT,
            PRIMARY KEY (uuid, milestone)
        );
    """);
            }

            System.out.println("[ChatGames] Database initialized");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean hasClaimed(UUID uuid, int milestone) {

        try {
            PreparedStatement ps = conn.prepareStatement("""
            SELECT 1 FROM milestone_claims
            WHERE uuid = ? AND milestone = ?
        """);

            ps.setString(1, uuid.toString());
            ps.setInt(2, milestone);

            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static UUID getFirstClaimer(int milestone) {

        try {
            PreparedStatement ps = conn.prepareStatement("""
            SELECT first_claimed_by FROM milestone_claims
            WHERE milestone = ? AND first_claimed_by IS NOT NULL
            LIMIT 1
        """);

            ps.setInt(1, milestone);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return UUID.fromString(rs.getString("first_claimed_by"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean claim(UUID uuid, int milestone) {

        try {

            if (hasClaimed(uuid, milestone)) return false;

            UUID first = getFirstClaimer(milestone);

            PreparedStatement ps = conn.prepareStatement("""
            INSERT INTO milestone_claims (uuid, milestone, first_claimed_by)
            VALUES (?, ?, ?)
        """);

            ps.setString(1, uuid.toString());
            ps.setInt(2, milestone);
            ps.setString(3, first == null ? uuid.toString() : null);

            ps.executeUpdate();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    public static void updateStats(UUID uuid, int streak) {

        try {
            PreparedStatement ps = conn.prepareStatement("""
            INSERT INTO leaderboard (uuid, best_streak, current_streak, wins)
            VALUES (?, ?, ?, 1)
            ON CONFLICT(uuid) DO UPDATE SET
                wins = wins + 1,
                current_streak = excluded.current_streak,
                best_streak = MAX(best_streak, excluded.best_streak);
        """);

            ps.setString(1, uuid.toString());
            ps.setInt(2, streak);
            ps.setInt(3, streak);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void resetStreak(UUID uuid) {
        try {
            PreparedStatement ps = conn.prepareStatement("""
            UPDATE leaderboard SET current_streak = 0 WHERE uuid = ?
        """);

            ps.setString(1, uuid.toString());
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Integer> getStats(UUID uuid) {

        Map<String, Integer> stats = new HashMap<>();

        try {
            PreparedStatement ps = conn.prepareStatement("""
            SELECT best_streak, current_streak, wins
            FROM leaderboard WHERE uuid = ?
        """);

            ps.setString(1, uuid.toString());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                stats.put("best", rs.getInt("best_streak"));
                stats.put("current", rs.getInt("current_streak"));
                stats.put("wins", rs.getInt("wins"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return stats;
    }


    public static List<Map.Entry<UUID, Integer>> getTop(int limit) {

        List<Map.Entry<UUID, Integer>> list = new ArrayList<>();

        try {
            PreparedStatement ps = conn.prepareStatement("""
                SELECT uuid, best_streak
                FROM leaderboard
                ORDER BY best_streak DESC
                LIMIT ?
            """);

            ps.setInt(1, limit);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                int streak = rs.getInt("best_streak");

                list.add(Map.entry(uuid, streak));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}