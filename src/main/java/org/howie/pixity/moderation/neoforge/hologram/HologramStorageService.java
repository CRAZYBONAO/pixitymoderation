package org.howie.pixity.moderation.neoforge.hologram;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.howie.pixity.moderation.neoforge.hologram.animation.HologramLine;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import java.lang.reflect.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HologramStorageService {

    private static final Gson GSON =
            new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

    private static final File FILE =
            new File(
                    "config/pixity/holograms.json"
            );

    public static void saveAll() {

        try {

            if (!FILE.getParentFile().exists()) {
                FILE.getParentFile().mkdirs();
            }

            List<HologramData> out =
                    new ArrayList<>();

            for (Hologram hologram :
                    HologramManager.all()) {

                HologramData data =
                        new HologramData();

                data.id =
                        hologram.id();

                data.dimension =
                        hologram.level()
                                .dimension()
                                .location()
                                .toString();

                data.viewDistance =
                        hologram.viewDistance();

                data.lineSpacing =
                        hologram.lineSpacing();

                data.x =
                        hologram.pos().getX();

                data.y =
                        hologram.pos().getY();

                data.z =
                        hologram.pos().getZ();

                for (var line :
                        hologram.lines()) {

                    HologramData.LineData ld =
                            new HologramData.LineData();

                    ld.text =
                            line.text();

                    ld.animation =
                            line.animation();

                    ld.speed =
                            line.speed();

                    data.lines.add(ld);
                }

                out.add(data);
            }

            FileWriter writer =
                    new FileWriter(FILE);

            GSON.toJson(out, writer);

            writer.close();

            System.out.println(
                    "[Pixity Holograms] Saved "
                            + out.size()
                            + " holograms."
            );

        } catch (Exception e) {

            System.out.println(
                    "[Pixity Holograms] Failed to save holograms."
            );

            e.printStackTrace();
        }
    }

    public static void loadAll() {

        try {

            if (!FILE.exists()) {
                return;
            }

            FileReader reader =
                    new FileReader(FILE);

            Type type =
                    new TypeToken<
                            List<HologramData>
                            >() {}.getType();

            List<HologramData> loaded =
                    GSON.fromJson(
                            reader,
                            type
                    );

            reader.close();

            if (loaded == null)
                return;

            for (HologramData data : loaded) {

                try {

                    ServerLevel level =
                            ServerLifecycleHooks
                                    .getCurrentServer()
                                    .getLevel(

                                            net.minecraft.resources.ResourceKey.create(
                                                    net.minecraft.core.registries.Registries.DIMENSION,
                                                    ResourceLocation.parse(
                                                            data.dimension
                                                    )
                                            )
                                    );

                    if (level == null)
                        continue;

                    Hologram hologram =
                            new Hologram(
                                    data.id,
                                    level,
                                    new BlockPos(
                                            data.x,
                                            data.y,
                                            data.z
                                    )
                            );

                    for (HologramData.LineData ld :
                            data.lines) {

                        HologramLine line =
                                new HologramLine(
                                        ld.text
                                );

                        line.setAnimation(
                                ld.animation
                        );

                        line.setSpeed(
                                ld.speed
                        );

                        hologram.addLineRaw(line);
                    }

                    HologramManager.register(
                            hologram
                    );

                    hologram.setViewDistanceRaw(
                            data.viewDistance
                    );

                    hologram.setLineSpacingRaw(
                            data.lineSpacing
                    );

                    hologram.spawn();

                } catch (Exception e) {

                    System.out.println(
                            "[Pixity Holograms] Failed loading hologram: "
                                    + data.id
                    );

                    e.printStackTrace();
                }
            }

            System.out.println(
                    "[Pixity Holograms] Loaded "
                            + loaded.size()
                            + " holograms."
            );

        } catch (Exception e) {

            System.out.println(
                    "[Pixity Holograms] Failed loading holograms."
            );

            e.printStackTrace();
        }
    }
}