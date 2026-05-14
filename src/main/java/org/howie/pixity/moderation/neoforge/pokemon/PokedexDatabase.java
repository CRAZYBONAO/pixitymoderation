package org.howie.pixity.moderation.neoforge.pokemon;

import java.io.File;
import java.sql.*;
import java.util.*;

public class PokedexDatabase {

    private static final String URL = "jdbc:sqlite:config/pixity/pokedex.db";



    private static List<Map.Entry<UUID, Integer>> topDexCache = new ArrayList<>();
    private static List<Map.Entry<UUID, Integer>> topShinyCache = new ArrayList<>();

    private static long lastCacheUpdate = 0;

    private static Connection conn;

    private static Connection get() throws SQLException {
        if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection(URL);
        }
        return conn;
    }

    public static void init() {

        File file = new File("config/pixity/pokedex.db");
        file.getParentFile().mkdirs();

        try (Connection conn = get()) {

            Statement stmt = conn.createStatement();

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS pokedex (
                    uuid TEXT,
                    species TEXT,
                    PRIMARY KEY (uuid, species)
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS shiny_dex (
                    uuid TEXT,
                    species TEXT,
                    PRIMARY KEY (uuid, species)
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS rewards (
                    uuid TEXT,
                    milestone INTEGER,
                    PRIMARY KEY (uuid, milestone)
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS player_names (
                    uuid TEXT PRIMARY KEY,
                    name TEXT
                )
            """);

            stmt.execute("""
    CREATE TABLE IF NOT EXISTS holograms (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        world TEXT,
        x INTEGER,
        y INTEGER,
        z INTEGER,
        type TEXT
    )
""");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    public static void addHologram(String world, int x, int y, int z, String type) {
        try (Connection conn = get()) {

            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO holograms(world, x, y, z, type) VALUES (?, ?, ?, ?, ?)"
            );

            ps.setString(1, world);
            ps.setInt(2, x);
            ps.setInt(3, y);
            ps.setInt(4, z);
            ps.setString(5, type);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<HologramData> getHolograms() {

        List<HologramData> list = new ArrayList<>();

        try (Connection conn = get()) {

            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM holograms");

            while (rs.next()) {
                list.add(new HologramData(
                        rs.getInt("id"),
                        rs.getString("world"),
                        rs.getInt("x"),
                        rs.getInt("y"),
                        rs.getInt("z"),
                        rs.getString("type")
                ));
            }

        } catch (Exception e) {
            System.err.println("[Pixity] Failed to load holograms");
            return Collections.emptyList();
        }

        return list;
    }

    public static void removeHologram(int id) {
        try (Connection conn = get()) {

            PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM holograms WHERE id = ?"
            );

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public static void refreshCache() {

        long now = System.currentTimeMillis();
        if (now - lastCacheUpdate < 10000) return;

        lastCacheUpdate = now;

        topDexCache = loadTop("pokedex");
        topShinyCache = loadTop("shiny_dex");
    }

    private static List<Map.Entry<UUID, Integer>> loadTop(String table) {

        List<Map.Entry<UUID, Integer>> list = new ArrayList<>();

        try (Connection conn = get()) {

            PreparedStatement ps = conn.prepareStatement(
                    "SELECT uuid, COUNT(*) as total FROM " + table + " GROUP BY uuid ORDER BY total DESC LIMIT 10"
            );

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(Map.entry(
                        UUID.fromString(rs.getString("uuid")),
                        rs.getInt("total")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static List<Map.Entry<UUID, Integer>> getTopDex() {
        refreshCache();
        return topDexCache;
    }

    public static List<Map.Entry<UUID, Integer>> getTopShinyDex() {
        refreshCache();
        return topShinyCache;
    }




    public static boolean addCatch(UUID uuid, String species) {
        try (Connection conn = get()) {

            PreparedStatement ps = conn.prepareStatement(
                    "INSERT OR IGNORE INTO pokedex(uuid, species) VALUES (?, ?)"
            );

            ps.setString(1, uuid.toString());
            ps.setString(2, species.toLowerCase());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean addShiny(UUID uuid, String species) {
        try (Connection conn = get()) {

            PreparedStatement ps = conn.prepareStatement(
                    "INSERT OR IGNORE INTO shiny_dex(uuid, species) VALUES (?, ?)"
            );

            ps.setString(1, uuid.toString());
            ps.setString(2, species.toLowerCase());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int getCount(UUID uuid) {
        try (Connection conn = get()) {

            PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) FROM pokedex WHERE uuid = ?"
            );

            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getInt(1);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getShinyCount(UUID uuid) {
        try (Connection conn = get()) {

            PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) FROM shiny_dex WHERE uuid = ?"
            );

            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getInt(1);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void updateName(UUID uuid, String name) {
        try (Connection conn = get()) {

            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO player_names(uuid, name) VALUES (?, ?) " +
                            "ON CONFLICT(uuid) DO UPDATE SET name = excluded.name"
            );

            ps.setString(1, uuid.toString());
            ps.setString(2, name);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getName(UUID uuid) {
        try (Connection conn = get()) {

            PreparedStatement ps = conn.prepareStatement(
                    "SELECT name FROM player_names WHERE uuid = ?"
            );

            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getString("name");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return uuid.toString().substring(0, 8);
    }

    public static boolean hasReward(UUID uuid, int milestone) {
        try (Connection conn = get()) {

            PreparedStatement ps = conn.prepareStatement(
                    "SELECT 1 FROM rewards WHERE uuid = ? AND milestone = ?"
            );

            ps.setString(1, uuid.toString());
            ps.setInt(2, milestone);

            return ps.executeQuery().next();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void addReward(UUID uuid, int milestone) {
        try (Connection conn = get()) {

            PreparedStatement ps = conn.prepareStatement(
                    "INSERT OR IGNORE INTO rewards(uuid, milestone) VALUES (?, ?)"
            );

            ps.setString(1, uuid.toString());
            ps.setInt(2, milestone);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}