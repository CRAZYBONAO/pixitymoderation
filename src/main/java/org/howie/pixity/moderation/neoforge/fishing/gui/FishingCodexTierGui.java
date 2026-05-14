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
import org.howie.pixity.moderation.chat.TextFormatter;
import org.howie.pixity.moderation.neoforge.fishing.FishData;
import org.howie.pixity.moderation.neoforge.fishing.FishTier;
import org.howie.pixity.moderation.neoforge.fishing.FishingDatabase;
import org.howie.pixity.moderation.neoforge.fishing.FishingManager;

public class FishingCodexTierGui {

    public static void open(ServerPlayer player, FishTier tier) {

        SimpleContainer cont = new SimpleContainer(54);

        MenuConstructor ctor = (id, inv, p) -> new ChestMenu(
                MenuType.GENERIC_9x6, id, inv, cont, 6
        ) {

            @Override
            public void broadcastChanges() {
                super.broadcastChanges();

                if (this.getSlot(10).hasItem()) return;

                ItemStack filler = new ItemStack(Items.BLACK_STAINED_GLASS_PANE);
                filler.set(DataComponents.CUSTOM_NAME, Component.empty());

                for (int i = 0; i < 54; i++) {
                    if (i < 9 || i >= 45 || i % 9 == 0 || i % 9 == 8) {
                        this.getSlot(i).set(filler);
                    }
                }



                ItemStack back = new ItemStack(Items.BARRIER);
                back.set(DataComponents.CUSTOM_NAME,
                        TextFormatter.parse("<red>&lBack</red>"));
                this.getSlot(49).set(back);

                int slot = 10;

                java.util.List<FishData> list = FishingManager.FISH.stream()
                        .filter(f -> f.tier == tier)
                        .toList();

                for (FishData fish : list) {

                    ItemStack item =
                            FishingManager.createFishDisplayItem(fish);





                    item.set(
                            DataComponents.CUSTOM_NAME,
                            TextFormatter.parse(
                                    FishingManager.getTierColor(fish.tier)
                                            + "&l" + fish.displayName
                            )
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

                    int caught =
                            FishingDatabase.getFishCaught(
                                    player.getUUID(),
                                    fish.id
                            );





                    java.util.List<Component> lore =
                            new java.util.ArrayList<>();

                    lore.add(Component.empty());

                    lore.add(
                            TextFormatter.parse(
                                    "<gray>Tier: </gray>"
                                            + FishingManager.getTierColor(fish.tier)
                                            + fish.tier.name()
                            )
                    );

                    lore.add(Component.empty());

                    lore.add(
                            TextFormatter.parse(
                                    "<yellow>Total Caught:</yellow> <white>"
                                            + caught
                                            + "</white>"
                            )
                    );

                    lore.add(Component.empty());

                    lore.add(
                            TextFormatter.parse(
                                    "<aqua>Smallest Size:</aqua> <white>"
                                            + String.format("%.2f", smallest)
                                            + " cm</white>"
                            )
                    );

                    lore.add(
                            TextFormatter.parse(
                                    "<gold>Largest Size:</gold> <white>"
                                            + String.format("%.2f", largest)
                                            + " cm</white>"
                            )
                    );

                    item.set(
                            DataComponents.LORE,
                            new net.minecraft.world.item.component.ItemLore(lore)
                    );





                    this.getSlot(slot).set(item);

                    slot++;
                    if (slot % 9 == 8) slot += 2;
                }
            }
            @Override
            public void clicked(int slot, int button, ClickType type, Player p) {

                if (type != ClickType.PICKUP) return;

                if (!(p instanceof ServerPlayer sp)) return;


                if (slot == 49) {
                    FishingCodexGui.open(sp);
                    return;
                }

                if (slot >= 54) return;

                java.util.List<FishData> list = FishingManager.FISH.stream()
                        .filter(f -> f.tier == tier)
                        .toList();

                int actualIndex = 0;

                for (int i = 10; i < 44; i++) {

                    if (i % 9 == 0 || i % 9 == 8) continue;

                    if (i == slot) {

                        if (actualIndex < list.size()) {
                            FishingCodexDetailGui.open(sp, list.get(actualIndex));
                        }
                        return;
                    }

                    actualIndex++;
                }
            }
        };

        player.openMenu(new SimpleMenuProvider(
                ctor,
                TextFormatter.parse("<gold>" + tier.name() + " Codex</gold> &7(&e"
                        + FishingManager.FISH.stream().filter(f -> f.tier == tier).count() + "&7)")
        ));
    }


}
