package org.howie.pixity.moderation.neoforge.chatgames.gui;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
import org.howie.pixity.moderation.neoforge.chatgames.ChatGamesConfig;
import org.howie.pixity.moderation.neoforge.chatgames.ChatGamesDatabase;

import java.util.*;


public class ChatGamesMilestonesGui {

    private static final int SIZE = 54;
    private static final Map<Integer, UUID> FIRST_CACHE = new HashMap<>();

    private static final int[] LEVEL_SLOTS = {
            37, 28, 19, 10,
            11, 12, 21, 30,
            39, 40, 41, 32,
            23, 14, 15, 16,
            25, 34, 43
    };

    public static void open(ServerPlayer player, int page) {

        int wins = ChatGamesDatabase.getStats(player.getUUID()).getOrDefault("wins", 0);

        int start = page * 45;

        MenuConstructor ctor = (id, inv, p) -> new ChestMenu(
                MenuType.GENERIC_9x6,
                id, inv,
                new SimpleContainer(SIZE),
                6
        ) {

            @Override
            public void broadcastChanges() {
                super.broadcastChanges();




                ItemStack glass = new ItemStack(Items.BLACK_STAINED_GLASS_PANE);
                glass.set(DataComponents.CUSTOM_NAME, Component.empty());

                for (int i = 0; i < 54; i++) {

                    boolean levelSlot = false;
                    for (int s : LEVEL_SLOTS) {
                        if (s == i) {
                            levelSlot = true;
                            break;
                        }
                    }

                    if (levelSlot || i == 48 || i == 49 || i == 50) continue;

                    this.getSlot(i).set(glass);
                }




                int startIndex = page * LEVEL_SLOTS.length;

                for (int i = 0; i < LEVEL_SLOTS.length; i++) {

                    int milestone = startIndex + i + 1;
                    int required = ChatGamesConfig.getRequirement(milestone);

                    boolean unlocked = wins >= required;
                    boolean claimed = ChatGamesDatabase.hasClaimed(player.getUUID(), milestone);

                    ItemStack item;

                    if (claimed) {
                        item = new ItemStack(Items.LIME_CONCRETE);
                    }
                    else if (unlocked) {
                        item = new ItemStack(Items.ORANGE_CONCRETE);
                    }
                    else {
                        item = new ItemStack(Items.RED_CONCRETE);
                    }

                    item.set(DataComponents.CUSTOM_NAME,
                            TextFormatter.parse("&6&lMILESTONE [" + milestone + "]"));

                    List<Component> lore = new ArrayList<>();

                    lore.add(TextFormatter.parse("&7ChatGames Milestones"));
                    lore.add(TextFormatter.parse(""));

                    lore.add(TextFormatter.parse("&eRequirements:"));
                    lore.add(TextFormatter.parse("&bWins: " + required));
                    lore.add(TextFormatter.parse(""));




                    UUID first = FIRST_CACHE.computeIfAbsent(
                            milestone,
                            ChatGamesDatabase::getFirstClaimer
                    );

                    if (first != null) {
                        String name = player.server.getProfileCache()
                                .get(first)
                                .map(p -> p.getName())
                                .orElse("Unknown");

                        lore.add(TextFormatter.parse("<yellow>First Claimed By:</yellow>"));
                        lore.add(TextFormatter.parse("&b" + name));
                    } else {
                        lore.add(TextFormatter.parse("<yellow>First Claimed By:</yellow>"));
                        lore.add(TextFormatter.parse("<red>None</red>"));
                    }

                    lore.add(TextFormatter.parse(""));




                    if (claimed) {
                        lore.add(TextFormatter.parse("<green>&l✔ Claimed</green>"));
                    }
                    else if (unlocked) {
                        lore.add(TextFormatter.parse("<yellow>Click to claim</yellow>"));
                    }
                    else {
                        lore.add(TextFormatter.parse("<red>Locked</red>"));
                    }

                    item.set(DataComponents.LORE, new ItemLore(lore));

                    this.getSlot(LEVEL_SLOTS[i]).set(item);
                }




                ItemStack info = new ItemStack(Items.PLAYER_HEAD);
                info.set(DataComponents.CUSTOM_NAME,
                        TextFormatter.parse("<gold>Your ChatGames Stats</gold>"));

                List<Component> infoLore = new ArrayList<>();
                infoLore.add(TextFormatter.parse("<yellow>Total Wins: </yellow> &b" + wins));

                info.set(DataComponents.LORE, new ItemLore(infoLore));

                this.getSlot(49).set(info);




                if (page > 0) {
                    ItemStack prev = new ItemStack(Items.ARROW);
                    prev.set(DataComponents.CUSTOM_NAME,
                            TextFormatter.parse("&7&l<< <yellow>Previous Page</yellow>"));
                    this.getSlot(48).set(prev);
                }

                ItemStack next = new ItemStack(Items.ARROW);
                next.set(DataComponents.CUSTOM_NAME,
                        TextFormatter.parse("<yellow>Next Page</yellow> &7&l➤"));
                this.getSlot(50).set(next);
            }

            @Override
            public void clicked(int slot, int button, ClickType type, Player p) {

                if (!(p instanceof ServerPlayer sp)) return;

                if (slot == 48 && page > 0) {
                    open(sp, page - 1);
                    return;
                }

                if (slot == 50) {
                    open(sp, page + 1);
                    return;
                }

                for (int i = 0; i < LEVEL_SLOTS.length; i++) {

                    if (slot != LEVEL_SLOTS[i]) continue;

                    int milestone = page * LEVEL_SLOTS.length + i + 1;
                    int required = ChatGamesConfig.getRequirement(milestone);

                    if (wins < required) {
                        sp.sendSystemMessage(TextFormatter.parse("<red>Not unlocked yet!"));
                        return;
                    }

                    claim(sp, milestone);
                    open(sp, page);
                    return;
                }
            }
        };

        player.openMenu(new SimpleMenuProvider(
                ctor,
                TextFormatter.parse("<gold>ChatGames Milestones")
        ));
    }

