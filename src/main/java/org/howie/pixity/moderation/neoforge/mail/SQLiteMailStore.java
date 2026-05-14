package org.howie.pixity.moderation.neoforge.mail;

import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.*;

public final class SQLiteMailStore {


    private final Logger logger;
    private final String url;

    public SQLiteMailStore(Logger logger, Path dataDir) {
        this.logger = logger;

        try {
            Files.createDirectories(dataDir);

            Path dbFile = dataDir.resolve("mail.db");

            if (Files.exists(dbFile) && Files.isDirectory(dbFile)) {
                Files.delete(dbFile);
            }

            this.url = "jdbc:sqlite:" + dbFile.toAbsolutePath();

        } catch (Exception e) {
            throw new RuntimeException("Failed to init SQLiteMailStore", e);
        }

        init();
    }

    public int nextId(UUID uuid) {

        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement("""
            SELECT COALESCE(MAX(CAST(id AS INTEGER)),0)+1
            FROM mail
            WHERE to_uuid=?
         """)) {

            ps.setString(1, uuid.toString());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            logger.error("Mail nextId failed", e);
        }

        return 1;
    }

    public UUID lookupUuidByName(String name) {

        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement("""
            SELECT to_uuid FROM mail
            WHERE LOWER(to_name)=LOWER(?)
            LIMIT 1
         """)) {

            ps.setString(1, name);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return UUID.fromString(rs.getString("to_uuid"));
            }

        } catch (Exception e) {
            logger.error("lookupUuidByName failed", e);
        }

        return null;
    }

    private void init() {
        try (Connection c = DriverManager.getConnection(url);
             Statement s = c.createStatement()) {

            s.execute("""
            CREATE TABLE IF NOT EXISTS mail (
                id TEXT PRIMARY KEY,
                ts LONG,
                from_uuid TEXT,
                from_name TEXT,
                to_uuid TEXT,
                to_name TEXT,
                message TEXT,
                read INT
            );
        """);

            s.execute("CREATE INDEX IF NOT EXISTS idx_mail_to ON mail(to_uuid);");

        } catch (Exception e) {
            logger.error("Failed to init mail DB", e);
        }
    }

    public void insert(MailMessage m) {
        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement("""
            INSERT INTO mail VALUES (?,?,?,?,?,?,?,?)
         """)) {

            ps.setString(1, m.id);
            ps.setLong(2, m.ts);
            ps.setString(3, m.fromUuid);
            ps.setString(4, m.fromName);
            ps.setString(5, m.toUuid);
            ps.setString(6, m.toName);
            ps.setString(7, m.message);
            ps.setInt(8, m.read ? 1 : 0);

            ps.executeUpdate();

        } catch (Exception e) {
            logger.error("Mail insert failed", e);
        }
    }

    public List<MailMessage> getInbox(UUID uuid) {
        List<MailMessage> list = new ArrayList<>();

        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement("""
            SELECT * FROM mail WHERE to_uuid=? ORDER BY ts DESC
         """)) {

            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                MailMessage m = new MailMessage();
                m.id = rs.getString("id");
                m.ts = rs.getLong("ts");
                m.fromUuid = rs.getString("from_uuid");
                m.fromName = rs.getString("from_name");
                m.toUuid = rs.getString("to_uuid");
                m.toName = rs.getString("to_name");
                m.message = rs.getString("message");
                m.read = rs.getInt("read") == 1;
                list.add(m);
            }

        } catch (Exception e) {
            logger.error("Mail read failed", e);
        }

        return list;
    }

    public void markRead(UUID uuid, String id) {
        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement("""
            UPDATE mail SET read=1 WHERE id=? AND to_uuid=?
         """)) {

            ps.setString(1, id);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();

        } catch (Exception e) {
            logger.error("Mail markRead failed", e);
        }
    }

    public MailMessage get(UUID uuid, String id) {
        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement("""
            SELECT * FROM mail WHERE id=? AND to_uuid=?
         """)) {

            ps.setString(1, id);
            ps.setString(2, uuid.toString());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                MailMessage m = new MailMessage();
                m.id = rs.getString("id");
                m.ts = rs.getLong("ts");
                m.fromUuid = rs.getString("from_uuid");
                m.fromName = rs.getString("from_name");
                m.toUuid = rs.getString("to_uuid");
                m.toName = rs.getString("to_name");
                m.message = rs.getString("message");
                m.read = rs.getInt("read") == 1;
                return m;
            }

        } catch (Exception e) {
            logger.error("Mail get failed", e);
        }

        return null;
    }

    public boolean delete(UUID uuid, String id) {
        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement("""
            DELETE FROM mail WHERE id=? AND to_uuid=?
         """)) {

            ps.setString(1, id);
            ps.setString(2, uuid.toString());
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            logger.error("Mail delete failed", e);
            return false;
        }
    }

    public int clear(UUID uuid) {
        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement("""
            DELETE FROM mail WHERE to_uuid=?
         """)) {

            ps.setString(1, uuid.toString());
            return ps.executeUpdate();

        } catch (Exception e) {
            logger.error("Mail clear failed", e);
            return 0;
        }
    }

    public int unread(UUID uuid) {
        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement("""
            SELECT COUNT(*) FROM mail WHERE to_uuid=? AND read=0
         """)) {

            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            logger.error("Mail unread count failed", e);
        }

        return 0;
    }


}
