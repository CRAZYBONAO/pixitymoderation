package org.howie.pixity.moderation.neoforge.fishing;

import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class FishData {

    public final String id;
    public final String displayName;
    public final FishTier tier;
    public final int minSize;
    public final int maxSize;
    public final int baseValue;
    public final int modelData;
    public final List<String> biomes;
    public final double weight;

    public FishData(String id,
                    String displayName,
                    FishTier tier,
                    int minSize,
                    int maxSize,
                    int baseValue,
                    int modelData,
                    List<String> biomes,
                    double weight) {

        if (minSize > maxSize) {
            throw new IllegalArgumentException("minSize > maxSize");
        }

        this.id = id;
        this.displayName = displayName;
        this.tier = tier;
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.baseValue = baseValue;
        this.modelData = modelData;
        this.biomes = biomes;
        this.weight = weight;
    }
}