package org.howie.pixity.moderation.neoforge.afk;

import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class SQLiteAfkStore {

    private final Logger logger;
    private final String url;

    public SQLiteAfkStore(Logger logger, String url) {
        this.logger = logger;
        this.url = url;
        init();
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(url);
    }

    private void init() {
        try (Connection conn = connect()) {

            conn.createStatement().execute("""
                CREATE TABLE IF NOT EXISTS afk (
                    uuid TEXT PRIMARY KEY,
                    is_afk INTEGER,
                    last_active BIGINT,
                    afk_since BIGINT
                )
            """);

        } catch (Exception e) {
            throw new RuntimeException("Failed to init AFK table", e);
        }
    }

    public Map<UUID, AfkData> load() {
        Map<UUID, AfkData> map = new HashMap<>();

        try (Connection conn = connect();
             ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM afk")) {

            while (rs.next()) {
                UUID u = UUID.fromString(rs.getString("uuid"));

                AfkData d = new AfkData();
                d.isAfk = rs.getInt("is_afk") == 1;
                d.lastActive = rs.getLong("last_active");
                d.afkSince = rs.getLong("afk_since");

                map.put(u, d);
            }

        } catch (Exception e) {
            logger.error("Failed to load AFK data", e);
        }

        return map;
    }

    public void save(Map<UUID, AfkData> map) {
        try (Connection conn = connect()) {

            conn.createStatement().execute("DELETE FROM afk");

            PreparedStatement ps = conn.prepareStatement("""
                INSERT INTO afk (uuid, is_afk, last_active, afk_since)
                VALUES (?, ?, ?, ?)
            """);

            for (var entry : map.entrySet()) {
                AfkData d = entry.getValue();

                ps.setString(1, entry.getKey().toString());
                ps.setInt(2, d.isAfk ? 1 : 0);
                ps.setLong(3, d.lastActive);
                ps.setLong(4, d.afkSince);

                ps.addBatch();
            }

            ps.executeBatch();

        } catch (Exception e) {
            logger.error("Failed to save AFK data", e);
        }
    }

    public static class AfkData {
        public boolean isAfk;
        public long lastActive;
        public long afkSince;
    }
}