package org.howie.pixity.moderation.neoforge.crate;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.network.chat.Component;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;

import org.howie.pixity.moderation.chat.CachedText;
import org.howie.pixity.moderation.chat.TextFormatter;

public class CrateKeyService {

    public static final String KEY = "crate";

    public static ItemStack create(String crateId) {

        var crate =
                CrateManager.get(crateId);

        if (
                crate == null
                        ||
                        crate.keyItem == null
        ) {

            return new ItemStack(
                    Items.TRIPWIRE_HOOK
            );
        }

        try {


            var itemType =
                    net.minecraft.core.registries.BuiltInRegistries.ITEM.get(

                            net.minecraft.resources.ResourceLocation.parse(
                                    crate.keyItem.item
                            )
                    );

            ItemStack item =
                    new ItemStack(itemType);



            var tag =
                    new net.minecraft.nbt.CompoundTag();

            tag.putString(
                    KEY,
                    crateId.toLowerCase()
            );

            item.set(
                    DataComponents.CUSTOM_DATA,
                    CustomData.of(tag)
            );



            item.set(
                    DataComponents.CUSTOM_NAME,

                    CachedText.of(
                            crate.keyItem.name
                    )
            );



            if (
                    crate.keyItem.lore != null
                            &&
                            !crate.keyItem.lore.isEmpty()
            ) {

                java.util.List<Component> lore =
                        new java.util.ArrayList<>();

                for (String line : crate.keyItem.lore) {

                    lore.add(
                            CachedText.of(line)
                    );
                }

                item.set(
                        DataComponents.LORE,
                        new net.minecraft.world.item.component.ItemLore(
                                lore
                        )
                );
            }

            return item;

        } catch (Exception e) {

            e.printStackTrace();

            return new ItemStack(
                    Items.TRIPWIRE_HOOK
            );
        }
    }

    public static boolean isKey(ItemStack stack) {
        return stack != null &&
                stack.has(DataComponents.CUSTOM_DATA) &&
                stack.get(DataComponents.CUSTOM_DATA).getUnsafe().contains(KEY);
    }

    public static String getCrate(ItemStack stack) {
        return stack.get(DataComponents.CUSTOM_DATA).getUnsafe().getString(KEY);
    }
}