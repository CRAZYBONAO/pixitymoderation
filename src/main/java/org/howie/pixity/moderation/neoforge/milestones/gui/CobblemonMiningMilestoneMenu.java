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
import org.howie.pixity.moderation.neoforge.milestones.core.MilestoneCategory;
import org.howie.pixity.moderation.neoforge.milestones.core.MilestoneEntry;
import org.howie.pixity.moderation.neoforge.milestones.core.MilestoneLoreBuilder;
import org.howie.pixity.moderation.neoforge.milestones.core.MilestoneRegistry;

import java.util.ArrayList;
import java.util.List;

public class CobblemonMiningMilestoneMenu extends ChestMenu {

    private final SimpleContainer container;

    public CobblemonMiningMilestoneMenu(
            int id,
            Inventory inv
    ) {

        this(
                id,
                inv,
                new SimpleContainer(45)
        );
    }

    private CobblemonMiningMilestoneMenu(
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





    private void render(Player player) {

        filler();





        createOre(
                player,
                10,
                "dawn_stone_ore",
                "<gradient:#00F5FF:#FFFFFF:#00F5FF>&lDAWN STONE ORE MILESTONES</gradient>",
                "dawn_stone_ore_mined"
        );





        createOre(
                player,
                12,
                "dusk_stone_ore",
                "<gradient:#5600FF:#404040:#4100FF>&lDUSK STONE ORE MILESTONES</gradient>",
                "dusk_stone_ore_mined"
        );





        createOre(
                player,
                14,
                "fire_stone_ore",
                "<gradient:#FF0000:#FF9B00:#FF0000>&lFIRE STONE ORE MILESTONES</gradient>",
                "fire_stone_ore_mined"
        );





        createOre(
                player,
                16,
                "ice_stone_ore",
                "<gradient:#84FAF0:#0064FF:#91FFF6>&lICE STONE ORE MILESTONES</gradient>",
                "ice_stone_ore_mined"
        );





        createOre(
                player,
                20,
                "water_stone_ore",
                "<gradient:#0E00FF:#0064FF:#0007FF>&lWATER STONE ORE MILESTONES</gradient>",
                "water_stone_ore_mined"
        );





        createOre(
                player,
                24,
                "thunder_stone_ore",
                "<gradient:#23D211:#F1FF00:#00FF11>&lTHUNDER STONE ORE MILESTONES</gradient>",
                "thunder_stone_ore_mined"
        );





        createOre(
                player,
                28,
                "leaf_stone_ore",
                "<gradient:#0A4704:#91FFA1:#025107>&lLEAF STONE ORE MILESTONES</gradient>",
                "leaf_stone_ore_mined"
        );





        createOre(
                player,
                30,
                "moon_stone_ore",
                "<gradient:#434343:#1E3A77:#585858>&lMOON STONE ORE MILESTONES</gradient>",
                "moon_stone_ore_mined"
        );





        createOre(
                player,
                32,
                "shiny_stone_ore",
                "<gradient:#F5E987:#9EF59D:#FEFF97>&lSHINY STONE ORE MILESTONES</gradient>",
                "shiny_stone_ore_mined"
        );





        createOre(
                player,
                34,
                "sun_stone_ore",
                "<gradient:#FFAC12:#91720D:#FFB000>&lSUN STONE ORE MILESTONES</gradient>",
                "sun_stone_ore_mined"
        );





        ItemStack back =
                new ItemStack(Items.BARRIER);

        back.set(
                DataComponents.CUSTOM_NAME,
                TextFormatter.parse(
                        "&c&lBACK TO MINING MILESTONE MENU"
                )
        );

        container.setItem(40, back);
    }





    private void createOre(
            Player player,
            int slot,
            String oreId,
            String display,
            String stat
    ) {

        ItemStack stack =
                MilestoneRegistry
                        .cobble(oreId)
                        .copy();

        stack.set(
                DataComponents.CUSTOM_NAME,
                TextFormatter.parse(display)
        );

        if (player instanceof ServerPlayer sp) {

            MilestoneEntry fake =
                    new MilestoneEntry(
                            oreId,
                            stat,
                            stack.copy(),
                            display,
                            slot,
                            MilestoneCategory.MINING,
                            "mining_cobblemon"
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

        container.setItem(slot, stack);
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
                    i == 10 ||
                            i == 12 ||
                            i == 14 ||
                            i == 16 ||

                            i == 20 ||
                            i == 24 ||

                            i == 28 ||
                            i == 30 ||
                            i == 32 ||
                            i == 34 ||

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

            case 10 ->
                    MilestoneProgressionGui.open(
                            sp,
                            "dawn_stone_ore",
                            0
                    );

            case 12 ->
                    MilestoneProgressionGui.open(
                            sp,
                            "dusk_stone_ore",
                            0
                    );

            case 14 ->
                    MilestoneProgressionGui.open(
                            sp,
                            "fire_stone_ore",
                            0
                    );

            case 16 ->
                    MilestoneProgressionGui.open(
                            sp,
                            "ice_stone_ore",
                            0
                    );

            case 20 ->
                    MilestoneProgressionGui.open(
                            sp,
                            "water_stone_ore",
                            0
                    );


            case 24 ->
                    MilestoneProgressionGui.open(
                            sp,
                            "thunder_stone_ore",
                            0
                    );

            case 28 ->
                    MilestoneProgressionGui.open(
                            sp,
                            "leaf_stone_ore",
                            0
                    );

            case 30 ->
                    MilestoneProgressionGui.open(
                            sp,
                            "moon_stone_ore",
                            0
                    );

            case 32 ->
                    MilestoneProgressionGui.open(
                            sp,
                            "shiny_stone_ore",
                            0
                    );

            case 34 ->
                    MilestoneProgressionGui.open(
                            sp,
                            "sun_stone_ore",
                            0
                    );
        }





        if (slot == 40) {

            MiningMilestoneMenu.open(sp);
            return;
        }


        if (slot >= 0 && slot < 45) {
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
                                "<gradient:#00F5FF:#FFFFFF:#00F5FF>&lCOBBLEMON ORE MILESTONES</gradient>"
                        );
                    }

                    @Override
                    public AbstractContainerMenu createMenu(
                            int id,
                            Inventory inv,
                            Player player
                    ) {

                        return new CobblemonMiningMilestoneMenu(
                                id,
                                inv
                        );
                    }
                }
        );
    }
}