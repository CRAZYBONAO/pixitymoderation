package org.howie.pixity.moderation.neoforge.spawners;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class SpawnerDatabase {

    private final String url;

    public SpawnerDatabase(String url) {
        this.url = url;
        init();
    }

    private void init() {

        try (Connection c = DriverManager.getConnection(url);
             Statement s = c.createStatement()) {

            s.execute("""
                CREATE TABLE IF NOT EXISTS spawners (
                    world TEXT,
                    x INT,
                    y INT,
                    z INT,
                    mob TEXT,
                    stack INT,
                    PRIMARY KEY(world,x,y,z)
                )
            """);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void save(
            String world,
            int x,
            int y,
            int z,
            String mob,
            int stack
    ) {

        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement("""
                REPLACE INTO spawners
                (world,x,y,z,mob,stack)
                VALUES (?,?,?,?,?,?)
             """)) {

            ps.setString(1, world);
            ps.setInt(2, x);
            ps.setInt(3, y);
            ps.setInt(4, z);
            ps.setString(5, mob);
            ps.setInt(6, stack);

            ps.executeUpdate();

        } catch (Exception ignored) {}
    }

    public Map<String, SpawnerData> load() {

        Map<String, SpawnerData> map = new HashMap<>();

        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps =
                     c.prepareStatement("SELECT * FROM spawners");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                String key =
                        rs.getString("world")
                                + ":"
                                + rs.getInt("x")
                                + ":"
                                + rs.getInt("y")
                                + ":"
                                + rs.getInt("z");

                map.put(
                        key,
                        new SpawnerData(
                                rs.getString("mob"),
                                rs.getInt("stack")
                        )
                );
            }

        } catch (Exception ignored) {}

        return map;
    }

    public record SpawnerData(
            String mob,
            int stack
    ) {}
}