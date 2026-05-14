package org.howie.pixity.moderation.neoforge.fishing.gui;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;

import org.howie.pixity.moderation.chat.TextFormatter;
import org.howie.pixity.moderation.neoforge.fishing.FishingDatabase;
import org.howie.pixity.moderation.neoforge.fishing.FishingManager;

import java.util.*;

public class FishingSkillsGui {

    public static void open(ServerPlayer player) {



        UUID uuid = player.getUUID();

        SimpleContainer cont = new SimpleContainer(27);

        MenuConstructor ctor = (id, inv, p) -> new ChestMenu(
                net.minecraft.world.inventory.MenuType.GENERIC_9x3,
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
                            i == 2  ||
                                    i == 4  ||
                                    i == 6  ||
                                    i == 12 ||
                                    i == 14 ||
                                    i == 20 ||
                                    i == 23 ||
                                    i == 25;

                    if (!reserved) {
                        this.getSlot(i).set(filler.copy());
                    }
                }

                if (this.getSlot(10).hasItem()) return;

                int points = FishingDatabase.getSkillPoints(uuid);

                ItemStack info = new ItemStack(Items.EXPERIENCE_BOTTLE);
                info.set(DataComponents.CUSTOM_NAME,
                        TextFormatter.parse("<green>&lSkill Points</green>"));

                List<Component> skillLore = new ArrayList<>();
                skillLore.add(TextFormatter.parse("<green>Available:</green> <yellow>" + points + "</yellow>"));

                info.set(DataComponents.LORE,
                        new net.minecraft.world.item.component.ItemLore(skillLore));

                this.getSlot(12).set(info);




                int gut = FishingDatabase.getSkill(uuid, "gutting_skill");
                double bonus = FishingManager.getScalingBonus(gut) * 100;


                ItemStack gutItem = new ItemStack(Items.COOKED_COD);
                gutItem.set(DataComponents.CUSTOM_NAME,
                        TextFormatter.parse("<gold>&lBetter Gutting</gold>"));

                List<Component> lore = new ArrayList<>();
                lore.add(TextFormatter.parse("<gray>Level:</gray> <yellow>" + gut + "</yellow><gray>/</gray><red>100</red>"));
                lore.add(TextFormatter.parse("<green>+" + String.format("%.1f", bonus) + "%</green> &bentropy <gray>from</gray> <red>gutting</red>"));

                gutItem.set(DataComponents.LORE,
                        new net.minecraft.world.item.component.ItemLore(lore));

                this.getSlot(2).set(gutItem);




                int luck = FishingDatabase.getSkill(uuid, "luck_skill");

                ItemStack luckItem = new ItemStack(Items.GOLD_INGOT);
                luckItem.set(DataComponents.CUSTOM_NAME,
                        TextFormatter.parse("<gold>&lLuck of the Catch</gold>"));

                List<Component> lore2 = new ArrayList<>();
                lore2.add(TextFormatter.parse("<gray>Level:</gray> <yellow>" + luck + "</yellow><gray>/</gray><red>100</red>"));
                double luckBonus = FishingManager.getScalingBonus(luck) * 100;

                lore2.add(TextFormatter.parse(
                        "<green>+" + String.format("%.1f", luckBonus) + "%</green> <gray>catch</gray> &bentropy"
                ));

                luckItem.set(DataComponents.LORE,
                        new net.minecraft.world.item.component.ItemLore(lore2));

                this.getSlot(4).set(luckItem);




                int aug = FishingDatabase.getSkill(uuid, "augment_skill");

                ItemStack augItem = new ItemStack(Items.ANVIL);
                augItem.set(DataComponents.CUSTOM_NAME,
                        TextFormatter.parse("<gold>&lMaster Augmenter</gold>"));

                List<Component> lore3 = new ArrayList<>();
                lore3.add(TextFormatter.parse("<gray>Level:</gray> <yellow>" + aug + "</yellow><gray>/</gray><red>100</red>"));
                double discount = FishingManager.getScalingBonus(aug) * 100;

                lore3.add(TextFormatter.parse(
                        "<green>-" + String.format("%.1f", discount) + "%</green> <yellow>augment cost</yellow>"
                ));

                augItem.set(DataComponents.LORE,
                        new net.minecraft.world.item.component.ItemLore(lore3));

                this.getSlot(6).set(augItem);




                ItemStack back = new ItemStack(Items.BARRIER);

                back.set(
                        DataComponents.CUSTOM_NAME,
                        TextFormatter.parse("<red>&lBACK</red>")
                );

                this.getSlot(14).set(back);




                addUnlock(this, 20, "&e&lDivine Judgement", "divine_unlocked", uuid, player);
                addUnlock(this, 23, "&c&lCombo Catcher", "combo_unlocked", uuid, player);
                addUnlock(this, 25, "&b&lAugment Infusion", "infusion_unlocked", uuid, player);
            }

