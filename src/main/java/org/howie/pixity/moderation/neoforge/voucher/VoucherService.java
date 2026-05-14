package org.howie.pixity.moderation.neoforge.voucher;

import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.nbt.TagParser;

import org.howie.pixity.moderation.chat.CachedText;
import org.howie.pixity.moderation.chat.TextFormatter;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

import java.util.*;

public class VoucherService {

    public static final String KEY = "voucher";
    public static final String NAME = "name";
    public static final String CMD = "command";




    public static ItemStack buildItem(String snbt, String id, String displayName, String command) {

        ItemStack item;

        try {
            var tag = TagParser.parseTag(snbt);
            item = ItemStack.parseOptional(net.minecraft.core.RegistryAccess.EMPTY, tag);

        } catch (Exception e) {
            item = new ItemStack(net.minecraft.world.item.Items.PAPER);
        }

        return applyVoucherData(item, id, displayName, command);
    }




    public static ItemStack applyVoucherData(
            ItemStack item,
            String id,
            String displayName,
            String command
    ) {

        var tag = new net.minecraft.nbt.CompoundTag();
        tag.putString(KEY, "true");
        tag.putString(NAME, id.toLowerCase());
        tag.putString(CMD, command);

        item.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));

        Component name = CachedText.of(displayName);


        name = name.copy().setStyle(
                name.getStyle().withItalic(false)
        );

        item.set(DataComponents.CUSTOM_NAME, name);

        List<Component> lore = new ArrayList<>();
        lore.add(LegacyAmpersand.parse("&7Right-click to redeem"));
        lore.add(Component.literal(""));

        item.set(DataComponents.LORE, new ItemLore(lore));

        item.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);

        return item;
    }


    public static boolean isVoucher(ItemStack stack) {
        return stack != null
                && stack.has(DataComponents.CUSTOM_DATA)
                && stack.get(DataComponents.CUSTOM_DATA).getUnsafe().contains(KEY);
    }

    public static String getCommand(ItemStack stack) {
        return stack.get(DataComponents.CUSTOM_DATA).getUnsafe().getString(CMD);
    }
}