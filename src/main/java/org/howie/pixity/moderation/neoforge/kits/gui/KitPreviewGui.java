package org.howie.pixity.moderation.neoforge.kits.gui;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import net.minecraft.world.SimpleMenuProvider;

import org.howie.pixity.moderation.neoforge.economy.*;
import org.howie.pixity.moderation.neoforge.kits.*;

public final class KitPreviewGui {

    public static void open(ServerPlayer player,
                            KitManager kits,
                            EconomyService economy,
                            Kit kit) {

        SimpleContainer cont = new SimpleContainer(54);

        int slot = 0;


        for (String snbt : kit.itemsSnbt) {
            if (slot >= 45) break;

            try {
                CompoundTag tag = TagParser.parseTag(snbt);
                ItemStack stack = ItemStack.parseOptional(player.registryAccess(), tag);
                cont.setItem(slot, stack);
                slot++;
            } catch (Exception ignored) {}
        }


        ItemStack confirm = new ItemStack(Items.LIME_WOOL);
        confirm.set(DataComponents.CUSTOM_NAME,
                Component.literal("§aConfirm Purchase"));

        cont.setItem(49, confirm);


        ItemStack cancel = new ItemStack(Items.RED_WOOL);
        cancel.set(DataComponents.CUSTOM_NAME,
                Component.literal("§cCancel"));

        cont.setItem(45, cancel);

        player.openMenu(new SimpleMenuProvider(
                (id, inv, p) -> new ChestMenu(
                        net.minecraft.world.inventory.MenuType.GENERIC_9x6,
                        id, inv, cont, 6) {

                    @Override
                    public void clicked(int slot, int button,
                                        net.minecraft.world.inventory.ClickType type,
                                        net.minecraft.world.entity.player.Player p) {

                        if (!(p instanceof ServerPlayer sp)) return;

                        if (slot == 49) {
                            boolean success = kits.tryClaimKit(sp, kit);

                            if (success) {
                                sp.playNotifySound(
                                        net.minecraft.sounds.SoundEvents.PLAYER_LEVELUP,
                                        net.minecraft.sounds.SoundSource.PLAYERS,
                                        1f, 1f);
                            } else {
                                sp.playNotifySound(
                                        net.minecraft.sounds.SoundEvents.VILLAGER_NO,
                                        net.minecraft.sounds.SoundSource.PLAYERS,
                                        1f, 1f);
                            }

                            sp.closeContainer();
                        }

                        if (slot == 45) {
                            sp.closeContainer();
                        }
                    }
                },
                Component.literal("Preview: " + kit.name)
        ));
    }
}