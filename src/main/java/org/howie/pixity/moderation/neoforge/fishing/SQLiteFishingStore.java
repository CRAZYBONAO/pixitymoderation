package org.howie.pixity.moderation.neoforge.fishing;

import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;

public class SQLiteFishingStore {

    private final Connection conn;

    public SQLiteFishingStore(Logger logger, String url) {
        try {
            this.conn = DriverManager.getConnection(url);
            logger.info("[FishingDB] Connected");


            FishingDatabase.init(this);

        } catch (Exception e) {
            throw new RuntimeException("Failed to init fishing DB", e);
        }
    }

    public Connection getConnection() {
        return conn;
    }
}