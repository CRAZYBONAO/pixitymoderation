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
import org.howie.pixity.moderation.neoforge.milestones.core.MilestoneEntry;
import org.howie.pixity.moderation.neoforge.milestones.core.MilestoneLoreBuilder;
import org.howie.pixity.moderation.neoforge.milestones.core.MilestoneRegistry;

import java.util.ArrayList;
import java.util.List;

public class VanillaMiningMilestoneMenu extends ChestMenu {

    private final SimpleContainer container;

    public VanillaMiningMilestoneMenu(
            int id,
            Inventory inv
    ) {

        this(
                id,
                inv,
                new SimpleContainer(45)
        );
    }

    private VanillaMiningMilestoneMenu(
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
                Items.COAL_ORE,
                "&8&lCOAL ORE MILESTONES",
                "coal_ore"
        );






        createOre(
                player,
                12,
                Items.COPPER_ORE,
                "&#B87333&lCOPPER ORE MILESTONES",
                "copper_ore"
        );






        createOre(
                player,
                14,
                Items.IRON_ORE,
                "&7&lIRON ORE MILESTONES",
                "iron_ore"
        );






        createOre(
                player,
                16,
                Items.GOLD_ORE,
                "&#FFD700&lGOLD ORE MILESTONES",
                "gold_ore"
        );






        createOre(
                player,
                20,
                Items.NETHER_QUARTZ_ORE,
                "<gradient:#7A2A2A:#FFFFFF:#7A2A2A>&lQUARTZ ORE MILESTONES</gradient>",
                "quartz_ore"
        );






        createOre(
                player,
                22,
                Items.ANCIENT_DEBRIS,
                "<gradient:#7A2A2A:#434241:#7A2A2A>&lANCIENT DEBRIS MILESTONES</gradient>",
                "ancient_debris"
        );






        createOre(
                player,
                24,
                Items.NETHER_GOLD_ORE,
                "<gradient:#7A2A2A:#FCD05C:#7A2A2A>&lNETHER GOLD ORE MILESTONES</gradient>",
                "nether_gold_ore"
        );






        createOre(
                player,
                28,
                Items.REDSTONE_ORE,
                "&#75121a&lREDSTONE ORE MILESTONES",
                "redstone_ore"
        );






        createOre(
                player,
                30,
                Items.LAPIS_ORE,
                "&#4433b0&lLAPIS LAZULI ORE MILESTONES",
                "lapis_ore"
        );






        createOre(
                player,
                32,
                Items.DIAMOND_ORE,
                "&#00ebd7&lDIAMOND ORE MILESTONES",
                "diamond_ore"
        );






        createOre(
                player,
                34,
                Items.EMERALD_ORE,
                "&#00ff33&lEMERALD ORE MILESTONES",
                "emerald_ore"
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
            net.minecraft.world.item.Item item,
            String name,
            String stat
    ) {

        ItemStack stack =
                new ItemStack(item);

        stack.set(
                DataComponents.CUSTOM_NAME,
                TextFormatter.parse(name)
        );

        if (player instanceof ServerPlayer sp) {

            MilestoneEntry entry =
                    MilestoneRegistry.getEntry(stat);

            if (entry != null) {

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
                            i == 22 ||
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
                            "coal_ore",
                            0
                    );

            case 12 ->
                    MilestoneProgressionGui.open(
                            sp,
                            "copper_ore",
                            0
                    );

            case 14 ->
                    MilestoneProgressionGui.open(
                            sp,
                            "iron_ore",
                            0
                    );

            case 16 ->
                    MilestoneProgressionGui.open(
                            sp,
                            "gold_ore",
                            0
                    );

            case 20 ->
                    MilestoneProgressionGui.open(
                            sp,
                            "quartz_ore",
                            0
                    );

            case 22 ->
                    MilestoneProgressionGui.open(
                            sp,
                            "ancient_debris",
                            0
                    );

            case 24 ->
                    MilestoneProgressionGui.open(
                            sp,
                            "nether_gold_ore",
                            0
                    );

            case 28 ->
                    MilestoneProgressionGui.open(
                            sp,
                            "redstone_ore",
                            0
                    );

            case 30 ->
                    MilestoneProgressionGui.open(
                            sp,
                            "lapis_ore",
                            0
                    );

            case 32 ->
                    MilestoneProgressionGui.open(
                            sp,
                            "diamond_ore",
                            0
                    );

            case 34 ->
                    MilestoneProgressionGui.open(
                            sp,
                            "emerald_ore",
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
                                "<gradient:#5D5D5D:#FFFFFF:#777777>&lVANILLA ORE MILESTONES</gradient>"
                        );
                    }

                    @Override
                    public AbstractContainerMenu createMenu(
                            int id,
                            Inventory inv,
                            Player player
                    ) {

                        return new VanillaMiningMilestoneMenu(
                                id,
                                inv
                        );
                    }
                }
        );
    }
}