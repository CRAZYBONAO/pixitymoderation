package org.howie.pixity.moderation.neoforge.fishing.gui;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;

import org.howie.pixity.moderation.chat.TextFormatter;
import org.howie.pixity.moderation.neoforge.fishing.FishingDatabase;
import org.howie.pixity.moderation.neoforge.util.NameResolver;

import java.sql.ResultSet;
import java.util.*;

public class FishingLeaderboardCategoriesGui {

    private static final int SIZE = 54;

    public static void open(ServerPlayer player) {

        SimpleContainer cont = new SimpleContainer(SIZE);

        MenuConstructor ctor = (id, inv, p) -> new ChestMenu(
                MenuType.GENERIC_9x6, id, inv, cont, 6
        ) {

            @Override
            public void broadcastChanges() {
                super.broadcastChanges();

                if (this.getSlot(19).hasItem()) return;

                fill();

                set(19, Items.DIAMOND, "&b&lENTROPY LEADERBOARD",
                        FishingLeaderboardGui.Mode.ENTROPY, "Amount");

                set(21, Items.NAME_TAG, "&e&lSHORTEST FISH",
                        FishingLeaderboardGui.Mode.SHORTEST, "Length");

                set(23, Items.NAME_TAG, "&e&lLONGEST FISH",
                        FishingLeaderboardGui.Mode.LONGEST, "Length");

                set(1, ball("poke_ball"), "&#cd7f32&lBRONZE FISH",
                        FishingLeaderboardGui.Mode.BRONZE, "Amount");

                set(2, ball("great_ball"), "&#bdbdbd&lSILVER FISH",
                        FishingLeaderboardGui.Mode.SILVER, "Amount");

                set(3, ball("ultra_ball"), "&#f5f788&lGOLD FISH",
                        FishingLeaderboardGui.Mode.GOLD, "Amount");

                set(5, ball("beast_ball"), "&#57f2e8&lDIAMOND FISH",
                        FishingLeaderboardGui.Mode.DIAMOND, "Amount");

                set(6, ball("master_ball"), "&#2deb95&lPLATINUM FISH",
                        FishingLeaderboardGui.Mode.PLATINUM, "Amount");

                set(7, ball("ancient_origin_ball"), "&#db2deb&lMYTHICAL FISH",
                        FishingLeaderboardGui.Mode.MYTHICAL, "Amount");

                set(42, Items.COD, "&e&lMOST FISH",
                        FishingLeaderboardGui.Mode.TOTAL_FISH, "Amount");

                set(31, Items.EMERALD, "&a&lHIGHEST LEVEL",
                        FishingLeaderboardGui.Mode.LEVEL, "Level");

                set(38, Items.NETHER_STAR, "&6&lEVENTS WON",
                        FishingLeaderboardGui.Mode.EVENTS, "Amount");

                set(40, Items.NETHERITE_SWORD, "&4&lTOURNAMENTS",
                        FishingLeaderboardGui.Mode.TOURNAMENTS, "Amount");

                set(25, Items.CHEST, "&9&lDELIVERIES",
                        FishingLeaderboardGui.Mode.DELIVERIES, "Amount");

                set(47, Items.INK_SAC, "&8&lSQUIDS KILLED",
                        FishingLeaderboardGui.Mode.SQUIDS, "Amount");

                set(49, Items.RABBIT_FOOT, "&b&lDOLPHINS KILLED",
                        FishingLeaderboardGui.Mode.DOLPHINS, "Amount");

                set(51, Items.QUARTZ, "&c&lCRABS KILLED",
                        FishingLeaderboardGui.Mode.CRABS, "Amount");
            }

            private void fill() {
                ItemStack filler = new ItemStack(Items.BLACK_STAINED_GLASS_PANE);
                filler.set(DataComponents.CUSTOM_NAME, Component.empty());

                for (int i = 0; i < SIZE; i++) {
                    this.getSlot(i).set(filler);
                }
            }

            private ItemStack ball(String id) {
                var item = BuiltInRegistries.ITEM.get(ResourceLocation.tryParse("cobblemon:" + id));
                return new ItemStack(item == null ? Items.BARRIER : item);
            }

            private void set(int slot, Item item, String name,
                             FishingLeaderboardGui.Mode mode, String valueLabel) {

                ItemStack stack = new ItemStack(item);
                build(stack, slot, name, mode, valueLabel);
            }

            private void set(int slot, ItemStack stack, String name,
                             FishingLeaderboardGui.Mode mode, String valueLabel) {

                build(stack, slot, name, mode, valueLabel);
            }

            private void build(ItemStack stack, int slot, String name,
                               FishingLeaderboardGui.Mode mode, String valueLabel) {

                stack.set(DataComponents.CUSTOM_NAME,
                        TextFormatter.parse(name));

                List<Component> lore = new ArrayList<>();

                lore.add(TextFormatter.parse("<gray>Click to view leaderboard</gray>"));
                lore.add(Component.empty());
                lore.add(TextFormatter.parse("<red>Current Leaders:</red>"));

                List<Entry> top = getTop(mode);

                UUID self = player.getUUID();

                int rank = FishingDatabase.getPlayerRank(self, mode);
                int value = FishingDatabase.getPlayerValue(self, mode);

                for (int i = 0; i < top.size(); i++) {

                    Entry e = top.get(i);

                    String color = switch (i) {
                        case 0 -> "&#ff0000";
                        case 1 -> "&#8f8d8d";
                        case 2 -> "&#9c7c52";
                        default -> "<white>";
                    };

                    String nameResolved = NameResolver.nameOrUuid(player.server, e.uuid);

                    lore.add(TextFormatter.parse(
                            color + (i + 1) + " <white>" + nameResolved +
                                    "</white> <yellow>" + valueLabel + "</yellow>&7: " + e.value
                    ));
                }

                lore.add(Component.empty());

                if (rank <= 3) {
                    lore.add(TextFormatter.parse("<gold>You are in the top 3!</gold>"));
                }

                if (rank > 0) {
                    lore.add(TextFormatter.parse(
                            "<green>Your Rank:</green> <gold>#" + rank +
                                    " </gold><gray>(</gray><yellow>" + valueLabel + "</yellow><gray>:</gray><yellow>" + value + "</yellow><gray>)</gray>"
                    ));
                } else {
                    lore.add(TextFormatter.parse("<gray>You are not ranked yet</gray>"));
                }

                stack.set(DataComponents.LORE,
                        new net.minecraft.world.item.component.ItemLore(lore));

                this.getSlot(slot).set(stack);
            }

            private List<Entry> getTop(FishingLeaderboardGui.Mode mode) {

                List<Entry> list = new ArrayList<>();

                try {
                    ResultSet rs = FishingDatabase.getLeaderboard(mode, 3);

                    int rank = 1;

                    while (rs.next()) {
                        UUID uuid = UUID.fromString(rs.getString("uuid"));
                        int value = rs.getInt("value");

                        list.add(new Entry(rank, uuid, value));
                        rank++;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return list;
            }

            @Override
            public void clicked(int slot, int button, ClickType type, Player p) {

                if (!(p instanceof ServerPlayer sp)) return;
                if (type != ClickType.PICKUP) return;

                FishingLeaderboardGui.Mode mode = switch (slot) {
                    case 19 -> FishingLeaderboardGui.Mode.ENTROPY;
                    case 21 -> FishingLeaderboardGui.Mode.SHORTEST;
                    case 23 -> FishingLeaderboardGui.Mode.LONGEST;
                    case 1 -> FishingLeaderboardGui.Mode.BRONZE;
                    case 2 -> FishingLeaderboardGui.Mode.SILVER;
                    case 3 -> FishingLeaderboardGui.Mode.GOLD;
                    case 5 -> FishingLeaderboardGui.Mode.DIAMOND;
                    case 6 -> FishingLeaderboardGui.Mode.PLATINUM;
                    case 7 -> FishingLeaderboardGui.Mode.MYTHICAL;
                    case 42 -> FishingLeaderboardGui.Mode.TOTAL_FISH;
                    case 31 -> FishingLeaderboardGui.Mode.LEVEL;
                    case 38 -> FishingLeaderboardGui.Mode.EVENTS;
                    case 40 -> FishingLeaderboardGui.Mode.TOURNAMENTS;
                    case 25 -> FishingLeaderboardGui.Mode.DELIVERIES;
                    case 47 -> FishingLeaderboardGui.Mode.SQUIDS;
                    case 49 -> FishingLeaderboardGui.Mode.DOLPHINS;
                    case 51 -> FishingLeaderboardGui.Mode.CRABS;
                    default -> null;
                };

                if (mode != null) {
                    FishingLeaderboardGui.open(sp, mode);
                }
            }
        };

        player.openMenu(new SimpleMenuProvider(
                ctor,
                TextFormatter.parse("<gold>Fishing Leaderboards</gold>")
        ));
    }

    private record Entry(int rank, UUID uuid, int value) {}


}