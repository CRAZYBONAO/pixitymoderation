package org.howie.pixity.moderation.neoforge.hologram.packet;

import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.neoforge.hologram.Hologram;
import org.howie.pixity.moderation.neoforge.hologram.animation.HologramLine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PacketHologramManager {

    private static final Map<
            String,
            List<FakeArmorStand>
            > PACKET_STANDS =
            new HashMap<>();

    public static void tick(
            Hologram hologram
    ) {

        List<FakeArmorStand> stands =
                PACKET_STANDS.computeIfAbsent(

                        hologram.id(),

                        id -> build(hologram)
                );

        for (ServerPlayer player :
                hologram.level().players()) {

            double distanceSq =
                    player.distanceToSqr(

                            hologram.pos()
                                    .getX(),

                            hologram.pos()
                                    .getY(),

                            hologram.pos()
                                    .getZ()
                    );

            boolean shouldSee =
                    distanceSq <=
                            (hologram.viewDistance()
                                    * hologram.viewDistance());

            boolean viewing =
                    PacketHologramViewerTracker.isViewing(

                            hologram.id(),

                            player
                    );

            if (shouldSee && !viewing) {

                spawnForViewer(
                        hologram,
                        player
                );
            }

            else if (!shouldSee && viewing) {

                destroyForViewer(
                        hologram,
                        player
                );
            }

            else if (shouldSee) {

                updateForViewer(
                        hologram,
                        player
                );
            }
        }
    }

    private static List<FakeArmorStand> build(
            Hologram hologram
    ) {

        List<FakeArmorStand> stands =
                new ArrayList<>();

        double startY =
                hologram.pos().getY() + 2.5;

        List<HologramLine> lines =
                hologram.lines();

        for (int i = 0; i < lines.size(); i++) {

            String rendered =
                    lines.get(i)
                            .render(null);

            double x =
                    hologram.pos().getX() + 0.5;

            double y =
                    startY
                            - (i * hologram.lineSpacing());

            double z =
                    hologram.pos().getZ() + 0.5;

            stands.add(

                    new FakeArmorStand(

                            x,
                            y,
                            z,

                            rendered
                    )
            );
        }

        return stands;
    }

    public static void spawnForViewer(

            Hologram hologram,

            ServerPlayer player
    ) {

        if (PacketHologramViewerTracker.isViewing(
                hologram.id(),
                player
        )) {
            return;
        }

        List<FakeArmorStand> stands =
                PACKET_STANDS.computeIfAbsent(

                        hologram.id(),

                        id -> build(hologram)
                );

        for (FakeArmorStand stand :
                stands) {

            PacketHologramRenderer.spawn(
                    player,
                    stand
            );
        }

        PacketHologramViewerTracker.addViewer(
                hologram.id(),
                player
        );
    }

    public static void destroyForViewer(

            Hologram hologram,

            ServerPlayer player
    ) {

        List<FakeArmorStand> stands =
                PACKET_STANDS.get(
                        hologram.id()
                );

        if (stands == null)
            return;

        for (FakeArmorStand stand :
                stands) {

            PacketHologramRenderer.destroy(
                    player,
                    stand
            );
        }

        PacketHologramViewerTracker.removeViewer(
                hologram.id(),
                player
        );
    }

    public static void updateForViewer(

            Hologram hologram,

            ServerPlayer player
    ) {

        List<FakeArmorStand> stands =
                PACKET_STANDS.computeIfAbsent(

                        hologram.id(),

                        id -> build(hologram)
                );

        for (int i = 0; i < stands.size(); i++) {

            if (i >= hologram.lines().size())
                continue;

            FakeArmorStand stand =
                    stands.get(i);

            String rendered =
                    hologram.lines()
                            .get(i)
                            .render(player);

            if (!rendered.equals(
                    stand.lastRendered()
            )) {

                stand.setText(rendered);

                stand.setLastRendered(rendered);

                PacketHologramRenderer.updateText(
                        player,
                        stand
                );
            }
        }
    }

    public static void clear(
            Hologram hologram
    ) {

        PACKET_STANDS.remove(
                hologram.id()
        );
    }
}