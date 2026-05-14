package org.howie.pixity.moderation.neoforge.giveaway;

import net.minecraft.world.item.ItemStack;
import java.util.List;

public class GiveawayPreset {

    public final String name;
    public final int time;
    public final String mode;
    public final List<ItemStack> items;

    public GiveawayPreset(String name, int time, String mode, List<ItemStack> items) {
        this.name = name;
        this.time = time;
        this.mode = mode;
        this.items = items;
    }
}