package org.howie.pixity.moderation.neoforge.giveaway;

import net.minecraft.world.item.ItemStack;

import java.util.*;

public class GiveawayPresetStore {

    private final Map<String, GiveawayPreset> presets = new HashMap<>();

    public void save(String name, GiveawayPreset preset) {
        presets.put(name.toLowerCase(), preset);
    }

    public GiveawayPreset get(String name) {
        return presets.get(name.toLowerCase());
    }

    public Set<String> list() {
        return presets.keySet();
    }
}