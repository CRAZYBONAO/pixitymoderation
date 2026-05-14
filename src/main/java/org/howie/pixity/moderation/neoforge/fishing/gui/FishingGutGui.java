package org.howie.pixity.moderation.neoforge.fishing.gui;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;

import org.howie.pixity.moderation.chat.TextFormatter;
import org.howie.pixity.moderation.neoforge.fishing.FishTier;
import org.howie.pixity.moderation.neoforge.fishing.FishingDatabase;
import org.howie.pixity.moderation.neoforge.fishing.FishingManager;
import org.howie.pixity.moderation.neoforge.fishing.augment.FishingAugment;

import java.util.ArrayList;
import java.util.List;

public class FishingGutGui {

    private static final int SIZE = 54;

    public static void open(ServerPlayer player) {

        SimpleContainer cont = new SimpleContainer(SIZE);

        MenuConstructor ctor = (id, inv, p) -> new ChestMenu(
                net.minecraft.world.inventory.MenuType.GENERIC_9x6,
                id,
                inv,
                cont,
                6
        ) {

            @Override
            public void broadcastChanges() {
                super.broadcastChanges();

                if (this.getSlot(46).hasItem()) return;



                ItemStack filler = new ItemStack(Items.BLACK_STAINED_GLASS_PANE);
                filler.set(DataComponents.CUSTOM_NAME, Component.empty());

                for (int i = 0; i < 54; i++) {


                    if (i >= 45) continue;


                    int row = i / 9;
                    int col = i % 9;

                    if (row >= 1 && row <= 4 && col >= 1 && col <= 7) continue;

                    this.getSlot(i).set(filler);
                }




                ItemStack back = new ItemStack(Items.BARRIER);
                back.set(DataComponents.CUSTOM_NAME,
                        TextFormatter.parse("<red>&lBACK TO MAIN MENU</red>"));

                this.getSlot(46).set(back);




                ItemStack gut = new ItemStack(Items.ANVIL);
                gut.set(DataComponents.CUSTOM_NAME,
                        TextFormatter.parse("<yellow>&lCLICK TO GUT FISH</yellow>"));

                this.getSlot(48).set(gut);




                ItemStack info = new ItemStack(Items.BOOK);

                info.set(DataComponents.CUSTOM_NAME,
                        TextFormatter.parse("<green>&lINFO</green>"));

                List<Component> lore = new ArrayList<>();

                lore.add(TextFormatter.parse("<gray>Fish will be gutted, but you </gray>"));
                lore.add(TextFormatter.parse("<gray>will gain entropy for gutting them.</gray>"));
                lore.add(TextFormatter.parse(""));

                lore.add(TextFormatter.parse("&#c99a73&lBRONZE: &b15"));
                lore.add(TextFormatter.parse("&#bdbdbd&lSILVER: &b30"));
                lore.add(TextFormatter.parse("&#f5f788&lGOLD: &b60"));
                lore.add(TextFormatter.parse("&#57f2e8&lDIAMOND: &b120"));
                lore.add(TextFormatter.parse("&#2deb95&lPLATINUM: &b200"));
                lore.add(TextFormatter.parse("&#db2deb&lMYTHICAL: &b500"));

                info.set(DataComponents.LORE,
                        new net.minecraft.world.item.component.ItemLore(lore));

                this.getSlot(49).set(info);




                for (int i = 45; i < 54; i++) {
                    if (i == 46 || i == 48 || i == 49) continue;
                    this.getSlot(i).set(filler);
                }
            }

            @Override
            public void clicked(int slot, int button, ClickType type, Player p) {

                if (!(p instanceof ServerPlayer sp)) return;

                int containerSlots = 54;




                if (slot >= this.slots.size() - 36) {

                    if (type == ClickType.QUICK_MOVE) {
                        this.quickMoveStack(sp, slot);
                        return;
                    }

                    super.clicked(slot, button, type, p);
                    return;
                }




                int row = slot / 9;
                int col = slot % 9;

                boolean isInput = (row >= 1 && row <= 4 && col >= 1 && col <= 7);
                boolean isBack = (slot == 46);
                boolean isGut = (slot == 48);
                boolean isInfo = (slot == 49);




                if (isBack || isGut || isInfo) {


                    if (isBack) {
                        sp.closeContainer();
                        return;
                    }


                    if (isInfo) {
                        return;
                    }


                    if (isGut) {

                        int totalFish = 0;
                        int totalEntropy = 0;

                        for (int i = 0; i < 45; i++) {

                            int r = i / 9;
                            int c = i % 9;

                            boolean input = (r >= 1 && r <= 4 && c >= 1 && c <= 7);
                            if (!input) continue;

                            ItemStack stack = this.getSlot(i).getItem();

                            if (stack.isEmpty()) continue;
                            if (!FishingManager.isFish(stack)) continue;

                            int count = stack.getCount();

                            FishTier tier = FishingManager.getFishTier(stack);

                            int base = FishingManager.getEntropy(tier);
                            int boosted = FishingManager.applyGuttingBonus(sp.getUUID(), base);

                            int precisionLvl = FishingManager.getAugmentLevel(
                                    sp.getMainHandItem(), FishingAugment.PRECISION);

                            if (precisionLvl > 0) {
                                boosted *= FishingManager.scaleBoost(precisionLvl, 10, 0.50);
                            }

                            totalFish += count;
                            totalEntropy += boosted * count;


                            this.getSlot(i).set(ItemStack.EMPTY);
                        }

                        if (totalFish == 0) {
                            sp.sendSystemMessage(TextFormatter.parse("<gradient:#00CFFF:#0066FF>&lFISHING</gradient> &7&l➤ <red>Error! No fish to gut!</red>"));
                            return;
                        }

                        FishingDatabase.addEntropy(sp.getUUID(), totalEntropy);

                        sp.sendSystemMessage(TextFormatter.parse(
                                "<gradient:#00CFFF:#0066FF>&lFISHING</gradient> &7&l➤ <gray>You gutted</gray> <gold>"
                                        + totalFish + "</gold> <gray>fish and got</gray> &b"
                                        + totalEntropy + " entropy<gray>!</gray>"
                        ));

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
                    return;
                }




            }

            @Override
            public ItemStack quickMoveStack(Player player, int index) {

                ItemStack empty = ItemStack.EMPTY;
                var slot = this.slots.get(index);

                if (slot == null || !slot.hasItem()) return empty;

                ItemStack stack = slot.getItem();
                ItemStack copy = stack.copy();

                int containerSlots = SIZE;




                if (index < containerSlots) {

                    if (!this.moveItemStackTo(stack, containerSlots, this.slots.size(), true)) {
                        return ItemStack.EMPTY;
                    }

                } else {




                    if (!FishingManager.isFish(stack)) {
                        return ItemStack.EMPTY;
                    }


                    for (int i = 0; i < 45; i++) {

                        int row = i / 9;
                        int col = i % 9;

                        boolean isInput = (row >= 1 && row <= 4 && col >= 1 && col <= 7);

                        if (!isInput) continue;

                        ItemStack target = this.getSlot(i).getItem();

                        if (target.isEmpty()) {
                            this.getSlot(i).set(stack.copy());
                            stack.setCount(0);
                            break;
                        }
                    }
                }

                if (stack.isEmpty()) {
                    slot.set(ItemStack.EMPTY);
                } else {
                    slot.setChanged();
                }

                return copy;
            }
        };

        player.openMenu(new SimpleMenuProvider(
                ctor,
                TextFormatter.parse("<gold>Fish Gutting</gold>")
        ));
    }
}