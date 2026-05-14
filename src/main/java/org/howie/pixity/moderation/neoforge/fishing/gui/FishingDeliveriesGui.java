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
import org.howie.pixity.moderation.neoforge.fishing.FishingManager;
import org.howie.pixity.moderation.neoforge.fishing.deliveries.Delivery;
import org.howie.pixity.moderation.neoforge.fishing.deliveries.DeliveryManager;
import org.howie.pixity.moderation.neoforge.fishing.deliveries.DeliveryReward;
import org.howie.pixity.moderation.neoforge.fishing.deliveries.DeliveryRewardManager;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



public class FishingDeliveriesGui {

    private static final int[] DELIVERY_SLOTS = {
            10,11,12,13,14,15,16,
            19,20,21,22,23,24,25,
            28,29,30,31,32,33,34
    };

    public static void open(ServerPlayer player) {

        SimpleContainer cont = new SimpleContainer(45);

        MenuConstructor ctor = (id, inv, p) -> new ChestMenu(
                MenuType.GENERIC_9x5, id, inv, cont, 5
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


                for (int i = 36; i <= 44; i++) {
                    this.getSlot(i).set(filler.copy());
                }


                for (int row = 1; row <= 3; row++) {

                    this.getSlot(row * 9).set(filler.copy());
                    this.getSlot(row * 9 + 8).set(filler.copy());
                }




                ItemStack back = new ItemStack(Items.BARRIER);

                back.set(
                        DataComponents.CUSTOM_NAME,
                        TextFormatter.parse("<red>&lBACK</red>")
                );

                this.getSlot(40).set(back);

                UUID uuid = player.getUUID();

                List<Delivery> list = DeliveryManager.getDeliveries(uuid);
                int maxSlots = 3;

                for (int i = 0; i < DELIVERY_SLOTS.length; i++) {

                    int guiSlot = DELIVERY_SLOTS[i];

                    if (i >= maxSlots) {
                        this.getSlot(guiSlot).set(createLocked());
                        continue;
                    }

                    if (i >= list.size()) {
                        this.getSlot(guiSlot).set(createEmpty());
                        continue;
                    }

                    Delivery d = list.get(i);

                    if (!d.started) {
                        this.getSlot(guiSlot).set(createReady(d));
                    }
                    else if (System.currentTimeMillis() < d.endTime) {
                        this.getSlot(guiSlot).set(createRunning(d));
                    }
                    else {
                        this.getSlot(guiSlot).set(createComplete(d));
                    }
                }
            }

            @Override
            public void clicked(int slot, int button, ClickType type, Player p) {

                if (!(p instanceof ServerPlayer sp)) return;
                if (type == ClickType.QUICK_MOVE) return;




                if (slot == 40) {

                    FishingMainMenu.open(sp);
                    return;
                }

                for (int i = 0; i < DELIVERY_SLOTS.length; i++) {

                    if (slot != DELIVERY_SLOTS[i]) continue;

                    List<Delivery> list = DeliveryManager.getDeliveries(sp.getUUID());

                    if (i >= list.size()) return;

                    Delivery d = list.get(i);

                    if (!d.started) {
                        DeliveryManager.startDelivery(sp, i);
                    } else {
                        DeliveryManager.completeDelivery(sp, i);
                    }


                    open(sp);
                    return;
                }
            }
        };

