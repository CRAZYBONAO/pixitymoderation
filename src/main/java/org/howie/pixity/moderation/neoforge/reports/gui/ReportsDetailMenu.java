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

import org.howie.pixity.moderation.neoforge.reports.ReportEntry;
import org.howie.pixity.moderation.neoforge.reports.ReportStatus;
import org.howie.pixity.moderation.neoforge.reports.ReportsService;
import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.tp.gui.ChatPromptService;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public final class ReportsDetailMenu extends ChestMenu {


    private static final int ROWS = 4;
    private static final int SIZE = 9 * ROWS;

    private static final DateTimeFormatter TS_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.systemDefault());

    private final SimpleContainer top;

    private final MinecraftServer server;
    private final ServerPlayer viewer;
    private final ReportsService reports;
    private final RankService ranks;

    private final int reportId;
    private final ReportStatus backFilter;
    private final int backPage;

    public ReportsDetailMenu(int id, Inventory inv,
                             MinecraftServer server,
                             ServerPlayer viewer,
                             ReportsService reports,
                             RankService ranks,
                             int reportId,
                             ReportStatus backFilter,
                             int backPage) {

        super(MenuType.GENERIC_9x4, id, inv, new SimpleContainer(SIZE), ROWS);

        this.top = (SimpleContainer) this.getContainer();
        this.server = server;
        this.viewer = viewer;
        this.reports = reports;
        this.ranks = ranks;
        this.reportId = reportId;
        this.backFilter = backFilter;
        this.backPage = backPage;

        render();
    }

    private boolean has(String perm) {
        return ranks != null && ranks.hasPerm(viewer, perm);
    }

    private void render() {
        for (int i = 0; i < SIZE; i++) top.setItem(i, ItemStack.EMPTY);

        ReportEntry r = reports.get(reportId);
        if (r == null) {
            top.setItem(13, button(Items.BARRIER,
                    Component.literal("Unknown report").withStyle(ChatFormatting.RED)));
            return;
        }


        ItemStack header = new ItemStack(Items.PAPER);
        header.set(DataComponents.CUSTOM_NAME,
                Component.literal("Report #" + r.id + " [" + r.status + "]")
                        .withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD));

        List<Component> lore = new ArrayList<>();

        lore.add(Component.literal(r.reporterName + " -> " + r.targetName)
                .withStyle(ChatFormatting.GRAY));

        if (r.reason != null) {
            lore.add(Component.literal("Reason: " + r.reason)
                    .withStyle(ChatFormatting.GRAY));
        }

        lore.add(Component.literal("Created: " + TS_FMT.format(Instant.ofEpochMilli(r.ts)))
                .withStyle(ChatFormatting.DARK_GRAY));

        if (r.assignedName != null) {
            lore.add(Component.literal("Assigned: " + r.assignedName)
                    .withStyle(ChatFormatting.GRAY));
        }

        if (r.assignedTs > 0) {
            long secs = (System.currentTimeMillis() - r.assignedTs) / 1000;
            lore.add(Component.literal("Assigned " + secs + "s ago")
                    .withStyle(ChatFormatting.DARK_GRAY));
        }

        if (r.closedBy != null) {
            lore.add(Component.literal("Closed by: " + r.closedBy)
                    .withStyle(ChatFormatting.GRAY));
        }

        header.set(DataComponents.LORE, new ItemLore(lore));
        top.setItem(4, header);


        top.setItem(10, button(Items.ENDER_PEARL, Component.literal("Teleport Target")));
        top.setItem(11, button(Items.COMPASS, Component.literal("Teleport Reporter")));
        top.setItem(12, button(Items.NAME_TAG, Component.literal("Assign")));
        top.setItem(13, button(Items.BARRIER, Component.literal("Unassign")));
        top.setItem(14, button(Items.REDSTONE_BLOCK, Component.literal("Close Report")));

        top.setItem(16, button(Items.IRON_SWORD,
                Component.literal("Punish (Optional)").withStyle(ChatFormatting.RED)));

        top.setItem(31, button(Items.OAK_DOOR, Component.literal("Back")));
    }

    private ItemStack button(net.minecraft.world.item.Item item, Component name) {
        ItemStack it = new ItemStack(item);
        it.set(DataComponents.CUSTOM_NAME, name);
        return it;
    }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickType, net.minecraft.world.entity.player.Player player) {
        if (!(player instanceof ServerPlayer sp)) return;
        if (slotId < 0 || slotId >= SIZE) return;

        this.setCarried(ItemStack.EMPTY);

        ReportEntry r = reports.get(reportId);
        if (r == null) return;

        switch (slotId) {

            case 10 -> {
                if (!has(ReportsService.PERM_REPORT_TP)) return;
                server.getCommands().performPrefixedCommand(sp.createCommandSourceStack(),
                        "report tp " + reportId);
            }

            case 11 -> {
                if (!has(ReportsService.PERM_REPORT_TP)) return;
                server.getCommands().performPrefixedCommand(sp.createCommandSourceStack(),
                        "tp " + r.reporterName);
            }

            case 12 -> {
                if (!has(ReportsService.PERM_REPORT_ASSIGN)) return;
                server.getCommands().performPrefixedCommand(sp.createCommandSourceStack(),
                        "report assign " + reportId + " " + sp.getGameProfile().getName());
            }

            case 13 -> {
                if (!has(ReportsService.PERM_REPORT_ASSIGN)) return;
                server.getCommands().performPrefixedCommand(sp.createCommandSourceStack(),
                        "report unassign " + reportId);
            }

            case 14 -> {
                if (!has(ReportsService.PERM_REPORT_CLOSE)) return;

                sp.closeContainer();
                ChatPromptService.prompt(sp, "Enter close note:", note -> {
                    if (note == null || note.equalsIgnoreCase("cancel")) return;

                    server.getCommands().performPrefixedCommand(
                            sp.createCommandSourceStack(),
                            "report close " + reportId + " " + note
                    );
                });
            }

            case 16 -> {
                server.getCommands().performPrefixedCommand(
                        sp.createCommandSourceStack(),
                        "punish " + r.targetName
                );
            }

            case 31 -> {
                sp.openMenu(ReportsListMenu.provider(server, sp, reports, ranks, backFilter, backPage));
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(net.minecraft.world.entity.player.Player player, int index) {
        return ItemStack.EMPTY;
    }

    public static net.minecraft.world.MenuProvider provider(MinecraftServer server,
                                                            ServerPlayer viewer,
                                                            ReportsService reports,
                                                            RankService ranks,
                                                            int reportId,
                                                            ReportStatus backFilter,
                                                            int backPage) {

        return new net.minecraft.world.SimpleMenuProvider(
                (id, inv, ply) -> new ReportsDetailMenu(
                        id,
                        inv,
                        server,
                        viewer,
                        reports,
                        ranks,
                        reportId,
                        backFilter,
                        backPage
                ),
                Component.literal("Report #" + reportId)
        );
    }


}
