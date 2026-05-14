package org.howie.pixity.moderation.neoforge.notes;

import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.*;

public final class SQLiteNotesStore {


    private final Logger logger;
    private final String url;

    public SQLiteNotesStore(Logger logger, Path dataDir) {
        this.logger = logger;

        try {
            Files.createDirectories(dataDir);

            Path dbFile = dataDir.resolve("notes.db");

            if (Files.exists(dbFile) && Files.isDirectory(dbFile)) {
                Files.delete(dbFile);
            }

            this.url = "jdbc:sqlite:" + dbFile.toAbsolutePath();

        } catch (Exception e) {
            throw new RuntimeException("Failed to init SQLiteNotesStore", e);
        }

        init();
    }

    private void init() {
        try (Connection c = DriverManager.getConnection(url);
             Statement s = c.createStatement()) {

            s.execute("""
            CREATE TABLE IF NOT EXISTS notes (
                id TEXT PRIMARY KEY,
                ts LONG,
                target_uuid TEXT,
                staff_uuid TEXT,
                staff_name TEXT,
                text TEXT
            );
        """);

            s.execute("CREATE INDEX IF NOT EXISTS idx_notes_target ON notes(target_uuid);");

        } catch (Exception e) {
            logger.error("Failed to init notes DB", e);
        }
    }

    public void insert(UUID target, NoteEntry n) {
        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement("""
            INSERT INTO notes VALUES (?,?,?,?,?,?)
         """)) {

            ps.setString(1, n.id);
            ps.setLong(2, n.ts);
            ps.setString(3, target.toString());
            ps.setString(4, n.staffUuid);
            ps.setString(5, n.staffName);
            ps.setString(6, n.text);

            ps.executeUpdate();

        } catch (Exception e) {
            logger.error("Failed to insert note", e);
        }
    }

    public List<NoteEntry> get(UUID target) {
        List<NoteEntry> list = new ArrayList<>();

        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement("""
            SELECT * FROM notes WHERE target_uuid=? ORDER BY ts DESC
         """)) {

            ps.setString(1, target.toString());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                NoteEntry n = new NoteEntry();
                n.id = rs.getString("id");
                n.ts = rs.getLong("ts");
                n.staffUuid = rs.getString("staff_uuid");
                n.staffName = rs.getString("staff_name");
                n.text = rs.getString("text");
                list.add(n);
            }

        } catch (Exception e) {
            logger.error("Failed loading notes", e);
        }

        return list;
    }

    public boolean delete(UUID target, String id) {
        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement("""
            DELETE FROM notes WHERE id=? AND target_uuid=?
         """)) {

            ps.setString(1, id);
            ps.setString(2, target.toString());
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            logger.error("Failed deleting note", e);
            return false;
        }
    }


}
