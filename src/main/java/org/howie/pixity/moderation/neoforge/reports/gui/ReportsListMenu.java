package org.howie.pixity.moderation.neoforge.reports.gui;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;

import org.howie.pixity.moderation.neoforge.reports.*;
import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public final class ReportsListMenu extends ChestMenu {


    private static final int ROWS = 6;
    private static final int SIZE = 9 * ROWS;

    private static final DateTimeFormatter TS_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.systemDefault());

    private final SimpleContainer top;

    private final MinecraftServer server;
    private final ServerPlayer viewer;
    private final ReportsService reports;
    private final RankService ranks;

    private ReportStatus filter;
    private int page = 0;

    private List<ReportEntry> current = List.of();

    public ReportsListMenu(int containerId,
                           Inventory inv,
                           MinecraftServer server,
                           ServerPlayer viewer,
                           ReportsService reports,
                           RankService ranks,
                           ReportStatus filter,
                           int page) {

        super(MenuType.GENERIC_9x6, containerId, inv, new SimpleContainer(SIZE), ROWS);

        this.top = (SimpleContainer) this.getContainer();
        this.server = server;
        this.viewer = viewer;
        this.reports = reports;
        this.ranks = ranks;
        this.filter = filter;
        this.page = Math.max(0, page);

        render();
    }

    private boolean has(String perm) {
        return ranks != null && ranks.hasPerm(viewer, perm);
    }

    private boolean canView() {
        return has(ReportsService.PERM_REPORT_VIEW);
    }

    private void render() {
        for (int i = 0; i < SIZE; i++) top.setItem(i, ItemStack.EMPTY);





        String title = "Reports";
        if (filter != null) title += " (" + filter + ")";

        top.setItem(4, button(Items.NETHER_STAR,
                Component.literal(title).withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD)));

        List<ReportEntry> list = reports.list(filter);

        int perPage = 28;
        int maxPage = list.isEmpty() ? 0 : (list.size() - 1) / perPage;
        if (page > maxPage) page = maxPage;

        int start = page * perPage;
        int end = Math.min(list.size(), start + perPage);

        current = (start < end) ? new ArrayList<>(list.subList(start, end)) : List.of();

        int[] slots = listSlots();
        for (int i = 0; i < current.size() && i < slots.length; i++) {
            top.setItem(slots[i], reportItem(current.get(i)));
        }
    }

    private static ItemStack button(net.minecraft.world.item.Item item, Component name) {
        ItemStack it = new ItemStack(item);
        it.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, name);
        return it;
    }


    private int[] listSlots() {
        List<Integer> s = new ArrayList<>();
        for (int row = 2; row <= 5; row++) {
            int base = row * 9;
            for (int col = 1; col <= 7; col++) s.add(base + col);
        }
        return s.stream().mapToInt(i -> i).toArray();
    }

    private ItemStack reportItem(ReportEntry r) {
        ItemStack it = new ItemStack(Items.PAPER);

        it.set(DataComponents.CUSTOM_NAME,
                Component.literal("&e#" + r.id + " &c" + r.reporterName + " &a-> &c" + r.targetName));

        return it;
    }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickType, net.minecraft.world.entity.player.Player player) {
        if (!(player instanceof ServerPlayer sp)) return;

        if (!canView()) {
            sp.sendSystemMessage(LegacyAmpersand.parse("&c&lREPORTS &7&l➤ &cError! No permission."));
            return;
        }

        int idx = indexOfSlot(slotId);
        if (idx >= 0 && idx < current.size()) {
            ReportEntry r = current.get(idx);

            sp.openMenu(ReportsDetailMenu.provider(
                    server,
                    sp,
                    reports,
                    ranks,
                    r.id,
                    filter,
                    page
            ));
        }
    }

    private int indexOfSlot(int slotId) {
        int[] slots = listSlots();
        for (int i = 0; i < slots.length; i++) if (slots[i] == slotId) return i;
        return -1;
    }

    public static net.minecraft.world.MenuProvider provider(
            MinecraftServer server,
            ServerPlayer viewer,
            ReportsService reports,
            RankService ranks,
            ReportStatus filter,
            int page) {

        return new net.minecraft.world.SimpleMenuProvider(
                (id, inv, ply) -> new ReportsListMenu(id, inv, server, viewer, reports, ranks, filter, page),
                LegacyAmpersand.parse("&c&lReports")
        );
    }


}
