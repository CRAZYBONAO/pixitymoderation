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

import java.util.ArrayList;
import java.util.List;

public class GenericMilestoneGui extends ChestMenu {






    private static final int[] DISPLAY_SLOTS = {

            37,
            28,
            19,
            10,

            11,
            12,

            21,
            30,
            39,

            40,
            41,

            32,
            23,
            14,

            15,
            16,

            25,
            34,
            43
    };





    private final List<MilestoneEntry> entries;

    private final MilestoneCategory category;

    private final int page;





    public GenericMilestoneGui(
            int id,
            Inventory inv,
            MilestoneCategory category,
            List<MilestoneEntry> entries,
            int page
    ) {

        super(
                MenuType.GENERIC_9x6,
                id,
                inv,
                new SimpleContainer(54),
                6
        );

        this.entries = entries;
        this.category = category;
        this.page = page;

        render(inv.player);
    }





    private void render(Player player) {

        filler();

        int start =
                page * DISPLAY_SLOTS.length;

        int end =
                Math.min(
                        start + DISPLAY_SLOTS.length,
                        entries.size()
                );

        for (int i = start; i < end; i++) {

            MilestoneEntry entry =
                    entries.get(i);

            int local =
                    i - start;

            int slot =
                    DISPLAY_SLOTS[local];

            ItemStack item =
                    entry.icon.copy();

            item.set(
                    DataComponents.CUSTOM_NAME,
                    TextFormatter.parse(
                            entry.displayName
                    )
            );





            List<Component> lore =
                    new ArrayList<>();

            MilestoneDefinition definition =
                    MilestoneDefinitionRegistry.get(
                            entry.id
                    );

            if (definition != null &&
                    player instanceof ServerPlayer sp) {

                var progress =
                        MilestoneProgressService
                                .getProgress(
                                        sp,
                                        definition
                                );

                lore.add(TextFormatter.parse(""));

                lore.add(TextFormatter.parse(
                        "<gray>Milestone Level:</gray> " +
                                "<green>" + progress.level() + "</green>"
                ));

                lore.add(TextFormatter.parse(
                        "<gray>Progress:</gray> " +
                                "<yellow>" +
                                progress.currentValue() +
                                "</yellow>" +
                                "<gray>/</gray>" +
                                "<green>" +
                                progress.nextRequirement() +
                                "</green>"
                ));





                int bars =
                        (int)(progress.percent() / 10.0);

                StringBuilder bar =
                        new StringBuilder();

                for (int b = 0; b < 10; b++) {

                    if (b < bars) {
                        bar.append("█");
                    }
                    else {
                        bar.append("░");
                    }
                }

                lore.add(TextFormatter.parse(
                        "<green>" +
                                bar +
                                "</green> <white>" +
                                (int)progress.percent() +
                                "%</white>"
                ));

                lore.add(TextFormatter.parse(""));

                lore.add(TextFormatter.parse(
                        "<gray>Next Reward:</gray>"
                ));

                lore.add(TextFormatter.parse(
                        "<green>$" +
                                definition.getMoney(
                                        Math.min(
                                                definition.maxLevel,
                                                progress.level() + 1
                                        )
                                )
                ));

                lore.add(TextFormatter.parse(
                        "&b" +
                                definition.getTokens(
                                        Math.min(
                                                definition.maxLevel,
                                                progress.level() + 1
                                        )
                                ) +
                                " Tokens"
                ));

                lore.add(TextFormatter.parse(""));

                lore.addAll(
                        MilestoneLoreBuilder.build(
                                sp,
                                entry
                        )
                );
            }

            item.set(
                    DataComponents.LORE,
                    new ItemLore(lore)
            );

            getContainer().setItem(
                    slot,
                    item
            );
        }

        buttons();
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

        for (int i = 0; i < 54; i++) {

            boolean skip = false;

            for (int s : DISPLAY_SLOTS) {

                if (s == i) {
                    skip = true;
                    break;
                }
            }

            if (
                    skip ||
                            i == 48 ||
                            i == 49 ||
                            i == 50
            ) {
                continue;
            }

            getContainer().setItem(
                    i,
                    filler
            );
        }
    }





    private void buttons() {





        if (page > 0) {

            ItemStack prev =
                    new ItemStack(Items.ARROW);

            prev.set(
                    DataComponents.CUSTOM_NAME,
                    TextFormatter.parse(
                            "<yellow>Previous Page</yellow>"
                    )
            );

            getContainer().setItem(48, prev);
        }





        if ((page + 1) * DISPLAY_SLOTS.length
                < entries.size()) {

            ItemStack next =
                    new ItemStack(Items.ARROW);

            next.set(
                    DataComponents.CUSTOM_NAME,
                    TextFormatter.parse(
                            "<yellow>Next Page</yellow>"
                    )
            );

            getContainer().setItem(50, next);
        }







        ItemStack back =
                new ItemStack(Items.BARRIER);

        back.set(
                DataComponents.CUSTOM_NAME,
                TextFormatter.parse(
                        "<red>Back</red>"
                )
        );

        getContainer().setItem(49, back);


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





        if (slot == 48 && page > 0) {

            open(
                    sp,
                    category,
                    entries,
                    page - 1
            );

            return;
        }





        if (
                slot == 50 &&
                        (page + 1) * DISPLAY_SLOTS.length
                                < entries.size()
        ) {

            open(
                    sp,
                    category,
                    entries,
                    page + 1
            );

            return;
        }





        for (int i = 0; i < DISPLAY_SLOTS.length; i++) {

            if (DISPLAY_SLOTS[i] != slot) {
                continue;
            }

            int index =
                    (page * DISPLAY_SLOTS.length) + i;

            if (index >= entries.size()) {
                return;
            }

            MilestoneEntry entry =
                    entries.get(index);

            MilestoneProgressionGui.open(
                    sp,
                    entry.id,
                    0
            );

            return;
        }





        if (slot == 49) {

            if (
                    category == MilestoneCategory.MINING ||
                            category == MilestoneCategory.PROFESSOR ||
                            category == MilestoneCategory.TRAINER
            ) {

                MilestoneGroupMenu.open(
                        sp,
                        category
                );
            }
            else {

                MilestoneCategoriesMenu.open(sp);
            }
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
            MilestoneCategory category,
            List<MilestoneEntry> entries,
            int page
    ) {

        player.openMenu(
                new MenuProvider() {

                    @Override
                    public Component getDisplayName() {

                        return TextFormatter.parse(
                                category.title
                        );
                    }

                    @Override
                    public AbstractContainerMenu createMenu(
                            int id,
                            Inventory inv,
                            Player player
                    ) {

                        return new GenericMilestoneGui(
                                id,
                                inv,
                                category,
                                entries,
                                page
                        );
                    }
                }
        );
    }
}