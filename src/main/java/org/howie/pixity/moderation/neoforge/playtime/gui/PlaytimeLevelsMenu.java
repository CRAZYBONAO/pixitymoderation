package org.howie.pixity.moderation.neoforge.playtime.gui;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import net.minecraft.world.item.component.ItemLore;

import org.howie.pixity.moderation.neoforge.playtime.PlaytimeService;
import org.howie.pixity.moderation.neoforge.playtime.SQLitePlaytimeStore;
import org.howie.pixity.moderation.neoforge.playtime.RewardExecutor;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;
import org.howie.pixity.moderation.neoforge.util.TimeUtil;

import java.util.*;

public class PlaytimeLevelsMenu extends ChestMenu {


    private static final int[] LEVEL_SLOTS = {
            37,
            28,
            19,
            10,
            11,
            12,
            21,
            30,
            39,
            40,
            41,
            32,
            23,
            14,
            15,
            16,
            25,
            34,
            43
    };

    private final PlaytimeService playtime;
    private final SQLitePlaytimeStore store;
    private final List<Level> levels;
    private final int page;

    public PlaytimeLevelsMenu(
            int id,
            Inventory inv,
            PlaytimeService playtime,
            SQLitePlaytimeStore store,
            List<Level> levels,
            int page
    ) {
        super(MenuType.GENERIC_9x6, id, inv, new SimpleContainer(54), 6);

        this.playtime = playtime;
        this.store = store;
        this.levels = levels;
        this.page = page;

        render(inv.player);
    }

    private void render(Player player) {

        border();

        long time = playtime.getPlaytime(player.getUUID());

        int startIndex = page * LEVEL_SLOTS.length;
        int endIndex = Math.min(startIndex + LEVEL_SLOTS.length, levels.size());

        for (int i = startIndex; i < endIndex; i++) {

            int localIndex = i - startIndex;
            int slot = LEVEL_SLOTS[localIndex];

            Level level = levels.get(i);

            boolean claimed =
                    store.isClaimed(player.getUUID(), level.level);

            boolean unlocked =
                    time >= level.seconds;

            ItemStack item;

            if (claimed) {
                item = green(level);
            }
            else if (unlocked) {
                item = orange(level);
            }
            else {
                item = red(level, time);
            }

            getContainer().setItem(slot, item);
        }

        buttons(player);
    }

