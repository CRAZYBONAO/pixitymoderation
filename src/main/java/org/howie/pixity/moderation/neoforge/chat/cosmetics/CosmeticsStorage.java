package org.howie.pixity.moderation.neoforge.chat.cosmetics;

import java.sql.*;
import java.util.*;

public class CosmeticsStorage {

    private final Connection connection;

    public CosmeticsStorage(String file) {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + file);
            createTables();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void createTables() throws SQLException {

        try (Statement s = connection.createStatement()) {

            s.execute("""
                CREATE TABLE IF NOT EXISTS cosmetics_unlocked (
                    uuid TEXT,
                    key TEXT
                )
            """);

            s.execute("""
                CREATE TABLE IF NOT EXISTS cosmetics_active (
                    uuid TEXT PRIMARY KEY,
                    type TEXT,
                    value1 TEXT,
                    value2 TEXT
                )
            """);
        }
    }



    public void unlock(UUID uuid, String key) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO cosmetics_unlocked(uuid,key) VALUES(?,?)"
        )) {
            ps.setString(1, uuid.toString());
            ps.setString(2, key);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Set<String> getUnlocked(UUID uuid) {

        Set<String> set = new HashSet<>();

        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT key FROM cosmetics_unlocked WHERE uuid=?"
        )) {
            ps.setString(1, uuid.toString());

            ResultSet rs = ps.executeQuery();

            while (rs.next())
                set.add(rs.getString("key"));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return set;
    }



    public void setColor(UUID uuid, String color) {
        set(uuid, "color", color, null);
    }

    public void setGradient(UUID uuid, String start, String end) {
        set(uuid, "gradient", start, end);
    }

    public void setAnimated(UUID uuid, String key) {
        set(uuid, "animated", key, null);
    }

    private void set(UUID uuid, String type, String v1, String v2) {
        try (PreparedStatement ps = connection.prepareStatement(
                "REPLACE INTO cosmetics_active(uuid,type,value1,value2) VALUES(?,?,?,?)"
        )) {
            ps.setString(1, uuid.toString());
            ps.setString(2, type);
            ps.setString(3, v1);
            ps.setString(4, v2);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Active loadActive(UUID uuid) {

        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM cosmetics_active WHERE uuid=?"
        )) {

            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Active(
                        rs.getString("type"),
                        rs.getString("value1"),
                        rs.getString("value2")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public record Active(String type, String v1, String v2) {}
}