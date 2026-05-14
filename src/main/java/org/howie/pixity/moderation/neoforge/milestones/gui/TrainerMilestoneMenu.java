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

public class TrainerMilestoneMenu extends ChestMenu {

    private final SimpleContainer container;





    public TrainerMilestoneMenu(
            int id,
            Inventory inv
    ) {

        this(
                id,
                inv,
                new SimpleContainer(27)
        );
    }

    private TrainerMilestoneMenu(
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
                1,
                "trainer_levels_gained",
                "trainer_levels_gained",
                MilestoneRegistry.cobble("rare_candy"),
                "<gradient:#0064FF:#FFFFFF:#0E84F5>&lTOTAL LEVELS GAINED</gradient>"
        );





        create(
                player,
                3,
                "trainer_level100",
                "trainer_level100",
                MilestoneRegistry.cobble("kings_rock"),
                "<gradient:#00FF3B:#FFFFFF:#0EF54C>&lTOTAL POKEMON GOTTEN TO 100</gradient>"
        );





        create(
                player,
                5,
                "trainer_evolutions",
                "trainer_evolutions",
                MilestoneRegistry.cobble("shiny_stone"),
                "<gradient:#E4FF81:#FFFFFF:#F2FF74>&lTOTAL POKEMON EVOLVED</gradient>"
        );





        create(
                player,
                7,
                "trainer_infusion",
                "trainer_infusion",
                new ItemStack(Items.END_CRYSTAL),
                "<gradient:#FF8181:#8174FF>&lTOTAL POKEMON INFUSED</gradient>"
        );





        create(
                player,
                13,
                "trainer_happiness_gained",
                "trainer_happiness_gained",
                MilestoneRegistry.cobble("blue_mint_leaf"),
                "<gradient:#70FF91:#F7FF74>&lTOTAL HAPPINESS GAINED</gradient>"
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
                            MilestoneCategory.TRAINER,
                            "trainer"
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
                    i == 1 ||
                            i == 3 ||
                            i == 5 ||
                            i == 7 ||
                            i == 13 ||
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

        switch (slot) {

            case 1 ->
                    MilestoneProgressionGui.open(
                            sp,
                            "trainer_levels_gained",
                            0
                    );

            case 3 ->
                    MilestoneProgressionGui.open(
                            sp,
                            "trainer_level100",
                            0
                    );

            case 5 ->
                    MilestoneProgressionGui.open(
                            sp,
                            "trainer_evolutions",
                            0
                    );

            case 7 ->
                    MilestoneProgressionGui.open(
                            sp,
                            "trainer_infusion",
                            0
                    );

            case 13 ->
                    MilestoneProgressionGui.open(
                            sp,
                            "trainer_happiness_gained",
                            0
                    );

            case 22 ->
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
                                "<gradient:#EE8D8D:#FFFFFF:#EE8D8D>&lTRAINING MILESTONES</gradient>"
                        );
                    }

                    @Override
                    public AbstractContainerMenu createMenu(
                            int id,
                            Inventory inv,
                            Player player
                    ) {

                        return new TrainerMilestoneMenu(
                                id,
                                inv
                        );
                    }
                }
        );
    }
}