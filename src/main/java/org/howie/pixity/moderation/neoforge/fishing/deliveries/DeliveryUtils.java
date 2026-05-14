package org.howie.pixity.moderation.neoforge.fishing.deliveries;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.HashMap;
import java.util.Map;

public class DeliveryUtils {





    public static boolean hasRequiredFish(ServerPlayer player, Delivery delivery) {

        Map<String, Integer> found = new HashMap<>();

        for (ItemStack stack : player.getInventory().items) {

            if (stack.isEmpty()) continue;

            String fishId = getFishId(stack);

            if (fishId == null) continue;

            found.merge(fishId, stack.getCount(), Integer::sum);
        }


        for (Map.Entry<String, Integer> entry : delivery.requiredFish.entrySet()) {

            int have = found.getOrDefault(entry.getKey(), 0);

            if (have < entry.getValue()) {
                return false;
            }
        }

        return true;
    }





    public static void removeRequiredFish(ServerPlayer player, Delivery delivery) {

        Map<String, Integer> remaining = new HashMap<>(delivery.requiredFish);

        for (int i = 0; i < player.getInventory().items.size(); i++) {

            ItemStack stack = player.getInventory().items.get(i);

            if (stack.isEmpty()) continue;

            String fishId = getFishId(stack);

            if (fishId == null) continue;

            if (!remaining.containsKey(fishId)) continue;

            int needed = remaining.get(fishId);

            int remove = Math.min(needed, stack.getCount());

            stack.shrink(remove);

            if (stack.isEmpty()) {
                player.getInventory().items.set(i, ItemStack.EMPTY);
            }

            needed -= remove;

            if (needed <= 0) {
                remaining.remove(fishId);
            } else {
                remaining.put(fishId, needed);
            }

            if (remaining.isEmpty()) return;
        }
    }





    public static String getFishId(ItemStack stack) {

        CustomData data = stack.get(DataComponents.CUSTOM_DATA);

        if (data == null) return null;

        CompoundTag tag = data.copyTag();

        if (!tag.contains("fish_id")) return null;

        return tag.getString("fish_id");
    }
}