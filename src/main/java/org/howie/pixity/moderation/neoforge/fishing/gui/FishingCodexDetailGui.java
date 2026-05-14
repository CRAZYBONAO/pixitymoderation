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
import org.howie.pixity.moderation.neoforge.fishing.FishData;
import org.howie.pixity.moderation.neoforge.fishing.FishingDatabase;
import org.howie.pixity.moderation.neoforge.fishing.FishingManager;

import java.util.ArrayList;
import java.util.List;

public class FishingCodexDetailGui {

    public static void open(
            ServerPlayer player,
            FishData fish
    ) {

        SimpleContainer cont =
                new SimpleContainer(27);

        MenuConstructor ctor =
                (id, inv, p) -> new ChestMenu(
                        MenuType.GENERIC_9x3,
                        id,
                        inv,
                        cont,
                        3
                ) {

                    @Override
                    public void broadcastChanges() {

                        super.broadcastChanges();

                        if (this.getSlot(13).hasItem()) {
                            return;
                        }





                        ItemStack filler =
                                new ItemStack(
                                        Items.BLACK_STAINED_GLASS_PANE
                                );

                        filler.set(
                                DataComponents.CUSTOM_NAME,
                                Component.empty()
                        );

                        for (int i = 0; i < 27; i++) {

                            if (i == 13 || i == 22) {
                                continue;
                            }

                            this.getSlot(i).set(filler);
                        }





                        ItemStack back =
                                new ItemStack(Items.BARRIER);

                        back.set(
                                DataComponents.CUSTOM_NAME,
                                TextFormatter.parse(
                                        "<red>&lBack</red>"
                                )
                        );

                        this.getSlot(22).set(back);





                        ItemStack item =
                                FishingManager.createFishDisplayItem(fish);

                        item.set(
                                DataComponents.CUSTOM_NAME,
                                TextFormatter.parse(
                                        FishingManager.getTierColor(fish.tier)
                                                + "&l"
                                                + fish.displayName
                                )
                        );





                        int caught =
                                FishingDatabase.getFishCaught(
                                        player.getUUID(),
                                        fish.id
                                );

                        double smallest =
                                FishingDatabase.getSmallestFish(
                                        player.getUUID(),
                                        fish.id
                                );

                        double largest =
                                FishingDatabase.getLargestFish(
                                        player.getUUID(),
                                        fish.id
                                );

                        boolean discovered =
                                FishingDatabase.hasFish(
                                        player.getUUID(),
                                        fish.id
                                );





                        List<Component> lore =
                                new ArrayList<>();

                        lore.add(Component.empty());

                        lore.add(
                                TextFormatter.parse(
                                        "<gray>Tier: </gray>"
                                                + FishingManager.getTierColor(fish.tier)
                                                + fish.tier.name()
                                )
                        );

                        lore.add(
                                TextFormatter.parse(
                                        "<gray>Natural Size Range:</gray> <yellow>"
                                                + fish.minSize
                                                + " - "
                                                + fish.maxSize
                                                + "cm</yellow>"
                                )
                        );

                        lore.add(Component.empty());

                        lore.add(
                                TextFormatter.parse(
                                        "<gold>Total Caught:</gold> <white>"
                                                + caught
                                                + "</white>"
                                )
                        );

                        if (discovered) {

                            lore.add(
                                    TextFormatter.parse(
                                            "<aqua>Smallest Catch:</aqua> <white>"
                                                    + String.format("%.2f", smallest)
                                                    + " cm</white>"
                                    )
                            );

                            lore.add(
                                    TextFormatter.parse(
                                            "<yellow>Largest Catch:</yellow> <white>"
                                                    + String.format("%.2f", largest)
                                                    + " cm</white>"
                                    )
                            );
                        }
                        else {

                            lore.add(
                                    TextFormatter.parse(
                                            "<red>You have not caught this fish yet.</red>"
                                    )
                            );
                        }

                        lore.add(Component.empty());

                        lore.add(
                                TextFormatter.parse(
                                        "<gray>Biomes:</gray>"
                                )
                        );

                        for (String biome : fish.biomes) {

                            lore.add(
                                    TextFormatter.parse(
                                            "<green>• "
                                                    + biome
                                                    + "</green>"
                                    )
                            );
                        }

                        item.set(
                                DataComponents.LORE,
                                new ItemLore(lore)
                        );





                        this.getSlot(13).set(item);
                    }

                    @Override
                    public void clicked(
                            int slot,
                            int button,
                            ClickType type,
                            Player p
                    ) {

                        if (!(p instanceof ServerPlayer sp)) {
                            return;
                        }

                        if (slot == 22) {

                            FishingCodexTierGui.open(
                                    sp,
                                    fish.tier
                            );
                        }
                    }
                };

        player.openMenu(
                new SimpleMenuProvider(
                        ctor,
                        TextFormatter.parse(
                                "<gold>Fish Details</gold>"
                        )
                )
        );
    }
}