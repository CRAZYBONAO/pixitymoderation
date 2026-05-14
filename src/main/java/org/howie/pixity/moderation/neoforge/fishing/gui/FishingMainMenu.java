package org.howie.pixity.moderation.neoforge.fishing.gui;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import org.howie.pixity.moderation.chat.TextFormatter;

import java.util.ArrayList;
import java.util.List;

public class FishingMainMenu {

    public static void open(ServerPlayer player) {

        SimpleContainer cont = new SimpleContainer(27);

        MenuConstructor ctor = (id, inv, p) -> new ChestMenu(
                MenuType.GENERIC_9x3,
                id,
                inv,
                cont,
                3
        ) {

            @Override
            public void broadcastChanges() {
                super.broadcastChanges();




                ItemStack filler = new ItemStack(Items.BLACK_STAINED_GLASS_PANE);

                filler.set(
                        DataComponents.CUSTOM_NAME,
                        Component.empty()
                );

                for (int i = 0; i < 27; i++) {

                    boolean reserved =
                            i == 2  || i == 4 ||
                                    i == 6  ||
                                    i == 10 ||
                                    i == 11 ||
                                    i == 12 ||
                                    i == 13 ||
                                    i == 14 ||
                                    i == 15 ||
                                    i == 16;

                    if (!reserved) {
                        this.getSlot(i).set(filler.copy());
                    }
                }




                setItem(
                        2,
                        Items.NETHER_STAR,
                        "&b&lAugment List",
                        "",
                        "&7Click to view &crequirements &7for &bfishing rod &eaugments&7.",
                        ""
                );




                setItem(
                        4,
                        Items.RECOVERY_COMPASS,
                        "&9&lFishing Codex",
                        "",
                        "&7Click to view the &9fishing codex &7and see where &efish &7can be &ecaught",
                        ""
                );




                setItem(
                        6,
                        Items.COMPASS,
                        "&e&lLeaderboards",
                        "",
                        "&7Click to view your the &eFishing Leaderboards&7.",
                        ""
                );




                setItem(
                        10,
                        Items.ANVIL,
                        "&6&lAPPLY AUGMENTS",
                        "",
                        "&7Click to &6apply augments &7to your &bfishing rod&7!",
                        ""
                );




                setItem(
                        11,
                        Items.CHEST,
                        "&3&lDELIVERIES",
                        "",
                        "&7Click to view your &3fishing deliveries &7and to turn them in.",
                        ""
                );




                setItem(
                        12,
                        Items.END_CRYSTAL,
                        "&3DELIVERY UPGRADES",
                        "",
                        "&7Click to upgrade your &3fishing delivery &7capabilities.",
                        ""
                );




                setItem(
                        13,
                        Items.IRON_AXE,
                        "&cGutting Station",
                        "",
                        "&7Click to &cgut &7your &efish &7and to turn them into entropy.",
                        ""
                );




                setItem(
                        14,
                        Items.EMERALD,
                        "&a&lFISHING SHOP",
                        "",
                        "&7Click to &asell &7your &efish &7for &amoney&7.",
                        ""
                );




                setItem(
                        15,
                        Items.NETHER_STAR,
                        "&6&lFishing Skills",
                        "",
                        "&7Click to view and upgrade your &6fishing skills&7.",
                        ""
                );




                setItem(
                        16,
                        Items.NAME_TAG,
                        "&4&lSTATS",
                        "",
                        "&7Click to view your &4fishing stats &7.",
                        ""
                );
            }

            private void setItem(int slot,
                                 net.minecraft.world.item.Item item,
                                 String name,
                                 String... loreLines) {

                ItemStack stack = new ItemStack(item);

                stack.set(
                        DataComponents.CUSTOM_NAME,
                        TextFormatter.parse(name)
                );

                List<Component> lore = new ArrayList<>();

                for (String line : loreLines) {
                    lore.add(TextFormatter.parse(line));
                }

                stack.set(
                        DataComponents.LORE,
                        new ItemLore(lore)
                );

                this.getSlot(slot).set(stack);
            }

            @Override
            public void clicked(int slot,
                                int button,
                                ClickType type,
                                Player p) {

                if (!(p instanceof ServerPlayer sp)) return;




                if (slot >= 0 && slot < 27) {

                    switch (slot) {

                        case 2 -> FishingAugmentsGui.open(sp);

                        case 4 -> FishingCodexGui.open(sp);

                        case 6 -> FishingLeaderboardCategoriesGui.open(sp);

                        case 10 -> FishingApplyAugmentGui.open(sp);

                        case 11 -> FishingDeliveriesGui.open(sp);

                        case 12 -> FishingDeliveryUpgradesGui.open(sp);

                        case 13 -> FishingGutGui.open(sp);

                        case 14 -> FishingShopGui.open(sp);

                        case 15 -> FishingSkillsGui.open(sp);

                        case 16 -> FishingStatsGui.open(sp);
                    }

                    return;
                }
            }
        };

        player.openMenu(new SimpleMenuProvider(
                ctor,
                TextFormatter.parse("<gradient:#00CFFF:#0066FF>&lFISHING MENU</gradient>")
        ));
    }
}