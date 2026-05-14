package org.howie.pixity.moderation.neoforge.tp;

import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class SQLiteBackStore implements BackStore {

    private final Logger logger;
    private final String url;

    public SQLiteBackStore(Logger logger, String url) {
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
                CREATE TABLE IF NOT EXISTS back (
                    uuid TEXT PRIMARY KEY,
                    x REAL, y REAL, z REAL,
                    yaw REAL, pitch REAL,
                    dimension TEXT
                )
            """);
        } catch (Exception e) {
            throw new RuntimeException("Failed to init back table", e);
        }
    }

    @Override
    public Map<UUID, WarpPos> load() {
        Map<UUID, WarpPos> map = new HashMap<>();

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM back");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                UUID u = UUID.fromString(rs.getString("uuid"));

                WarpPos wp = new WarpPos();
                wp.x = rs.getDouble("x");
                wp.y = rs.getDouble("y");
                wp.z = rs.getDouble("z");
                wp.yaw = rs.getFloat("yaw");
                wp.pitch = rs.getFloat("pitch");
                wp.dimension = rs.getString("dimension");

                map.put(u, wp);
            }

        } catch (Exception e) {
            logger.error("Failed to load back locations", e);
        }

        return map;
    }

    @Override
    public void save(Map<UUID, WarpPos> map) {
        try (Connection conn = connect()) {

            PreparedStatement delete = conn.prepareStatement("DELETE FROM back");
            delete.executeUpdate();

            PreparedStatement insert = conn.prepareStatement("""
                INSERT INTO back (uuid, x, y, z, yaw, pitch, dimension)
                VALUES (?, ?, ?, ?, ?, ?, ?)
            """);

            for (var entry : map.entrySet()) {
                WarpPos wp = entry.getValue();

                insert.setString(1, entry.getKey().toString());
                insert.setDouble(2, wp.x);
                insert.setDouble(3, wp.y);
                insert.setDouble(4, wp.z);
                insert.setFloat(5, wp.yaw);
                insert.setFloat(6, wp.pitch);
                insert.setString(7, wp.dimension);

                insert.addBatch();
            }

            insert.executeBatch();

        } catch (Exception e) {
            logger.error("Failed to save back locations", e);
        }
    }
}