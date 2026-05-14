package org.howie.pixity.moderation.neoforge.crate;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;

import org.howie.pixity.moderation.chat.CachedText;
import org.howie.pixity.moderation.chat.TextFormatter;

import java.util.*;

public class CrateHologramService {

    private static final Map<String, ArmorStand> holograms = new HashMap<>();

    private static String key(ServerLevel level, BlockPos pos) {
        return level.dimension().location() + ":" +
                pos.getX() + "," + pos.getY() + "," + pos.getZ();
    }

    public static void create(ServerLevel level, BlockPos pos, String crateId) {

        remove(level, pos);

        var crate = CrateManager.get(crateId);
        if (crate == null) return;

        ArmorStand stand = new ArmorStand(EntityType.ARMOR_STAND, level);

        stand.setPos(
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5
        );

        stand.setInvisible(true);
        stand.setNoGravity(true);
        stand.setCustomNameVisible(true);

        stand.setCustomName(
                CachedText.of(
                        "&b&l" + crate.display + "\n&7Right-click to open\n&8Left-click to preview"
                )
        );

        level.addFreshEntity(stand);

        holograms.put(key(level, pos), stand);
    }

    public static void remove(ServerLevel level, BlockPos pos) {

        String k = key(level, pos);

        if (!holograms.containsKey(k)) return;

        ArmorStand stand = holograms.remove(k);

        if (stand != null && stand.isAlive()) {
            stand.discard();
        }
    }

    public static void reloadAll(ServerLevel level) {

        holograms.values().forEach(ArmorStand::discard);
        holograms.clear();

        for (var entry : getAllBlocks().entrySet()) {

            String crateId = entry.getValue();
            BlockPos pos = entry.getKey();

            create(level, pos, crateId);
        }
    }

    private static Map<BlockPos, String> getAllBlocks() {

        Map<BlockPos, String> map =
                new HashMap<>();

        for (var entry : CrateBlockManager.getAll().entrySet()) {

            try {

                String full =
                        entry.getKey();



                int lastColon =
                        full.lastIndexOf(":");

                if (lastColon == -1) {
                    continue;
                }

                String xyz =
                        full.substring(
                                lastColon + 1
                        );

                String[] parts =
                        xyz.split(",");

                if (parts.length != 3) {
                    continue;
                }

                int x =
                        Integer.parseInt(parts[0]);

                int y =
                        Integer.parseInt(parts[1]);

                int z =
                        Integer.parseInt(parts[2]);

                map.put(

                        new BlockPos(x, y, z),

                        entry.getValue()
                );

            } catch (Exception e) {

                System.out.println(
                        "[Pixity] Failed to load crate hologram position: "
                                + entry.getKey()
                );

                e.printStackTrace();
            }
        }

        return map;
    }
}