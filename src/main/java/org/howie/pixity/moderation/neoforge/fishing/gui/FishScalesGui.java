

package org.howie.pixity.moderation.neoforge.fishing.gui;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;

import org.howie.pixity.moderation.chat.TextFormatter;
import org.howie.pixity.moderation.neoforge.fishing.FishingManager;
import org.howie.pixity.moderation.neoforge.fishing.augment.FishingAugment;

import java.util.ArrayList;
import java.util.List;

import static org.howie.pixity.moderation.neoforge.fishing.FishingManager.*;

public class FishScalesGui {

    public static void open(ServerPlayer player) {

        SimpleContainer cont = new SimpleContainer(54);

        MenuConstructor ctor = (id, inv, p) -> new ChestMenu(
                MenuType.GENERIC_9x6,
                id,
                inv,
                cont,
                6
        ) {
            private String selectedRisk = "LOW";

            @Override
            public void broadcastChanges() {
                super.broadcastChanges();





                ItemStack filler = new ItemStack(Items.BLACK_STAINED_GLASS_PANE);
                filler.set(DataComponents.CUSTOM_NAME, Component.empty());

                for (int i = 0; i < 54; i++) {
                    if (i < 9 || i >= 45 || i % 9 == 0 || i % 9 == 8) {
                        this.getSlot(i).set(filler);
                    }
                }




                ItemStack info = new ItemStack(Items.BOOK);
                info.set(DataComponents.CUSTOM_NAME,
                        TextFormatter.parse("<green>&lINFO</green>"));

                this.getSlot(49).set(info);




                ItemStack anvil = new ItemStack(Items.ANVIL);
                anvil.set(DataComponents.CUSTOM_NAME,
                        TextFormatter.parse("<yellow>&lCLICK TO WEIGH FISH</yellow>"));

                this.getSlot(50).set(anvil);




                ItemStack barrier = new ItemStack(Items.BARRIER);
                barrier.set(DataComponents.CUSTOM_NAME,
                        TextFormatter.parse("<red>&lBACK</red>"));

                this.getSlot(48).set(barrier);




                this.getSlot(9).set(createRisk("&#9ceb2d&lLOW RISK", "LOW", selectedRisk, Items.LIME_STAINED_GLASS_PANE));

                this.getSlot(18).set(createRisk("&#e7eb17&lMEDIUM RISK", "MEDIUM", selectedRisk, Items.YELLOW_STAINED_GLASS_PANE));

                this.getSlot(27).set(createRisk("&#ed7e0e&lHIGH RISK", "HIGH", selectedRisk, Items.ORANGE_STAINED_GLASS_PANE));

                this.getSlot(36).set(createRisk("&#ff0000&lEXTREME RISK", "EXTREME", selectedRisk, Items.RED_STAINED_GLASS_PANE));
            }

            @Override
            public void clicked(int slot, int button, ClickType type, Player p) {

                if (!(p instanceof ServerPlayer sp)) return;




                if (slot >= this.slots.size() - 36) {

                    if (type == ClickType.QUICK_MOVE) {
                        this.quickMoveStack(sp, slot);
                        return;
                    }

                    super.clicked(slot, button, type, p);
                    return;
                }




                boolean isInput = (slot >= 10 && slot <= 34 && slot % 9 != 0 && slot % 9 != 8);

                boolean isBack = (slot == 48);
                boolean isInfo = (slot == 49);
                boolean isScale = (slot == 50);

                boolean isRisk = (
                        slot == 9 || slot == 18 || slot == 27 || slot == 36
                );




                if (isBack || isInfo || isScale || isRisk) {


                    if (isBack) {
                        FishingMainMenu.open(sp);
                        return;
                    }


                    if (slot == 9) selectedRisk = "LOW";
                    if (slot == 18) selectedRisk = "MEDIUM";
                    if (slot == 27) selectedRisk = "HIGH";
                    if (slot == 36) selectedRisk = "EXTREME";


                    if (isScale) {
                        applyScaling(sp, this, selectedRisk);
                        this.broadcastChanges();
                    }

                    return;
                }




                if (isInput) {

                    ItemStack cursor = p.containerMenu.getCarried();

                    if (!cursor.isEmpty() && !FishingManager.isFish(cursor)) {
                        return;
                    }

                    super.clicked(slot, button, type, p);
                }
            }

            @Override
            public ItemStack quickMoveStack(Player player, int index) {

                ItemStack empty = ItemStack.EMPTY;
                var slot = this.slots.get(index);

                if (slot == null || !slot.hasItem()) return empty;

                ItemStack stack = slot.getItem();
                ItemStack copy = stack.copy();

                int containerSlots = 54;

                if (index < containerSlots) {
                    if (!this.moveItemStackTo(stack, containerSlots, this.slots.size(), true)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (!FishingManager.isFish(stack)) return ItemStack.EMPTY;

                    for (int i = 10; i <= 34; i++) {
                        if (i % 9 == 0 || i % 9 == 8) continue;

                        if (this.getSlot(i).getItem().isEmpty()) {

                            int move = Math.min(stack.getCount(), 64);

                            ItemStack moved = stack.copy();
                            moved.setCount(move);

                            this.getSlot(i).set(moved);

                            stack.shrink(move);
                            break;
                        }
                    }
                }

                if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
                else slot.setChanged();

                return copy;
            }
        };

        player.openMenu(new SimpleMenuProvider(
                ctor,
                TextFormatter.parse("<gold>Fish Scales</gold>")
        ));
    }


    private static ItemStack createRisk(String name, String risk, String selectedRisk, Item pane) {

        ItemStack item = new ItemStack(pane);

        item.set(DataComponents.CUSTOM_NAME,
                TextFormatter.parse(name));

        List<Component> lore = new ArrayList<>();

        lore.add(TextFormatter.parse("<gray>Click to select</gray>"));

        if (risk.equals(selectedRisk)) {
            lore.add(TextFormatter.parse("<green>✔ Selected</green>"));
        }

        item.set(DataComponents.LORE,
                new net.minecraft.world.item.component.ItemLore(lore));

        return item;
    }


    private static void applyScaling(ServerPlayer sp, ChestMenu menu, String risk) {

        int affected = 0;

        for (int i = 10; i <= 34; i++) {

            if (i % 9 == 0 || i % 9 == 8) continue;

            ItemStack stack = menu.getSlot(i).getItem();

            if (stack.isEmpty()) continue;
            if (!FishingManager.isFish(stack)) continue;


            if (FishingManager.hasBeenScaled(stack)) {
                sp.sendSystemMessage(TextFormatter.parse("<gradient:#00CFFF:#0066FF>&lFISHING</gradient> &7&l➤ &cError! One or more of your fish were already scaled, those have been skipped."));
                continue;
            }

            double mult = FishingManager.rollScaleMultiplier(risk);

            int lvl = getAugmentLevel(sp.getMainHandItem(), FishingAugment.TROPHY);

            if (lvl > 0) {
                mult *= scaleBoost(lvl, 10, 0.25);
            }

            FishingManager.setScaleMultiplier(stack, mult);


            var data = stack.get(DataComponents.CUSTOM_DATA);
            var tag = data != null ? data.copyTag() : new net.minecraft.nbt.CompoundTag();

            tag.putBoolean("scaled", true);

            stack.set(DataComponents.CUSTOM_DATA,
                    net.minecraft.world.item.component.CustomData.of(tag));
            FishingManager.updateFishLore(stack);

            affected++;
        }

        if (affected == 0) {
            sp.sendSystemMessage(TextFormatter.parse("<gradient:#00CFFF:#0066FF>&lFISHING</gradient> &7&l➤ <red>Error! No fish to scale or they have already been scaled!</red>"));
            return;
        }

        sp.sendSystemMessage(TextFormatter.parse(
                "<gradient:#00CFFF:#0066FF>&lFISHING</gradient> &7&l➤ <gold>Scales</gold> <gray>Applied</gray> <red>"
                        + risk + "</red><gray> risk to </gray><yellow>" + affected + "</yellow> <gray>fish!</gray>"
        ));
    }
}