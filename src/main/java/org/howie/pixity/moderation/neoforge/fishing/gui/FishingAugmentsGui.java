package org.howie.pixity.moderation.neoforge.fishing.gui;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.howie.pixity.moderation.chat.TextFormatter;
import org.howie.pixity.moderation.neoforge.fishing.FishingDatabase;
import org.howie.pixity.moderation.neoforge.fishing.FishingManager;
import org.howie.pixity.moderation.neoforge.fishing.augment.FishingAugment;

import java.util.UUID;

public class FishingAugmentsGui {

    private static final int[] AUGMENT_SLOTS = {

            10,
            11,
            12,
            13,
            14,
            15,
            16,

            19,
            20,
            21,
            22,
            23,
            24,
            25
    };

    public static void open(ServerPlayer player) {

        SimpleContainer cont = new SimpleContainer(36);

        MenuConstructor ctor = (id, inv, p) -> new ChestMenu(
                MenuType.GENERIC_9x4, id, inv, cont, 4
        ) {

            @Override
            public void broadcastChanges() {
                super.broadcastChanges();




                ItemStack filler = new ItemStack(net.minecraft.world.item.Items.BLACK_STAINED_GLASS_PANE);

                filler.set(
                        net.minecraft.core.component.DataComponents.CUSTOM_NAME,
                        net.minecraft.network.chat.Component.literal("")
                );


                for (int i = 0; i <= 8; i++) {
                    this.getSlot(i).set(filler.copy());
                }


                for (int i = 27; i <= 35; i++) {
                    this.getSlot(i).set(filler.copy());
                }


                for (int row = 1; row <= 2; row++) {

                    this.getSlot(row * 9).set(filler.copy());
                    this.getSlot(row * 9 + 8).set(filler.copy());
                }

                if (this.getSlot(11).hasItem()) return;

                FishingAugment[] values =
                        FishingAugment.values();

                for (int i = 0; i < values.length; i++) {

                    if (i >= AUGMENT_SLOTS.length) {
                        break;
                    }

                    FishingAugment aug =
                            values[i];

                    ItemStack item =
                            FishingManager.createAugmentItem(
                                    aug,
                                    player
                            );

                    this.getSlot(
                            AUGMENT_SLOTS[i]
                    ).set(item);
                }
            }

            @Override
            public void clicked(int slot, int button, ClickType type, Player p) {

                if (type != ClickType.PICKUP) return;

                if (!(p instanceof ServerPlayer sp)) return;


                if (slot >= 54) return;

                FishingAugment[] values =
                        FishingAugment.values();

                for (int i = 0; i < AUGMENT_SLOTS.length; i++) {

                    if (i >= values.length) {
                        break;
                    }

                    if (AUGMENT_SLOTS[i] != slot) {
                        continue;
                    }

                    craftAugment(
                            sp,
                            values[i]
                    );

                    return;
                }
            }
        };

        player.openMenu(new SimpleMenuProvider(
                ctor,
                TextFormatter.parse("<gold>Fishing Augments</gold>")
        ));
    }

    private static void craftAugment(ServerPlayer player, FishingAugment aug) {

        UUID uuid = player.getUUID();

        int entropy = FishingDatabase.getEntropy(uuid);

        double discount = FishingManager.getAugmentDiscount(uuid);
        int cost = (int) (aug.entropyCost * (1.0 - discount));




        if (entropy < cost) {
            player.sendSystemMessage(TextFormatter.parse("<gradient:#00CFFF:#0066FF>&lFISHING</gradient> &7&l➤ <red>Error! Not enough </red>&7&l>"));
            return;
        }




        if (!FishingManager.hasMaterials(player, aug)) {
            player.sendSystemMessage(TextFormatter.parse("<gradient:#00CFFF:#0066FF>&lFISHING</gradient> &7&l➤  <red>Error! Missing materials</red>"));
            return;
        }




        if (FishingDatabase.isUnlocked(uuid, "infusion_unlocked")) {

            double chance = FishingManager.getInfusionChance(uuid);

            int refundedItems = 0;
            int totalItems = aug.requirements.stream().mapToInt(r -> r.amount).sum();
            int maxRefund = (int) Math.ceil(totalItems * 0.5);

            for (FishingAugment.Req req : aug.requirements) {

                int amount = req.amount;
                int refundedThisReq = 0;

                for (int i = 0; i < amount; i++) {
                    if (Math.random() < chance && refundedItems < maxRefund) {
                        refundedThisReq++;
                        refundedItems++;
                    }
                }

                int toRemove = amount - refundedThisReq;

                if (toRemove > 0) {
                    FishingManager.removeSpecificMaterial(player, req, toRemove);
                }
            }

            FishingDatabase.addEntropy(uuid, -cost);

            if (refundedItems > 0) {
                player.sendSystemMessage(TextFormatter.parse(
                        "<gradient:#00CFFF:#0066FF>&lFISHING</gradient> &7&l➤ &b&lINFUSION! <gray>Saved </gray><yellow>" + refundedItems + " materials</yellow>"
                ));
            }

        } else {
            FishingDatabase.addEntropy(uuid, -cost);
            FishingManager.removeMaterials(player, aug);
        }




        ItemStack item = FishingManager.createAugmentItem(aug, player);

        if (!player.getInventory().add(item)) {
            player.drop(item, false);
        }

        player.sendSystemMessage(TextFormatter.parse(
                "<gradient:#00CFFF:#0066FF>&lFISHING</gradient> &7&l➤ <green>Crafted </green>&b" + aug.display + "<green>!</green>"
        ));

        player.containerMenu.broadcastChanges();
    }


}
