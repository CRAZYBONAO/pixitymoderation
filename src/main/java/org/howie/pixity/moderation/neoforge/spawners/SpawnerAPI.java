package org.howie.pixity.moderation.neoforge.spawners;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;

import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

public final class SpawnerAPI {

    private SpawnerAPI() {}





    public static ItemStack create(String mob) {

        ItemStack item = new ItemStack(Items.SPAWNER);

        String nice =
                mob.replace("minecraft:", "")
                        .replace("_", " ");

        nice =
                Character.toUpperCase(nice.charAt(0))
                        + nice.substring(1);

        item.set(
                DataComponents.CUSTOM_NAME,
                LegacyAmpersand.parse("&b" + nice + " Spawner")
        );

        return item;
    }





    public static boolean isSpawner(ItemStack item) {

        if (item == null) return false;
        if (!item.has(DataComponents.CUSTOM_NAME)) return false;

        String name =
                item.get(DataComponents.CUSTOM_NAME)
                        .getString();

        return name.endsWith("Spawner");
    }





    public static String getType(ItemStack item) {

        if (!isSpawner(item)) return null;

        String text =
                item.get(DataComponents.CUSTOM_NAME)
                        .getString();

        return "minecraft:" +
                text.replace(" Spawner", "")
                        .toLowerCase()
                        .replace(" ", "_");
    }





    public static void apply(
            SpawnerBlockEntity spawner,
            ResourceLocation id,
            net.minecraft.server.level.ServerLevel level,
            net.minecraft.core.BlockPos pos
    ) {

        var type =
                BuiltInRegistries.ENTITY_TYPE
                        .getOptional(id)
                        .orElse(EntityType.PIG);

        var logic = spawner.getSpawner();

        logic.setEntityId(
                type,
                level,
                level.random,
                pos
        );


        spawner.setChanged();

        level.sendBlockUpdated(
                pos,
                spawner.getBlockState(),
                spawner.getBlockState(),
                3
        );
    }
}