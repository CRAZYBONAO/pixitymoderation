package org.howie.pixity.moderation.neoforge.rules;

import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class SQLiteRulesSeenStore {


    private final Logger logger;
    private final String url;

    public SQLiteRulesSeenStore(Logger logger, Path dataDir) {
        this.logger = logger;

        try {
            Files.createDirectories(dataDir);

            Path dbFile = dataDir.resolve("rules.db");

            if (Files.exists(dbFile) && Files.isDirectory(dbFile)) {
                Files.delete(dbFile);
            }

            this.url = "jdbc:sqlite:" + dbFile.toAbsolutePath();

        } catch (Exception e) {
            throw new RuntimeException("Failed to init SQLiteRulesSeenStore", e);
        }

        init();
    }

    private void init() {
        try (Connection c = DriverManager.getConnection(url);
             Statement s = c.createStatement()) {

            s.execute("""
            CREATE TABLE IF NOT EXISTS rules_seen (
                uuid TEXT PRIMARY KEY
            );
        """);

        } catch (Exception e) {
            logger.error("Failed to init rules DB", e);
        }
    }

    public Set<UUID> loadAll() {
        Set<UUID> set = new HashSet<>();

        try (Connection c = DriverManager.getConnection(url);
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT uuid FROM rules_seen")) {

            while (rs.next()) {
                set.add(UUID.fromString(rs.getString("uuid")));
            }

        } catch (Exception e) {
            logger.error("Failed loading rules seen", e);
        }

        return set;
    }

    public boolean hasSeen(UUID uuid) {
        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement("SELECT 1 FROM rules_seen WHERE uuid=?")) {

            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (Exception e) {
            logger.error("Failed checking rules seen", e);
        }

        return false;
    }

    public void markSeen(UUID uuid) {
        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement(
                     "INSERT OR IGNORE INTO rules_seen(uuid) VALUES(?)")) {

            ps.setString(1, uuid.toString());
            ps.executeUpdate();

        } catch (Exception e) {
            logger.error("Failed saving rules seen", e);
        }
    }


}