    private static void claim(ServerPlayer player, int milestone) {

        UUID uuid = player.getUUID();

        if (ChatGamesDatabase.hasClaimed(uuid, milestone)) {
            player.sendSystemMessage(TextFormatter.parse("<red>Already claimed!"));
            return;
        }

        int wins = ChatGamesDatabase.getStats(uuid).getOrDefault("wins", 0);
        int required = ChatGamesConfig.getRequirement(milestone);

        if (wins < required) {
            player.sendSystemMessage(TextFormatter.parse("<red>Not unlocked yet!"));
            return;
        }

        boolean success = ChatGamesDatabase.claim(uuid, milestone);

        if (!success) {
            player.sendSystemMessage(TextFormatter.parse("<red>Error claiming milestone"));
            return;
        }

        JsonObject reward = ChatGamesConfig.MILESTONES.get(milestone);
        if (reward == null) return;

        UUID firstClaimer = ChatGamesDatabase.getFirstClaimer(milestone);
        boolean first = firstClaimer != null && firstClaimer.equals(uuid);




        if (reward.has("commands")) {
            for (JsonElement el : reward.getAsJsonArray("commands")) {

                String cmd = el.getAsString()
                        .replace("%player%", player.getName().getString());

                player.server.getCommands().performPrefixedCommand(
                        player.server.createCommandSourceStack(),
                        cmd
                );
            }
        }




        if (first && reward.has("first_commands")) {

            for (JsonElement el : reward.getAsJsonArray("first_commands")) {

                String cmd = el.getAsString()
                        .replace("%player%", player.getName().getString());

                player.server.getCommands().performPrefixedCommand(
                        player.server.createCommandSourceStack(),
                        cmd
                );
            }

            player.getServer().sendSystemMessage(
                    TextFormatter.parse("<gold>" + player.getName().getString()
                            + " was the first to claim chatgames milestone " + milestone + "!")
            );
        }

        player.sendSystemMessage(TextFormatter.parse("<green>Milestone claimed!"));
    }


}
