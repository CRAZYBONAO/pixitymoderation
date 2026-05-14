package org.howie.pixity.moderation.neoforge.economy;

import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class SQLiteEconomyStore {

    private final Logger logger;
    private final String url;

    public SQLiteEconomyStore(Logger logger, String url) {
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
                CREATE TABLE IF NOT EXISTS economy (
                    uuid TEXT PRIMARY KEY,
                    money REAL,
                    coins REAL,
                    tokens REAL
                )
            """);

        } catch (Exception e) {
            throw new RuntimeException("Failed to init economy DB", e);
        }
    }





    public Map<UUID, EconomyAccount> load() {

        Map<UUID, EconomyAccount> map = new HashMap<>();

        try (Connection conn = connect();
             ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM economy")) {

            while (rs.next()) {

                UUID u = UUID.fromString(rs.getString("uuid"));

                EconomyAccount acc = new EconomyAccount();
                acc.money = rs.getDouble("money");
                acc.coins = rs.getDouble("coins");
                acc.tokens = rs.getDouble("tokens");

                map.put(u, acc);
            }

        } catch (Exception e) {
            logger.error("Failed to load economy", e);
        }

        return map;
    }





    public void save(Map<UUID, EconomyAccount> map) {

        try (Connection conn = connect()) {

            conn.createStatement().execute("DELETE FROM economy");

            PreparedStatement ps = conn.prepareStatement("""
                INSERT INTO economy (uuid, money, coins, tokens)
                VALUES (?, ?, ?, ?)
            """);

            for (var entry : map.entrySet()) {

                EconomyAccount acc = entry.getValue();

                ps.setString(1, entry.getKey().toString());
                ps.setDouble(2, acc.money);
                ps.setDouble(3, acc.coins);
                ps.setDouble(4, acc.tokens);

                ps.addBatch();
            }

            ps.executeBatch();

        } catch (Exception e) {
            logger.error("Failed to save economy", e);
        }
    }
}