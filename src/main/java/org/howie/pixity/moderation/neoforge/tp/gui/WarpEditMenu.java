package org.howie.pixity.moderation.neoforge.tp.gui;

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

import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;
import org.howie.pixity.moderation.neoforge.tp.PlayerWarp;
import org.howie.pixity.moderation.neoforge.tp.TpService;

import java.util.List;

public class WarpEditMenu extends ChestMenu {

    private final PlayerWarp warp;
    private final TpChatPromptService prompts;
    private final TpService tp;

    public WarpEditMenu(int id, Inventory inv, PlayerWarp warp, TpChatPromptService prompts, TpService tp) {
        super(MenuType.GENERIC_9x3, id, inv, new SimpleContainer(27), 3);
        this.warp = warp;
        this.prompts = prompts;
        this.tp = tp;
        render();
    }

    private void render() {
        setItem(10, button(Items.NAME_TAG, "Rename"));
        setItem(11, button(Items.WRITABLE_BOOK, "Set Description"));
        setItem(12, button(Items.BOOK, "Category: " + (warp.category == null ? "none" : warp.category)));
        setItem(14, button(Items.NETHER_STAR, "Featured: " + warp.featured));
        setItem(16, button(Items.ITEM_FRAME, "Change Icon"));
        setItem(22, button(Items.BARRIER, "Close"));
    }

    private void setItem(int slot, ItemStack item) {
        getContainer().setItem(slot, item);
    }

    private ItemStack button(net.minecraft.world.item.Item item, String name) {
        ItemStack it = new ItemStack(item);

        it.set(DataComponents.CUSTOM_NAME,
                Component.literal(name)
                        .withStyle(style -> style.withItalic(false))
        );

        return it;
    }

    @Override
    public void clicked(int slot, int button, ClickType type, Player player) {

        if (!(player instanceof ServerPlayer sp)) return;

        if (warp.owner != null && !warp.owner.equals(sp.getUUID())) {
            sp.sendSystemMessage(
                    LegacyAmpersand.parse("&d&lPLAYER WARPS &7&l➤ &cError! You cannot edit this warp.")

            );
            return;
        }

        if (slot == 10) {
            sp.closeContainer();
            prompts.requestRenameWarp(sp, warp.name);
            return;
        }

        if (slot == 11) {
            sp.closeContainer();
            prompts.requestWarpDescription(sp, warp.name);
            return;
        }

        if (slot == 12) {
            List<String> cats = List.of("pvp", "shop", "gym", "grind");

            int i = cats.indexOf(warp.category);
            if (i == -1) i = 0;

            i = (i + 1) % cats.size();

            warp.category = cats.get(i);
            warp.icon = warp.category;

            tp.savePlayerWarp(warp);

            render();
            return;
        }

        if (slot == 14) {
            warp.featured = !warp.featured;

            tp.savePlayerWarp(warp);

            render();
            return;
        }

        if (slot == 16) {
            cycleIcon();

            tp.savePlayerWarp(warp);

            render();
            return;
        }

        if (slot == 22) {
            sp.closeContainer();
        }
    }

    private void cycleIcon() {
        List<String> icons = List.of("default","pvp","shop","gym","grind");

        int i = icons.indexOf(warp.icon);
        if (i == -1) i = 0;

        i = (i + 1) % icons.size();

        warp.icon = icons.get(i);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}