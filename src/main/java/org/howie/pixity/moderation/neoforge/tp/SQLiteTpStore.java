package org.howie.pixity.moderation.neoforge.tp;

import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public final class SQLiteTpStore {

    private final Logger logger;
    private final String url;

    public SQLiteTpStore(Logger logger, Path dataDir) {
        this.logger = logger;

        try {
            Files.createDirectories(dataDir);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create data folder", e);
        }

        this.url = "jdbc:sqlite:" + dataDir.resolve("tp.db").toAbsolutePath();

        init();
    }

    private Connection connect() throws Exception {
        return DriverManager.getConnection(url);
    }

    private void init() {
        try (Connection conn = connect()) {

            conn.createStatement().execute("""
                CREATE TABLE IF NOT EXISTS homes (
                    uuid TEXT,
                    name TEXT,
                    x REAL, y REAL, z REAL,
                    yaw REAL, pitch REAL,
                    dimension TEXT,
                    PRIMARY KEY(uuid, name)
                )
            """);

            conn.createStatement().execute("""
                CREATE TABLE IF NOT EXISTS warps (
                    name TEXT PRIMARY KEY,
                    x REAL, y REAL, z REAL,
                    yaw REAL, pitch REAL,
                    dimension TEXT
                )
            """);

            conn.createStatement().execute("""
                CREATE TABLE IF NOT EXISTS pwarps (
                    name TEXT PRIMARY KEY,
                    owner TEXT,
                    ownerName TEXT,
                    x REAL, y REAL, z REAL,
                    yaw REAL, pitch REAL,
                    dimension TEXT,
                    visits INTEGER,
                    featured INTEGER,
                    category TEXT,
                    icon TEXT,
                    description TEXT
                )
            """);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to init SQLiteTpStore", e);
        }
    }



    public Map<UUID, Map<String, WarpPos>> loadHomes() {
        Map<UUID, Map<String, WarpPos>> out = new HashMap<>();

        try (Connection conn = connect();
             var ps = conn.prepareStatement("SELECT * FROM homes");
             var rs = ps.executeQuery()) {

            while (rs.next()) {
                UUID u = UUID.fromString(rs.getString("uuid"));
                String name = rs.getString("name");

                WarpPos wp = new WarpPos();
                wp.x = rs.getDouble("x");
                wp.y = rs.getDouble("y");
                wp.z = rs.getDouble("z");
                wp.yaw = rs.getFloat("yaw");
                wp.pitch = rs.getFloat("pitch");
                wp.dimension = rs.getString("dimension");

                out.computeIfAbsent(u, k -> new HashMap<>()).put(name, wp);
            }

        } catch (Exception e) {
            logger.error("Failed to load homes", e);
        }

        return out;
    }

    public void saveHomes(Map<UUID, Map<String, WarpPos>> homes) {
        try (Connection conn = connect()) {

            conn.createStatement().execute("DELETE FROM homes");

            var ps = conn.prepareStatement("""
            INSERT INTO homes (uuid, name, x, y, z, yaw, pitch, dimension)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """);

            for (var entry : homes.entrySet()) {
                for (var e : entry.getValue().entrySet()) {

                    WarpPos wp = e.getValue();

                    ps.setString(1, entry.getKey().toString());
                    ps.setString(2, e.getKey());
                    ps.setDouble(3, wp.x);
                    ps.setDouble(4, wp.y);
                    ps.setDouble(5, wp.z);
                    ps.setFloat(6, wp.yaw);
                    ps.setFloat(7, wp.pitch);
                    ps.setString(8, wp.dimension);

                    ps.addBatch();
                }
            }

            ps.executeBatch();

        } catch (Exception e) {
            logger.error("Failed to save homes", e);
        }
    }

    public Map<String, WarpPos> loadWarps() {
        Map<String, WarpPos> out = new HashMap<>();

        try (Connection conn = connect();
             var rs = conn.createStatement().executeQuery("SELECT * FROM warps")) {

            while (rs.next()) {
                WarpPos wp = new WarpPos();
                wp.x = rs.getDouble("x");
                wp.y = rs.getDouble("y");
                wp.z = rs.getDouble("z");
                wp.yaw = rs.getFloat("yaw");
                wp.pitch = rs.getFloat("pitch");
                wp.dimension = rs.getString("dimension");

                out.put(rs.getString("name"), wp);
            }

        } catch (Exception e) {
            logger.error("Failed to load warps", e);
        }

        return out;
    }

    public void saveWarps(Map<String, WarpPos> warps) {
        try (Connection conn = connect()) {

            conn.createStatement().execute("DELETE FROM warps");

            var ps = conn.prepareStatement("""
            INSERT INTO warps (name, x, y, z, yaw, pitch, dimension)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """);

            for (var e : warps.entrySet()) {
                WarpPos wp = e.getValue();

                ps.setString(1, e.getKey());
                ps.setDouble(2, wp.x);
                ps.setDouble(3, wp.y);
                ps.setDouble(4, wp.z);
                ps.setFloat(5, wp.yaw);
                ps.setFloat(6, wp.pitch);
                ps.setString(7, wp.dimension);

                ps.addBatch();
            }

            ps.executeBatch();

        } catch (Exception e) {
            logger.error("Failed to save warps", e);
        }
    }

    public Map<String, PlayerWarp> loadPlayerWarps() {
        Map<String, PlayerWarp> out = new HashMap<>();

        try (Connection conn = connect();
             var rs = conn.createStatement().executeQuery("SELECT * FROM pwarps")) {

            while (rs.next()) {
                PlayerWarp w = new PlayerWarp();

                w.name = rs.getString("name");
                w.owner = UUID.fromString(rs.getString("owner"));
                w.ownerName = rs.getString("ownerName");

                WarpPos wp = new WarpPos();
                wp.x = rs.getDouble("x");
                wp.y = rs.getDouble("y");
                wp.z = rs.getDouble("z");
                wp.yaw = rs.getFloat("yaw");
                wp.pitch = rs.getFloat("pitch");
                wp.dimension = rs.getString("dimension");

                w.pos = wp;

                w.visits = rs.getInt("visits");
                w.featured = rs.getInt("featured") == 1;
                w.category = rs.getString("category");
                w.icon = rs.getString("icon");
                w.description = rs.getString("description");

                out.put(w.name, w);
            }

        } catch (Exception e) {
            logger.error("Failed to load pwarps", e);
        }

        return out;
    }

    public void savePlayerWarps(Map<String, PlayerWarp> pwarps) {
        try (Connection conn = connect()) {

            conn.createStatement().execute("DELETE FROM pwarps");

            var ps = conn.prepareStatement("""
            INSERT INTO pwarps (name, owner, ownerName, x, y, z, yaw, pitch, dimension,
                                visits, featured, category, icon, description)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """);

            for (var e : pwarps.entrySet()) {
                PlayerWarp w = e.getValue();
                WarpPos wp = w.pos;

                ps.setString(1, w.name);
                ps.setString(2, w.owner.toString());
                ps.setString(3, w.ownerName);

                ps.setDouble(4, wp.x);
                ps.setDouble(5, wp.y);
                ps.setDouble(6, wp.z);
                ps.setFloat(7, wp.yaw);
                ps.setFloat(8, wp.pitch);
                ps.setString(9, wp.dimension);

                ps.setInt(10, w.visits);
                ps.setInt(11, w.featured ? 1 : 0);
                ps.setString(12, w.category);
                ps.setString(13, w.icon);
                ps.setString(14, w.description);

                ps.addBatch();
            }

            ps.executeBatch();

        } catch (Exception e) {
            logger.error("Failed to save pwarps", e);
        }
    }

    public WarpPos loadSpawn() {
        try (Connection conn = connect();
             var rs = conn.createStatement().executeQuery("SELECT * FROM spawn LIMIT 1")) {

            if (rs.next()) {
                WarpPos wp = new WarpPos();
                wp.x = rs.getDouble("x");
                wp.y = rs.getDouble("y");
                wp.z = rs.getDouble("z");
                wp.yaw = rs.getFloat("yaw");
                wp.pitch = rs.getFloat("pitch");
                wp.dimension = rs.getString("dimension");
                return wp;
            }

        } catch (Exception e) {
            logger.error("Failed to load spawn", e);
        }

        return null;
    }

    public void saveSpawn(WarpPos spawn) {
        try (Connection conn = connect()) {

            conn.createStatement().execute("DELETE FROM spawn");

            var ps = conn.prepareStatement("""
            INSERT INTO spawn (x, y, z, yaw, pitch, dimension)
            VALUES (?, ?, ?, ?, ?, ?)
        """);

            ps.setDouble(1, spawn.x);
            ps.setDouble(2, spawn.y);
            ps.setDouble(3, spawn.z);
            ps.setFloat(4, spawn.yaw);
            ps.setFloat(5, spawn.pitch);
            ps.setString(6, spawn.dimension);

            ps.executeUpdate();

        } catch (Exception e) {
            logger.error("Failed to save spawn", e);
        }
    }



    public Set<UUID> loadFirstJoins() {
        return new HashSet<>();
    }

    public void saveFirstJoin(UUID u) {
    }
}