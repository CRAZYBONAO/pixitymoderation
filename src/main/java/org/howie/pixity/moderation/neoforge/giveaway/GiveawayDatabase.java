package org.howie.pixity.moderation.neoforge.giveaway;

import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;

import java.nio.file.Path;
import java.sql.*;
import java.util.*;

public class GiveawayDatabase {

    private final String url;

    public GiveawayDatabase(Path folder) {
        this.url = "jdbc:sqlite:" + folder.resolve("giveaways.db");
        init();
    }

    private void init() {
        try (Connection c = DriverManager.getConnection(url);
             Statement s = c.createStatement()) {

            s.executeUpdate("""
                CREATE TABLE IF NOT EXISTS giveaway_presets (
                    name TEXT PRIMARY KEY,
                    time INTEGER,
                    mode TEXT
                )
            """);

            s.executeUpdate("""
                CREATE TABLE IF NOT EXISTS giveaway_items (
                    preset TEXT,
                    slot INTEGER,
                    item TEXT
                )
            """);

            s.executeUpdate("""
                CREATE TABLE IF NOT EXISTS giveaway_winners (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    player TEXT,
                    timestamp INTEGER
                )
            """);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    public void savePreset(String name, int time, String mode, List<ItemStack> items) {

        try (Connection c = DriverManager.getConnection(url)) {

            PreparedStatement ps = c.prepareStatement(
                    "INSERT OR REPLACE INTO giveaway_presets(name,time,mode) VALUES(?,?,?)"
            );

            ps.setString(1, name);
            ps.setInt(2, time);
            ps.setString(3, mode);
            ps.executeUpdate();

            c.createStatement().executeUpdate(
                    "DELETE FROM giveaway_items WHERE preset='" + name + "'"
            );

            int slot = 0;

            for (ItemStack item : items) {

                CompoundTag tag = (CompoundTag) ItemStack.CODEC
                        .encodeStart(NbtOps.INSTANCE, item)
                        .getOrThrow();

                PreparedStatement ip = c.prepareStatement(
                        "INSERT INTO giveaway_items VALUES(?,?,?)"
                );

                ip.setString(1, name);
                ip.setInt(2, slot++);
                ip.setString(3, tag.toString());
                ip.executeUpdate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    public Map<String, GiveawayPreset> loadPresets() {

        Map<String, GiveawayPreset> map = new LinkedHashMap<>();

        try (Connection c = DriverManager.getConnection(url)) {

            ResultSet rs = c.createStatement().executeQuery(
                    "SELECT * FROM giveaway_presets"
            );

            while (rs.next()) {

                String name = rs.getString("name");
                int time = rs.getInt("time");
                String mode = rs.getString("mode");

                List<ItemStack> items = new ArrayList<>();

                ResultSet irs = c.createStatement().executeQuery(
                        "SELECT * FROM giveaway_items WHERE preset='" + name + "'"
                );

                while (irs.next()) {

                    CompoundTag tag =
                            net.minecraft.nbt.TagParser.parseTag(
                                    irs.getString("item")
                            );

                    ItemStack item = ItemStack.CODEC
                            .parse(NbtOps.INSTANCE, tag)
                            .getOrThrow();

                    items.add(item);
                }

                map.put(name.toLowerCase(),
                        new GiveawayPreset(name, time, mode, items));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    public void deletePreset(String name) {

        try (Connection c = DriverManager.getConnection(url)) {

            PreparedStatement ps = c.prepareStatement(
                    "DELETE FROM giveaway_presets WHERE LOWER(name)=LOWER(?)"
            );

            ps.setString(1, name);
            ps.executeUpdate();

            PreparedStatement ps2 = c.prepareStatement(
                    "DELETE FROM giveaway_items WHERE LOWER(preset)=LOWER(?)"
            );

            ps2.setString(1, name);
            ps2.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    public void addWinner(String player) {

        try (Connection c = DriverManager.getConnection(url)) {

            PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO giveaway_winners(player,timestamp) VALUES(?,?)"
            );

            ps.setString(1, player);
            ps.setLong(2, System.currentTimeMillis());
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> loadWinners(int limit) {

        List<String> out = new ArrayList<>();

        try (Connection c = DriverManager.getConnection(url)) {

            ResultSet rs = c.createStatement().executeQuery(
                    "SELECT * FROM giveaway_winners ORDER BY id DESC LIMIT " + limit
            );

            while (rs.next()) {
                out.add(rs.getString("player"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return out;
    }
}