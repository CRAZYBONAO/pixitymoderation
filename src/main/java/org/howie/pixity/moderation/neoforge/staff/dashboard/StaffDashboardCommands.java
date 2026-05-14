package org.howie.pixity.moderation.neoforge.staff.dashboard;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;

import org.howie.pixity.moderation.neoforge.freeze.FreezeService;
import org.howie.pixity.moderation.neoforge.jail.JailService;
import org.howie.pixity.moderation.neoforge.punish.PunishmentManager;
import org.howie.pixity.moderation.neoforge.reports.ReportsService;
import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

public final class StaffDashboardCommands {

    private final RankService ranks;
    private final PunishmentManager punish;
    private final ReportsService reports;
    private final FreezeService freeze;
    private final JailService jail;

    public StaffDashboardCommands(
            RankService ranks,
            PunishmentManager punish,
            ReportsService reports,
            FreezeService freeze,
            JailService jail
    ) {
        this.ranks = ranks;
        this.punish = punish;
        this.reports = reports;
        this.freeze = freeze;
        this.jail = jail;
    }

    private boolean has(ServerPlayer p) {
        return ranks != null && ranks.hasPerm(p, StaffDashboardMenu.PERM_DASHBOARD);
    }

    public void register(CommandDispatcher<CommandSourceStack> d) {

        d.register(Commands.literal("dashboard")
                .requires(src -> src.getEntity() instanceof ServerPlayer p && has(p))
                .executes(ctx -> {

                    ServerPlayer sp = (ServerPlayer) ctx.getSource().getEntity();
                    if (sp == null) return 0;

                    MenuProvider prov = new SimpleMenuProvider(
                            (id, inv, ply) -> new StaffDashboardMenu(
                                    id,
                                    inv,
                                    sp.server,
                                    sp,
                                    ranks,
                                    punish,
                                    reports,
                                    freeze,
                                    jail
                            ),
                            LegacyAmpersand.parse("&c&lStaff Dashboard")
                    );

                    sp.openMenu(prov);
                    return 1;
                })
        );

        d.register(Commands.literal("staffpanel")
                .requires(src -> src.getEntity() instanceof ServerPlayer p && has(p))
                .executes(ctx -> {

                    ServerPlayer sp = (ServerPlayer) ctx.getSource().getEntity();
                    if (sp == null) return 0;

                    MenuProvider prov = new SimpleMenuProvider(
                            (id, inv, ply) -> new StaffDashboardMenu(
                                    id,
                                    inv,
                                    sp.server,
                                    sp,
                                    ranks,
                                    punish,
                                    reports,
                                    freeze,
                                    jail
                            ),
                            LegacyAmpersand.parse("&c&lStaff Dashboard")
                    );

                    sp.openMenu(prov);
                    return 1;
                })
        );
    }
}