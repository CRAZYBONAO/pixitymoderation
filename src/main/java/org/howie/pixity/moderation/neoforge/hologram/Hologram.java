package org.howie.pixity.moderation.neoforge.hologram;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;

import org.howie.pixity.moderation.neoforge.hologram.animation.HologramAnimationType;
import org.howie.pixity.moderation.neoforge.hologram.animation.HologramLine;
import org.howie.pixity.moderation.neoforge.hologram.packet.PacketHologramManager;
import org.howie.pixity.moderation.neoforge.hologram.packet.PacketHologramViewerTracker;

import java.util.ArrayList;
import java.util.List;

public class Hologram {

    private final String id;

    private final ServerLevel level;

    private BlockPos pos;

    private double viewDistance = 32.0;

    private double lineSpacing = 0.25;

    private final List<HologramLine> lines =
            new ArrayList<>();

    private final List<ArmorStand> stands =
            new ArrayList<>();

    public Hologram(
            String id,
            ServerLevel level,
            BlockPos pos
    ) {

        this.id = id;
        this.level = level;
        this.pos = pos;
    }

    public String id() {
        return id;
    }

    public ServerLevel level() {
        return level;
    }

    public BlockPos pos() {
        return pos;
    }

    public List<HologramLine> lines() {
        return lines;
    }

    public List<ArmorStand> getStands() {
        return stands;
    }

    public double viewDistance() {
        return viewDistance;
    }

    public double lineSpacing() {
        return lineSpacing;
    }

    public void setViewDistance(
            double viewDistance
    ) {

        this.viewDistance =
                viewDistance;

        HologramStorageService.saveAll();
    }

    public void setLineSpacing(
            double lineSpacing
    ) {

        this.lineSpacing =
                lineSpacing;

        respawn();

        HologramStorageService.saveAll();
    }

    public void setPos(BlockPos pos) {

        this.pos = pos;

        HologramStorageService.saveAll();
    }

    public void addLine(String line) {

        lines.add(
                new HologramLine(line)
        );

        respawn();

        HologramStorageService.saveAll();
    }

    public void setLine(
            int index,
            String line
    ) {

        if (index < 0 ||
                index >= lines.size())
            return;

        lines.get(index)
                .setText(line);

        refresh();

        HologramStorageService.saveAll();
    }

    public void removeLine(int index) {

        if (index < 0 ||
                index >= lines.size())
            return;

        lines.remove(index);

        respawn();

        HologramStorageService.saveAll();
    }

    public void setAnimation(

            int line,

            HologramAnimationType type
    ) {

        if (line < 0 ||
                line >= lines.size())
            return;

        lines.get(line)
                .setAnimation(type);

        HologramStorageService.saveAll();
    }

    public void spawn() {

        for (var player :
                level.players()) {

            double distanceSq =
                    player.distanceToSqr(

                            pos.getX(),
                            pos.getY(),
                            pos.getZ()
                    );

            if (distanceSq >
                    (viewDistance * viewDistance)) {
                continue;
            }

            PacketHologramManager.spawnForViewer(
                    this,
                    player
            );
        }
    }

    public void refresh() {

        for (int i = 0; i < stands.size(); i++) {

            if (i >= lines.size())
                continue;

            ArmorStand stand =
                    stands.get(i);

            if (stand == null ||
                    !stand.isAlive())
                continue;

            String rendered =
                    lines.get(i)
                            .render(null);

            Component parsed =
                    HologramPlaceholderService
                            .parse(rendered);

            stand.setCustomName(parsed);
        }
    }

    public void addLineRaw(
            HologramLine line
    ) {

        lines.add(line);
    }

    public void setLineSpacingRaw(
            double lineSpacing
    ) {

        this.lineSpacing =
                lineSpacing;
    }

    public void setViewDistanceRaw(
            double viewDistance
    ) {

        this.viewDistance =
                viewDistance;
    }

    public void respawn() {

        for (var player :
                level.players()) {

            PacketHologramManager.destroyForViewer(
                    this,
                    player
            );
        }

        remove();

        PacketHologramManager.clear(
                this
        );

        PacketHologramViewerTracker.clearViewers(
                id
        );

        spawn();
    }

    public void remove() {

        for (ArmorStand stand :
                stands) {

            if (stand != null &&
                    stand.isAlive()) {

                stand.discard();
            }
        }

        stands.clear();
    }
}