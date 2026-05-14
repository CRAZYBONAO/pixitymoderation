package org.howie.pixity.moderation.neoforge.shop;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.network.chat.Component;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.CustomData;

import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

import java.util.*;

public class SellWandService {

    public static final String USES_KEY = "uses";
    public static final String TIER_KEY = "tier";
    public static final String MULT_KEY = "multiplier";

    private static final Map<String, Integer> TIERS = new HashMap<>();

    static {
        TIERS.put("bronze", 100);
        TIERS.put("silver", 250);
        TIERS.put("gold", 500);
        TIERS.put("diamond", 1000);
        TIERS.put("event", 1250);
        TIERS.put("platinum", 1500);
        TIERS.put("obsidian", 2500);
    }



    public static ItemStack create(int uses, String tier, double multiplier) {

        ItemStack wand = new ItemStack(Items.BLAZE_ROD);

        var tag = new net.minecraft.nbt.CompoundTag();
        tag.putInt(USES_KEY, uses);
        tag.putString(TIER_KEY, tier.toLowerCase());
        tag.putDouble(MULT_KEY, multiplier);

        wand.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));

        updateLore(wand);

        return wand;
    }


    public static boolean isWand(ItemStack stack) {
        return stack != null
                && stack.has(DataComponents.CUSTOM_DATA)
                && stack.get(DataComponents.CUSTOM_DATA) != null
                && stack.get(DataComponents.CUSTOM_DATA).getUnsafe().contains(USES_KEY);
    }



    public static int getUses(ItemStack stack) {
        if (!isWand(stack)) return 0;
        return stack.get(DataComponents.CUSTOM_DATA).getUnsafe().getInt(USES_KEY);
    }

    public static double getMultiplier(ItemStack stack) {
        if (!isWand(stack)) return 1.0;
        return stack.get(DataComponents.CUSTOM_DATA).getUnsafe().getDouble(MULT_KEY);
    }

    public static String getTier(ItemStack stack) {
        if (!isWand(stack)) return "unknown";
        return stack.get(DataComponents.CUSTOM_DATA).getUnsafe().getString(TIER_KEY);
    }


    public static void setUses(ItemStack stack, int uses) {

        if (!isWand(stack)) return;

        var data = stack.get(DataComponents.CUSTOM_DATA);

        data.getUnsafe().putInt(USES_KEY, uses);

        stack.set(DataComponents.CUSTOM_DATA, data);

        updateLore(stack);
    }



    public static int getTierUses(String tier) {
        return TIERS.getOrDefault(tier.toLowerCase(), -1);
    }

    public static Set<String> getTiers() {
        return TIERS.keySet();
    }



    private static void updateLore(ItemStack wand) {

        if (!isWand(wand)) return;

        var data = wand.get(DataComponents.CUSTOM_DATA).getUnsafe();

        int uses = data.getInt(USES_KEY);
        String tier = data.getString(TIER_KEY);
        double mult = data.getDouble(MULT_KEY);

        wand.set(DataComponents.CUSTOM_NAME,
                LegacyAmpersand.parse("&6&lSELL WAND &7[" + tier.toUpperCase() + "]"));

        List<Component> lore = new ArrayList<>();

        lore.add(LegacyAmpersand.parse("&7Right-click chest to sell contents"));
        lore.add(Component.literal(""));

        lore.add(LegacyAmpersand.parse("&eUses: &f" + uses));
        lore.add(LegacyAmpersand.parse("&eBase Multiplier: &f" + mult + "x"));

        lore.add(Component.literal(""));
        lore.add(LegacyAmpersand.parse("&7&oBoosts stack with ranks & perks"));

        wand.set(DataComponents.LORE, new ItemLore(lore));
    }
}