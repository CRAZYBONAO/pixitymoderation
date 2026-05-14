package org.howie.pixity.moderation.neoforge.fishing.gui;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.component.ResolvableProfile;
import com.mojang.authlib.GameProfile;

import org.howie.pixity.moderation.chat.TextFormatter;
import org.howie.pixity.moderation.neoforge.fishing.FishingDatabase;
import org.howie.pixity.moderation.neoforge.util.NameResolver;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FishingLeaderboardGui {

    public static void open(ServerPlayer player, Mode mode) {

        List<Entry> entries = new ArrayList<>();

        try {
            ResultSet rs;




            switch (mode) {
                case LONGEST -> rs = FishingDatabase.getTopLongest(45);
                case SHORTEST -> rs = FishingDatabase.getTopShortest(45);
                case TOTAL_FISH -> rs = FishingDatabase.getTopTotalFish(45);
                case LEVEL -> rs = FishingDatabase.getTopLevel(45);
                case BRONZE -> rs = FishingDatabase.getTopTier("bronze", 45);
                case SILVER -> rs = FishingDatabase.getTopTier("silver", 45);
                case GOLD -> rs = FishingDatabase.getTopTier("gold", 45);
                case DIAMOND -> rs = FishingDatabase.getTopTier("diamond", 45);
                case PLATINUM -> rs = FishingDatabase.getTopTier("platinum", 45);
                case MYTHICAL -> rs = FishingDatabase.getTopTier("mythical", 45);

                case EVENTS -> rs = FishingDatabase.getTopTier("events_won", 45);
                case TOURNAMENTS -> rs = FishingDatabase.getTopTier("tournaments_won", 45);
                case DELIVERIES -> rs = FishingDatabase.getTopTier("deliveries_completed", 45);

                case SQUIDS -> rs = FishingDatabase.getTopTier("squids_killed", 45);
                case DOLPHINS -> rs = FishingDatabase.getTopTier("dolphins_killed", 45);
                case CRABS -> rs = FishingDatabase.getTopTier("crabs_killed", 45);

                default -> rs = FishingDatabase.getTopEntropy(45);
            }

            int rank = 1;

            while (rs != null && rs.next()) {

                UUID uuid = UUID.fromString(rs.getString("uuid"));

                int value = switch (mode) {
                    case TOTAL_FISH -> rs.getInt("total_fish");
                    case LEVEL -> rs.getInt("level");
                    case BRONZE -> rs.getInt("bronze");
                    case SILVER -> rs.getInt("silver");
                    case GOLD -> rs.getInt("gold");
                    case DIAMOND -> rs.getInt("diamond");
                    case PLATINUM -> rs.getInt("platinum");
                    case MYTHICAL -> rs.getInt("mythical");

                    case EVENTS -> rs.getInt("events_won");
                    case TOURNAMENTS -> rs.getInt("tournaments_won");
                    case DELIVERIES -> rs.getInt("deliveries_completed");

                    case SQUIDS -> rs.getInt("squids_killed");
                    case DOLPHINS -> rs.getInt("dolphins_killed");
                    case CRABS -> rs.getInt("crabs_killed");

                    case LONGEST -> rs.getInt("longest_fish");
                    case SHORTEST -> rs.getInt("shortest_fish");

                    default -> rs.getInt("entropy");
                };
                entries.add(new Entry(rank, uuid, value));
                rank++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        SimpleContainer cont = new SimpleContainer(54);

        MenuConstructor ctor = (id, inv, p) -> new ChestMenu(
                net.minecraft.world.inventory.MenuType.GENERIC_9x6,
                id,
                inv,
                cont,
                6
        ) {

            @Override
            public void clicked(int slot, int button, ClickType type, Player p) {

                if (type == ClickType.QUICK_MOVE) return;

                if (!(p instanceof ServerPlayer sp)) return;




                if (slot == 49) {
                    FishingLeaderboardCategoriesGui.open(sp);
                    return;
                }


                if (slot < 54) return;
            }

            @Override
            public void broadcastChanges() {
                super.broadcastChanges();




                ItemStack filler = new ItemStack(Items.BLACK_STAINED_GLASS_PANE);
                filler.set(DataComponents.CUSTOM_NAME, Component.empty());

                for (int i = 0; i < 54; i++) {
                    if (
                            i < 9 ||
                                    (i >= 45 && i != 49) ||
                                    i % 9 == 0 ||
                                    i % 9 == 8
                    ) {
                        this.getSlot(i).set(filler);
                    }
                }




                ItemStack back = new ItemStack(Items.BARRIER);

                back.set(
                        DataComponents.CUSTOM_NAME,
                        TextFormatter.parse("<red>&lBACK</red>")
                );

                this.getSlot(49).set(back);




                int slot = 10;

                for (Entry e : entries) {

                    if (slot >= 44) break;

                    String name = NameResolver.nameOrUuid(player.server, e.uuid);

                    ItemStack head = new ItemStack(Items.PLAYER_HEAD);

                    GameProfile profile = player.server.getProfileCache()
                            .get(e.uuid)
                            .orElse(new GameProfile(e.uuid, name));

                    head.set(DataComponents.PROFILE, new ResolvableProfile(profile));

                    head.set(DataComponents.CUSTOM_NAME,
                            TextFormatter.parse("<gold>#" + e.rank + " </gold><white>" + name + "</white>"));

                    List<Component> lore = new ArrayList<>();

                    String label = switch (mode) {
                        case TOTAL_FISH -> "<gray>Total Fish:</gray> <yellow>" + e.value + "</yellow>";
                        case LEVEL -> "<gray>Level:</gray> <green>" + e.value + "</green>";

                        case BRONZE -> "&#cd7f32Bronze Fish: &e" + e.value;
                        case SILVER -> "&#bdbdbdSilver Fish: &e" + e.value;
                        case GOLD -> "&#f5f788Gold Fish: &e" + e.value;
                        case DIAMOND -> "&#57f2e8Diamond Fish: &e" + e.value;
                        case PLATINUM -> "&#2deb95Platinum Fish: &e" + e.value;
                        case MYTHICAL -> "&#db2debMythical Fish: &e" + e.value;

                        case EVENTS -> "&cEvents Won: &e" + e.value;
                        case TOURNAMENTS -> "&4Tournaments Won: &e" + e.value;
                        case DELIVERIES -> "&9Deliveries: &e" + e.value;

                        case SQUIDS -> "&8Squids Killed: &e" + e.value;
                        case DOLPHINS -> "&3Dolphins Killed: &e" + e.value;
                        case CRABS -> "&cCrabs Killed: &e" + e.value;

                        case LONGEST -> "&eLength: &e" + e.value + "cm";
                        case SHORTEST -> "&eLength: &e" + e.value + "cm";

                        default -> "&bEntropy: &e" + e.value;
                    };
                    lore.add(TextFormatter.parse(label));


                    if (e.rank == 1) {
                        lore.add(TextFormatter.parse("<gold>&l★ 1st</gold>"));
                    } else if (e.rank == 2) {
                        lore.add(TextFormatter.parse("<gray>&l★ 2nd</gray>"));
                    } else if (e.rank == 3) {
                        lore.add(TextFormatter.parse("&#cd7f32&l★ 3rd"));
                    }


                    if (e.uuid.equals(player.getUUID())) {
                        lore.add(TextFormatter.parse("<green>&lYOU</green>"));
                    }

                    head.set(DataComponents.LORE,
                            new net.minecraft.world.item.component.ItemLore(lore));

                    this.getSlot(slot).set(head);

                    slot++;
                    if (slot % 9 == 8) slot += 2;
                }
            }
        };

        player.openMenu(new SimpleMenuProvider(
                ctor,
                TextFormatter.parse("<gold>Fishing Leaderboard</gold>")
        ));
    }

    public enum Mode {
        ENTROPY,
        LONGEST,
        SHORTEST,
        TOTAL_FISH,
        LEVEL,
        BRONZE,
        SILVER,
        GOLD,
        DIAMOND,
        PLATINUM,
        MYTHICAL,
        EVENTS,
        TOURNAMENTS,
        DELIVERIES,
        SQUIDS,
        DOLPHINS,
        CRABS
    }

    public static void open(ServerPlayer player) {
        open(player, Mode.ENTROPY);
    }

    public static void openLongest(ServerPlayer player) {
        open(player, Mode.LONGEST);
    }

    public static void openShortest(ServerPlayer player) {
        open(player, Mode.SHORTEST);
    }

    private record Entry(int rank, UUID uuid, int value) {}

}