package org.howie.pixity.moderation.neoforge.punish;

import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.*;

public final class SQLitePunishStore {


    private final Logger logger;
    private final String url;

    public SQLitePunishStore(Logger logger, Path dataDir) {
        this.logger = logger;

        try {

            Files.createDirectories(dataDir);

            Path dbFile = dataDir.resolve("punishments.db");


            if (Files.exists(dbFile) && Files.isDirectory(dbFile)) {
                Files.delete(dbFile);
            }

            this.url = "jdbc:sqlite:" + dbFile.toAbsolutePath();

        } catch (Exception e) {
            throw new RuntimeException("Failed to init SQLitePunishStore", e);
        }

        init();
    }

    private void init() {
        try (Connection c = DriverManager.getConnection(url);
             Statement s = c.createStatement()) {

            s.execute("""
        CREATE TABLE IF NOT EXISTS bans (
            uuid TEXT PRIMARY KEY,
            name TEXT,
            expires BIGINT,
            reason TEXT,
            staff TEXT,
            created BIGINT
        );
        """);

            s.execute("""
        CREATE TABLE IF NOT EXISTS ip_bans (
            ip TEXT PRIMARY KEY,
            reason TEXT,
            staff TEXT,
            created BIGINT
        );
        """);

            s.execute("""
        CREATE TABLE IF NOT EXISTS history (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            ts BIGINT,
            action TEXT,
            staff_uuid TEXT,
            staff_name TEXT,
            target_uuid TEXT,
            target_name TEXT,
            duration BIGINT,
            reason TEXT
        );
        """);

            s.execute("CREATE INDEX IF NOT EXISTS idx_history_target ON history(target_uuid);");

        } catch (Exception e) {
            logger.error("Failed to init punish DB", e);
        }
    }





    public Map<UUID, ActiveBan> loadBans() {
        Map<UUID, ActiveBan> map = new HashMap<>();

        try (Connection c = DriverManager.getConnection(url);
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT * FROM bans")) {

            while (rs.next()) {
                ActiveBan b = new ActiveBan();
                b.targetUuid = UUID.fromString(rs.getString("uuid"));
                b.targetNameLower = rs.getString("name");
                b.expiresAtMs = rs.getLong("expires");
                b.reason = rs.getString("reason");
                b.staffName = rs.getString("staff");
                b.createdAtMs = rs.getLong("created");

                map.put(b.targetUuid, b);
            }

        } catch (Exception e) {
            logger.error("Failed loading bans", e);
        }

        return map;
    }

    public void saveBan(ActiveBan ban) {
        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement("""
            INSERT OR REPLACE INTO bans VALUES (?,?,?,?,?,?)
         """)) {

            ps.setString(1, ban.targetUuid.toString());
            ps.setString(2, ban.targetNameLower);
            ps.setLong(3, ban.expiresAtMs);
            ps.setString(4, ban.reason);
            ps.setString(5, ban.staffName);
            ps.setLong(6, ban.createdAtMs);

            ps.executeUpdate();

        } catch (Exception e) {
            logger.error("Failed saving ban", e);
        }
    }

    public void removeBan(UUID uuid) {
        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement("DELETE FROM bans WHERE uuid=?")) {

            ps.setString(1, uuid.toString());
            ps.executeUpdate();

        } catch (Exception e) {
            logger.error("Failed removing ban", e);
        }
    }





    public void insertHistory(PunishEntry e) {
        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement("""
            INSERT INTO history VALUES (NULL,?,?,?,?,?,?,?,?)
         """)) {

            ps.setLong(1, e.tsEpochMs);
            ps.setString(2, e.action.name());
            ps.setString(3, String.valueOf(e.staffUuid));
            ps.setString(4, e.staffName);
            ps.setString(5, String.valueOf(e.targetUuid));
            ps.setString(6, e.targetName);
            ps.setObject(7, e.durationSeconds);
            ps.setString(8, e.reason);

            ps.executeUpdate();

        } catch (Exception ex) {
            logger.error("Failed inserting history", ex);
        }
    }

    public List<PunishEntry> getHistory(UUID target) {
        List<PunishEntry> list = new ArrayList<>();

        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement("""
            SELECT * FROM history WHERE target_uuid=? ORDER BY ts DESC
         """)) {

            ps.setString(1, target.toString());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                PunishEntry e = new PunishEntry();
                e.tsEpochMs = rs.getLong("ts");
                e.action = PunishAction.valueOf(rs.getString("action"));
                e.staffUuid = UUID.fromString(rs.getString("staff_uuid"));
                e.staffName = rs.getString("staff_name");
                e.targetUuid = UUID.fromString(rs.getString("target_uuid"));
                e.targetName = rs.getString("target_name");
                e.durationSeconds = rs.getObject("duration") == null ? null : rs.getLong("duration");
                e.reason = rs.getString("reason");

                list.add(e);
            }

        } catch (Exception e) {
            logger.error("Failed loading history", e);
        }

        return list;
    }





    public Set<String> loadIpBans() {
        Set<String> set = new HashSet<>();

        try (Connection c = DriverManager.getConnection(url);
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT ip FROM ip_bans")) {

            while (rs.next()) {
                set.add(rs.getString("ip"));
            }

        } catch (Exception e) {
            logger.error("Failed loading ip bans", e);
        }

        return set;
    }

    public void saveIpBan(String ip, String reason, String staff) {
        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement("""
            INSERT OR REPLACE INTO ip_bans VALUES (?,?,?,?)
         """)) {

            ps.setString(1, ip);
            ps.setString(2, reason);
            ps.setString(3, staff);
            ps.setLong(4, System.currentTimeMillis());

            ps.executeUpdate();

        } catch (Exception e) {
            logger.error("Failed saving ip ban", e);
        }
    }

    public void removeIpBan(String ip) {
        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement(
                     "DELETE FROM ip_bans WHERE ip=?"
             )) {

            ps.setString(1, ip);
            ps.executeUpdate();

        } catch (Exception e) {
            logger.error("Failed removing ip ban", e);
        }
    }


}
