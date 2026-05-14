package org.howie.pixity.moderation.neoforge.tp.gui;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class WarpIconRegistry {

    public static ItemStack getIcon(String icon) {
        if (icon == null) return new ItemStack(Items.ENDER_PEARL);

        return switch (icon.toLowerCase()) {
            case "pvp" -> new ItemStack(Items.IRON_SWORD);
            case "shop" -> new ItemStack(Items.EMERALD);
            case "gym" -> new ItemStack(Items.BLAZE_POWDER);
            case "spawn" -> new ItemStack(Items.NETHER_STAR);
            case "grind" -> new ItemStack(Items.DIAMOND_PICKAXE);
            case "farm" -> new ItemStack(Items.WHEAT);
            default -> new ItemStack(Items.ENDER_PEARL);
        };
    }
}