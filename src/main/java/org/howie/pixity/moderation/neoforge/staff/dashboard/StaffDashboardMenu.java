package org.howie.pixity.moderation.neoforge.staff.dashboard;

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

import org.howie.pixity.moderation.neoforge.freeze.FreezeService;
import org.howie.pixity.moderation.neoforge.jail.JailService;
import org.howie.pixity.moderation.neoforge.reports.ReportsService;
import org.howie.pixity.moderation.neoforge.punish.PunishmentManager;
import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.reports.gui.ReportsListMenu;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

import java.util.*;

public final class StaffDashboardMenu extends ChestMenu {


    public static final String PERM_DASHBOARD = "pixity.staff.dashboard";

    private static final int ROWS = 6;
    private static final int SIZE = 9 * ROWS;

    private final SimpleContainer top;

    private final MinecraftServer server;
    private final ServerPlayer viewer;

    private final RankService ranks;
    private final PunishmentManager punish;
    private final ReportsService reports;
    private final FreezeService freeze;
    private final JailService jail;

    public StaffDashboardMenu(int containerId,
                              Inventory inv,
                              MinecraftServer server,
                              ServerPlayer viewer,
                              RankService ranks,
                              PunishmentManager punish,
                              ReportsService reports,
                              FreezeService freeze,
                              JailService jail) {

        super(MenuType.GENERIC_9x6, containerId, inv, new SimpleContainer(SIZE), ROWS);

        this.top = (SimpleContainer) this.getContainer();
        this.server = server;
        this.viewer = viewer;
        this.ranks = ranks;
        this.punish = punish;
        this.reports = reports;
        this.freeze = freeze;
        this.jail = jail;

        render();
    }

    private boolean isStaff(ServerPlayer p) {
        return ranks != null && ranks.hasPerm(p, "pixity.staff");
    }

    private int countStaffOnline() {
        int n = 0;
        for (ServerPlayer p : server.getPlayerList().getPlayers()) {
            if (isStaff(p)) n++;
        }
        return n;
    }

    private ItemStack button(net.minecraft.world.item.Item item, Component name) {
        ItemStack it = new ItemStack(item);
        it.set(DataComponents.CUSTOM_NAME, name);
        return it;
    }

    private void render() {
        top.setItem(4, button(Items.NETHER_STAR,
                LegacyAmpersand.parse("&bStaff Dashboard")));

        top.setItem(21, button(Items.PAPER, LegacyAmpersand.parse("&cReports")));
        top.setItem(22, button(Items.PACKED_ICE, LegacyAmpersand.parse("&bFrozen: &e" + freeze.listFrozenNames().size())));
        top.setItem(23, button(Items.IRON_BARS, LegacyAmpersand.parse("&bJailed: &e" + jail.listJailedNames().size())));

        top.setItem(49, button(Items.PLAYER_HEAD,
                LegacyAmpersand.parse("&aStaff Online: &e" + countStaffOnline())));

        top.setItem(53, button(Items.OAK_DOOR, LegacyAmpersand.parse("&4Close")));
    }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickType, net.minecraft.world.entity.player.Player player) {
        if (!(player instanceof ServerPlayer sp)) return;

        if (slotId == 21) {
            sp.openMenu(ReportsListMenu.provider(server, sp, reports, ranks, null, 0));
        }

        if (slotId == 53) {
            sp.closeContainer();
        }
    }


}
