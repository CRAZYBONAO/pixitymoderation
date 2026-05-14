package org.howie.pixity.moderation.neoforge.stats;

import java.sql.*;
import java.util.UUID;

public class PlayerStatsDatabase {

    private static String url;

    private static final java.util.Set<String> VALID_COLUMNS =
            java.util.Set.of(





                    "bronze_fish_caught",
                    "silver_fish_caught",
                    "gold_fish_caught",
                    "diamond_fish_caught",
                    "platinum_fish_caught",
                    "mythical_fish_caught",
                    "squid_kills",
                    "dolphin_kills",
                    "crabs_killed",
                    "total_fish_caught",





                    "blocks_mined",
                    "total_ores_mined",
                    "coal_ore_mined",
                    "copper_ore_mined",
                    "iron_ore_mined",
                    "gold_ore_mined",
                    "lapis_ore_mined",
                    "redstone_ore_mined",
                    "diamond_ore_mined",
                    "emerald_ore_mined",
                    "ancient_debris_mined",
                    "nether_gold_ore_mined",
                    "quartz_ore_mined",

                    "dawn_stone_ore_mined",
                    "dusk_stone_ore_mined",
                    "fire_stone_ore_mined",
                    "ice_stone_ore_mined",
                    "leaf_stone_ore_mined",
                    "moon_stone_ore_mined",
                    "shiny_stone_ore_mined",
                    "sun_stone_ore_mined",
                    "thunder_stone_ore_mined",
                    "water_stone_ore_mined",

                    "overworld_ores_mined",
                    "nether_ores_mined",
                    "deepslate_ores_mined",
                    "cobblemon_ores_mined",





                    "total_crops_harvested",
                    "wheat_crops_harvested",
                    "potato_crops_harvested",
                    "carrot_crops_harvested",
                    "sugarcane_crops_harvested",
                    "melon_crops_harvested",
                    "pumpkin_crops_harvested",
                    "cocoa_crops_harvested",
                    "beetroot_crops_harvested",
                    "bamboo_crop_harvested",

                    "red_apricorn_harvested",
                    "blue_apricorn_harvested",
                    "yellow_apricorn_harvested",
                    "green_apricorn_harvested",
                    "pink_apricorn_harvested",
                    "black_apricorn_harvested",
                    "white_apricorn_harvested",





                    "total_mob_kills",
                    "chicken_kills",
                    "cow_kills",
                    "pig_kills",
                    "sheep_kills",
                    "zombie_kills",
                    "skeleton_kills",
                    "spider_kills",
                    "creeper_kills",
                    "blaze_kills",
                    "magma_cube_kills",
                    "enderman_kills",
                    "phantom_kills",
                    "boss_kills",
                    "wither_skeleton_kills",





                    "trainer_levels_gained",
                    "trainer_level100",
                    "trainer_evolutions",
                    "trainer_infusion",
                    "trainer_happiness_gained",





                    "pokemon_caught",
                    "pokemon_hidden_ability_caught",
                    "pokemon_shiny_caught",
                    "pokemon_legendary_caught",
                    "pokemon_mythical_caught",
                    "pokemon_special_caught",





                    "fishing_event_wins",
                    "mining_event_wins",
                    "farming_event_wins",
                    "pvp_event_wins",
                    "tournament_event_wins",
                    "total_event_wins"
            );




    public static void init(String dbPath) {

        url = dbPath;

        createTable();
    }




