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
import org.howie.pixity.moderation.neoforge.tp.PlayerWarp;
import org.howie.pixity.moderation.neoforge.tp.TeleportWarmupManager;
import org.howie.pixity.moderation.neoforge.tp.TpService;

import java.util.*;

public final class PixityPWarpsMenu extends ChestMenu {

    private static final int SIZE = 54;

    private final SimpleContainer top;
    private final TpService tp;
    private final TeleportWarmupManager warmup;
    private final TpChatPromptService prompts;

    private List<String> allWarps;
    private List<String> visible;

    private int page;
    private String search;
    private String category;
    private boolean featuredOnly;

    public PixityPWarpsMenu(
            int id,
            Inventory inv,
            TpService tp,
            TeleportWarmupManager warmup,
            TpChatPromptService prompts,
            List<String> allWarps,
            int page,
            String search,
            boolean featuredOnly
    ) {
        super(MenuType.GENERIC_9x6, id, inv, new SimpleContainer(SIZE), 6);

        this.top = (SimpleContainer) this.getContainer();
        this.tp = tp;
        this.warmup = warmup;
        this.prompts = prompts;

        this.allWarps = allWarps;
        this.page = page;
        this.search = search;
        this.featuredOnly = featuredOnly;

        this.visible = applyFilters();

        render();
    }

    private List<String> applyFilters() {

        List<String> out = new ArrayList<>();

        for (String name : allWarps) {

            PlayerWarp w = tp.getPlayerWarp(name);
            if (w == null) continue;

            if (search != null &&
                    !name.toLowerCase().contains(search.toLowerCase()))
                continue;

            if (category != null &&
                    (w.category == null ||
                            !category.equalsIgnoreCase(w.category)))
                continue;

            if (featuredOnly && !w.featured)
                continue;

            out.add(name);
        }

        out.sort((a, b) -> {
            PlayerWarp wa = tp.getPlayerWarp(a);
            PlayerWarp wb = tp.getPlayerWarp(b);

            int scoreA = wa.visits + wa.recentVisits * 2;
            int scoreB = wb.visits + wb.recentVisits * 2;

            return Integer.compare(scoreB, scoreA);
        });

        return out;
    }

    private void render() {

        for (int i = 0; i < SIZE; i++)
            top.setItem(i, ItemStack.EMPTY);

        drawBorder();
        drawWarps();
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

    private void drawWarps() {

        int perPage = 28;
        int start = page * perPage;
        int end = Math.min(visible.size(), start + perPage);

        int slot = 10;

        for (int i = start; i < end; i++) {

            if (slot % 9 == 8) slot += 2;

            PlayerWarp w = tp.getPlayerWarp(visible.get(i));

            String iconKey = w.category;

            if (iconKey == null || iconKey.equalsIgnoreCase("default")) {
                iconKey = w.icon;
            }

            ItemStack icon = WarpIconRegistry.getIcon(iconKey);

            icon.set(
                    DataComponents.CUSTOM_NAME,
                    LegacyAmpersand.parse("&b" + w.name)
            );

            List<Component> lore = new ArrayList<>();

            lore.add(LegacyAmpersand.parse("&7Rating: &e" +
                    String.format("%.1f", w.getRating())));

            lore.add(LegacyAmpersand.parse("&7Visits: &e" + w.visits));
            lore.add(LegacyAmpersand.parse("&7Category: &e" + w.category));

            if (w.featured)
                lore.add(LegacyAmpersand.parse("&6&l★ FEATURED"));

            lore.add(Component.empty());
            lore.add(LegacyAmpersand.parse("&aLeft Click to teleport"));

            lore.add(LegacyAmpersand.parse("&eRight Click to rate"));

            lore.add(LegacyAmpersand.parse("&6Middle Click to edit"));

            icon.set(DataComponents.LORE, new ItemLore(lore));

            top.setItem(slot, icon);
            slot++;
        }
    }

    private void drawControls() {

        top.setItem(4, button(
                Items.NETHER_STAR,
                "&bFEATURED",
                featuredOnly
                        ? "&7Click to toggle &bfeatured &7warps"
                        : "&7Click to toggle featured warps"
        ));

        top.setItem(45, button(
                Items.ARROW,
                "&e<< Previous Page"
        ));

        top.setItem(48, button(
                Items.SPYGLASS,
                "&eSEARCH",
                "&7Click then type name in chat"
        ));

        top.setItem(50, button(
                Items.ENDER_PEARL,
                "&cCATEGORY",
                "&7Selected Category: " +
                        (category == null ? "&aALL" : "&e" + category)
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

        if (slot == 4) {
            featuredOnly = !featuredOnly;
            visible = applyFilters();
            render();
            return;
        }

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

        if (slot == 48) {
            sp.closeContainer();
            prompts.requestPWarpsSearch(sp, search);
            return;
        }

        if (slot == 50) {
            cycleCategory();
            visible = applyFilters();
            render();
            return;
        }

        if (slot == 49) {
            sp.closeContainer();
            return;
        }

        if (slot >= 10 && slot <= 43) {

            int index = convertSlotToIndex(slot);
            if (index < 0) return;

            int real = page * 28 + index;
            if (real >= visible.size()) return;

            PlayerWarp w = tp.getPlayerWarp(visible.get(real));
            if (w == null) return;

            if (button == 0) {
                w.visits++;
                w.recentVisits++;
                warmup.request(sp.getServer(), sp, w.pos, "pwarp " + w.name);
            }

            if (button == 1 && !sp.isShiftKeyDown()) {
                sp.openMenu(new net.minecraft.world.SimpleMenuProvider(
                        (id, inv, pl) -> new WarpRatingMenu(id, inv, w),
                        LegacyAmpersand.parse("&aRate Warp")
                ));
                return;
            }


            if (button == 2 ) {

                if (w.owner != null && !w.owner.equals(sp.getUUID())) {
                    sp.sendSystemMessage(
                            LegacyAmpersand.parse("&cYou do not own this warp.")
                    );
                    return;
                }

                sp.openMenu(new net.minecraft.world.SimpleMenuProvider(
                        (id, inv, pl) -> new WarpEditMenu(id, inv, w, prompts, tp),
                        LegacyAmpersand.parse("&eEdit Warp")
                ));
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

    private void cycleCategory() {
        List<String> cats = List.of("all","pvp","shop","gym","grind");
        int idx = cats.indexOf(category == null ? "all" : category);
        idx = (idx + 1) % cats.size();
        category = cats.get(idx);
        if (category.equals("all")) category = null;
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

            List<Component> lore = new ArrayList<>();

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