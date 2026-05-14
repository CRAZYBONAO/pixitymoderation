package org.howie.pixity.moderation.chat;

import java.nio.file.Path;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NickDatabase {

    private final String url;

    public NickDatabase(Path folder) {
        this.url = "jdbc:sqlite:" + folder.resolve("pixity.db");

        init();
    }

    private void init() {
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS nicknames (
                    uuid TEXT PRIMARY KEY,
                    nickname TEXT NOT NULL
                )
            """);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<UUID, String> loadAll() {

        Map<UUID, String> map = new HashMap<>();

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps =
                     conn.prepareStatement("SELECT uuid, nickname FROM nicknames");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                UUID uuid = UUID.fromString(rs.getString("uuid"));
                String nick = rs.getString("nickname");

                map.put(uuid, nick);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return map;
    }

    public void save(UUID uuid, String nick) {

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps =
                     conn.prepareStatement("""
                        INSERT INTO nicknames(uuid, nickname)
                        VALUES(?, ?)
                        ON CONFLICT(uuid) DO UPDATE SET nickname=excluded.nickname
                     """)) {

            ps.setString(1, uuid.toString());
            ps.setString(2, nick);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(UUID uuid) {

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps =
                     conn.prepareStatement("DELETE FROM nicknames WHERE uuid=?")) {

            ps.setString(1, uuid.toString());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}