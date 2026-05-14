package org.howie.pixity.moderation.neoforge.crate;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import org.joml.Vector3f;

import java.util.*;

public class CrateAuraService {

    private static final Map<String, Double> angles = new HashMap<>();

    public static void tick(ServerLevel level) {

        for (var entry : CrateBlockManager.getAll().entrySet()) {

            String key = entry.getKey();
            String crateId = entry.getValue();

            var aura = CrateAuraManager.get(crateId);
            if (aura == null) continue;

            String xyz =
                    key.substring(
                            key.lastIndexOf(":") + 1
                    );

            String[] parts =
                    xyz.split(",");

            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            int z = Integer.parseInt(parts[2]);

            BlockPos pos = new BlockPos(x, y, z);

            spawnOrbit(level, pos, key, aura);
        }
    }

    private static void spawnOrbit(ServerLevel level, BlockPos pos, String key, CrateAuraManager.Aura aura) {

        double angle = angles.getOrDefault(key, 0.0);

        angle += aura.speed;
        angles.put(key, angle);

        int points = 10;

        for (int i = 0; i < points; i++) {

            double a = angle + (i * (Math.PI * 2 / points));

            double xOffset = Math.cos(a) * aura.radius;
            double zOffset = Math.sin(a) * aura.radius;

            float r, g, b;

            if (aura.rainbow) {
                float hue = (float)((angle + i) % (Math.PI * 2)) / (float)(Math.PI * 2);
                int rgb = java.awt.Color.HSBtoRGB(hue, 1f, 1f);
                r = ((rgb >> 16) & 255) / 255f;
                g = ((rgb >> 8) & 255) / 255f;
                b = (rgb & 255) / 255f;
            } else {
                r = aura.r / 255f;
                g = aura.g / 255f;
                b = aura.b / 255f;
            }

            DustParticleOptions dust = new DustParticleOptions(
                    new org.joml.Vector3f(r, g, b),
                    aura.size
            );


            double spiralY = Math.sin(angle + i * 0.5) * aura.verticalRange;

            double baseX = pos.getX() + 0.5 + xOffset;
            double baseZ = pos.getZ() + 0.5 + zOffset;


            double pyTop = pos.getY() + aura.height + spiralY;

            level.sendParticles(dust, baseX, pyTop, baseZ, 1, 0, 0, 0, 0);


            if (aura.doubleRing) {

                double pyBottom = pos.getY() + aura.height - 0.4 + spiralY;

                level.sendParticles(dust, baseX, pyBottom, baseZ, 1, 0, 0, 0, 0);
            }
        }
    }
}