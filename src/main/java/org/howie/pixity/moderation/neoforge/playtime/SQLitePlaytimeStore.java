package org.howie.pixity.moderation.neoforge.playtime;

import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class SQLitePlaytimeStore {

    private final Logger logger;
    private final String url;

    public SQLitePlaytimeStore(Logger logger, String url) {
        this.logger = logger;
        this.url = url;
        init();
    }

    private void init() {
        try (Connection c = DriverManager.getConnection(url);
             Statement s = c.createStatement()) {

            s.execute(
                    "CREATE TABLE IF NOT EXISTS playtime (" +
                            "uuid TEXT PRIMARY KEY," +
                            "seconds INTEGER NOT NULL" +
                            ")"
            );

            s.execute(
                    "CREATE TABLE IF NOT EXISTS playtime_claims (" +
                            "uuid TEXT NOT NULL," +
                            "level INTEGER NOT NULL," +
                            "PRIMARY KEY(uuid, level)" +
                            ")"
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to init SQLitePlaytimeStore", e);
        }
    }

    public Map<UUID, Long> load() {

        Map<UUID, Long> map = new HashMap<>();

        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement(
                     "SELECT uuid, seconds FROM playtime"
             );
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                long seconds = rs.getLong("seconds");
                map.put(uuid, seconds);
            }

        } catch (Exception e) {
            logger.error("Failed loading playtime", e);
        }

        return map;
    }



    public void save(Map<UUID, Long> map) {

        try (Connection c = DriverManager.getConnection(url)) {

            c.setAutoCommit(false);

            try (PreparedStatement ps = c.prepareStatement(
                    "REPLACE INTO playtime (uuid, seconds) VALUES (?, ?)"
            )) {

                for (var e : map.entrySet()) {
                    ps.setString(1, e.getKey().toString());
                    ps.setLong(2, e.getValue());
                    ps.addBatch();
                }

                ps.executeBatch();
            }

            c.commit();

        } catch (Exception e) {
            logger.error("Failed saving playtime", e);
        }
    }



    public boolean isClaimed(UUID uuid, int level) {

        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement(
                     "SELECT 1 FROM playtime_claims WHERE uuid=? AND level=?"
             )) {

            ps.setString(1, uuid.toString());
            ps.setInt(2, level);

            return ps.executeQuery().next();

        } catch (Exception e) {
            return false;
        }
    }

    public void claim(UUID uuid, int level) {

        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement(
                     "INSERT OR IGNORE INTO playtime_claims VALUES (?, ?)"
             )) {

            ps.setString(1, uuid.toString());
            ps.setInt(2, level);
            ps.execute();

        } catch (Exception ignored) {}
    }
}