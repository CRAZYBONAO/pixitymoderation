package org.howie.pixity.moderation.neoforge.milestones.gui;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;

import net.minecraft.server.level.ServerPlayer;

import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;

import org.howie.pixity.moderation.chat.TextFormatter;

import org.howie.pixity.moderation.neoforge.milestones.core.*;

public class EventMilestoneMenu extends ChestMenu {

    private final SimpleContainer container;





    public EventMilestoneMenu(
            int id,
            Inventory inv
    ) {

        this(
                id,
                inv,
                new SimpleContainer(27)
        );
    }

    private EventMilestoneMenu(
            int id,
            Inventory inv,
            SimpleContainer cont
    ) {

        super(
                MenuType.GENERIC_9x3,
                id,
                inv,
                cont,
                3
        );

        this.container = cont;

        render(inv.player);
    }





    private void render(Player player) {

        filler();





        create(
                player,
                4,
                "total_event_wins",
                "total_event_wins",
                new ItemStack(Items.NETHER_STAR),
                "&r<rainbow>&lTOTAL EVENTS WON MILESTONES</rainbow>"
        );





        create(
                player,
                9,
                "events_fishing",
                "fishing_event_wins",
                new ItemStack(Items.FISHING_ROD),
                "<gradient:#00CFFF:#0066FF>TOTAL FISHIN</gradient><gradient:#0066FF:#0066FF>G EVENTS WON MILESTONES</gradient>"
        );





        create(
                player,
                11,
                "events_mining",
                "mining_event_wins",
                new ItemStack(Items.NETHERITE_PICKAXE),
                "<gradient:#5D5D5D:#FFFFFF:#777777>&lTOTAL MINING EVENTS WON MILESTONES</gradient>"
        );





        create(
                player,
                13,
                "events_farming",
                "farming_event_wins",
                new ItemStack(Items.NETHERITE_HOE),
                "<gradient:#2DFF00:#14640C:#2DFF00>&lTOTAL FARMING EVENTS WON MILESTONES</gradient>"
        );





        create(
                player,
                15,
                "events_pvp",
                "pvp_event_wins",
                new ItemStack(Items.NETHERITE_SWORD),
                "<gradient:#FF0000:#FFFFFF:#FF0000>TOTAL PVP EVENTS WON</gradient>"
        );





        create(
                player,
                17,
                "events_tournaments",
                "tournament_event_wins",
                MilestoneRegistry.cobble("poke_ball"),
                "<gradient:#FFFFFF:#FF0000:#FFFFFF>TOTAL TOURNAMENT EVENTS WON</gradient>"
        );





        ItemStack back =
                new ItemStack(Items.BARRIER);

        back.set(
                DataComponents.CUSTOM_NAME,
                TextFormatter.parse(
                        "&c&lBACK TO MILESTONE MENU"
                )
        );

        container.setItem(22, back);
    }





    private void create(
            Player player,
            int slot,
            String id,
            String stat,
            ItemStack stack,
            String display
    ) {

        stack.set(
                DataComponents.CUSTOM_NAME,
                TextFormatter.parse(display)
        );

        if (player instanceof ServerPlayer sp) {

            MilestoneEntry fake =
                    new MilestoneEntry(
                            id,
                            stat,
                            stack.copy(),
                            display,
                            slot,
                            MilestoneCategory.EVENTS,
                            "events"
                    );

            stack.set(
                    DataComponents.LORE,
                    new ItemLore(
                            MilestoneLoreBuilder.build(
                                    sp,
                                    fake
                            )
                    )
            );
        }

        container.setItem(
                slot,
                stack
        );
    }





    private void filler() {

        ItemStack filler =
                new ItemStack(
                        Items.BLACK_STAINED_GLASS_PANE
                );

        filler.set(
                DataComponents.CUSTOM_NAME,
                Component.empty()
        );

        for (int i = 0; i < 27; i++) {

            if (
                    i == 4 ||
                            i == 9 ||
                            i == 11 ||
                            i == 13 ||
                            i == 15 ||
                            i == 17 ||
                            i == 22
            ) {
                continue;
            }

            container.setItem(i, filler);
        }
    }





    @Override
    public void clicked(
            int slot,
            int button,
            ClickType type,
            Player player
    ) {

        if (!(player instanceof ServerPlayer sp)) {
            return;
        }


        if (slot >= 0 && slot < 27) {

            switch (slot) {

                case 4 -> MilestoneProgressionGui.open(
                        sp,
                        "total_event_wins",
                        0
                );

                case 9 -> MilestoneProgressionGui.open(
                        sp,
                        "events_fishing",
                        0
                );

                case 11 -> MilestoneProgressionGui.open(
                        sp,
                        "events_mining",
                        0
                );

                case 13 -> MilestoneProgressionGui.open(
                        sp,
                        "events_farming",
                        0
                );

                case 15 -> MilestoneProgressionGui.open(
                        sp,
                        "events_pvp",
                        0
                );

                case 17 -> MilestoneProgressionGui.open(
                        sp,
                        "events_tournaments",
                        0
                );

                case 22 -> MilestoneCategoriesMenu.open(sp);
            }

            return;
        }

        super.clicked(slot, button, type, player);
    }





    @Override
    public ItemStack quickMoveStack(
            Player player,
            int index
    ) {
        return ItemStack.EMPTY;
    }





    public static void open(
            ServerPlayer player
    ) {

        player.openMenu(
                new MenuProvider() {

                    @Override
                    public Component getDisplayName() {

                        return TextFormatter.parse(
                                "<rainbow>&lEVENT MILESTONES</rainbow>"
                        );
                    }

                    @Override
                    public AbstractContainerMenu createMenu(
                            int id,
                            Inventory inv,
                            Player player
                    ) {

                        return new EventMilestoneMenu(
                                id,
                                inv
                        );
                    }
                }
        );
    }
}