    private void border() {

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


            if (levelSlot || i == 48 || i == 49 || i == 50) {
                continue;
            }

            getContainer().setItem(i, glass);
        }
    }

    private void buttons(Player player) {

        if (page > 0) {
            ItemStack prev = new ItemStack(Items.ARROW);
            prev.set(DataComponents.CUSTOM_NAME,
                    LegacyAmpersand.parse("&ePrevious Page"));
            getContainer().setItem(48, prev);
        }

        if ((page + 1) * 19 < levels.size()) {
            ItemStack next = new ItemStack(Items.ARROW);
            next.set(DataComponents.CUSTOM_NAME,
                    LegacyAmpersand.parse("&eNext Page"));
            getContainer().setItem(50, next);
        }

        ItemStack skull = new ItemStack(Items.PLAYER_HEAD);
        skull.set(DataComponents.CUSTOM_NAME,
                LegacyAmpersand.parse("&eYour Playtime"));

        getContainer().setItem(49, skull);
    }

    private ItemStack green(Level level) {

        ItemStack item = new ItemStack(Items.LIME_CONCRETE);

        item.set(DataComponents.CUSTOM_NAME, name(level));
        item.set(DataComponents.LORE,
                new ItemLore(lore(level, true)));

        return item;
    }

    private ItemStack orange(Level level) {

        ItemStack item = new ItemStack(Items.ORANGE_CONCRETE);

        item.set(DataComponents.CUSTOM_NAME, name(level));
        item.set(DataComponents.LORE,
                new ItemLore(lore(level, false)));

        return item;
    }

    private ItemStack red(Level level, long time) {

        ItemStack item = new ItemStack(Items.RED_CONCRETE);

        List<Component> lore = lore(level, false);

        long missing = level.seconds - time;

        lore.add(Component.literal(""));
        lore.add(LegacyAmpersand.parse(
                "&cMissing time: &e" +
                        TimeUtil.formatDuration(missing)
        ));

        item.set(DataComponents.CUSTOM_NAME, name(level));
        item.set(DataComponents.LORE, new ItemLore(lore));

        return item;
    }

    private Component name(Level level) {
        return LegacyAmpersand.parse(
                "&a&lLEVEL &f[" + level.level + "]"
        );
    }

    private List<Component> lore(Level level, boolean claimed) {

        List<Component> lore = new ArrayList<>();

        lore.add(LegacyAmpersand.parse("&7Playtime levels"));
        lore.add(Component.literal(""));

        lore.add(LegacyAmpersand.parse("&eDescription:"));
        lore.add(LegacyAmpersand.parse("&rEarn &arewards &ras you play"));
        lore.add(LegacyAmpersand.parse("&rLevel up by &espending &rtime on the &cserver"));
        lore.add(Component.literal(""));

        lore.add(LegacyAmpersand.parse("&eRequirements:"));
        lore.add(LegacyAmpersand.parse(
                "&rPlaytime: &b" +
                        TimeUtil.formatDuration(level.seconds)
        ));

        lore.add(Component.literal(""));
        lore.add(LegacyAmpersand.parse("&eRewards:"));

        for (String reward : level.rewards) {
            lore.add(LegacyAmpersand.parse("&7" + reward));
        }

        if (claimed) {
            lore.add(Component.literal(""));
            lore.add(LegacyAmpersand.parse("&a✔ Already Claimed"));
        }

        return lore;
    }

    @Override
    public void clicked(int slot, int button, ClickType type, Player player) {

        if (!(player instanceof ServerPlayer sp)) return;


        if (slot == 48 && page > 0) {

            sp.openMenu(new net.minecraft.world.MenuProvider() {

                @Override
                public Component getDisplayName() {
                    return Component.literal("Playtime Levels");
                }

                @Override
                public net.minecraft.world.inventory.AbstractContainerMenu createMenu(
                        int id,
                        Inventory inv,
                        Player player
                ) {
                    return new PlaytimeLevelsMenu(
                            id,
                            inv,
                            playtime,
                            store,
                            levels,
                            page - 1
                    );
                }
            });

            return;
        }


        if (slot == 50) {

            sp.openMenu(new net.minecraft.world.MenuProvider() {

                @Override
                public Component getDisplayName() {
                    return Component.literal("Playtime Levels");
                }

                @Override
                public net.minecraft.world.inventory.AbstractContainerMenu createMenu(
                        int id,
                        Inventory inv,
                        Player player
                ) {
                    return new PlaytimeLevelsMenu(
                            id,
                            inv,
                            playtime,
                            store,
                            levels,
                            page + 1
                    );
                }
            });

            return;
        }

        for (int i = 0; i < LEVEL_SLOTS.length; i++) {

            if (slot != LEVEL_SLOTS[i]) continue;

            int index = page * 19 + i;

            if (index >= levels.size()) return;

            Level level = levels.get(index);

            long time = playtime.getPlaytime(sp.getUUID());

            if (time < level.seconds) return;

            if (store.isClaimed(sp.getUUID(), level.level)) return;

            store.claim(sp.getUUID(), level.level);

            RewardExecutor.execute(sp, level.rewards);

            render(player);
            return;
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    public static class Level {

        public final int level;
        public final long seconds;
        public final List<String> rewards;

        public Level(int level, long seconds, List<String> rewards) {
            this.level = level;
            this.seconds = seconds;
            this.rewards = rewards;
        }
    }
}