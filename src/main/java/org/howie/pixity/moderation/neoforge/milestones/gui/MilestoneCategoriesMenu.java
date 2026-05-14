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

public class MilestoneCategoriesMenu extends ChestMenu {

    private final SimpleContainer container;

    public MilestoneCategoriesMenu(
            int id,
            Inventory inv
    ) {

        this(
                id,
                inv,
                new SimpleContainer(27)
        );
    }

    private MilestoneCategoriesMenu(
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






        ItemStack mining =
                new ItemStack(Items.NETHERITE_PICKAXE);

        mining.set(
                DataComponents.CUSTOM_NAME,
                TextFormatter.parse(
                        "<gradient:#5D5D5D:#FFFFFF:#777777>&lMINING MILESTONES</gradient>"
                )
        );

        container.setItem(1, mining);






        ItemStack fishing =
                new ItemStack(Items.FISHING_ROD);

        fishing.set(
                DataComponents.CUSTOM_NAME,
                TextFormatter.parse(
                        "<gradient:#00CFFF:#0066FF>&lFISHING MILESTONES</gradient>"
                )
        );

        container.setItem(3, fishing);






        ItemStack farming =
                new ItemStack(Items.WHEAT);

        farming.set(
                DataComponents.CUSTOM_NAME,
                TextFormatter.parse(
                        "<gradient:#2DFF00:#14640C:#2DFF00>&lFARMING MILESTONES</gradient>"
                )
        );

        container.setItem(5, farming);






        ItemStack mobs =
                new ItemStack(Items.NETHERITE_SWORD);

        mobs.set(
                DataComponents.CUSTOM_NAME,
                TextFormatter.parse(
                        "<gradient:#4C0F0F:#FFAE18:#4C0F0F>&lMOB KILL MILESTONES</gradient>"
                )
        );

        container.setItem(7, mobs);






        ItemStack trainer =
                MilestoneRegistry.cobble("poke_ball").copy();

        trainer.set(
                DataComponents.CUSTOM_NAME,
                TextFormatter.parse(
                        "<gradient:#EE8D8D:#FFFFFF:#EE8D8D>&lTRAINING MILESTONES</gradient>"
                )
        );

        container.setItem(11, trainer);






        ItemStack events =
                new ItemStack(Items.NETHER_STAR);

        events.set(
                DataComponents.CUSTOM_NAME,
                TextFormatter.parse(
                        "<rainbow>&lEVENT MILESTONES</rainbow>"
                )
        );

        container.setItem(13, events);






        ItemStack professor =
                MilestoneRegistry.cobble("beast_ball").copy();

        professor.set(
                DataComponents.CUSTOM_NAME,
                TextFormatter.parse(
                        "<gradient:#8DD6EE:#FFFFFF:#8DD6EE>&lPOKEMON CAPTURE MILESTONES</gradient>"
                )
        );

        container.setItem(15, professor);






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
                    i == 1 ||
                            i == 3 ||
                            i == 5 ||
                            i == 7 ||
                            i == 11 ||
                            i == 13 ||
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

        switch (slot) {





            case 1 ->
                    MiningMilestoneMenu.open(sp);





            case 3 ->
                    FishingMilestoneMenu.open(sp);





            case 5 ->
                    FarmingMilestoneMenu.open(sp);





            case 7 ->
                    MobKillMilestoneMenu.open(sp);





            case 11 ->
                    TrainerMilestoneMenu.open(sp);





            case 13 ->
                    EventMilestoneMenu.open(sp);





            case 15 ->
                    ProfessorMilestoneMenu.open(sp);





            case 22 ->
                    sp.closeContainer();
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
                                "<gold>&lMILESTONES</gold>"
                        );
                    }

                    @Override
                    public AbstractContainerMenu createMenu(
                            int id,
                            Inventory inv,
                            Player player
                    ) {

                        return new MilestoneCategoriesMenu(
                                id,
                                inv
                        );
                    }
                }
        );
    }
}