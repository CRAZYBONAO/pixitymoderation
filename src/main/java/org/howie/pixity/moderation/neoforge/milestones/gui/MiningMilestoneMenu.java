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

import org.howie.pixity.moderation.chat.TextFormatter;
import org.howie.pixity.moderation.neoforge.milestones.core.MilestoneRegistry;

public class MiningMilestoneMenu extends ChestMenu {

    private final SimpleContainer container;

    public MiningMilestoneMenu(
            int id,
            Inventory inv
    ) {

        this(
                id,
                inv,
                new SimpleContainer(27)
        );
    }

    private MiningMilestoneMenu(
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

        render();
    }





    private void render() {

        filler();






        ItemStack vanilla =
                new ItemStack(Items.DIAMOND_ORE);

        vanilla.set(
                DataComponents.CUSTOM_NAME,
                TextFormatter.parse(
                        "<gradient:#5D5D5D:#FFFFFF:#777777>&lVANILLA ORE MILESTONES</gradient>"
                )
        );

        container.setItem(11, vanilla);






        ItemStack cobble =
                MilestoneRegistry
                        .cobble("dawn_stone_ore")
                        .copy();

        cobble.set(
                DataComponents.CUSTOM_NAME,
                TextFormatter.parse(
                        "<gradient:#00F5FF:#FFFFFF:#00F5FF>&lCOBBLEMON ORE MILESTONES</gradient>"
                )
        );

        container.setItem(15, cobble);






        ItemStack back =
                new ItemStack(Items.BARRIER);

        back.set(
                DataComponents.CUSTOM_NAME,
                TextFormatter.parse(
                        "&c&lBACK"
                )
        );

        container.setItem(22, back);
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
                    i == 11 ||
                            i == 15 ||
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





        if (slot == 11) {

            VanillaMiningMilestoneMenu.open(sp);
            return;
        }





        if (slot == 15) {

            CobblemonMiningMilestoneMenu.open(sp);
            return;
        }





        if (slot == 22) {

            MilestoneCategoriesMenu.open(sp);
            return;
        }


        if (slot >= 0 && slot < 27) {
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
                                "<gradient:#5D5D5D:#FFFFFF:#777777>&lMINING MILESTONES</gradient>"
                        );
                    }

                    @Override
                    public AbstractContainerMenu createMenu(
                            int id,
                            Inventory inv,
                            Player player
                    ) {

                        return new MiningMilestoneMenu(
                                id,
                                inv
                        );
                    }
                }
        );
    }
}