package org.howie.pixity.moderation.neoforge.fishing.gui;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;

import org.howie.pixity.moderation.chat.TextFormatter;
import org.howie.pixity.moderation.neoforge.fishing.FishingManager;
import org.howie.pixity.moderation.neoforge.fishing.augment.FishingAugment;

import java.util.ArrayList;
import java.util.List;

public class FishingApplyAugmentGui {

    public static void open(ServerPlayer player) {

        SimpleContainer cont = new SimpleContainer(27);

        MenuConstructor ctor = (id, inv, p) -> new ChestMenu(
                MenuType.GENERIC_9x3, id, inv, cont, 3
        ) {

            @Override
            public void broadcastChanges() {
                super.broadcastChanges();

                if (this.getSlot(13).hasItem()) return;




                ItemStack filler = new ItemStack(Items.GRAY_STAINED_GLASS_PANE);
                filler.set(DataComponents.CUSTOM_NAME, Component.empty());

                for (int i = 0; i < 27; i++) {
                    if (i == 11 || i == 15 || i == 13 || i == 2 || i == 6) continue;
                    this.getSlot(i).set(filler);
                }




                ItemStack rodLabel = new ItemStack(Items.FISHING_ROD);
                rodLabel.set(DataComponents.CUSTOM_NAME,
                        TextFormatter.parse("<gold>&lPLACE ROD BELOW</gold>"));

                ItemStack augLabel = new ItemStack(Items.NETHER_STAR);
                augLabel.set(DataComponents.CUSTOM_NAME,
                        TextFormatter.parse("&b&l>>PLACE AUGMENT BELOW<<"));

                this.getSlot(2).set(rodLabel);
                this.getSlot(6).set(augLabel);




                if (this.getSlot(11).getItem().isEmpty()) {
                    this.getSlot(11).set(ItemStack.EMPTY);
                }

                if (this.getSlot(15).getItem().isEmpty()) {
                    this.getSlot(15).set(ItemStack.EMPTY);
                }




                ItemStack confirm = new ItemStack(Items.EMERALD);
                confirm.set(DataComponents.CUSTOM_NAME,
                        TextFormatter.parse("<green>&lCLICK TO APPLY</green>"));

                this.getSlot(13).set(confirm);
            }

            @Override
            public void clicked(int slot, int button, ClickType type, Player p) {

                if (!(p instanceof ServerPlayer sp)) return;

                int containerSlots = 27;




                if (slot >= this.slots.size() - 36) {

                    if (type == ClickType.QUICK_MOVE) {
                        this.quickMoveStack(sp, slot);
                        return;
                    }

                    super.clicked(slot, button, type, p);
                    return;
                }




                boolean isRodSlot = (slot == 11);
                boolean isAugSlot = (slot == 15);
                boolean isConfirm = (slot == 13);




                if (isConfirm) {

                    ItemStack rod = this.getSlot(11).getItem();
                    ItemStack augItem = this.getSlot(15).getItem();

                    if (rod.isEmpty() || augItem.isEmpty()) return;

                    if (!FishingManager.isFishingRod(rod)) {
                        sp.sendSystemMessage(TextFormatter.parse("<gradient:#00CFFF:#0066FF>&lFISHING</gradient> &7&l➤ <red>Error! Invalid rod</red>"));
                        return;
                    }

                    FishingAugment aug = FishingManager.getAugment(augItem);

                    if (aug == null) {
                        sp.sendSystemMessage(TextFormatter.parse("<gradient:#00CFFF:#0066FF>&lFISHING</gradient> &7&l➤ <red>Error! Invalid augment</red>"));
                        return;
                    }

                    int max = FishingManager.getMaxLevel(aug);
                    int applied = 0;

                    int amount = augItem.getCount();

                    for (int i = 0; i < amount; i++) {

                        int current = FishingManager.getAugmentLevel(rod, aug);

                        if (current >= max) break;

                        FishingManager.addAugment(rod, aug);
                        applied++;
                    }

                    if (applied == 0) {
                        sp.sendSystemMessage(TextFormatter.parse("<gradient:#00CFFF:#0066FF>&lFISHING</gradient> &7&l➤ <red>Error! Already max level</red>"));
                        return;
                    }


                    this.getSlot(15).set(ItemStack.EMPTY);

                    int newLevel = FishingManager.getAugmentLevel(rod, aug);

                    sp.sendSystemMessage(TextFormatter.parse(
                            "<gradient:#00CFFF:#0066FF>&lFISHING</gradient> &7&l➤ <green>Applied </green>" + aug.display +
                                    " <gray>(</gray><yellow>" + newLevel + "</yellow><gray>/</gray><yellow>" + max + "<gray>)</gray>"
                    ));

                    return;
                }




                if (isRodSlot || isAugSlot) {

                    ItemStack cursor = p.containerMenu.getCarried();

                    if (!cursor.isEmpty()) {

                        if (isRodSlot && !FishingManager.isFishingRod(cursor)) return;
                        if (isAugSlot && FishingManager.getAugment(cursor) == null) return;
                    }

                    super.clicked(slot, button, type, p);
                    return;
                }




            }
        };

        player.openMenu(new SimpleMenuProvider(
                ctor,
                TextFormatter.parse("<gold>Apply Augment</gold>")
        ));
    }
}