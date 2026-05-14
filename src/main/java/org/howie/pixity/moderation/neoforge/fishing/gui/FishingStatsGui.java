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

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class FishingStatsGui {

    private record Stats(
            int entropy,
            int custom,
            int bronze,
            int silver,
            int gold,
            int diamond,
            int platinum,
            int mythical,
            int longest,
            int shortest,
            int level,
            int skill,
            int totalFish,
            int events,
            int tournaments,
            int deliveries,
            int squids,
            int dolphins,
            int crabs
    ) {}

    public static void open(ServerPlayer player) {

        ResultSet rs = FishingDatabase.getStats(player.getUUID());

        Stats temp;

        try {
            if (rs != null && rs.next()) {

                temp = new Stats(
                        rs.getInt("entropy"),
                        rs.getInt("custom_fish"),
                        rs.getInt("bronze"),
                        rs.getInt("silver"),
                        rs.getInt("gold"),
                        rs.getInt("diamond"),
                        rs.getInt("platinum"),
                        rs.getInt("mythical"),
                        rs.getInt("longest_fish"),
                        rs.getInt("shortest_fish"),
                        rs.getInt("level"),
                        rs.getInt("skill_points"),
                        rs.getInt("total_fish"),
                        rs.getInt("events_won"),
                        rs.getInt("tournaments_won"),
                        rs.getInt("deliveries_completed"),
                        rs.getInt("squids_killed"),
                        rs.getInt("dolphins_killed"),
                        rs.getInt("crabs_killed")
                );

            } else {
                temp = new Stats(0,0,0,0,0,0,0,0,0,0,0,0,
                        0,0,0,0,0,0,0);
            }

        } catch (Exception e) {
            e.printStackTrace();

            temp = new Stats(0,0,0,0,0,0,0,0,0,0,0,0,
                    0,0,0,0,0,0,0);
        }

        final Stats stats = temp;

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


                if (this.getSlot(13).hasItem()) return;




                ItemStack entropyItem = new ItemStack(Items.ENDER_EYE);
                entropyItem.set(DataComponents.CUSTOM_NAME,
                        TextFormatter.parse("&b&lEntropy"));

                List<Component> lore = new ArrayList<>();
                lore.add(TextFormatter.parse("&7Total: &b" + stats.entropy));
                entropyItem.set(DataComponents.LORE,
                        new net.minecraft.world.item.component.ItemLore(lore));

                this.getSlot(11).set(entropyItem);




                ItemStack fish = new ItemStack(Items.COD);
                fish.set(DataComponents.CUSTOM_NAME,
                        TextFormatter.parse("<gold>&lFish Stats</gold>"));

                List<Component> lore2 = new ArrayList<>();
                lore2.add(TextFormatter.parse("&8Total: &e" + stats.custom));
                lore2.add(Component.empty());
                lore2.add(TextFormatter.parse("&#c99a73&lBRONZE: &e" + stats.bronze));
                lore2.add(TextFormatter.parse("&#bdbdbd&lSILVER: &e" + stats.silver));
                lore2.add(TextFormatter.parse("&#f5f788&lGOLD: &e" + stats.gold));
                lore2.add(TextFormatter.parse("&#57f2e8&lDIAMOND: &e" + stats.diamond));
                lore2.add(TextFormatter.parse("&#2deb95&lPLATINUM: &e" + stats.platinum));
                lore2.add(TextFormatter.parse("&#db2deb&lMYTHICAL: &e" + stats.mythical));

                fish.set(DataComponents.LORE,
                        new net.minecraft.world.item.component.ItemLore(lore2));

                this.getSlot(13).set(fish);




                ItemStack size = new ItemStack(Items.FISHING_ROD);
                size.set(DataComponents.CUSTOM_NAME,
                        TextFormatter.parse("<green>&lSize Records</green>"));

                List<Component> lore3 = new ArrayList<>();
                lore3.add(TextFormatter.parse("&eLongest: &b" + stats.longest));
                lore3.add(TextFormatter.parse("&eShortest: &b" + stats.shortest));

                size.set(DataComponents.LORE,
                        new net.minecraft.world.item.component.ItemLore(lore3));

                this.getSlot(15).set(size);




                ItemStack levelItem = new ItemStack(Items.EXPERIENCE_BOTTLE);
                levelItem.set(DataComponents.CUSTOM_NAME,
                        TextFormatter.parse("<green>&lLevel</green>"));

                List<Component> lore4 = new ArrayList<>();
                lore4.add(TextFormatter.parse("&aLevel: &a" + stats.level));
                lore4.add(TextFormatter.parse("&eSkill Points: &e" + stats.skill));

                levelItem.set(DataComponents.LORE,
                        new net.minecraft.world.item.component.ItemLore(lore4));

                this.getSlot(17).set(levelItem);




                ItemStack activity = new ItemStack(Items.CHEST);
                activity.set(DataComponents.CUSTOM_NAME,
                        TextFormatter.parse("<yellow>&lActivity</yellow>"));

                List<Component> lore5 = new ArrayList<>();
                lore5.add(TextFormatter.parse("&eTotal Fish: &e" + stats.totalFish));
                lore5.add(TextFormatter.parse("&9Deliveries: &e" + stats.deliveries));

                activity.set(DataComponents.LORE,
                        new net.minecraft.world.item.component.ItemLore(lore5));

                this.getSlot(9).set(activity);




                ItemStack comp = new ItemStack(Items.NETHER_STAR);
                comp.set(DataComponents.CUSTOM_NAME,
                        TextFormatter.parse("<gold>&lCompetitions</gold>"));

                List<Component> lore6 = new ArrayList<>();
                lore6.add(TextFormatter.parse("&cEvents Won: &e" + stats.events));
                lore6.add(TextFormatter.parse("&rTournaments: &e" + stats.tournaments));

                comp.set(DataComponents.LORE,
                        new net.minecraft.world.item.component.ItemLore(lore6));

                this.getSlot(10).set(comp);




                ItemStack mobs = new ItemStack(Items.INK_SAC);
                mobs.set(DataComponents.CUSTOM_NAME,
                        TextFormatter.parse("&4&lMob Kills"));

                List<Component> lore7 = new ArrayList<>();
                lore7.add(TextFormatter.parse("&8Squids: &e" + stats.squids));
                lore7.add(TextFormatter.parse("&3Dolphins: &e" + stats.dolphins));
                lore7.add(TextFormatter.parse("&cCrabs: &e" + stats.crabs));

                mobs.set(DataComponents.LORE,
                        new net.minecraft.world.item.component.ItemLore(lore7));

                this.getSlot(16).set(mobs);




                ItemStack filler = new ItemStack(Items.GRAY_STAINED_GLASS_PANE);
                filler.set(DataComponents.CUSTOM_NAME, Component.empty());

                for (int i = 0; i < 27; i++) {
                    if (i != 22 && this.getSlot(i).getItem().isEmpty()) {
                        this.getSlot(i).set(filler);
                    }
                }




                ItemStack back = new ItemStack(Items.BARRIER);

                back.set(
                        DataComponents.CUSTOM_NAME,
                        TextFormatter.parse("<red>&lBACK</red>")
                );

                this.getSlot(22).set(back);
            }

            @Override
            public void clicked(int slot, int button,
                                net.minecraft.world.inventory.ClickType type,
                                net.minecraft.world.entity.player.Player p) {

                if (!(p instanceof ServerPlayer sp)) return;




                if (slot == 22) {

                    FishingMainMenu.open(sp);
                    return;
                }




                if (slot >= 0 && slot < 27) {
                    return;
                }
            }
        };

        player.openMenu(new SimpleMenuProvider(
                ctor,
                TextFormatter.parse("<gold>Fishing Stats</gold>")
        ));


    }


}