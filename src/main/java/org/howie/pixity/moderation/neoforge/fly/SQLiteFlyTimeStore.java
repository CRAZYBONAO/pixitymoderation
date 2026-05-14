package org.howie.pixity.moderation.neoforge.fly;

import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class SQLiteFlyTimeStore {

    private final Logger logger;
    private final String url;

    public SQLiteFlyTimeStore(Logger logger, String url) {
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
                CREATE TABLE IF NOT EXISTS flytime (
                    uuid TEXT PRIMARY KEY,
                    seconds BIGINT
                )
            """);

        } catch (Exception e) {
            throw new RuntimeException("Failed to init flytime table", e);
        }
    }

    public Map<UUID, Long> load() {
        Map<UUID, Long> map = new HashMap<>();

        try (Connection conn = connect();
             ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM flytime")) {

            while (rs.next()) {
                UUID u = UUID.fromString(rs.getString("uuid"));
                long time = rs.getLong("seconds");
                map.put(u, time);
            }

        } catch (Exception e) {
            logger.error("Failed to load flytime", e);
        }

        return map;
    }

    public void save(Map<UUID, Long> map) {
        try (Connection conn = connect()) {

            conn.createStatement().execute("DELETE FROM flytime");

            PreparedStatement ps = conn.prepareStatement("""
                INSERT INTO flytime (uuid, seconds)
                VALUES (?, ?)
            """);

            for (var entry : map.entrySet()) {
                ps.setString(1, entry.getKey().toString());
                ps.setLong(2, entry.getValue());
                ps.addBatch();
            }

            ps.executeBatch();

        } catch (Exception e) {
            logger.error("Failed to save flytime", e);
        }
    }
}