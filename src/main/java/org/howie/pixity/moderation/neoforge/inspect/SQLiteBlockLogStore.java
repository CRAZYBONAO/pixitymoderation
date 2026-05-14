package org.howie.pixity.moderation.neoforge.inspect;

import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.*;

public final class SQLiteBlockLogStore {


    private final Logger logger;
    private final String url;


    public SQLiteBlockLogStore(Logger logger, Path dataDir) {
        this.logger = logger;

        try {

            Files.createDirectories(dataDir);

            Path dbFile = dataDir.resolve("blocklog.db");


            if (Files.exists(dbFile) && Files.isDirectory(dbFile)) {
                Files.delete(dbFile);
            }

            this.url = "jdbc:sqlite:" + dbFile.toAbsolutePath();

        } catch (Exception e) {
            throw new RuntimeException("Failed to init SQLiteBlockLogStore", e);
        }

        init();
    }

    private void init() {
        try (Connection c = DriverManager.getConnection(url);
             Statement s = c.createStatement()) {

            s.execute("""
            CREATE TABLE IF NOT EXISTS block_logs (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                dim TEXT,
                x INT,
                y INT,
                z INT,
                ts LONG,
                action TEXT,
                player_uuid TEXT,
                player_name TEXT,
                block_id TEXT
            );
        """);

            s.execute("CREATE INDEX IF NOT EXISTS idx_pos ON block_logs(dim,x,y,z);");

        } catch (Exception e) {
            logger.error("Failed to init blocklog database", e);
        }
    }

    public void insert(String dim, int x, int y, int z, BlockLogEntry e) {
        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement("""
            INSERT INTO block_logs (dim,x,y,z,ts,action,player_uuid,player_name,block_id)
            VALUES (?,?,?,?,?,?,?,?,?)
         """)) {

            ps.setString(1, dim);
            ps.setInt(2, x);
            ps.setInt(3, y);
            ps.setInt(4, z);
            ps.setLong(5, e.ts);
            ps.setString(6, e.action);
            ps.setString(7, e.playerUuid);
            ps.setString(8, e.playerName);
            ps.setString(9, e.blockId);

            ps.executeUpdate();

        } catch (Exception ex) {
            logger.error("Failed inserting block log", ex);
        }
    }

    public List<BlockLogEntry> get(String dim, int x, int y, int z, int limit) {
        List<BlockLogEntry> list = new ArrayList<>();

        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement("""
            SELECT * FROM block_logs
            WHERE dim=? AND x=? AND y=? AND z=?
            ORDER BY ts DESC
            LIMIT ?
         """)) {

            ps.setString(1, dim);
            ps.setInt(2, x);
            ps.setInt(3, y);
            ps.setInt(4, z);
            ps.setInt(5, limit);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                BlockLogEntry e = new BlockLogEntry();
                e.ts = rs.getLong("ts");
                e.action = rs.getString("action");
                e.playerUuid = rs.getString("player_uuid");
                e.playerName = rs.getString("player_name");
                e.blockId = rs.getString("block_id");
                list.add(e);
            }

        } catch (Exception ex) {
            logger.error("Failed reading block logs", ex);
        }

        return list;
    }


}
