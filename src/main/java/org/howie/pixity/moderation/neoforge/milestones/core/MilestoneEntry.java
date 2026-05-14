package org.howie.pixity.moderation.neoforge.milestones.core;

import net.minecraft.world.item.ItemStack;

public class MilestoneEntry {

    public final String id;


    public final String statColumn;


    public final ItemStack icon;
    public final String displayName;
    public final int slot;


    public final MilestoneCategory category;
    public final String group;

    public MilestoneEntry(
            String id,
            String statColumn,
            ItemStack icon,
            String displayName,
            int slot,
            MilestoneCategory category,
            String group
    ) {

        this.id = id;
        this.statColumn = statColumn;

        this.icon = icon;
        this.displayName = displayName;
        this.slot = slot;

        this.category = category;
        this.group = group;
    }
}