package org.howie.pixity.moderation.neoforge.alts;

import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.*;

public final class SQLiteAltsStore {

    private final Logger logger;
    private final String url;

    public SQLiteAltsStore(Logger logger, String url) {
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
                CREATE TABLE IF NOT EXISTS alts (
                    uuid TEXT NOT NULL,
                    alt TEXT NOT NULL,
                    PRIMARY KEY (uuid, alt)
                )
            """);

        } catch (Exception e) {
            throw new RuntimeException("Failed to init Alts DB", e);
        }
    }



    public Map<UUID, Set<UUID>> load() {

        Map<UUID, Set<UUID>> map = new HashMap<>();

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM alts");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                UUID u = UUID.fromString(rs.getString("uuid"));
                UUID alt = UUID.fromString(rs.getString("alt"));

                map.computeIfAbsent(u, k -> new HashSet<>()).add(alt);
            }

        } catch (Exception e) {
            logger.error("Failed to load alts", e);
        }

        return map;
    }



    public void save(Map<UUID, Set<UUID>> map) {

        try (Connection conn = connect()) {

            conn.createStatement().execute("DELETE FROM alts");

            PreparedStatement ps = conn.prepareStatement("""
                INSERT INTO alts (uuid, alt)
                VALUES (?, ?)
            """);

            for (var entry : map.entrySet()) {

                UUID u = entry.getKey();
                Set<UUID> set = entry.getValue();

                if (u == null || set == null) continue;

                for (UUID alt : set) {
                    if (alt == null) continue;

                    ps.setString(1, u.toString());
                    ps.setString(2, alt.toString());
                    ps.addBatch();
                }
            }

            ps.executeBatch();

        } catch (Exception e) {
            logger.error("Failed to save alts", e);
        }
    }
}