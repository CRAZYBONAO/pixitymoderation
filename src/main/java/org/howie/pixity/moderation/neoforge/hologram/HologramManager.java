package org.howie.pixity.moderation.neoforge.hologram;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.howie.pixity.moderation.neoforge.hologram.packet.PacketHologramManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class HologramManager {

    private static final Map<String, Hologram>
            HOLOGRAMS = new HashMap<>();

    public static Hologram create(
            String id,
            ServerLevel level,
            BlockPos pos,
            String firstLine
    ) {

        delete(id);

        Hologram hologram =
                new Hologram(
                        id,
                        level,
                        pos
                );

        HOLOGRAMS.put(
                id.toLowerCase(),
                hologram
        );

        hologram.addLine(firstLine);

        HologramStorageService.saveAll();

        return hologram;
    }

    public static Hologram get(String id) {

        return HOLOGRAMS.get(
                id.toLowerCase()
        );
    }

    public static void delete(String id) {

        Hologram existing =
                get(id);

        if (existing == null)
            return;

        for (var player :
                existing.level().players()) {

            PacketHologramManager.destroyForViewer(

                    existing,

                    player
            );
        }

        PacketHologramManager.clear(
                existing
        );

        existing.remove();

        HOLOGRAMS.remove(
                id.toLowerCase()
        );

        HologramStorageService.saveAll();
    }

    public static Collection<Hologram> all() {
        return HOLOGRAMS.values();
    }

    public static void refreshAll() {

        for (Hologram hologram : all()) {
            hologram.refresh();
        }
    }

    public static void register(Hologram hologram) {

        HOLOGRAMS.put(
                hologram.id().toLowerCase(),
                hologram
        );
    }

    public static void clear() {

        for (Hologram hologram :
                HOLOGRAMS.values()) {

            for (var player :
                    hologram.level().players()) {

                PacketHologramManager.destroyForViewer(

                        hologram,

                        player
                );
            }

            PacketHologramManager.clear(
                    hologram
            );

            hologram.remove();
        }

        HOLOGRAMS.clear();
    }

    public static void reload() {

        clear();

        HologramStorageService.loadAll();
    }
}