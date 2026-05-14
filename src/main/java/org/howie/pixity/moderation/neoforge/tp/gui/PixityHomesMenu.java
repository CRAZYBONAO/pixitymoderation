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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemLore;

import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;
import org.howie.pixity.moderation.neoforge.tp.TeleportWarmupManager;
import org.howie.pixity.moderation.neoforge.tp.TpService;
import org.howie.pixity.moderation.neoforge.tp.WarpPos;

import java.util.List;

public final class PixityHomesMenu extends ChestMenu {

    private static final int SIZE = 54;

    private final SimpleContainer top;
    private final TpService tp;
    private final TeleportWarmupManager warmup;
    private final TpChatPromptService prompts;

    private List<String> homes;
    private int page;
    private String query;

    public PixityHomesMenu(
            int containerId,
            Inventory inv,
            TpService tp,
            TeleportWarmupManager warmup,
            TpChatPromptService prompts,
            List<String> homes,
            int page,
            String query
    ) {
        super(MenuType.GENERIC_9x6, containerId, inv, new SimpleContainer(SIZE), 6);

        this.top = (SimpleContainer) this.getContainer();
        this.tp = tp;
        this.warmup = warmup;
        this.prompts = prompts;

        this.homes = homes;
        this.page = page;
        this.query = query == null ? "" : query;

        render();
    }

    private void render() {
        for (int i = 0; i < SIZE; i++)
            top.setItem(i, ItemStack.EMPTY);

        drawBorder();
        drawHomes();
        drawControls();
    }

    private void drawBorder() {

        ItemStack filler = filler();

        for (int i = 0; i < 9; i++)
            top.setItem(i, filler);

        for (int i = 45; i < 54; i++)
            top.setItem(i, filler);

        for (int r = 1; r < 5; r++) {
            top.setItem(r * 9, filler);
            top.setItem(r * 9 + 8, filler);
        }
    }

    private void drawHomes() {

        int perPage = 28;
        int start = page * perPage;
        int end = Math.min(homes.size(), start + perPage);

        int slot = 10;

        for (int i = start; i < end; i++) {

            if (slot % 9 == 8) slot += 2;

            String name = homes.get(i);

            ItemStack it = new ItemStack(Items.RED_BED);

            it.set(
                    DataComponents.CUSTOM_NAME,
                    LegacyAmpersand.parse("&b" + name)
            );

            it.set(DataComponents.LORE, new ItemLore(List.of(
                    LegacyAmpersand.parse("&aLeft Click to teleport"),
                    LegacyAmpersand.parse("&cRight Click to delete")
            )));

            top.setItem(slot, it);
            slot++;
        }
    }

    private void drawControls() {

        top.setItem(4, button(
                Items.SPYGLASS,
                "&eSEARCH",
                "&7Click then type the name you wish to search for in chat"
        ));

        top.setItem(45, button(
                Items.ARROW,
                "&e<< Previous Page"
        ));

        top.setItem(49, button(
                Items.BARRIER,
                "&c<< CLOSE >>",
                "&7Click to close menu"
        ));

        top.setItem(53, button(
                Items.ARROW,
                "&eNext Page >>"
        ));
    }

    @Override
    public void clicked(int slot, int button, ClickType type, Player player) {

        if (!(player instanceof ServerPlayer sp)) return;
        if (slot < 0 || slot >= SIZE) return;

        this.setCarried(ItemStack.EMPTY);

        if (slot == 45) {
            if (page > 0) page--;
            render();
            return;
        }

        if (slot == 53) {
            page++;
            render();
            return;
        }

        if (slot == 49) {
            sp.closeContainer();
            return;
        }

        if (slot == 4) {
            sp.closeContainer();
            prompts.requestHomesSearch(sp, query);
            return;
        }

        if (slot >= 10 && slot <= 43) {

            int index = convertSlotToIndex(slot);
            if (index < 0) return;

            int real = page * 28 + index;
            if (real >= homes.size()) return;

            String name = homes.get(real);

            if (button == 0) {
                WarpPos pos = tp.getHomePos(sp, name);
                if (pos != null)
                    warmup.request(sp.getServer(), sp, pos, "home " + name);
                sp.closeContainer();
            }

            if (button == 1) {

                if (!tp.hasPerm(sp, TpService.PERM_DELHOME)) {
                    sp.sendSystemMessage(
                            LegacyAmpersand.parse("&cNo permission")
                    );
                    return;
                }

                tp.delHome(sp, name);
                homes = tp.listHomes(sp);
                render();
            }
        }
    }

    private int convertSlotToIndex(int slot) {

        int row = slot / 9;
        int col = slot % 9;

        if (row == 0 || row == 5) return -1;
        if (col == 0 || col == 8) return -1;

        return (row - 1) * 7 + (col - 1);
    }

    private ItemStack filler() {
        return new ItemStack(Items.GRAY_STAINED_GLASS_PANE);
    }

    private ItemStack button(Item item, String title, String... loreLines) {

        ItemStack it = new ItemStack(item);

        it.set(
                DataComponents.CUSTOM_NAME,
                LegacyAmpersand.parse(title)
        );

        if (loreLines != null && loreLines.length > 0) {

            List<Component> lore = new java.util.ArrayList<>();

            for (String line : loreLines) {
                lore.add(LegacyAmpersand.parse(line));
            }

            it.set(DataComponents.LORE, new ItemLore(lore));
        }

        return it;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}