package org.howie.pixity.moderation.neoforge.milestones.gui;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import org.howie.pixity.moderation.chat.TextFormatter;

import org.howie.pixity.moderation.neoforge.milestones.core.MilestoneCategory;
import org.howie.pixity.moderation.neoforge.milestones.core.MilestoneRegistry;

public class MilestoneGroupMenu extends ChestMenu {

    private final MilestoneCategory category;





    public MilestoneGroupMenu(
            int id,
            Inventory inv,
            MilestoneCategory category
    ) {

        super(
                MenuType.GENERIC_9x3,
                id,
                inv,
                new SimpleContainer(27),
                3
        );

        this.category = category;

        render();
    }





    private void render() {

        filler();

        switch (category) {





            case MINING -> {

                ItemStack vanilla =
                        new ItemStack(Items.DIAMOND_ORE);

                vanilla.set(
                        DataComponents.CUSTOM_NAME,
                        TextFormatter.parse(
                                "<gradient:#5D5D5D:#FFFFFF:#777777>&lVANILLA ORE MILESTONES</gradient>"
                        )
                );

                getContainer().setItem(11, vanilla);

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

                getContainer().setItem(15, cobble);
            }





            case PROFESSOR -> {

                ItemStack caught =
                        MilestoneRegistry
                                .cobble("poke_ball")
                                .copy();

                caught.set(
                        DataComponents.CUSTOM_NAME,
                        TextFormatter.parse(
                                "<gradient:#FF0000:#FFFFFF:#FF0000>&lPOKEMON CAUGHT</gradient>"
                        )
                );

                getContainer().setItem(10, caught);

                ItemStack shiny =
                        MilestoneRegistry
                                .cobble("luxury_ball")
                                .copy();

                shiny.set(
                        DataComponents.CUSTOM_NAME,
                        TextFormatter.parse(
                                "<gradient:#C8FF00:#FFFFFF:#BEFF00>&lSHINY POKEMON</gradient>"
                        )
                );

                getContainer().setItem(12, shiny);

                ItemStack legendary =
                        MilestoneRegistry
                                .cobble("master_ball")
                                .copy();

                legendary.set(
                        DataComponents.CUSTOM_NAME,
                        TextFormatter.parse(
                                "<gradient:#A020F0:#FFFFFF:#A020F0>&lLEGENDARY POKEMON</gradient>"
                        )
                );

                getContainer().setItem(14, legendary);

                ItemStack mythical =
                        MilestoneRegistry
                                .cobble("beast_ball")
                                .copy();

                mythical.set(
                        DataComponents.CUSTOM_NAME,
                        TextFormatter.parse(
                                "<gradient:#FF00FF:#FFFFFF:#FF00FF>&lMYTHICAL POKEMON</gradient>"
                        )
                );

                getContainer().setItem(16, mythical);
            }





            case TRAINER -> {

                ItemStack levels =
                        new ItemStack(Items.EXPERIENCE_BOTTLE);

                levels.set(
                        DataComponents.CUSTOM_NAME,
                        TextFormatter.parse(
                                "<gradient:#0064FF:#FFFFFF:#0E84F5>&lLEVELS GAINED</gradient>"
                        )
                );

                getContainer().setItem(10, levels);

                ItemStack lvl100 =
                        MilestoneRegistry
                                .cobble("rare_candy")
                                .copy();

                lvl100.set(
                        DataComponents.CUSTOM_NAME,
                        TextFormatter.parse(
                                "<gradient:#00FF3B:#FFFFFF:#0EF54C>&lLEVEL 100 POKEMON</gradient>"
                        )
                );

                getContainer().setItem(12, lvl100);

                ItemStack infusion =
                        new ItemStack(Items.NETHER_STAR);

                infusion.set(
                        DataComponents.CUSTOM_NAME,
                        TextFormatter.parse(
                                "<gradient:#00FFF7:#FFFFFF:#00FFF7>&lINFUSION</gradient>"
                        )
                );

                getContainer().setItem(14, infusion);

                ItemStack evolution =
                        new ItemStack(Items.DRAGON_BREATH);

                evolution.set(
                        DataComponents.CUSTOM_NAME,
                        TextFormatter.parse(
                                "<gradient:#FF7B00:#FFFFFF:#FF7B00>&lEVOLUTIONS</gradient>"
                        )
                );

                getContainer().setItem(16, evolution);
            }
        }





        ItemStack back =
                new ItemStack(Items.BARRIER);

        back.set(
                DataComponents.CUSTOM_NAME,
                TextFormatter.parse(
                        "<red>Back"
                )
        );

        getContainer().setItem(22, back);
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
                    i == 10 ||
                            i == 11 ||
                            i == 12 ||
                            i == 14 ||
                            i == 15 ||
                            i == 16 ||
                            i == 22
            ) {
                continue;
            }

            getContainer().setItem(i, filler);
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

        switch (category) {





            case MINING -> {

                if (slot == 11) {

                    GenericMilestoneGui.open(
                            sp,
                            MilestoneCategory.MINING,
                            MilestoneRegistry.getGroup(
                                    "mining_vanilla"
                            ),
                            0
                    );
                }

                if (slot == 15) {

                    GenericMilestoneGui.open(
                            sp,
                            MilestoneCategory.MINING,
                            MilestoneRegistry.getGroup(
                                    "mining_cobblemon"
                            ),
                            0
                    );
                }
            }





            case PROFESSOR -> {

                if (slot == 10) {

                    GenericMilestoneGui.open(
                            sp,
                            MilestoneCategory.PROFESSOR,
                            MilestoneRegistry.getGroup(
                                    "professor"
                            ),
                            0
                    );
                }
            }





            case TRAINER -> {

                if (slot == 10) {

                    GenericMilestoneGui.open(
                            sp,
                            MilestoneCategory.TRAINER,
                            MilestoneRegistry.getGroup(
                                    "trainer"
                            ),
                            0
                    );
                }
            }
        }





        if (slot == 22) {
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
            ServerPlayer player,
            MilestoneCategory category
    ) {

        player.openMenu(
                new net.minecraft.world.MenuProvider() {

                    @Override
                    public Component getDisplayName() {

                        return TextFormatter.parse(
                                category.title
                        );
                    }

                    @Override
                    public net.minecraft.world.inventory.AbstractContainerMenu createMenu(
                            int id,
                            Inventory inv,
                            Player player
                    ) {

                        return new MilestoneGroupMenu(
                                id,
                                inv,
                                category
                        );
                    }
                }
        );
    }
}