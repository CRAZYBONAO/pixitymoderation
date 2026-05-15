package org.howie.pixity.moderation.neoforge.contribution;

import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SQLiteContributionStore {

    private final Logger logger;

    private final String url;

    public SQLiteContributionStore(
            Logger logger,
            String url
    ) {

        this.logger = logger;
        this.url = url;

        init();
    }

    private Connection connect()
            throws SQLException {

        return DriverManager.getConnection(url);
    }

    private void init() {

        try (
                Connection conn = connect()
        ) {

            conn.createStatement().execute(
                    """
                    CREATE TABLE IF NOT EXISTS contributions (
                        uuid TEXT PRIMARY KEY,
                        current REAL,
                        lifetime REAL
                    )
                    """
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to init contribution DB",
                    e
            );
        }
    }

    public Map<UUID, ContributionData> load() {

        Map<UUID, ContributionData> map =
                new HashMap<>();

        try (
                Connection conn = connect();

                ResultSet rs = conn
                        .createStatement()
                        .executeQuery(
                                "SELECT * FROM contributions"
                        )
        ) {

            while (rs.next()) {

                UUID uuid = UUID.fromString(
                        rs.getString("uuid")
                );

                ContributionData data =
                        new ContributionData();

                data.setCurrent(
                        rs.getDouble("current")
                );

                data.setLifetime(
                        rs.getDouble("lifetime")
                );

                map.put(uuid, data);
            }

        } catch (Exception e) {

            logger.error(
                    "Failed loading contributions",
                    e
            );
        }

        return map;
    }

    public void save(
            Map<UUID, ContributionData> map
    ) {

        try (
                Connection conn = connect()
        ) {

            conn.createStatement().execute(
                    "DELETE FROM contributions"
            );

            PreparedStatement ps =
                    conn.prepareStatement(
                            """
                            INSERT INTO contributions
                            (uuid, current, lifetime)
                            VALUES (?, ?, ?)
                            """
                    );

            for (var entry : map.entrySet()) {

                ContributionData data =
                        entry.getValue();

                ps.setString(
                        1,
                        entry.getKey().toString()
                );

                ps.setDouble(
                        2,
                        data.getCurrent()
                );

                ps.setDouble(
                        3,
                        data.getLifetime()
                );

                ps.addBatch();
            }

            ps.executeBatch();

        } catch (Exception e) {

            logger.error(
                    "Failed saving contributions",
                    e
            );
        }
    }
}