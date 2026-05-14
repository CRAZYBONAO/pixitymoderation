package org.howie.pixity.moderation.neoforge.chatgames.gui;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.SimpleMenuProvider;

import org.howie.pixity.moderation.chat.TextFormatter;
import org.howie.pixity.moderation.neoforge.chatgames.ChatGameManager;

import java.util.*;

public class ChatGamesStreakGui {

    public static void open(ServerPlayer player) {

        int streak = ChatGameManager.getStreak(player.getUUID());
        int next = ChatGameManager.getNextMilestone(streak);

        MenuConstructor ctor = (id, inv, p) -> new ChestMenu(
                net.minecraft.world.inventory.MenuType.GENERIC_9x3,
                id,
                inv,
                new net.minecraft.world.SimpleContainer(27),
                3
        ) {

            @Override
            public void broadcastChanges() {
                super.broadcastChanges();




                ItemStack current = new ItemStack(Items.BLAZE_POWDER);

                current.set(DataComponents.CUSTOM_NAME,
                        TextFormatter.parse("<gold>Current Streak"));

                List<Component> lore = new ArrayList<>();
                lore.add(TextFormatter.parse("<yellow>" + streak + "x"));

                current.set(DataComponents.LORE, new ItemLore(lore));

                this.getSlot(11).set(current);




                ItemStack nextItem = new ItemStack(Items.EMERALD);

                nextItem.set(DataComponents.CUSTOM_NAME,
                        TextFormatter.parse("<green>Next Milestone"));

                List<Component> lore2 = new ArrayList<>();

                if (next == -1) {
                    lore2.add(TextFormatter.parse("<gold>Max reached!"));
                } else {
                    int progress = streak;
                    int needed = next;

                    lore2.add(TextFormatter.parse("<gray>" + progress + " / " + needed));
                    lore2.add(TextFormatter.parse("<green>" + (needed - progress) + " to go"));
                }

                nextItem.set(DataComponents.LORE, new ItemLore(lore2));

                this.getSlot(15).set(nextItem);
            }
        };

        player.openMenu(new SimpleMenuProvider(
                ctor,
                TextFormatter.parse("<gold>Streak Progress</gold>")
        ));
    }
}