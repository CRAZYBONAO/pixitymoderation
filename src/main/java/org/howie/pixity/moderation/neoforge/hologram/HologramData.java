package org.howie.pixity.moderation.neoforge.hologram;

import org.howie.pixity.moderation.neoforge.hologram.animation.HologramAnimationType;

import java.util.ArrayList;
import java.util.List;

public class HologramData {

    public String id;

    public String dimension;

    public int x;
    public int y;
    public int z;

    public double viewDistance = 32.0;

    public double lineSpacing = 0.25;

    public List<LineData> lines =
            new ArrayList<>();

    public static class LineData {

        public String text;

        public HologramAnimationType animation =
                HologramAnimationType.NONE;

        public int speed = 20;
    }
}