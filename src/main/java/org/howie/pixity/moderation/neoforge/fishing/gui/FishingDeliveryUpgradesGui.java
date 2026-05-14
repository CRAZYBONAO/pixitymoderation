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
import org.howie.pixity.moderation.neoforge.fishing.FishingDatabase;
import org.howie.pixity.moderation.neoforge.fishing.deliveries.DeliveryUpgrade;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FishingDeliveryUpgradesGui {

    private static final int[] UPGRADE_SLOTS = {
            10, 11, 13, 15, 16
    };

    public static void open(ServerPlayer player) {

        SimpleContainer cont = new SimpleContainer(27);

        MenuConstructor ctor = (id, inv, p) -> new ChestMenu(
                MenuType.GENERIC_9x3, id, inv, cont, 3
        ) {

            @Override
            public void broadcastChanges() {
                super.broadcastChanges();




                ItemStack filler = new ItemStack(Items.BLACK_STAINED_GLASS_PANE);

                filler.set(
                        DataComponents.CUSTOM_NAME,
                        Component.literal("")
                );


                for (int i = 0; i <= 8; i++) {
                    this.getSlot(i).set(filler.copy());
                }


                for (int i = 18; i <= 26; i++) {
                    this.getSlot(i).set(filler.copy());
                }


                this.getSlot(9).set(filler.copy());
                this.getSlot(17).set(filler.copy());

                UUID uuid = player.getUUID();

                int index = 0;

                for (DeliveryUpgrade up : DeliveryUpgrade.values()) {

                    int level = FishingDatabase.getDeliveryUpgrade(uuid, up.dbKey);

                    ItemStack item = new ItemStack(Items.NETHER_STAR);

                    item.set(DataComponents.CUSTOM_NAME,
                            TextFormatter.parse("<gold>&l" + up.display + "</gold>"));

                    List<Component> lore = new ArrayList<>();

                    lore.add(TextFormatter.parse("<gray>Level:</gray> <yellow>" + level + "</yellow>/<red>" + up.maxLevel + "</red>"));

                    if (level < up.maxLevel) {
                        int cost = up.getCost(level);

                        lore.add(TextFormatter.parse("<gray>Cost: </gray><green>" + cost + "</green> &bentropy"));
                        lore.add(TextFormatter.parse("<green>Click to upgrade</green>"));
                    } else {
                        lore.add(TextFormatter.parse("<green>MAXED</green>"));
                    }

                    item.set(DataComponents.LORE, new ItemLore(lore));

                    if (index >= UPGRADE_SLOTS.length) break;

                    this.getSlot(UPGRADE_SLOTS[index]).set(item);

                    index++;
                }
            }

            @Override
            public void clicked(int slot, int button, ClickType type, Player p) {

                if (!(p instanceof ServerPlayer sp)) return;
                if (type == ClickType.QUICK_MOVE) return;

                int index = -1;

                for (int i = 0; i < UPGRADE_SLOTS.length; i++) {

                    if (slot == UPGRADE_SLOTS[i]) {
                        index = i;
                        break;
                    }
                }

                if (index == -1) return;

                if (index >= DeliveryUpgrade.values().length) return;

                DeliveryUpgrade up = DeliveryUpgrade.values()[index];

                UUID uuid = sp.getUUID();

                int level = FishingDatabase.getDeliveryUpgrade(uuid, up.dbKey);

                if (level >= up.maxLevel) return;

                int cost = up.getCost(level);

                int entropy = FishingDatabase.getEntropy(uuid);

                if (entropy < cost) {
                    sp.sendSystemMessage(TextFormatter.parse("<gradient:#00CFFF:#0066FF>&lFISHING</gradient> &7&l➤ <red>Error! Not enough entropy </red>"));
                    return;
                }


                FishingDatabase.removeEntropy(uuid, cost);
                FishingDatabase.addDeliveryUpgrade(uuid, up.dbKey, 1);

                sp.sendSystemMessage(TextFormatter.parse(
                        "<gradient:#00CFFF:#0066FF>&lFISHING</gradient> &7&l➤ <green>Upgraded </green>" + up.display + " <green>to level</green> <yellow>" + (level + 1) + "</yellow>"
                ));

                open(sp);
            }
        };

        player.openMenu(new SimpleMenuProvider(
                ctor,
                TextFormatter.parse("<gold>Delivery Upgrades</gold>")
        ));
    }
}