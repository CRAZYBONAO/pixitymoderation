package org.howie.pixity.moderation.neoforge.jail;

import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.howie.pixity.moderation.neoforge.tp.WarpPos;

public final class SQLiteJailStore {

    private final Logger logger;
    private final String url;

    public SQLiteJailStore(Logger logger, String file) {
        this.logger = logger;
        this.url = "jdbc:sqlite:" + file;
        init();
    }

    private void init() {
        try (Connection c = DriverManager.getConnection(url);
             Statement s = c.createStatement()) {

            s.execute("""
            CREATE TABLE IF NOT EXISTS jails(
                name TEXT PRIMARY KEY,
                dimension TEXT,
                x REAL,
                y REAL,
                z REAL,
                yaw REAL,
                pitch REAL
            )
            """);

            s.execute("""
            CREATE TABLE IF NOT EXISTS jailed(
                uuid TEXT PRIMARY KEY,
                name TEXT,
                jail TEXT,
                expires BIGINT,
                staff TEXT,
                reason TEXT
            )
            """);

        } catch (Exception e) {
            logger.error("Jail DB init failed", e);
        }
    }


    public Map<String, WarpPos> loadJails() {

        Map<String, WarpPos> map = new HashMap<>();

        try (Connection c = DriverManager.getConnection(url);
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT * FROM jails")) {

            while (rs.next()) {

                WarpPos pos = new WarpPos();
                pos.dimension = rs.getString("dimension");
                pos.x = rs.getDouble("x");
                pos.y = rs.getDouble("y");
                pos.z = rs.getDouble("z");
                pos.yaw = rs.getFloat("yaw");
                pos.pitch = rs.getFloat("pitch");

                map.put(rs.getString("name"), pos);
            }

        } catch (Exception e) {
            logger.error("Load jails failed", e);
        }

        return map;
    }

    public void saveJail(String name, WarpPos pos) {

        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement(
                     "REPLACE INTO jails VALUES (?,?,?,?,?,?,?)")) {

            ps.setString(1, name);
            ps.setString(2, pos.dimension);
            ps.setDouble(3, pos.x);
            ps.setDouble(4, pos.y);
            ps.setDouble(5, pos.z);
            ps.setFloat(6, pos.yaw);
            ps.setFloat(7, pos.pitch);

            ps.executeUpdate();

        } catch (Exception e) {
            logger.error("Save jail failed", e);
        }
    }

    public void deleteJail(String name) {

        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement(
                     "DELETE FROM jails WHERE name=?")) {

            ps.setString(1, name);
            ps.executeUpdate();

        } catch (Exception e) {
            logger.error("Delete jail failed", e);
        }
    }



    public Map<UUID, JailRecord> loadActive() {

        Map<UUID, JailRecord> map = new HashMap<>();

        try (Connection c = DriverManager.getConnection(url);
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT * FROM jailed")) {

            while (rs.next()) {

                JailRecord r = new JailRecord();

                r.player = UUID.fromString(rs.getString("uuid"));
                r.playerName = rs.getString("name");
                r.jailName = rs.getString("jail");
                r.expiresAtMs = rs.getLong("expires");
                r.staffName = rs.getString("staff");
                r.reason = rs.getString("reason");

                map.put(r.player, r);
            }

        } catch (Exception e) {
            logger.error("Load jailed failed", e);
        }

        return map;
    }

    public void saveJailRecord(JailRecord r) {

        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement(
                     "REPLACE INTO jailed VALUES (?,?,?,?,?,?)")) {

            ps.setString(1, r.player.toString());
            ps.setString(2, r.playerName);
            ps.setString(3, r.jailName);
            ps.setLong(4, r.expiresAtMs);
            ps.setString(5, r.staffName);
            ps.setString(6, r.reason);

            ps.executeUpdate();

        } catch (Exception e) {
            logger.error("Save jailed failed", e);
        }
    }

    public void remove(UUID uuid) {

        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement(
                     "DELETE FROM jailed WHERE uuid=?")) {

            ps.setString(1, uuid.toString());
            ps.executeUpdate();

        } catch (Exception e) {
            logger.error("Remove jailed failed", e);
        }
    }
}