package org.howie.pixity.moderation.neoforge.joinleave;

import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.*;

public final class SQLiteJoinLeaveStore {


    private final Logger logger;
    private final String url;

    public SQLiteJoinLeaveStore(Logger logger, Path dataDir) {
        this.logger = logger;

        try {

            Files.createDirectories(dataDir);

            Path dbFile = dataDir.resolve("joinleave.db");


            if (Files.exists(dbFile) && Files.isDirectory(dbFile)) {
                Files.delete(dbFile);
            }

            this.url = "jdbc:sqlite:" + dbFile.toAbsolutePath();

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize SQLiteJoinLeaveStore path", e);
        }

        init();
    }

    private void init() {
        try (Connection c = DriverManager.getConnection(url);
             Statement s = c.createStatement()) {

            s.execute("""
            CREATE TABLE IF NOT EXISTS joinleave_messages (
                uuid TEXT PRIMARY KEY,
                join_msg TEXT,
                leave_msg TEXT
            );
        """);

            s.execute("""
            CREATE TABLE IF NOT EXISTS joinleave_seen (
                uuid TEXT PRIMARY KEY
            );
        """);

        } catch (Exception e) {
            logger.error("Failed to init joinleave database", e);
        }
    }





    public Map<UUID, PlayerJoinLeave> loadMessages() {
        Map<UUID, PlayerJoinLeave> map = new HashMap<>();

        try (Connection c = DriverManager.getConnection(url);
             Statement s = c.createStatement()) {

            ResultSet rs = s.executeQuery("SELECT * FROM joinleave_messages");

            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("uuid"));

                PlayerJoinLeave pj = new PlayerJoinLeave();
                pj.join = rs.getString("join_msg");
                pj.leave = rs.getString("leave_msg");

                map.put(uuid, pj);
            }

        } catch (Exception e) {
            logger.error("Failed loading joinleave messages", e);
        }

        return map;
    }

    public Set<UUID> loadSeen() {
        Set<UUID> set = new HashSet<>();

        try (Connection c = DriverManager.getConnection(url);
             Statement s = c.createStatement()) {

            ResultSet rs = s.executeQuery("SELECT uuid FROM joinleave_seen");

            while (rs.next()) {
                set.add(UUID.fromString(rs.getString("uuid")));
            }

        } catch (Exception e) {
            logger.error("Failed loading seen players", e);
        }

        return set;
    }





    public void saveMessage(UUID uuid, PlayerJoinLeave pj) {
        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement("""
            INSERT INTO joinleave_messages (uuid, join_msg, leave_msg)
            VALUES (?, ?, ?)
            ON CONFLICT(uuid) DO UPDATE SET
                join_msg=excluded.join_msg,
                leave_msg=excluded.leave_msg
         """)) {

            ps.setString(1, uuid.toString());
            ps.setString(2, pj.join);
            ps.setString(3, pj.leave);

            ps.executeUpdate();

        } catch (Exception e) {
            logger.error("Failed saving joinleave message", e);
        }
    }

    public void saveSeen(UUID uuid) {
        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement("""
            INSERT OR IGNORE INTO joinleave_seen (uuid)
            VALUES (?)
         """)) {

            ps.setString(1, uuid.toString());
            ps.executeUpdate();

        } catch (Exception e) {
            logger.error("Failed saving seen player", e);
        }
    }

    public void delete(UUID uuid) {
        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement("""
            DELETE FROM joinleave_messages WHERE uuid=?
         """)) {

            ps.setString(1, uuid.toString());
            ps.executeUpdate();

        } catch (Exception e) {
            logger.error("Failed deleting joinleave message", e);
        }
    }


}
