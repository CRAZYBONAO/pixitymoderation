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

public class FarmingMilestoneMenu extends ChestMenu {

    private final SimpleContainer container;





    public FarmingMilestoneMenu(
            int id,
            Inventory inv
    ) {

        this(
                id,
                inv,
                new SimpleContainer(45)
        );
    }

    private FarmingMilestoneMenu(
            int id,
            Inventory inv,
            SimpleContainer cont
    ) {

        super(
                MenuType.GENERIC_9x5,
                id,
                inv,
                cont,
                5
        );

        this.container = cont;

        render(inv.player);
    }





    private void render(
            Player player
    ) {

        filler();





        createCustom(
                player,
                1,
                "wheat",
                "wheat_crops_harvested",
                new ItemStack(Items.WHEAT),
                "<gradient:#CED729:#FCAF04:#DAEF0D>&lWHEAT CROPS HARVESTED MILESTONES</gradient>"
        );

        createCustom(
                player,
                3,
                "carrot",
                "carrot_crops_harvested",
                new ItemStack(Items.CARROT),
                "<gradient:#FF9100:#115F08:#EF960D>&lCARROT CROPS HARVESTED MILESTONES</gradient>"
        );

        createCustom(
                player,
                5,
                "potato",
                "potato_crops_harvested",
                new ItemStack(Items.POTATO),
                "<gradient:#F5C585:#6EFA5E:#FCCC83>&lPOTATO CROPS HARVESTED MILESTONES</gradient>"
        );

        createCustom(
                player,
                7,
                "beetroot",
                "beetroot_crops_harvested",
                new ItemStack(Items.BEETROOT),
                "<gradient:#7A1F3D:#6EFA5E:#7A1F3D>&lBEETROOT CROPS HARVESTED MILESTONES</gradient>"
        );





        createCustom(
                player,
                11,
                "sugarcane",
                "sugarcane_crops_harvested",
                new ItemStack(Items.SUGAR_CANE),
                "<gradient:#00FF07:#FFFFFF:#03FF00>&lSUGARCANE HARVESTED MILESTONES</gradient>"
        );

        createCustom(
                player,
                13,
                "bamboo",
                "bamboo_crop_harvested",
                new ItemStack(Items.BAMBOO),
                "<gradient:#075409:#FFFFFF:#074C06>&lBAMBOO HARVESTED MILESTONES</gradient>"
        );

        createCustom(
                player,
                15,
                "cocoa",
                "cocoa_crops_harvested",
                new ItemStack(Items.COCOA_BEANS),
                "<gradient:#543907:#867A35:#4C3906>&lCOCOA BEANS HARVESTED MILESTONES</gradient>"
        );





        createCustom(
                player,
                21,
                "pumpkin",
                "pumpkin_crops_harvested",
                new ItemStack(Items.PUMPKIN),
                "<gradient:#FFA600:#1C6707:#FFBA00>&lPUMPKINS HARVESTED MILESTONES</gradient>"
        );

        createCustom(
                player,
                23,
                "melon",
                "melon_crops_harvested",
                new ItemStack(Items.MELON),
                "<gradient:#286E04:#FF0000:#145601>&lMELONS HARVESTED MILESTONES</gradient>"
        );

        createCustom(
                player,
                31,
                "total_crops",
                "total_crops_harvested",
                new ItemStack(Items.NETHER_STAR),
                "&e&lTOTAL CROPS HARVESTED"
        );





        ItemStack back =
                new ItemStack(Items.BARRIER);

        back.set(
                DataComponents.CUSTOM_NAME,
                TextFormatter.parse(
                        "&c&lBACK TO MILESTONE MENU"
                )
        );

        container.setItem(40, back);
    }





    private void createCustom(
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
                            MilestoneCategory.FARMING,
                            "farming"
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

        for (int i = 0; i < 45; i++) {

            if (
                    i == 1 ||
                            i == 3 ||
                            i == 5 ||
                            i == 7 ||

                            i == 11 ||
                            i == 13 ||
                            i == 15 ||

                            i == 21 ||
                            i == 23 ||

                            i == 31 ||

                            i == 40
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

        switch (slot) {

            case 1 ->
                    MilestoneProgressionGui.open(
                            sp,
                            "wheat",
                            0
                    );

            case 3 ->
                    MilestoneProgressionGui.open(
                            sp,
                            "carrot",
                            0
                    );

            case 5 ->
                    MilestoneProgressionGui.open(
                            sp,
                            "potato",
                            0
                    );

            case 7 ->
                    MilestoneProgressionGui.open(
                            sp,
                            "beetroot",
                            0
                    );

            case 11 ->
                    MilestoneProgressionGui.open(
                            sp,
                            "sugarcane",
                            0
                    );

            case 13 ->
                    MilestoneProgressionGui.open(
                            sp,
                            "bamboo",
                            0
                    );

            case 15 ->
                    MilestoneProgressionGui.open(
                            sp,
                            "cocoa",
                            0
                    );

            case 21 ->
                    MilestoneProgressionGui.open(
                            sp,
                            "pumpkin",
                            0
                    );

            case 23 ->
                    MilestoneProgressionGui.open(
                            sp,
                            "melon",
                            0
                    );

            case 31 ->
                    MilestoneProgressionGui.open(
                            sp,
                            "total_crops",
                            0
                    );

            case 40 ->
                    MilestoneCategoriesMenu.open(sp);
        }
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
                                "<gradient:#2DFF00:#14640C:#2DFF00>&lFARMING MILESTONES</gradient>"
                        );
                    }

                    @Override
                    public AbstractContainerMenu createMenu(
                            int id,
                            Inventory inv,
                            Player player
                    ) {

                        return new FarmingMilestoneMenu(
                                id,
                                inv
                        );
                    }
                }
        );
    }
}