package org.howie.pixity.moderation.neoforge.fishing;

import org.howie.pixity.moderation.neoforge.fishing.deliveries.Delivery;
import org.howie.pixity.moderation.neoforge.fishing.deliveries.DeliveryProgress;
import org.howie.pixity.moderation.neoforge.fishing.deliveries.DeliveryReward;
import org.howie.pixity.moderation.neoforge.fishing.gui.FishingLeaderboardGui;

import java.sql.*;
import java.util.*;

public class FishingDatabase {

    private static Connection conn;
    public record RecordResult(boolean longest, boolean shortest) {}




    public static void init(SQLiteFishingStore store) {
        conn = store.getConnection();
        createTable();
        ensureUpgradeColumns();
        createDeliveryTable();
        createDeliveryProgressTable();
        createCodexStatsTable();
    }

    private static void createDeliveryTable() {

        try {
            Statement st = conn.createStatement();

            st.executeUpdate("""
            CREATE TABLE IF NOT EXISTS fishing_deliveries (
                uuid TEXT,
                slot INTEGER,
                tier INTEGER,
                npc TEXT,
                start_time INTEGER,
                end_time INTEGER,
                started INTEGER,
                entropy INTEGER,

                fish TEXT,
                rewards TEXT,

                PRIMARY KEY (uuid, slot)
            )
        """);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createCodexStatsTable() {

        try {

            Statement st =
                    conn.createStatement();

            st.executeUpdate("""
            CREATE TABLE IF NOT EXISTS fishing_codex_stats (

                uuid TEXT,
                fish_id TEXT,

                caught INTEGER DEFAULT 0,
                smallest DOUBLE DEFAULT 999999,
                largest DOUBLE DEFAULT 0,

                PRIMARY KEY (uuid, fish_id)
            )
        """);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    public static void updateFishStats(
            UUID uuid,
            String fishId,
            double size
    ) {

        try {

            PreparedStatement ps =
                    conn.prepareStatement("""
                    INSERT INTO fishing_codex_stats
                    (uuid, fish_id, caught, smallest, largest)

                    VALUES (?, ?, 1, ?, ?)

                    ON CONFLICT(uuid, fish_id)
                    DO UPDATE SET

                    caught = caught + 1,

                    smallest = MIN(
                        smallest,
                        excluded.smallest
                    ),

                    largest = MAX(
                        largest,
                        excluded.largest
                    )
                """);

            ps.setString(1, uuid.toString());
            ps.setString(2, fishId);

            ps.setDouble(3, size);
            ps.setDouble(4, size);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getFishCaught(
            UUID uuid,
            String fishId
    ) {

        try {

            PreparedStatement ps =
                    conn.prepareStatement("""
                    SELECT caught
                    FROM fishing_codex_stats
                    WHERE uuid = ?
                    AND fish_id = ?
                """);

            ps.setString(1, uuid.toString());
            ps.setString(2, fishId);

            ResultSet rs =
                    ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("caught");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static double getLargestFish(
            UUID uuid,
            String fishId
    ) {

        try {

            PreparedStatement ps =
                    conn.prepareStatement("""
                    SELECT largest
                    FROM fishing_codex_stats
                    WHERE uuid = ?
                    AND fish_id = ?
                """);

            ps.setString(1, uuid.toString());
            ps.setString(2, fishId);

            ResultSet rs =
                    ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("largest");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static double getSmallestFish(
            UUID uuid,
            String fishId
    ) {

        try {

            PreparedStatement ps =
                    conn.prepareStatement("""
                    SELECT smallest
                    FROM fishing_codex_stats
                    WHERE uuid = ?
                    AND fish_id = ?
                """);

            ps.setString(1, uuid.toString());
            ps.setString(2, fishId);

            ResultSet rs =
                    ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("smallest");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    private static void createDeliveryProgressTable() {
        try {
            Statement st = conn.createStatement();

            st.executeUpdate("""
            CREATE TABLE IF NOT EXISTS fishing_delivery_progress (
                uuid TEXT PRIMARY KEY,
                fish_caught INTEGER,
                squid_killed INTEGER,
                dolphin_killed INTEGER
            )
        """);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DeliveryProgress loadDeliveryProgress(UUID uuid) {

        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM fishing_delivery_progress WHERE uuid = ?"
            );

            ps.setString(1, uuid.toString());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                DeliveryProgress p = new DeliveryProgress();

                p.fishCaught = rs.getInt("fish_caught");
                p.squidKilled = rs.getInt("squid_killed");
                p.dolphinKilled = rs.getInt("dolphin_killed");

                return p;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new DeliveryProgress();
    }

    public static void saveDeliveryProgress(UUID uuid, DeliveryProgress p) {

        try {
            PreparedStatement ps = conn.prepareStatement("""
            INSERT INTO fishing_delivery_progress (uuid, fish_caught, squid_killed, dolphin_killed)
            VALUES (?, ?, ?, ?)
            ON CONFLICT(uuid) DO UPDATE SET
                fish_caught = excluded.fish_caught,
                squid_killed = excluded.squid_killed,
                dolphin_killed = excluded.dolphin_killed
        """);

            ps.setString(1, uuid.toString());
            ps.setInt(2, p.fishCaught);
            ps.setInt(3, p.squidKilled);
            ps.setInt(4, p.dolphinKilled);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void saveDeliveries(UUID uuid, List<Delivery> list) {

        ensure(uuid);

        try {


            PreparedStatement delete = conn.prepareStatement(
                    "DELETE FROM fishing_deliveries WHERE uuid = ?"
            );
            delete.setString(1, uuid.toString());
            delete.executeUpdate();


            for (Delivery d : list) {

                PreparedStatement ps = conn.prepareStatement("""
                INSERT INTO fishing_deliveries
                (uuid, slot, tier, npc, start_time, end_time, started, entropy, fish, rewards)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """);

                ps.setString(1, uuid.toString());
                ps.setInt(2, d.slot);
                ps.setInt(3, d.tier);
                ps.setString(4, d.npcName);
                ps.setLong(5, d.startTime);
                ps.setLong(6, d.endTime);
                ps.setInt(7, d.started ? 1 : 0);
                ps.setInt(8, d.entropyReward);


                ps.setString(9, serializeFish(d));


                ps.setString(10, serializeRewards(d));

                ps.executeUpdate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Delivery> loadDeliveries(UUID uuid) {

        List<Delivery> list = new ArrayList<>();

        try {

            PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM fishing_deliveries WHERE uuid = ? ORDER BY slot"
            );

            ps.setString(1, uuid.toString());

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Delivery d = new Delivery(
                        rs.getInt("slot"),
                        rs.getInt("tier")
                );

                d.npcName = rs.getString("npc");
                d.startTime = rs.getLong("start_time");
                d.endTime = rs.getLong("end_time");
                d.started = rs.getInt("started") == 1;
                d.entropyReward = rs.getInt("entropy");


                deserializeFish(d, rs.getString("fish"));


                deserializeRewards(d, rs.getString("rewards"));

                list.add(d);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    private static String serializeFish(Delivery d) {

        StringBuilder sb = new StringBuilder();

        for (var e : d.requiredFish.entrySet()) {
            sb.append(e.getKey()).append(":").append(e.getValue()).append(";");
        }

        return sb.toString();
    }

    private static void deserializeFish(Delivery d, String data) {

        if (data == null || data.isEmpty()) return;

        for (String part : data.split(";")) {

            if (part.isEmpty()) continue;

            String[] split = part.split(":");

            d.requiredFish.put(split[0], Integer.parseInt(split[1]));
        }
    }

    private static String serializeRewards(Delivery d) {

        StringBuilder sb = new StringBuilder();

        for (DeliveryReward r : d.rolledRewards) {

            sb.append(r.type).append("|")
                    .append(r.value).append("|")
                    .append(r.rarity == null ? "" : r.rarity).append("|")
                    .append(r.display == null ? "" : r.display)
                    .append(";");
        }

        return sb.toString();
    }

    private static void deserializeRewards(Delivery d, String data) {

        if (data == null || data.isEmpty()) return;

        for (String part : data.split(";")) {

            if (part.isEmpty()) continue;

            String[] split = part.split("\\|");

            DeliveryReward r = new DeliveryReward();

            r.type = split[0];
            r.value = split[1];

            if (split.length > 2 && !split[2].isEmpty())
                r.rarity = split[2];

            if (split.length > 3 && !split[3].isEmpty())
                r.display = split[3];

            d.rolledRewards.add(r);
        }
    }

    public static ResultSet getStats(UUID uuid) {

        ensure(uuid);

        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM fishing_stats WHERE uuid = ?"
            );

            ps.setString(1, uuid.toString());

            return ps.executeQuery();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static ResultSet getTopEntropy(int limit) {

        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT uuid, entropy FROM fishing_stats ORDER BY entropy DESC LIMIT ?"
            );

            ps.setInt(1, limit);

            return ps.executeQuery();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }




    private static void createTable() {

        try {
            Statement st = conn.createStatement();

            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS fishing_stats (
                    uuid TEXT PRIMARY KEY,

                    entropy INTEGER DEFAULT 0,

                    custom_fish INTEGER DEFAULT 0,
                    vanilla_fish INTEGER DEFAULT 0,

                    bronze INTEGER DEFAULT 0,
                    silver INTEGER DEFAULT 0,
                    gold INTEGER DEFAULT 0,
                    diamond INTEGER DEFAULT 0,
                    platinum INTEGER DEFAULT 0,
                    mythical INTEGER DEFAULT 0,

                    money_made INTEGER DEFAULT 0,
                    entropy_gained INTEGER DEFAULT 0,


                    crabs INTEGER DEFAULT 0,
                    squids INTEGER DEFAULT 0,
                    dolphins INTEGER DEFAULT 0,

                    level INTEGER DEFAULT 0,
                    skill_points INTEGER DEFAULT 0,
                    xp INTEGER DEFAULT 0,
                    gutting_skill INTEGER DEFAULT 0,
                    luck_skill INTEGER DEFAULT 0,
                    augment_skill INTEGER DEFAULT 0,
                    
                    divine_unlocked INTEGER DEFAULT 0,
                    combo_unlocked INTEGER DEFAULT 0,
                    infusion_unlocked INTEGER DEFAULT 0,
                    codex TEXT DEFAULT '',
                    longest_fish INT DEFAULT 0,
                    shortest_fish INT DEFAULT 999999,
                    fish_sold INTEGER DEFAULT 0,
                    total_fish INTEGER DEFAULT 0,
                    
                    events_won INTEGER DEFAULT 0,
                    tournaments_won INTEGER DEFAULT 0,
                    deliveries_completed INTEGER DEFAULT 0,
                    
                    crabs_killed INTEGER DEFAULT 0,
                    squids_killed INTEGER DEFAULT 0,
                    dolphins_killed INTEGER DEFAULT 0,
                    delivery_capacity INT DEFAULT 0,
                    delivery_jetboat INT DEFAULT 0,
                    delivery_expert INT DEFAULT 0,
                    delivery_payrise INT DEFAULT 0,
                    delivery_lucky INT DEFAULT 0,
                    
                    lure_tier TEXT DEFAULT '',
                    lure_end BIGINT DEFAULT 0
                )
            """);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setLure(UUID uuid, String tier, long endTime) {

        ensure(uuid);

        try {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE fishing_stats SET lure_tier = ?, lure_end = ? WHERE uuid = ?"
            );

            ps.setString(1, tier);
            ps.setLong(2, endTime);
            ps.setString(3, uuid.toString());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        return conn;
    }

    public static int getTierCount(UUID uuid, FishTier tier) {

        ensure(uuid);

        try {
            String column = tier.name().toLowerCase();

            PreparedStatement ps = conn.prepareStatement(
                    "SELECT " + column + " FROM fishing_stats WHERE uuid = ?"
            );

            ps.setString(1, uuid.toString());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(column);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static int getTotalFish(UUID uuid) {

        ensure(uuid);

        try {
            PreparedStatement ps = conn.prepareStatement(
                    """
                    SELECT bronze, silver, gold,
                           diamond, platinum, mythical
                    FROM fishing_stats
                    WHERE uuid = ?
                    """
            );

            ps.setString(1, uuid.toString());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("bronze")
                        + rs.getInt("silver")
                        + rs.getInt("gold")
                        + rs.getInt("diamond")
                        + rs.getInt("platinum")
                        + rs.getInt("mythical");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    private static void ensureUpgradeColumns() {

        addColumn("fishing_stats", "xp", "INTEGER DEFAULT 0");
        addColumn("fishing_stats", "codex", "TEXT DEFAULT ''");
        addColumn("fishing_stats", "total_fish", "INTEGER DEFAULT 0");
        addColumn("fishing_stats", "fish_sold", "INTEGER DEFAULT 0");

        addColumn("fishing_stats", "longest_fish", "INTEGER DEFAULT 0");
        addColumn("fishing_stats", "shortest_fish", "INTEGER DEFAULT 999999");

        addColumn("fishing_stats", "events_won", "INTEGER DEFAULT 0");
        addColumn("fishing_stats", "tournaments_won", "INTEGER DEFAULT 0");
        addColumn("fishing_stats", "deliveries_completed", "INTEGER DEFAULT 0");

        addColumn("fishing_stats", "crabs_killed", "INTEGER DEFAULT 0");
        addColumn("fishing_stats", "squids_killed", "INTEGER DEFAULT 0");
        addColumn("fishing_stats", "dolphins_killed", "INTEGER DEFAULT 0");

        addColumn("fishing_stats", "delivery_capacity", "INTEGER DEFAULT 0");
        addColumn("fishing_stats", "delivery_jetboat", "INTEGER DEFAULT 0");
        addColumn("fishing_stats", "delivery_expert", "INTEGER DEFAULT 0");
        addColumn("fishing_stats", "delivery_payrise", "INTEGER DEFAULT 0");
        addColumn("fishing_stats", "delivery_lucky", "INTEGER DEFAULT 0");

        addColumn("fishing_stats", "gutting_skill", "INTEGER DEFAULT 0");
        addColumn("fishing_stats", "luck_skill", "INTEGER DEFAULT 0");
        addColumn("fishing_stats", "augment_skill", "INTEGER DEFAULT 0");

        addColumn("fishing_stats", "divine_unlocked", "INTEGER DEFAULT 0");
        addColumn("fishing_stats", "combo_unlocked", "INTEGER DEFAULT 0");
        addColumn("fishing_stats", "infusion_unlocked", "INTEGER DEFAULT 0");

        addColumn("fishing_stats", "lure_tier", "TEXT DEFAULT ''");
        addColumn("fishing_stats", "lure_end", "BIGINT DEFAULT 0");

        addColumn("fishing_stats", "combo_skill", "INTEGER DEFAULT 0");
    }

    public static String getLureTier(UUID uuid) {

        ensure(uuid);

        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT lure_tier, lure_end FROM fishing_stats WHERE uuid = ?"
            );

            ps.setString(1, uuid.toString());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                long end = rs.getLong("lure_end");

                if (System.currentTimeMillis() > end) return null;

                return rs.getString("lure_tier");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static int getLevel(UUID uuid) {
        try {
            var rs = getStats(uuid);

            if (rs != null && rs.next()) {
                return rs.getInt("level");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static int getSkill(UUID uuid, String column) {
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT " + column + " FROM fishing_stats WHERE uuid = ?"
            );

            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getInt(column);

        } catch (Exception e) {

            System.out.println("[FishingDB] Missing column: " + column);
        }
        return 0;
    }

    public static void upgradeSkill(UUID uuid, String column) {
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE fishing_stats SET " + column + " = " + column + " + 1 WHERE uuid = ?"
            );

            ps.setString(1, uuid.toString());
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isUnlocked(UUID uuid, String column) {
        return getSkill(uuid, column) == 1;
    }

    public static void unlock(UUID uuid, String column) {
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE fishing_stats SET " + column + " = 1 WHERE uuid = ?"
            );

            ps.setString(1, uuid.toString());
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public static void ensure(UUID uuid) {

        try {
            PreparedStatement ps = conn.prepareStatement("""
                INSERT OR IGNORE INTO fishing_stats (uuid)
                VALUES (?)
            """);

            ps.setString(1, uuid.toString());
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addXP(UUID uuid, int amount) {

        ensure(uuid);

        try {
            PreparedStatement ps = conn.prepareStatement("""
            UPDATE fishing_stats
            SET xp = xp + ?
            WHERE uuid = ?
        """);

            ps.setInt(1, amount);
            ps.setString(2, uuid.toString());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getXP(UUID uuid) {

        ensure(uuid);

        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT xp FROM fishing_stats WHERE uuid = ?"
            );

            ps.setString(1, uuid.toString());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("xp");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static void setLevel(UUID uuid, int level) {

        ensure(uuid);

        try {
            PreparedStatement ps = conn.prepareStatement("""
            UPDATE fishing_stats
            SET level = ?
            WHERE uuid = ?
        """);

            ps.setInt(1, level);
            ps.setString(2, uuid.toString());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addSkillPoints(UUID uuid, int amount) {

        ensure(uuid);

        try {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE fishing_stats SET skill_points = skill_points + ? WHERE uuid = ?"
            );

            ps.setInt(1, amount);
            ps.setString(2, uuid.toString());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getSkillPoints(UUID uuid) {

        ensure(uuid);

        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT skill_points FROM fishing_stats WHERE uuid = ?"
            );

            ps.setString(1, uuid.toString());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("skill_points");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }




    public static void addEntropy(UUID uuid, int amount) {

        ensure(uuid);

        try {
            PreparedStatement ps = conn.prepareStatement("""
                UPDATE fishing_stats
                SET entropy = entropy + ?,
                    entropy_gained = entropy_gained + ?
                WHERE uuid = ?
            """);

            ps.setInt(1, amount);
            ps.setInt(2, amount);
            ps.setString(3, uuid.toString());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public static int getEntropy(UUID uuid) {

        ensure(uuid);

        try {
            PreparedStatement ps = conn.prepareStatement("""
                SELECT entropy FROM fishing_stats WHERE uuid = ?
            """);

            ps.setString(1, uuid.toString());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("entropy");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }




    public static void addFish(UUID uuid, FishTier tier) {

        ensure(uuid);

        String column = switch (tier) {
            case BRONZE -> "bronze";
            case SILVER -> "silver";
            case GOLD -> "gold";
            case DIAMOND -> "diamond";
            case PLATINUM -> "platinum";
            case MYTHICAL -> "mythical";
        };

        try {
            String sql = "UPDATE fishing_stats " +
                    "SET custom_fish = custom_fish + 1, " +
                    "total_fish = total_fish + 1, " +
                    column + " = " + column + " + 1 " +
                    "WHERE uuid = ?";

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, uuid.toString());
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ResultSet getTopTotalFish(int limit) throws Exception {
        PreparedStatement ps = conn.prepareStatement(
                "SELECT uuid, total_fish FROM fishing_stats ORDER BY total_fish DESC LIMIT ?"
        );
        ps.setInt(1, limit);
        return ps.executeQuery();
    }

    public static ResultSet getTopLevel(int limit) throws Exception {
        PreparedStatement ps = conn.prepareStatement(
                "SELECT uuid, level FROM fishing_stats ORDER BY level DESC LIMIT ?"
        );
        ps.setInt(1, limit);
        return ps.executeQuery();
    }

    public static ResultSet getTopTier(String column, int limit) throws Exception {
        PreparedStatement ps = conn.prepareStatement(
                "SELECT uuid, " + column + " FROM fishing_stats ORDER BY " + column + " DESC LIMIT ?"
        );
        ps.setInt(1, limit);
        return ps.executeQuery();
    }




    public static RecordResult updateSize(UUID uuid, int size) {

        ensure(uuid);

        boolean newLongest = false;
        boolean newShortest = false;

        try {

            PreparedStatement get = conn.prepareStatement(
                    "SELECT longest_fish, shortest_fish FROM fishing_stats WHERE uuid = ?"
            );
            get.setString(1, uuid.toString());

            ResultSet rs = get.executeQuery();

            int longest = 0;
            int shortest = Integer.MAX_VALUE;

            if (rs.next()) {
                longest = rs.getInt("longest_fish");
                shortest = rs.getInt("shortest_fish");
            }


            if (size > longest) newLongest = true;
            if (size < shortest) newShortest = true;


            PreparedStatement ps = conn.prepareStatement(
                    """
                    UPDATE fishing_stats SET
                    longest_fish = MAX(longest_fish, ?),
                    shortest_fish = MIN(shortest_fish, ?)
                    WHERE uuid = ?
                    """
            );

            ps.setInt(1, size);
            ps.setInt(2, size);
            ps.setString(3, uuid.toString());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new RecordResult(newLongest, newShortest);
    }
    public static void unlockFish(UUID uuid, String fishId) {

        ensure(uuid);

        try {
            PreparedStatement get = conn.prepareStatement(
                    "SELECT codex FROM fishing_stats WHERE uuid = ?"
            );

            get.setString(1, uuid.toString());

            ResultSet rs = get.executeQuery();

            String current = "";

            if (rs.next()) {
                current = rs.getString("codex");
            }

            if (current == null) current = "";

            Set<String> set = new HashSet<>();

            if (!current.isEmpty()) {
                set.addAll(Arrays.asList(current.split(",")));
            }

            if (!set.contains(fishId)) {
                set.add(fishId);

                String updated = String.join(",", set);

                PreparedStatement update = conn.prepareStatement(
                        "UPDATE fishing_stats SET codex = ? WHERE uuid = ?"
                );

                update.setString(1, updated);
                update.setString(2, uuid.toString());
                update.executeUpdate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean hasFish(UUID uuid, String fishId) {

        ensure(uuid);

        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT codex FROM fishing_stats WHERE uuid = ?"
            );

            ps.setString(1, uuid.toString());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                String data = rs.getString("codex");

                if (data == null || data.isEmpty()) return false;

                return Arrays.asList(data.split(",")).contains(fishId);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
    public static int getLongest(UUID uuid) {
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT longest_fish FROM fishing_stats WHERE uuid = ?"
            );
            ps.setString(1, uuid.toString());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("longest_fish");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getShortest(UUID uuid) {
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT shortest_fish FROM fishing_stats WHERE uuid = ?"
            );
            ps.setString(1, uuid.toString());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("shortest_fish");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static ResultSet getTopLongest(int limit) throws Exception {
        PreparedStatement ps = conn.prepareStatement(
                "SELECT uuid, longest_fish FROM fishing_stats ORDER BY longest_fish DESC LIMIT ?"
        );
        ps.setInt(1, limit);
        return ps.executeQuery();
    }

    public static ResultSet getTopShortest(int limit) throws Exception {
        PreparedStatement ps = conn.prepareStatement(
                "SELECT uuid, shortest_fish FROM fishing_stats WHERE shortest_fish < 999999 ORDER BY shortest_fish ASC LIMIT ?"
        );
        ps.setInt(1, limit);
        return ps.executeQuery();
    }

    public static int getRank(UUID uuid, String column, boolean ascending) {

        try {

            String operator = ascending ? "<" : ">";

            PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) + 1 AS rank FROM fishing_stats WHERE " + column + " " + operator +
                            " (SELECT " + column + " FROM fishing_stats WHERE uuid = ?)"
            );

            ps.setString(1, uuid.toString());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("rank");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    public static void addMoney(UUID uuid, int amount) {

        ensure(uuid);

        try {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE fishing_stats SET money_made = money_made + ? WHERE uuid = ?"
            );

            ps.setInt(1, amount);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void handleLevelUp(UUID uuid) {

        ensure(uuid);

        int xp = getXP(uuid);
        int level = getLevel(uuid);

        int needed = getXPRequired(level);

        boolean leveled = false;

        while (xp >= needed) {

            xp -= needed;
            level++;

            leveled = true;

            setLevel(uuid, level);
            addSkillPoints(uuid, 1);

            needed = getXPRequired(level);
        }


        try {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE fishing_stats SET xp = ? WHERE uuid = ?"
            );

            ps.setInt(1, xp);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getXPRequired(int level) {


        return 100 + (level * 50) + (level * level * 10);
    }

    public static void addFishSold(UUID uuid, int amount) {

        ensure(uuid);

        try {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE fishing_stats SET fish_sold = fish_sold + ? WHERE uuid = ?"
            );

            ps.setInt(1, amount);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void incrementStat(UUID uuid, String column) {

        ensure(uuid);

        try {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE fishing_stats SET " + column + " = " + column + " + 1 WHERE uuid = ?"
            );

            ps.setString(1, uuid.toString());
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ResultSet getLeaderboard(
            org.howie.pixity.moderation.neoforge.fishing.gui.FishingLeaderboardGui.Mode mode,
            int limit) throws Exception {

        String column = switch (mode) {
            case TOTAL_FISH -> "total_fish";
            case LEVEL -> "level";
            case BRONZE -> "bronze";
            case SILVER -> "silver";
            case GOLD -> "gold";
            case DIAMOND -> "diamond";
            case PLATINUM -> "platinum";
            case MYTHICAL -> "mythical";
            case LONGEST -> "longest_fish";
            case SHORTEST -> "shortest_fish";

            case EVENTS -> "events_won";
            case TOURNAMENTS -> "tournaments_won";
            case DELIVERIES -> "deliveries_completed";

            case SQUIDS -> "squids_killed";
            case DOLPHINS -> "dolphins_killed";
            case CRABS -> "crabs_killed";

            default -> "entropy";
        };

        String order = (mode == FishingLeaderboardGui.Mode.SHORTEST)
                ? "ASC" : "DESC";

        PreparedStatement ps = conn.prepareStatement(
                "SELECT uuid, " + column + " AS value FROM fishing_stats ORDER BY " + column + " " + order + " LIMIT ?"
        );

        ps.setInt(1, limit);
        return ps.executeQuery();
    }

    public static int getPlayerRank(
            UUID uuid,
            org.howie.pixity.moderation.neoforge.fishing.gui.FishingLeaderboardGui.Mode mode) {

        try {

            String column = switch (mode) {
                case TOTAL_FISH -> "total_fish";
                case LEVEL -> "level";
                case BRONZE -> "bronze";
                case SILVER -> "silver";
                case GOLD -> "gold";
                case DIAMOND -> "diamond";
                case PLATINUM -> "platinum";
                case MYTHICAL -> "mythical";
                case LONGEST -> "longest_fish";
                case SHORTEST -> "shortest_fish";

                case EVENTS -> "events_won";
                case TOURNAMENTS -> "tournaments_won";
                case DELIVERIES -> "deliveries_completed";

                case SQUIDS -> "squids_killed";
                case DOLPHINS -> "dolphins_killed";
                case CRABS -> "crabs_killed";

                default -> "entropy";
            };

            String order = (mode == org.howie.pixity.moderation.neoforge.fishing.gui.FishingLeaderboardGui.Mode.SHORTEST)
                    ? "ASC" : "DESC";

            PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) + 1 AS rank FROM fishing_stats " +
                            "WHERE " + column + " " + (order.equals("DESC") ? ">" : "<") + " " +
                            "(SELECT " + column + " FROM fishing_stats WHERE uuid = ?)"
            );

            ps.setString(1, uuid.toString());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("rank");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    public static int getPlayerValue(
            UUID uuid,
            org.howie.pixity.moderation.neoforge.fishing.gui.FishingLeaderboardGui.Mode mode) {

        try {

            String column = switch (mode) {
                case TOTAL_FISH -> "total_fish";
                case LEVEL -> "level";
                case BRONZE -> "bronze";
                case SILVER -> "silver";
                case GOLD -> "gold";
                case DIAMOND -> "diamond";
                case PLATINUM -> "platinum";
                case MYTHICAL -> "mythical";
                case LONGEST -> "longest_fish";
                case SHORTEST -> "shortest_fish";

                case EVENTS -> "events_won";
                case TOURNAMENTS -> "tournaments_won";
                case DELIVERIES -> "deliveries_completed";

                case SQUIDS -> "squids_killed";
                case DOLPHINS -> "dolphins_killed";
                case CRABS -> "crabs_killed";

                default -> "entropy";
            };

            PreparedStatement ps = conn.prepareStatement(
                    "SELECT " + column + " FROM fishing_stats WHERE uuid = ?"
            );

            ps.setString(1, uuid.toString());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(column);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static int getDeliveryUpgrade(UUID uuid, String key) {
        ensure(uuid);

        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT " + key + " FROM fishing_stats WHERE uuid = ?"
            );
            ps.setString(1, uuid.toString());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(key);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static void addDeliveryUpgrade(UUID uuid, String key, int amount) {
        ensure(uuid);

        try {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE fishing_stats SET " + key + " = " + key + " + ? WHERE uuid = ?"
            );

            ps.setInt(1, amount);
            ps.setString(2, uuid.toString());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeEntropy(UUID uuid, int amount) {

        ensure(uuid);

        try {
            PreparedStatement ps = conn.prepareStatement("""
            UPDATE fishing_stats
            SET entropy = MAX(0, entropy - ?)
            WHERE uuid = ?
        """);

            ps.setInt(1, amount);
            ps.setString(2, uuid.toString());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean columnExists(String table, String column) {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("PRAGMA table_info(" + table + ")")) {

            while (rs.next()) {
                if (rs.getString("name").equalsIgnoreCase(column)) {
                    return true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private static void addColumn(String table, String column, String definition) {
        try {
            if (columnExists(table, column)) return;

            try (Statement st = conn.createStatement()) {
                st.executeUpdate("ALTER TABLE " + table + " ADD COLUMN " + column + " " + definition);
                System.out.println("[FishingDB] Added column: " + column);
            }

        } catch (Exception e) {
            System.out.println("[FishingDB] Failed adding column: " + column);
            e.printStackTrace();
        }
    }





}