        player.openMenu(new SimpleMenuProvider(
                ctor,
                TextFormatter.parse("<gold>Fishing Deliveries</gold>")
        ));
    }





    private static ItemStack createLocked() {

        ItemStack item = new ItemStack(Items.IRON_BARS);

        item.set(DataComponents.CUSTOM_NAME,
                TextFormatter.parse("<red>&lDELIVERY SLOT LOCKED</red>"));

        item.set(DataComponents.LORE, new ItemLore(List.of(
                TextFormatter.parse("<gray>Upgrade your capacity to unlock this slot!</gray>")
        )));

        return item;
    }

    private static ItemStack createEmpty() {

        ItemStack item = new ItemStack(Items.PAPER);

        item.set(DataComponents.CUSTOM_NAME,
                TextFormatter.parse("<yellow>&lWaiting for delivery...</yellow>"));

        item.set(DataComponents.LORE,
                new ItemLore(List.of(
                        TextFormatter.parse("&7Catch &efish &7or &ckill &3squids &7or &edolphins &7to generate a &adelivery")
                )));

        return item;
    }

    private static ItemStack createReady(Delivery d) {

        ItemStack item = new ItemStack(Items.OAK_CHEST_BOAT);

        String color = getTierColor(d.tier);

        item.set(DataComponents.CUSTOM_NAME,
                TextFormatter.parse(color + "&lTier " + d.tier + " &#3da6ffDelivery"));

        item.set(DataComponents.LORE,
                new ItemLore(buildReadyLore(d)));

        return item;
    }

    private static List<Component> buildReadyLore(Delivery d) {

        List<Component> lore = new ArrayList<>();


        lore.add(TextFormatter.parse("<gray>Going to: </gray><white>" + d.npcName + "</white>"));


        lore.add(Component.empty());


        lore.add(TextFormatter.parse("<red>Required Fish:</red>"));


        d.requiredFish.forEach((id, amount) -> {

            FishData fish = FishingManager.getFishById(id);
            String name = (fish != null) ? fish.displayName : id;

            lore.add(TextFormatter.parse("<yellow>" + amount + "x</yellow> <white>" + name + "</white>"));
        });


        lore.add(Component.empty());


        lore.add(TextFormatter.parse("&bEntropy Reward: " + d.entropyReward));


        if (!d.rolledRewards.isEmpty()) {

            lore.add(Component.empty());
            lore.add(TextFormatter.parse("<green>Rewards:</green>"));

            for (DeliveryReward r : d.rolledRewards) {

                String name = (r.display != null)
                        ? r.display
                        : r.value;

                String color = (r.rarity != null)
                        ? DeliveryRewardManager.getRarityColor(r.rarity)
                        : "<gray>";

                lore.add(TextFormatter.parse(color + "• " + name));
            }
        }

        return lore;
    }



    private static String getTierColor(int tier) {
        return switch (tier) {
            case 1 -> "&#cd7f32";
            case 2 -> "&#c0c0c0";
            case 3 -> "&#ffd700";
            case 4 -> "&#55ffff";
            case 5 -> "&#00ff95";
            case 6 -> "&#00ffcc";
            case 7 -> "&#9b59b6";
            case 8 -> "&#e91e63";
            case 9 -> "&#ff5722";
            case 10 -> "&#ff0000";
            default -> "<white>";
        };
    }




    private static ItemStack createRunning(Delivery d) {

        ItemStack item = new ItemStack(Items.OAK_CHEST_BOAT);

        String color = getTierColor(d.tier);

        item.set(DataComponents.CUSTOM_NAME,
                TextFormatter.parse(color + "&lTier " + d.tier + " Delivery"));

        long remaining = d.endTime - System.currentTimeMillis();

        List<Component> lore = buildReadyLore(d);

        lore.add(Component.empty());
        lore.add(TextFormatter.parse("<yellow>Time Left: " + formatTime(remaining) + "</yellow>"));

        item.set(DataComponents.LORE, new ItemLore(lore));

        return item;
    }

    private static ItemStack createComplete(Delivery d) {

        ItemStack item = new ItemStack(Items.CHEST);

        String color = getTierColor(d.tier);

        item.set(DataComponents.CUSTOM_NAME,
                TextFormatter.parse(color + "&lTier " + d.tier + " Delivery <green>Completed</green>"));

        List<Component> lore = new ArrayList<>();

        lore.add(TextFormatter.parse("<green>Click to collect your delivery rewards!</green>"));

        item.set(DataComponents.LORE, new ItemLore(lore));

        return item;
    }



    private static String formatTime(long ms) {

        long seconds = ms / 1000;

        long m = seconds / 60;
        long s = seconds % 60;

        return m + "m " + s + "s";
    }
}