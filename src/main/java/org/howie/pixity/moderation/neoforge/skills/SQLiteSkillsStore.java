package org.howie.pixity.moderation.neoforge.skills;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SQLiteSkillsStore {

    private final Connection conn;
    private final Gson gson = new Gson();

    public SQLiteSkillsStore(Logger logger, String url) {
        try {
            this.conn = DriverManager.getConnection(url);
            logger.info("[SkillsDB] Connected");

            createTable();

        } catch (Exception e) {
            throw new RuntimeException("Failed to init skills DB", e);
        }
    }

    private void createTable() {

        try (Statement st = conn.createStatement()) {

            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS skills_data (
                    uuid TEXT PRIMARY KEY,
                    data TEXT
                )
            """);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public Map<UUID, SkillData> load() {

        Map<UUID, SkillData> map = new HashMap<>();

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM skills_data")) {

            while (rs.next()) {

                UUID uuid = UUID.fromString(rs.getString("uuid"));
                String json = rs.getString("data");

                SkillData data = gson.fromJson(json, SkillData.class);

                if (data == null) data = new SkillData();

                map.put(uuid, data);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }




    public void save(Map<UUID, SkillData> data) {

        try (PreparedStatement ps = conn.prepareStatement("""
            INSERT INTO skills_data(uuid, data)
            VALUES(?, ?)
            ON CONFLICT(uuid) DO UPDATE SET data = excluded.data
        """)) {

            for (Map.Entry<UUID, SkillData> entry : data.entrySet()) {

                ps.setString(1, entry.getKey().toString());
                ps.setString(2, gson.toJson(entry.getValue()));

                ps.addBatch();
            }

            ps.executeBatch();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return conn;
    }
}