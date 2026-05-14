package org.howie.pixity.moderation.neoforge.state;

import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.UUID;

public final class SQLitePlayerStateStore {

    private final Logger logger;
    private final String url;

    public SQLitePlayerStateStore(Logger logger, String url) {
        this.logger = logger;
        this.url = url;
        init();
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(url);
    }

    private void init() {
        try (Connection conn = connect()) {

            conn.createStatement().execute("""
                CREATE TABLE IF NOT EXISTS player_states (
                    uuid TEXT PRIMARY KEY,
                    vanished INTEGER,
                    flying INTEGER,
                    god INTEGER
                )
            """);

        } catch (Exception e) {
            throw new RuntimeException("Failed to init player_states table", e);
        }
    }

    public PlayerStateData load() {
        PlayerStateData data = new PlayerStateData();

        try (Connection conn = connect();
             ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM player_states")) {

            while (rs.next()) {
                UUID u = UUID.fromString(rs.getString("uuid"));

                if (rs.getInt("vanished") == 1) data.vanished.add(u);
                if (rs.getInt("flying") == 1) data.flying.add(u);
                if (rs.getInt("god") == 1) data.god.add(u);
            }

        } catch (Exception e) {
            logger.error("Failed to load player states", e);
        }

        return data;
    }

    public void save(PlayerStateData data) {
        try (Connection conn = connect()) {

            conn.createStatement().execute("DELETE FROM player_states");

            PreparedStatement ps = conn.prepareStatement("""
                INSERT INTO player_states (uuid, vanished, flying, god)
                VALUES (?, ?, ?, ?)
            """);

            for (UUID u : data.vanished) {
                ps.setString(1, u.toString());
                ps.setInt(2, 1);
                ps.setInt(3, data.flying.contains(u) ? 1 : 0);
                ps.setInt(4, data.god.contains(u) ? 1 : 0);
                ps.addBatch();
            }

            for (UUID u : data.flying) {
                if (data.vanished.contains(u)) continue;

                ps.setString(1, u.toString());
                ps.setInt(2, 0);
                ps.setInt(3, 1);
                ps.setInt(4, data.god.contains(u) ? 1 : 0);
                ps.addBatch();
            }

            for (UUID u : data.god) {
                if (data.vanished.contains(u) || data.flying.contains(u)) continue;

                ps.setString(1, u.toString());
                ps.setInt(2, 0);
                ps.setInt(3, 0);
                ps.setInt(4, 1);
                ps.addBatch();
            }

            ps.executeBatch();

        } catch (Exception e) {
            logger.error("Failed to save player states", e);
        }
    }
}