    private static void createTable() {

        try (Connection conn = DriverManager.getConnection(url)) {

            Statement st = conn.createStatement();

            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS player_stats (

                    uuid TEXT PRIMARY KEY,
                    username TEXT DEFAULT '',
                    last_updated INTEGER DEFAULT 0,

                    bronze_fish_caught INTEGER DEFAULT 0,
                    silver_fish_caught INTEGER DEFAULT 0,
                    gold_fish_caught INTEGER DEFAULT 0,
                    diamond_fish_caught INTEGER DEFAULT 0,
                    platinum_fish_caught INTEGER DEFAULT 0,
                    mythical_fish_caught INTEGER DEFAULT 0,
                    squid_kills INTEGER DEFAULT 0,
                    dolphin_kills INTEGER DEFAULT 0,
                    crabs_killed INTEGER DEFAULT 0,
                    total_fish_caught INTEGER DEFAULT 0,

                    blocks_mined INTEGER DEFAULT 0,
                    total_ores_mined INTEGER DEFAULT 0,
                    coal_ore_mined INTEGER DEFAULT 0,
                    copper_ore_mined INTEGER DEFAULT 0,
                    iron_ore_mined INTEGER DEFAULT 0,
                    gold_ore_mined INTEGER DEFAULT 0,
                    lapis_ore_mined INTEGER DEFAULT 0,
                    redstone_ore_mined INTEGER DEFAULT 0,
                    diamond_ore_mined INTEGER DEFAULT 0,
                    emerald_ore_mined INTEGER DEFAULT 0,
                    ancient_debris_mined INTEGER DEFAULT 0,
                    nether_gold_ore_mined INTEGER DEFAULT 0,
                    quartz_ore_mined INTEGER DEFAULT 0,
                    dawn_stone_ore_mined INTEGER DEFAULT 0,
                    dusk_stone_ore_mined INTEGER DEFAULT 0,
                    fire_stone_ore_mined INTEGER DEFAULT 0,
                    ice_stone_ore_mined INTEGER DEFAULT 0,
                    leaf_stone_ore_mined INTEGER DEFAULT 0,
                    moon_stone_ore_mined INTEGER DEFAULT 0,
                    shiny_stone_ore_mined INTEGER DEFAULT 0,
                    sun_stone_ore_mined INTEGER DEFAULT 0,
                    thunder_stone_ore_mined INTEGER DEFAULT 0,
                    water_stone_ore_mined INTEGER DEFAULT 0,
                    overworld_ores_mined INTEGER DEFAULT 0,
                    nether_ores_mined INTEGER DEFAULT 0,
                    deepslate_ores_mined INTEGER DEFAULT 0,
                    cobblemon_ores_mined INTEGER DEFAULT 0,

                    total_crops_harvested INTEGER DEFAULT 0,
                    wheat_crops_harvested INTEGER DEFAULT 0,
                    potato_crops_harvested INTEGER DEFAULT 0,
                    carrot_crops_harvested INTEGER DEFAULT 0,
                    sugarcane_crops_harvested INTEGER DEFAULT 0,
                    melon_crops_harvested INTEGER DEFAULT 0,
                    pumpkin_crops_harvested INTEGER DEFAULT 0,
                    cocoa_crops_harvested INTEGER DEFAULT 0,
                    beetroot_crops_harvested INTEGER DEFAULT 0,
                    bamboo_crop_harvested INTEGER DEFAULT 0,
                    red_apricorn_harvested INTEGER DEFAULT 0,
                    blue_apricorn_harvested INTEGER DEFAULT 0,
                    yellow_apricorn_harvested INTEGER DEFAULT 0,
                    green_apricorn_harvested INTEGER DEFAULT 0,
                    pink_apricorn_harvested INTEGER DEFAULT 0,
                    black_apricorn_harvested INTEGER DEFAULT 0,
                    white_apricorn_harvested INTEGER DEFAULT 0,

                    total_mob_kills INTEGER DEFAULT 0,
                    chicken_kills INTEGER DEFAULT 0,
                    cow_kills INTEGER DEFAULT 0,
                    pig_kills INTEGER DEFAULT 0,
                    sheep_kills INTEGER DEFAULT 0,
                    zombie_kills INTEGER DEFAULT 0,
                    skeleton_kills INTEGER DEFAULT 0,
                    spider_kills INTEGER DEFAULT 0,
                    creeper_kills INTEGER DEFAULT 0,
                    blaze_kills INTEGER DEFAULT 0,
                    magma_cube_kills INTEGER DEFAULT 0,
                    enderman_kills INTEGER DEFAULT 0,
                    phantom_kills INTEGER DEFAULT 0,
                    boss_kills INTEGER DEFAULT 0,
                    wither_skeleton_kills INTEGER DEFAULT 0,
                    
                    
                    trainer_levels_gained INTEGER DEFAULT 0,
                    trainer_level100 INTEGER DEFAULT 0,
                    trainer_evolutions INTEGER DEFAULT 0,
                    trainer_infusion INTEGER DEFAULT 0,
                    trainer_happiness_gained INTEGER DEFAULT 0,

                    pokemon_caught INTEGER DEFAULT 0,
                    pokemon_hidden_ability_caught INTEGER DEFAULT 0,
                    pokemon_shiny_caught INTEGER DEFAULT 0,
                    pokemon_legendary_caught INTEGER DEFAULT 0,
                    pokemon_mythical_caught INTEGER DEFAULT 0,
                    pokemon_special_caught INTEGER DEFAULT 0,
                    

                    fishing_event_wins INTEGER DEFAULT 0,
                    mining_event_wins INTEGER DEFAULT 0,
                    farming_event_wins INTEGER DEFAULT 0,
                    pvp_event_wins INTEGER DEFAULT 0,
                    tournament_event_wins INTEGER DEFAULT 0,
                    total_event_wins INTEGER DEFAULT 0
                )
            """);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    private static void ensure(UUID uuid) {

        try (Connection conn = DriverManager.getConnection(url)) {

            PreparedStatement ps = conn.prepareStatement("""
                INSERT OR IGNORE INTO player_stats (uuid)
                VALUES (?)
            """);

            ps.setString(1, uuid.toString());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isValidColumn(
            String column
    ) {

        return VALID_COLUMNS.contains(column);
    }




    public static void add(UUID uuid, String column, int amount) {

        ensure(uuid);

        if (!isValidColumn(column)) {
            return;
        }

        try (Connection conn = DriverManager.getConnection(url)) {

            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE player_stats SET " +
                            column +
                            " = " +
                            column +
                            " + ? WHERE uuid = ?"
            );

            ps.setInt(1, amount);
            ps.setString(2, uuid.toString());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public static void set(UUID uuid, String column, int value) {

        ensure(uuid);

        if (!isValidColumn(column)) {
            return;
        }

        try (Connection conn = DriverManager.getConnection(url)) {

            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE player_stats SET " +
                            column +
                            " = ? WHERE uuid = ?"
            );

            ps.setInt(1, value);
            ps.setString(2, uuid.toString());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    public static void updateName(UUID uuid, String username) {

        ensure(uuid);

        try (Connection conn = DriverManager.getConnection(url)) {

            PreparedStatement ps = conn.prepareStatement("""
            UPDATE player_stats
            SET username = ?, last_updated = ?
            WHERE uuid = ?
        """);

            ps.setString(1, username);
            ps.setLong(2, System.currentTimeMillis());
            ps.setString(3, uuid.toString());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getUsername(UUID uuid) {

        ensure(uuid);

        try (Connection conn = DriverManager.getConnection(url)) {

            PreparedStatement ps = conn.prepareStatement("""
            SELECT username
            FROM player_stats
            WHERE uuid = ?
        """);

            ps.setString(1, uuid.toString());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("username");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Unknown";
    }




    public static int get(UUID uuid, String column) {

        ensure(uuid);

        if (!isValidColumn(column)) {
            return 0;
        }

        try (Connection conn = DriverManager.getConnection(url)) {

            PreparedStatement ps = conn.prepareStatement(
                    "SELECT " + column +
                            " FROM player_stats WHERE uuid = ?"
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
}