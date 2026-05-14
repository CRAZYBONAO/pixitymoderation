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

public class MobKillMilestoneMenu extends ChestMenu {

    private final SimpleContainer container;





    public MobKillMilestoneMenu(
            int id,
            Inventory inv
    ) {

        this(
                id,
                inv,
                new SimpleContainer(45)
        );
    }

    private MobKillMilestoneMenu(
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


        create(player, 1, "chicken", "chicken_kills",
                new ItemStack(Items.CHICKEN_SPAWN_EGG),
                "<gradient:#FFFFFF:#FF0000:#FFFFFF>&lCHICKENS KILLED MILESTONES</gradient>");

        create(player, 3, "cow", "cow_kills",
                new ItemStack(Items.COW_SPAWN_EGG),
                "<gradient:#211404:#A58862:#1C1503>&lCOWS KILLED MILESTONES</gradient>");

        create(player, 5, "pig", "pig_kills",
                new ItemStack(Items.PIG_SPAWN_EGG),
                "<gradient:#F69CE5:#C63FB8:#EE8DD7>&lPIGS KILLED MILESTONES</gradient>");

        create(player, 7, "sheep", "sheep_kills",
                new ItemStack(Items.SHEEP_SPAWN_EGG),
                "<gradient:#FFFFFF:#FF8DF3:#FFFFFF>&lSHEEP KILLED MILESTONES</gradient>");


        create(player, 11, "zombie", "zombie_kills",
                new ItemStack(Items.ZOMBIE_SPAWN_EGG),
                "<gradient:#155B47:#1A54BF:#1A5637>&lZOMBIES KILLED MILESTONES</gradient>");

        create(player, 13, "skeleton", "skeleton_kills",
                new ItemStack(Items.SKELETON_SPAWN_EGG),
                "<gradient:#A6A9A8:#343435:#9FA0A0>&lSKELETONS KILLED MILESTONES</gradient>");

        create(player, 15, "spider", "spider_kills",
                new ItemStack(Items.SPIDER_SPAWN_EGG),
                "<gradient:#343435:#FF0000:#343435>&lSPIDERS KILLED MILESTONES</gradient>");


        create(player, 19, "creeper", "creeper_kills",
                new ItemStack(Items.CREEPER_SPAWN_EGG),
                "<gradient:#2DFF00:#343435:#2DFF00>&lCREEPERS KILLED MILESTONES</gradient>");

        create(player, 21, "blaze", "blaze_kills",
                new ItemStack(Items.BLAZE_SPAWN_EGG),
                "<gradient:#DEC810:#FF9B00:#FFE300>&lBLAZES KILLED MILESTONES</gradient>");

        create(player, 23, "magma_cube", "magma_cube_kills",
                new ItemStack(Items.MAGMA_CUBE_SPAWN_EGG),
                "<gradient:#620909:#F1FF00:#620505>&lMAGMA CUBES KILLED MILESTONES</gradient>");

        create(player, 25, "enderman", "enderman_kills",
                new ItemStack(Items.ENDERMAN_SPAWN_EGG),
                "<gradient:#454444:#7500FF:#3B3B3B>&lENDERMEN KILLED MILESTONES</gradient>");


        create(player, 29, "wither_skeleton", "wither_skeleton_kills",
                new ItemStack(Items.WITHER_SKELETON_SPAWN_EGG),
                "<gradient:#A9A1A1:#2B2929:#9B9494>&lWITHER SKELETONS KILLED MILESTONES</gradient>");

        create(player, 31, "boss", "boss_kills",
                new ItemStack(Items.WITHER_SPAWN_EGG),
                "<gradient:#727372:#6B00FF>&lBOSSES K</gradient><gradient:#6B00FF:#183B70>&lILLED MI</gradient><gradient:#183B70:#383B3A>&lLESTONES</gradient>");

        create(player, 33, "phantom", "phantom_kills",
                new ItemStack(Items.PHANTOM_SPAWN_EGG),
                "<gradient:#727372:#22E02F>&lPHANTOMS </gradient><gradient:#22E02F:#383B3A>&lKILLED MILESTONES</gradient>");


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
                            MilestoneCategory.MOBS,
                            "mobs"
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
                    i == 1 || i == 3 || i == 5 || i == 7 ||
                            i == 11 || i == 13 || i == 15 ||
                            i == 19 || i == 21 || i == 23 || i == 25 ||
                            i == 29 || i == 31 || i == 33 ||
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

            case 1 -> MilestoneProgressionGui.open(sp, "chicken", 0);
            case 3 -> MilestoneProgressionGui.open(sp, "cow", 0);
            case 5 -> MilestoneProgressionGui.open(sp, "pig", 0);
            case 7 -> MilestoneProgressionGui.open(sp, "sheep", 0);

            case 11 -> MilestoneProgressionGui.open(sp, "zombie", 0);
            case 13 -> MilestoneProgressionGui.open(sp, "skeleton", 0);
            case 15 -> MilestoneProgressionGui.open(sp, "spider", 0);

            case 19 -> MilestoneProgressionGui.open(sp, "creeper", 0);
            case 21 -> MilestoneProgressionGui.open(sp, "blaze", 0);
            case 23 -> MilestoneProgressionGui.open(sp, "magma_cube", 0 );
            case 25 -> MilestoneProgressionGui.open(sp, "enderman", 0);

            case 29 -> MilestoneProgressionGui.open(sp, "wither_skeleton", 0);
            case 31 -> MilestoneProgressionGui.open(sp, "boss", 0);
            case 33 -> MilestoneProgressionGui.open(sp, "phantom", 0);

            case 40 -> MilestoneCategoriesMenu.open(sp);
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
                                "<gradient:#4C0F0F:#FFAE18:#4C0F0F>&lMOB KILL MILESTONES</gradient>"
                        );
                    }

                    @Override
                    public AbstractContainerMenu createMenu(
                            int id,
                            Inventory inv,
                            Player player
                    ) {

                        return new MobKillMilestoneMenu(
                                id,
                                inv
                        );
                    }
                }
        );
    }
}