            @Override
            public void clicked(int slot, int button,
                                net.minecraft.world.inventory.ClickType type,
                                net.minecraft.world.entity.player.Player p) {

                if (!(p instanceof ServerPlayer sp)) return;

                if (type != net.minecraft.world.inventory.ClickType.PICKUP) return;

                if (slot < 54) {

                    boolean isButton = (
                            slot == 2  ||
                                    slot == 4  ||
                                    slot == 6  ||
                                    slot == 14 ||
                                    slot == 20 ||
                                    slot == 23 ||
                                    slot == 25
                    );

                    if (!isButton) return;
                }




                if (slot == 2) upgrade(sp, "gutting_skill", 100);
                if (slot == 4) upgrade(sp, "luck_skill", 100);
                if (slot == 6) upgrade(sp, "augment_skill", 100);




                if (slot == 14) {
                    FishingMainMenu.open(sp);
                    return;
                }




                if (slot == 20) unlock(sp, "divine_unlocked");
                if (slot == 23) unlock(sp, "combo_unlocked");
                if (slot == 25) unlock(sp, "infusion_unlocked");
            }
        };

        player.openMenu(new SimpleMenuProvider(
                ctor,
                TextFormatter.parse("<gold>Fishing Skills</gold>")
        ));
    }


    private static void upgrade(ServerPlayer p, String col, int max) {

        UUID uuid = p.getUUID();

        int level = FishingDatabase.getSkill(uuid, col);

        if (level >= max) {
            p.sendSystemMessage(TextFormatter.parse("<gradient:#00CFFF:#0066FF>&lFISHING</gradient> &7&l➤ <red>Error! Max level reached</red>"));
            return;
        }

        int points = FishingDatabase.getSkillPoints(uuid);

        if (points <= 0) {
            p.sendSystemMessage(TextFormatter.parse("<gradient:#00CFFF:#0066FF>&lFISHING</gradient> &7&l➤ <red>Error! No skill points</red>"));
            return;
        }

        FishingDatabase.addSkillPoints(uuid, -1);
        FishingDatabase.upgradeSkill(uuid, col);

        p.sendSystemMessage(TextFormatter.parse("<gradient:#00CFFF:#0066FF>&lFISHING</gradient> &7&l➤ <green>Skill upgraded!</green>"));
        p.containerMenu.broadcastChanges();
    }

    private static void unlock(ServerPlayer p, String col) {

        UUID uuid = p.getUUID();

        if (FishingDatabase.isUnlocked(uuid, col)) {
            p.sendSystemMessage(TextFormatter.parse("<gradient:#00CFFF:#0066FF>&lFISHING</gradient> &7&l➤ <red>Error! Already unlocked</red>"));
            return;
        }

        int points = FishingDatabase.getSkillPoints(uuid);

        if (points < 15) {
            p.sendSystemMessage(TextFormatter.parse("<gradient:#00CFFF:#0066FF>&lFISHING</gradient> &7&l➤ <red>Error! Not enough skill points.</red>"));
            return;
        }

        FishingDatabase.addSkillPoints(uuid, -15);
        FishingDatabase.unlock(uuid, col);

        p.sendSystemMessage(TextFormatter.parse("<gradient:#00CFFF:#0066FF>&lFISHING</gradient> &7&l➤ <green>Skill unlocked!</green>"));
        p.containerMenu.broadcastChanges();
    }

    private static void addUnlock(ChestMenu menu, int slot, String name, String col, UUID uuid, ServerPlayer player) {

        boolean unlocked = FishingDatabase.isUnlocked(uuid, col);

        ItemStack item;

        switch (col) {
            case "divine_unlocked" -> item = new ItemStack(Items.NETHER_STAR);
            case "combo_unlocked" -> item = new ItemStack(Items.CLOCK);
            case "infusion_unlocked" -> {
                item = new ItemStack(Items.FIREWORK_STAR);

                net.minecraft.nbt.CompoundTag explosion = new net.minecraft.nbt.CompoundTag();
                explosion.putIntArray("Colors", new int[]{0x0000FF});

                net.minecraft.nbt.CompoundTag tag = new net.minecraft.nbt.CompoundTag();
                tag.put("Explosion", explosion);

                item.set(DataComponents.CUSTOM_DATA,
                        net.minecraft.world.item.component.CustomData.of(tag));
            }
            default -> item = new ItemStack(Items.BARRIER);
        }

        item.set(DataComponents.CUSTOM_NAME,
                TextFormatter.parse("<light_purple>&l" + name + "</light_purple>"));

        List<Component> lore = new ArrayList<>();

        lore.add(TextFormatter.parse(unlocked ? "<green>Unlocked</green>" : "<red>Locked</red>"));
        lore.add(TextFormatter.parse("<gray>Cost:</gray> <yellow>15 Skill Points</yellow>"));


        switch (col) {




            case "divine_unlocked" -> {

                double chance = FishingManager.getDivineChance(uuid) * 100;

                lore.add(Component.empty());
                lore.add(TextFormatter.parse("<yellow>Proc Chance:</yellow> <gold>" + String.format("%.1f", chance) + "%</gold>"));
                lore.add(TextFormatter.parse("<gold>+1%</gold> <gray>every</gray> <green>10 Fishing Levels</green>"));
                lore.add(TextFormatter.parse("<gray>Max:</gray> <red>15%</red>"));
            }




            case "combo_unlocked" -> {

                double chance = FishingManager.getComboChance(uuid) * 100;

                lore.add(Component.empty());
                lore.add(TextFormatter.parse("<yellow>Bonus Proc Chance:</yellow> <gold>" + String.format("%.1f", chance) + "%</gold>"));
                lore.add(TextFormatter.parse("<gold>+3%</gold> <gray>every</gray>  <green>5 Fishing Levels</green>"));
                lore.add(TextFormatter.parse("<gray>Max:</gray> <red>60%</red>"));
            }




            case "infusion_unlocked" -> {

                double chance = FishingManager.getInfusionChance(uuid) * 100;

                lore.add(Component.empty());
                lore.add(TextFormatter.parse("<yellow>Refund Chance:</yellow> <gold>" + String.format("%.1f", chance) + "%</gold>"));
                lore.add(TextFormatter.parse("<gold>+5%</gold> <gray>every</gray> <green>7 Fishing Levels</green>"));
                lore.add(TextFormatter.parse("<gray>Max:</gray> <red>75%</red>"));
                lore.add(Component.empty());
                lore.add(TextFormatter.parse("<gray>Applies per item</gray>"));
                lore.add(TextFormatter.parse("<gray>Max refund:</gray> <yellow>50% </yellow><gray>of materials</gray>"));
            }
        }

        item.set(DataComponents.LORE,
                new net.minecraft.world.item.component.ItemLore(lore));

        menu.getSlot(slot).set(item);
    }
}