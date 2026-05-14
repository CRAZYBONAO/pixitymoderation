package org.howie.pixity.moderation.neoforge.fishing;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.howie.pixity.moderation.chat.TextFormatter;

public class FishingItemHandler {

    public static void onRightClick(ServerPlayer player, ItemStack stack) {

        CompoundTag tag = stack.get(DataComponents.CUSTOM_DATA) != null
                ? stack.get(DataComponents.CUSTOM_DATA).copyTag()
                : null;

        if (tag == null) return;

        if (!"lure".equals(tag.getString("pixity_type"))) return;

        String tier = tag.getString("lure_tier");
        int duration = tag.getInt("lure_duration");

        long end = System.currentTimeMillis() + (duration * 1000L);

        FishingDatabase.setLure(player.getUUID(), tier, end);

        player.sendSystemMessage(TextFormatter.parse(
                "&a&lFISHING &7&l➤ <green>Activated " + tier + " Lure!</green>"
        ));

        stack.shrink(1);
    }
}
