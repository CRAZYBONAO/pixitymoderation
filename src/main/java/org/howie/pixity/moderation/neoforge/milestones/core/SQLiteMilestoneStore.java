package org.howie.pixity.moderation.neoforge.milestones.core;

import org.apache.logging.log4j.Logger;

import java.io.File;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class SQLiteMilestoneStore {





    private final Logger logger;

    private final File file;

    private final String url;





    public SQLiteMilestoneStore(
            Logger logger,
            File dataFolder
    ) {

        this.logger = logger;

        this.file =
                new File(
                        dataFolder,
                        "milestones.db"
                );

        this.url =
                "jdbc:sqlite:" +
                        file.getAbsolutePath();

        init();
    }





    private void init() {

        try (
                Connection con =
                        DriverManager.getConnection(url);

                PreparedStatement ps =
                        con.prepareStatement(
                                """
                                CREATE TABLE IF NOT EXISTS milestone_claims (
                                
                                    uuid TEXT NOT NULL,
                                    milestone_id TEXT NOT NULL,
                                    level INTEGER NOT NULL,
                                    
                                    PRIMARY KEY(uuid, milestone_id, level)
                                )
                                """
                        )
        ) {

            ps.execute();

            logger.info(
                    "[Milestones] SQLite milestone tables initialized"
            );

        } catch (Exception e) {

            logger.error(
                    "Failed to initialize milestone database",
                    e
            );
        }
    }





    public Map<UUID, MilestonePlayerData> load() {

        Map<UUID, MilestonePlayerData> loaded =
                new HashMap<>();

        try (
                Connection con =
                        DriverManager.getConnection(url);

                PreparedStatement ps =
                        con.prepareStatement(
                                """
                                SELECT *
                                FROM milestone_claims
                                """
                        );

                ResultSet rs =
                        ps.executeQuery()
        ) {

            while (rs.next()) {

                UUID uuid =
                        UUID.fromString(
                                rs.getString("uuid")
                        );

                String milestoneId =
                        rs.getString("milestone_id");

                int level =
                        rs.getInt("level");

                MilestonePlayerData data =
                        loaded.computeIfAbsent(
                                uuid,
                                k -> new MilestonePlayerData()
                        );

                data.claim(
                        milestoneId,
                        level
                );
            }

            logger.info(
                    "[Milestones] Loaded " +
                            loaded.size() +
                            " player milestone profiles"
            );

        } catch (Exception e) {

            logger.error(
                    "Failed to load milestone data",
                    e
            );
        }

        return loaded;
    }





    public void save(
            Map<UUID, MilestonePlayerData> data
    ) {

        try (
                Connection con =
                        DriverManager.getConnection(url)
        ) {

            con.setAutoCommit(false);





            try (
                    PreparedStatement delete =
                            con.prepareStatement(
                                    """
                                    DELETE FROM milestone_claims
                                    """
                            )
            ) {

                delete.executeUpdate();
            }





            try (
                    PreparedStatement insert =
                            con.prepareStatement(
                                    """
                                    INSERT INTO milestone_claims
                                    (
                                        uuid,
                                        milestone_id,
                                        level
                                    )
                                    VALUES (?, ?, ?)
                                    """
                            )
            ) {

                for (Map.Entry<UUID, MilestonePlayerData> entry :
                        data.entrySet()) {

                    UUID uuid =
                            entry.getKey();

                    MilestonePlayerData playerData =
                            entry.getValue();

                    for (Map.Entry<String, Set<Integer>> claim :
                            playerData.getAll().entrySet()) {

                        String milestoneId =
                                claim.getKey();

                        for (Integer level :
                                claim.getValue()) {

                            insert.setString(
                                    1,
                                    uuid.toString()
                            );

                            insert.setString(
                                    2,
                                    milestoneId
                            );

                            insert.setInt(
                                    3,
                                    level
                            );

                            insert.addBatch();
                        }
                    }
                }

                insert.executeBatch();
            }

            con.commit();

        } catch (Exception e) {

            logger.error(
                    "Failed to save milestone data",
                    e
            );
        }
    }
}