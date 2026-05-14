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

import org.howie.pixity.moderation.neoforge.fishing.FishingManager;
import org.howie.pixity.moderation.neoforge.milestones.core.*;

public class FishingMilestoneMenu extends ChestMenu {

    private final SimpleContainer container;





    public FishingMilestoneMenu(
            int id,
            Inventory inv
    ) {

        this(
                id,
                inv,
                new SimpleContainer(45)
        );
    }

    private FishingMilestoneMenu(
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
                "bronze_fish",
                "bronze_fish_caught",
                MilestoneRegistry.cobble("poke_ball"),
                "&#c99a73&lBRONZE FISH MILESTONES"
        );





        createCustom(
                player,
                3,
                "silver_fish",
                "silver_fish_caught",
                MilestoneRegistry.cobble("great_ball"),
                "&#bdbdbd&lSILVER FISH MILESTONES"
        );





        createCustom(
                player,
                5,
                "gold_fish",
                "gold_fish_caught",
                MilestoneRegistry.cobble("ultra_ball"),
                "&#f5f788&lGOLD FISH MILESTONES"
        );





        createCustom(
                player,
                7,
                "diamond_fish",
                "diamond_fish_caught",
                MilestoneRegistry.cobble("beast_ball"),
                "&#57f2e8&lDIAMOND FISH MILESTONES"
        );





        createCustom(
                player,
                11,
                "platinum_fish",
                "platinum_fish_caught",
                MilestoneRegistry.cobble("master_ball"),
                "<gradient:#A7F8FF:#FFFFFF:#A7F8FF>&lPLATINUM FISH MILESTONES</gradient>"
        );





        createCustom(
                player,
                13,
                "crabs_killed",
                "crabs_killed",
                FishingManager.createCustomItem("crab_claw"),
                "<gradient:#FF0000:#FFFFFF:#FF0000>&lCRAB KILLS MILESTONES</gradient>"
        );





        createCustom(
                player,
                15,
                "mythical_fish",
                "mythical_fish_caught",
                MilestoneRegistry.cobble("ancient_origin_ball"),
                "&#db2deb&lMYTHICAL FISH MILESTONES"
        );





        createCustom(
                player,
                21,
                "squid_kills",
                "squid_kills",
                FishingManager.createCustomItem("squid_tentacle"),
                "<gradient:#3B3B3B:#A400FF:#3B3B3B>&lSQUID KILLS MILESTONES</gradient>"
        );





        createCustom(
                player,
                23,
                "dolphin_kills",
                "dolphin_kills",
                FishingManager.createCustomItem("dolphin_treasure"),
                "<gradient:#00D9FF:#FFFFFF:#00D9FF>&lDOLPHIN KILLS MILESTONES</gradient>"
        );





        createCustom(
                player,
                31,
                "total_fish_caught",
                "total_fish_caught",
                new ItemStack(Items.TROPICAL_FISH),
                "<gradient:#00CFFF:#0066FF>&lTOTAL FISH CAUGHT</gradient>"
        );





        ItemStack back =
                new ItemStack(Items.BARRIER);

        back.set(
                DataComponents.CUSTOM_NAME,
                TextFormatter.parse(
                        "&c&lBACK"
                )
        );

        container.setItem(40, back);
    }





    private void create(
            Player player,
            int slot,
            MilestoneEntry entry
    ) {

        if (entry == null) {
            return;
        }

        ItemStack stack =
                entry.icon.copy();

        stack.set(
                DataComponents.CUSTOM_NAME,
                TextFormatter.parse(
                        entry.displayName
                )
        );

        if (player instanceof ServerPlayer sp) {

            stack.set(
                    DataComponents.LORE,
                    new ItemLore(
                            MilestoneLoreBuilder.build(
                                    sp,
                                    entry
                            )
                    )
            );
        }

        container.setItem(
                slot,
                stack
        );
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
                            MilestoneCategory.FISHING,
                            "fishing"
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
                            "bronze_fish",
                            0
                    );

            case 3 ->
                    MilestoneProgressionGui.open(
                            sp,
                            "silver_fish",
                            0
                    );

            case 5 ->
                    MilestoneProgressionGui.open(
                            sp,
                            "gold_fish",
                            0
                    );

            case 7 ->
                    MilestoneProgressionGui.open(
                            sp,
                            "diamond_fish",
                            0
                    );

            case 11 ->
                    MilestoneProgressionGui.open(
                            sp,
                            "platinum_fish",
                            0
                    );

            case 13 ->
                    MilestoneProgressionGui.open(
                            sp,
                            "crabs_killed",
                            0
                    );

            case 15 ->
                    MilestoneProgressionGui.open(
                            sp,
                            "mythical_fish",
                            0
                    );

            case 21 ->
                    MilestoneProgressionGui.open(
                            sp,
                            "squid_kills",
                            0
                    );

            case 23 ->
                    MilestoneProgressionGui.open(
                            sp,
                            "dolphin_kills",
                            0
                    );

            case 31 ->
                    MilestoneProgressionGui.open(
                            sp,
                            "total_fish_caught",
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
                                "<gradient:#00CFFF:#0066FF>&lFISHING MILESTONES</gradient>"
                        );
                    }

                    @Override
                    public AbstractContainerMenu createMenu(
                            int id,
                            Inventory inv,
                            Player player
                    ) {

                        return new FishingMilestoneMenu(
                                id,
                                inv
                        );
                    }
                }
        );
    }
}