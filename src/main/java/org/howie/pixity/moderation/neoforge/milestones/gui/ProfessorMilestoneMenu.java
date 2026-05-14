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

public class ProfessorMilestoneMenu extends ChestMenu {

    private final SimpleContainer container;





    public ProfessorMilestoneMenu(
            int id,
            Inventory inv
    ) {

        this(
                id,
                inv,
                new SimpleContainer(27)
        );
    }

    private ProfessorMilestoneMenu(
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
                "pokemon_caught",
                "pokemon_caught",
                MilestoneRegistry.cobble("poke_ball"),
                "<gradient:#FF0000:#FFFFFF:#FF0000>&lPOKEMON CAUGHT</gradient>"
        );





        create(
                player,
                3,
                "pokemon_hidden_ability",
                "pokemon_hidden_ability_caught",
                MilestoneRegistry.cobble("premier_ball"),
                "<gradient:#FF0000:#FFFFFF:#FF0000>&lHIDDEN ABILITY POKEMON CAUGHT</gradient>"
        );





        create(
                player,
                5,
                "pokemon_shiny",
                "pokemon_shiny_caught",
                MilestoneRegistry.cobble("citrine_ball"),
                "<gradient:#C8FF00:#FFFFFF:#BEFF00>&lSHINY POKEMON CAUGHT</gradient>"
        );





        create(
                player,
                7,
                "pokemon_mythical",
                "pokemon_mythical_caught",
                MilestoneRegistry.cobble("master_ball"),
                "<gradient:#FF81EC:#920CF3>&lMYTHICAL POKEMON CAUGHT</gradient>"
        );





        create(
                player,
                11,
                "pokemon_legendary",
                "pokemon_legendary_caught",
                MilestoneRegistry.cobble("ancient_origin_ball"),
                "<gradient:#FF0000:#9400FF>&lLEGENDARY POKEMON CAUGHT</gradient>"
        );





        create(
                player,
                15,
                "pokemon_special",
                "pokemon_special_caught",
                MilestoneRegistry.cobble("ancient_poke_ball"),
                "<gradient:#FF0808:#920CF3>&lTOTAL SPE</gradient><gradient:#920CF3:#D88BFF>&lCIAL POKEM</gradient><gradient:#D88BFF:#001CFF>&lON CAUGHT</gradient>"
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
                            MilestoneCategory.PROFESSOR,
                            "professor"
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

        switch (slot) {

            case 1 ->
                    MilestoneProgressionGui.open(
                            sp,
                            "pokemon_caught",
                            0
                    );

            case 3 ->
                    MilestoneProgressionGui.open(
                            sp,
                            "pokemon_hidden_ability",
                            0
                    );

            case 5 ->
                    MilestoneProgressionGui.open(
                            sp,
                            "pokemon_shiny",
                            0
                    );

            case 7 ->
                    MilestoneProgressionGui.open(
                            sp,
                            "pokemon_mythical",
                            0
                    );

            case 11 ->
                    MilestoneProgressionGui.open(
                            sp,
                            "pokemon_legendary",
                            0
                    );

            case 15 ->
                    MilestoneProgressionGui.open(
                            sp,
                            "pokemon_special",
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
                                "<gradient:#8DD6EE:#FFFFFF:#8DD6EE>&lPOKEMON CAPTURE MILESTONES</gradient>"
                        );
                    }

                    @Override
                    public AbstractContainerMenu createMenu(
                            int id,
                            Inventory inv,
                            Player player
                    ) {

                        return new ProfessorMilestoneMenu(
                                id,
                                inv
                        );
                    }
                }
        );
    }
}