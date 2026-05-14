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

import org.howie.pixity.moderation.PixityModerationNeoForge;
import org.howie.pixity.moderation.chat.TextFormatter;

import org.howie.pixity.moderation.neoforge.milestones.core.*;
import org.howie.pixity.moderation.neoforge.stats.PlayerStatsDatabase;

import java.util.ArrayList;
import java.util.List;

public class MilestoneProgressionGui extends ChestMenu {





    private static final int[] LEVEL_SLOTS = {

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





    private final SimpleContainer container;

    private final String milestoneId;

    private final MilestoneDefinition definition;

    private final int page;





    public MilestoneProgressionGui(
            int id,
            Inventory inv,
            String milestoneId,
            int page
    ) {

        this(
                id,
                inv,
                milestoneId,
                page,
                new SimpleContainer(54)
        );
    }

    private MilestoneProgressionGui(
            int id,
            Inventory inv,
            String milestoneId,
            int page,
            SimpleContainer cont
    ) {

        super(
                MenuType.GENERIC_9x6,
                id,
                inv,
                cont,
                6
        );

        this.container = cont;

        this.milestoneId = milestoneId;
        this.page = page;

        this.definition =
                MilestoneDefinitionRegistry.get(
                        milestoneId
                );

        render(inv.player);
    }





    private void render(Player player) {

        filler();

        if (!(player instanceof ServerPlayer sp)) {
            return;
        }





        if (definition == null) {

            ItemStack error =
                    new ItemStack(Items.BARRIER);

            error.set(
                    DataComponents.CUSTOM_NAME,
                    TextFormatter.parse(
                            "<red>&lINVALID MILESTONE</red>"
                    )
            );

            container.setItem(22, error);
            return;
        }





        int statValue =
                PlayerStatsDatabase.get(
                        sp.getUUID(),
                        definition.statColumn
                );

        var progress =
                MilestoneProgressService.getProgress(
                        sp,
                        definition
                );

        int currentLevel =
                progress.level();





        for (int i = 0; i < LEVEL_SLOTS.length; i++) {

            int level =
                    (page * LEVEL_SLOTS.length)
                            + i
                            + 1;

            int slot = LEVEL_SLOTS[i];

            ItemStack item;

            boolean claimed =
                    PixityModerationNeoForge
                            .MILESTONE_PLAYERS
                            .hasClaimed(
                                    sp,
                                    milestoneId,
                                    level
                            );

            boolean unlocked =
                    currentLevel >= level;





            if (claimed) {

                item =
                        new ItemStack(
                                Items.GREEN_CONCRETE_POWDER
                        );

                item.set(
                        DataComponents.CUSTOM_NAME,
                        TextFormatter.parse(
                                "&a&lCLAIMED MILESTONE "
                                        + level
                        )
                );
            }





            else if (unlocked) {

                item =
                        new ItemStack(
                                Items.ORANGE_CONCRETE_POWDER
                        );

                item.set(
                        DataComponents.ENCHANTMENT_GLINT_OVERRIDE,
                        true
                );

                item.set(
                        DataComponents.CUSTOM_NAME,
                        TextFormatter.parse(
                                "&e&lCLICK TO CLAIM MILESTONE "
                                        + level
                        )
                );
            }





            else {

                item =
                        new ItemStack(
                                Items.RED_CONCRETE_POWDER
                        );

                item.set(
                        DataComponents.CUSTOM_NAME,
                        TextFormatter.parse(
                                "&c&lLOCKED MILESTONE "
                                        + level
                        )
                );
            }





            int required =
                    definition.getRequired(level);

            double percent =
                    Math.min(
                            100D,
                            (statValue * 100D)
                                    / Math.max(1, required)
                    );





            List<Component> lore =
                    new ArrayList<>();

            lore.add(TextFormatter.parse(""));

            lore.add(TextFormatter.parse(
                    "<gray>Milestone Level:</gray> " +
                            "<yellow>" + level + "</yellow>"
            ));

            lore.add(TextFormatter.parse(
                    "<gray>Your Progress:</gray> " +
                            "<green>" + statValue + "</green>"
            ));

            lore.add(TextFormatter.parse(
                    "&7Required: " +
                            "&b" + required
            ));

            lore.add(TextFormatter.parse(
                    "<gray>Completion:</gray> " +
                            "<gold>" +
                            String.format("%.1f", percent) +
                            "%</gold>"
            ));

            lore.add(TextFormatter.parse(""));

            lore.add(TextFormatter.parse(
                    "<green>&lRewards</green>"
            ));

            lore.add(TextFormatter.parse(
                    "<green>$" +
                            definition.getMoney(level) +
                            "</green>"
            ));

            lore.add(TextFormatter.parse(
                    "<gold>" +
                            definition.getTokens(level) +
                            " Tokens</gold>"
            ));





            if (!definition.commandRewards.isEmpty()) {

                lore.add(TextFormatter.parse(""));

                lore.add(TextFormatter.parse(
                        "<light_purple>*lSpecial Rewards</light_purple>"
                ));

                for (MilestoneCommandReward reward :
                        definition.commandRewards) {

                    if (reward.shouldGive(level)) {

                        lore.add(TextFormatter.parse(
                                "<light_purple>• Special Reward Every "
                                        + reward.every +
                                        " Levels</light_purple>"
                        ));
                    }
                }
            }

            lore.add(TextFormatter.parse(""));





            if (claimed) {

                lore.add(TextFormatter.parse(
                        "<green>&lCLAIMED</green>"
                ));
            }

            else if (unlocked) {

                lore.add(TextFormatter.parse(
                        "<yellow>&lCLICK TO CLAIM</yellow>"
                ));
            }

            else {

                lore.add(TextFormatter.parse(
                        "<red>&lLOCKED</red>"
                ));
            }

            item.set(
                    DataComponents.LORE,
                    new ItemLore(lore)
            );

            container.setItem(
                    slot,
                    item
            );
        }





        if (page > 0) {

            ItemStack prev =
                    new ItemStack(
                            Items.ARROW
                    );

            prev.set(
                    DataComponents.CUSTOM_NAME,
                    TextFormatter.parse(
                            "<yellow>&lPREVIOUS PAGE</yellow>"
                    )
            );

            container.setItem(45, prev);
        }





        int maxPages =
                (int)Math.ceil(
                        definition.maxLevel
                                / (double)LEVEL_SLOTS.length
                );

        if (page + 1 < maxPages) {

            ItemStack next =
                    new ItemStack(
                            Items.ARROW
                    );

            next.set(
                    DataComponents.CUSTOM_NAME,
                    TextFormatter.parse(
                            "<yellow>&lNEXT PAGE</yellow>"
                    )
            );

            container.setItem(53, next);
        }





        ItemStack back =
                new ItemStack(
                        Items.BARRIER
                );

        back.set(
                DataComponents.CUSTOM_NAME,
                TextFormatter.parse(
                        "<red>&lBACK</red>"
                )
        );

        container.setItem(49, back);
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

            for (int s : LEVEL_SLOTS) {

                if (s == i) {
                    skip = true;
                    break;
                }
            }

            if (i == 49) {
                skip = true;
            }

            if (skip) {
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





        if (slot == 45 && page > 0) {

            open(
                    sp,
                    milestoneId,
                    page - 1
            );

            return;
        }





        int maxPages =
                (int)Math.ceil(
                        definition.maxLevel
                                / (double)LEVEL_SLOTS.length
                );

        if (slot == 53 && page + 1 < maxPages) {

            open(
                    sp,
                    milestoneId,
                    page + 1
            );

            return;
        }





        if (slot == 49) {

            MilestoneCategoriesMenu.open(sp);
            return;
        }





        for (int i = 0; i < LEVEL_SLOTS.length; i++) {

            int level = i + 1;

            if (LEVEL_SLOTS[i] != slot) {
                continue;
            }

            boolean success =
                    MilestoneClaimService.claim(
                            sp,
                            milestoneId,
                            level
                    );

            if (success) {

                open(
                        sp,
                        milestoneId,
                        page
                );
            }

            return;
        }


        if (slot >= 0 && slot < 54) {
            return;
        }

        super.clicked(
                slot,
                button,
                type,
                player
        );
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
            String milestoneId,
            int page
    ) {

        player.openMenu(
                new MenuProvider() {

                    @Override
                    public Component getDisplayName() {

                        return TextFormatter.parse(
                                "<gold>&lMILESTONE PROGRESSION</gold>"
                        );
                    }

                    @Override
                    public AbstractContainerMenu createMenu(
                            int id,
                            Inventory inv,
                            Player player
                    ) {

                        return new MilestoneProgressionGui(
                                id,
                                inv,
                                milestoneId,
                                page
                        );
                    }
                }
        );
    }
}