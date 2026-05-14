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

import net.minecraft.world.item.component.ItemLore;
import org.howie.pixity.moderation.chat.TextFormatter;
import org.howie.pixity.moderation.neoforge.economy.EconomyService;
import org.howie.pixity.moderation.neoforge.fishing.FishingManager;
import org.howie.pixity.moderation.neoforge.fishing.FishingDatabase;
import org.howie.pixity.moderation.neoforge.economy.CurrencyType;
import org.howie.pixity.moderation.PixityModerationNeoForge;
import org.howie.pixity.moderation.neoforge.fishing.augment.FishingAugment;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FishingShopGui {


    public static void open(ServerPlayer player) {

        SimpleContainer cont = new SimpleContainer(54);

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

                boolean initialized = this.getSlot(48).hasItem();

                if (!initialized) {




                    ItemStack filler = new ItemStack(Items.GRAY_STAINED_GLASS_PANE);
                    filler.set(DataComponents.CUSTOM_NAME, Component.empty());

                    for (int i = 0; i < 54; i++) {
                        if (i < 9 || i >= 45 || i % 9 == 0 || i % 9 == 8) {
                            this.getSlot(i).set(filler);
                        }
                    }




                    ItemStack barrier = new ItemStack(Items.BARRIER);
                    barrier.set(DataComponents.CUSTOM_NAME,
                            TextFormatter.parse("<red>&lBACK</red>"));
                    this.getSlot(48).set(barrier);




                    ItemStack sell = new ItemStack(Items.EMERALD);
                    sell.set(DataComponents.CUSTOM_NAME,
                            TextFormatter.parse("<green>&lCLICK TO SELL FISH</green>"));

                    List<Component> lore = new ArrayList<>();
                    lore.add(TextFormatter.parse("<gray>Sells all fish in the grid</gray>"));
                    lore.add(TextFormatter.parse("<yellow>Scaled fish</yellow> <gray>affect</gray> <green>value</green>"));

                    sell.set(DataComponents.LORE, new ItemLore(lore));
                    this.getSlot(50).set(sell);
                }

                int preview = calculatePreview(player, this);

                ItemStack previewItem = new ItemStack(Items.EMERALD);

                previewItem.set(DataComponents.CUSTOM_NAME,
                        TextFormatter.parse("<green>&lEstimated Value</green>"));

                List<Component> Estimatelore = new ArrayList<>();

                Estimatelore.add(TextFormatter.parse("<gray>Total Value:</gray>"));
                Estimatelore.add(TextFormatter.parse("<green>$" + String.format("%,d", preview) + "</green>"));

                Estimatelore.add(Component.empty());

                Estimatelore.add(TextFormatter.parse("<yellow>Place fish above to calculate</yellow>"));

                previewItem.set(DataComponents.LORE,
                        new ItemLore(Estimatelore));


                this.getSlot(49).set(previewItem);






                ItemStack sell = new ItemStack(Items.EMERALD);

                sell.set(DataComponents.CUSTOM_NAME,
                        TextFormatter.parse("<green>&lCLICK TO SELL FISH</green>"));

                List<Component> lore = new ArrayList<>();
                lore.add(TextFormatter.parse("<gray>Sells all fish in the grid</gray>"));
                lore.add(TextFormatter.parse("<yellow>Scaled fish</yellow> <gray>affect</gray> <green>value</green>"));

                sell.set(DataComponents.LORE,
                        new net.minecraft.world.item.component.ItemLore(lore));

                this.getSlot(50).set(sell);




                ItemStack barrier = new ItemStack(Items.BARRIER);

                barrier.set(DataComponents.CUSTOM_NAME,
                        TextFormatter.parse("<red>&lBACK</red>"));

                this.getSlot(48).set(barrier);


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




                boolean isInput = (slot >= 10 && slot <= 34 && slot % 9 != 0 && slot % 9 != 8);

                boolean isBack = (slot == 48);
                boolean isPreview = (slot == 49);
                boolean isSell = (slot == 50);




                if (isBack || isPreview || isSell) {


                    if (isBack) {
                        sp.closeContainer();
                        return;
                    }


                    if (isPreview) {
                        return;
                    }


                    if (isSell) {

                        UUID uuid = sp.getUUID();

                        int total = 0;
                        int count = 0;

                        for (int i = 10; i <= 34; i++) {

                            if (i % 9 == 0 || i % 9 == 8) continue;

                            ItemStack stack = this.getSlot(i).getItem();

                            if (stack.isEmpty()) continue;
                            if (!FishingManager.isFish(stack)) continue;

                            int amount = stack.getCount();

                            double base = FishingManager.getFishValue(stack);
                            double mult = FishingManager.getScaleMultiplier(stack);

                            int solarLvl = FishingManager.getAugmentLevel(
                                    sp.getMainHandItem(), FishingAugment.SOLAR
                            );

                            if (solarLvl > 0) {
                                mult *= FishingManager.scaleBoost(solarLvl, 20, 0.50);
                            }

                            int finalValue = (int) Math.round(base * mult);

                            total += finalValue * amount;
                            count += amount;


                            this.getSlot(i).set(ItemStack.EMPTY);
                        }

                        if (count == 0) {
                            sp.sendSystemMessage(TextFormatter.parse(
                                    "<gradient:#00CFFF:#0066FF>&lFISHING</gradient> &7&l➤ &cError! No fish to sell"
                            ));
                            return;
                        }


                        PixityModerationNeoForge.getInstance()
                                .economy.add(sp, CurrencyType.MONEY, total);


                        FishingDatabase.addMoney(uuid, total);
                        FishingDatabase.addFishSold(uuid, count);

                        sp.sendSystemMessage(TextFormatter.parse(
                                "<gradient:#00CFFF:#0066FF>&lFISHING</gradient> &7&l➤ <green>&l✦ SOLD ✦</green> <gray>You sold </gray><yellow>"
                                        + count + "</yellow> <gray>fish for </gray><green>$" + total + "</green>"
                        ));

                    }
                    this.broadcastChanges();

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
                TextFormatter.parse("<gold>Fishing Shop</gold>")
        ));
    }

    private static int calculatePreview(ServerPlayer player, ChestMenu menu) {

        int total = 0;

        for (int i = 10; i <= 34; i++) {

            if (i % 9 == 0 || i % 9 == 8) continue;

            ItemStack stack = menu.getSlot(i).getItem();

            if (stack.isEmpty()) continue;
            if (!FishingManager.isFish(stack)) continue;

            int amount = stack.getCount();

            double base = FishingManager.getFishValue(stack);
            double mult = FishingManager.getScaleMultiplier(stack);


            int lvl = FishingManager.getAugmentLevel(player.getMainHandItem(), FishingAugment.SOLAR);

            if (lvl > 0) {
                mult *= FishingManager.scaleBoost(lvl, 20, 0.50);
            }

            int value = (int) Math.round(base * mult);

            total += value * amount;
        }

        return total;
    }
}