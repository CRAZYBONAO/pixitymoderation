package org.howie.pixity.moderation.neoforge.crate.gui;

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

import org.howie.pixity.moderation.chat.CachedText;
import org.howie.pixity.moderation.chat.TextFormatter;
import org.howie.pixity.moderation.neoforge.crate.CrateManager;

import java.util.ArrayList;
import java.util.List;

public class CratePreviewMenu {



    private static final int[] REWARD_SLOTS = {

            10,11,12,13,14,15,16,

            19,20,21,22,23,24,25,

            28,29,30,31,32,33,34,

            37,38,39,40,41,42,43
    };



    public static void open(

            ServerPlayer player,

            String crateId
    ) {

        open(player, crateId, 0);
    }


    public static void open(

            ServerPlayer player,

            String crateId,

            int page
    ) {

        var crate =
                CrateManager.get(crateId);

        if (crate == null) {
            return;
        }

        SimpleContainer cont =
                new SimpleContainer(54);

        MenuConstructor ctor =
                (id, inv, p) -> new ChestMenu(
                        MenuType.GENERIC_9x6,
                        id,
                        inv,
                        cont,
                        6
                ) {

                    @Override
                    public void broadcastChanges() {

                        super.broadcastChanges();



                        ItemStack filler =
                                new ItemStack(
                                        Items.GRAY_STAINED_GLASS_PANE
                                );

                        filler.set(
                                DataComponents.CUSTOM_NAME,
                                Component.literal("")
                        );

                        int[] border = {

                                0,1,2,3,4,5,6,7,8,

                                9,17,

                                18,26,

                                27,35,36,

                                44,45,46,47,

                                49, 51,

                                52,53
                        };

                        for (int slot : border) {

                            this.getSlot(slot)
                                    .set(filler.copy());
                        }



                        this.getSlot(48)
                                .set(filler.copy());

                        this.getSlot(50)
                                .set(filler.copy());



                        int totalWeight = 0;

                        for (var reward : crate.rewards) {
                            totalWeight += reward.weight;
                        }

                        int start =
                                page * REWARD_SLOTS.length;

                        int end =
                                Math.min(
                                        start + REWARD_SLOTS.length,
                                        crate.rewards.size()
                                );

                        int slotIndex = 0;

                        for (
                                int i = start;
                                i < end;
                                i++
                        ) {

                            var reward =
                                    crate.rewards.get(i);

                            ItemStack item =
                                    CrateAnimationMenu.createDisplayItem(reward);

                            List<Component> lore =
                                    new ArrayList<>();


                            var existingLore =
                                    item.get(DataComponents.LORE);

                            if (existingLore != null) {

                                lore.addAll(
                                        existingLore.lines()
                                );
                            }



                            double chance =
                                    (reward.weight / (double) totalWeight)
                                            * 100.0;

                            lore.add(Component.empty());

                            lore.add(
                                    CachedText.of(
                                            "&eChance: &f"
                                                    + String.format(
                                                    "%.2f",
                                                    chance
                                            )
                                                    + "%"
                                    )
                            );



                            item.set(
                                    DataComponents.LORE,
                                    new ItemLore(lore)
                            );

                            this.getSlot(
                                    REWARD_SLOTS[slotIndex++]
                            ).set(item);
                        }


                        if (page > 0) {

                            ItemStack prev =
                                    new ItemStack(
                                            Items.ARROW
                                    );

                            prev.set(
                                    DataComponents.CUSTOM_NAME,
                                    CachedText.of(
                                            "&c← Previous Page"
                                    )
                            );

                            this.getSlot(48)
                                    .set(prev);
                        }


                        if (
                                end < crate.rewards.size()
                        ) {

                            ItemStack next =
                                    new ItemStack(
                                            Items.ARROW
                                    );

                            next.set(
                                    DataComponents.CUSTOM_NAME,
                                    CachedText.of(
                                            "&aNext Page →"
                                    )
                            );

                            this.getSlot(50)
                                    .set(next);
                        }
                    }

                    @Override
                    public void clicked(

                            int slot,

                            int button,

                            ClickType type,

                            Player p
                    ) {



                        if (!(p instanceof ServerPlayer sp)) {
                            return;
                        }



                        if (slot == 48) {

                            if (page > 0) {

                                CratePreviewMenu.open(
                                        sp,
                                        crateId,
                                        page - 1
                                );
                            }

                            return;
                        }



                        if (slot == 50) {

                            int nextStart =
                                    (page + 1) * REWARD_SLOTS.length;

                            if (nextStart < crate.rewards.size()) {

                                CratePreviewMenu.open(
                                        sp,
                                        crateId,
                                        page + 1
                                );
                            }

                            return;
                        }


                        if (slot < 54) {
                            return;
                        }

                        super.clicked(
                                slot,
                                button,
                                type,
                                p
                        );
                    }

                    @Override
                    public boolean stillValid(
                            Player player
                    ) {
                        return true;
                    }
                };

        player.openMenu(

                new SimpleMenuProvider(

                        ctor,

                        TextFormatter.parse(

                                crate.display
                                        + " &7(Page "
                                        + (page + 1)
                                        + ")"
                        )
                )
        );
    }
}