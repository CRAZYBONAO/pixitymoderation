package org.howie.pixity.moderation.neoforge.alts.smart;

import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.*;

public final class SQLiteSmartAltStore {

    private final Logger logger;
    private final String url;

    public static class IpEntry {
        public final String ip;
        public final long ts;

        public IpEntry(String ip, long ts) {
            this.ip = ip;
            this.ts = ts;
        }
    }

    public SQLiteSmartAltStore(Logger logger, String url) {
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
                CREATE TABLE IF NOT EXISTS smart_alts (
                    uuid TEXT,
                    ip TEXT,
                    ts BIGINT
                )
            """);

        } catch (Exception e) {
            throw new RuntimeException("Failed to init smart_alts table", e);
        }
    }

    public Map<UUID, List<IpEntry>> load() {

        Map<UUID, List<IpEntry>> map = new HashMap<>();

        try (Connection conn = connect();
             ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM smart_alts")) {

            while (rs.next()) {

                UUID u = UUID.fromString(rs.getString("uuid"));

                IpEntry entry = new IpEntry(
                        rs.getString("ip"),
                        rs.getLong("ts")
                );

                map.computeIfAbsent(u, k -> new ArrayList<>()).add(entry);
            }

        } catch (Exception e) {
            logger.error("Failed to load smart alts", e);
        }

        return map;
    }

    public void save(Map<UUID, List<IpEntry>> map) {

        try (Connection conn = connect()) {

            conn.createStatement().execute("DELETE FROM smart_alts");

            PreparedStatement ps = conn.prepareStatement("""
                INSERT INTO smart_alts (uuid, ip, ts)
                VALUES (?, ?, ?)
            """);

            for (var entry : map.entrySet()) {

                UUID u = entry.getKey();

                for (IpEntry ip : entry.getValue()) {

                    ps.setString(1, u.toString());
                    ps.setString(2, ip.ip);
                    ps.setLong(3, ip.ts);

                    ps.addBatch();
                }
            }

            ps.executeBatch();

        } catch (Exception e) {
            logger.error("Failed to save smart alts", e);
        }
    }
}