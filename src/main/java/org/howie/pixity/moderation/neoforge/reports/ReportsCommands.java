package org.howie.pixity.moderation.neoforge.reports;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;
import org.howie.pixity.moderation.neoforge.util.Suggest;

import java.util.List;

public final class ReportsCommands {

    private final ReportsService reports;
    private final RankService ranks;

    public ReportsCommands(final ReportsService reports, final RankService ranks) {
        this.reports = reports;
        this.ranks = ranks;
    }

    private boolean has(ServerPlayer p, String perm) {
        return ranks.hasPerm(p, perm) || ranks.hasPerm(p, "pixity.admin");
    }

    public void register(CommandDispatcher<CommandSourceStack> d) {

        d.register(
                Commands.literal("report")

                        .then(Commands.argument("player", StringArgumentType.word())
                                .suggests(Suggest.playersOnline())
                                .then(Commands.argument("reason", StringArgumentType.greedyString())
                                        .executes(ctx -> {

                                            ServerPlayer reporter = ctx.getSource().getPlayerOrException();

                                            if (!has(reporter, ReportsService.PERM_REPORT_CREATE)) {
                                                reporter.sendSystemMessage(LegacyAmpersand.parse("&4&lREPORTS &7&l➤ &cError! No permission."));
                                                return 0;
                                            }

                                            String name = StringArgumentType.getString(ctx, "player");
                                            MinecraftServer server = ctx.getSource().getServer();
                                            ServerPlayer target = server.getPlayerList().getPlayerByName(name);

                                            if (target == null) {
                                                reporter.sendSystemMessage(LegacyAmpersand.parse("&4&lREPORTS &7&l➤ &cError! Player must be online."));
                                                return 0;
                                            }

                                            String reason = StringArgumentType.getString(ctx, "reason");
                                            ReportEntry r = reports.create(server, reporter, target, reason);

                                            if (r == null) return 0;

                                            reporter.sendSystemMessage(LegacyAmpersand.parse("&4&lREPORTS &7&l➤ &aReport submitted (&e#" + r.id + "&a)"));
                                            return 1;
                                        })
                                )
                        )

                        .then(Commands.literal("info")
                                .requires(src -> src.getEntity() instanceof ServerPlayer p &&
                                        has(p, ReportsService.PERM_REPORT_VIEW))
                                .then(Commands.argument("id", IntegerArgumentType.integer(1))
                                        .executes(ctx -> {

                                            ServerPlayer p = ctx.getSource().getPlayerOrException();

                                            if (!has(p, ReportsService.PERM_REPORT_VIEW)) {
                                                p.sendSystemMessage(LegacyAmpersand.parse("&4&lREPORTS &7&l➤ &cError! No permission."));
                                                return 0;
                                            }

                                            int id = IntegerArgumentType.getInteger(ctx, "id");
                                            ReportEntry r = reports.get(id);

                                            if (r == null) {
                                                ctx.getSource().sendFailure(LegacyAmpersand.parse("&4&lREPORTS &7&l➤ &cError! Unknown report."));
                                                return 0;
                                            }

                                            p.sendSystemMessage(LegacyAmpersand.parse("&4&lREPORTS &7&l➤ Report &e#" + r.id + " &7[" + r.status + "]"));
                                            p.sendSystemMessage(LegacyAmpersand.parse("&4&lREPORTS &7&l➤" + r.reporterName + " -> " + r.targetName));
                                            p.sendSystemMessage(LegacyAmpersand.parse("&4&lREPORTS &7&l➤ &cReason: &e" + r.reason));
                                            return 1;
                                        })
                                )
                        )

                        .then(Commands.literal("assign")
                                .requires(src -> src.getEntity() instanceof ServerPlayer p &&
                                        has(p, ReportsService.PERM_REPORT_ASSIGN))
                                .then(Commands.argument("id", IntegerArgumentType.integer(1))
                                        .then(Commands.argument("staff", StringArgumentType.word())
                                                .suggests(Suggest.playersOnline())
                                                .executes(ctx -> {

                                                    ServerPlayer p = ctx.getSource().getPlayerOrException();

                                                    if (!has(p, ReportsService.PERM_REPORT_ASSIGN)) {
                                                        p.sendSystemMessage(LegacyAmpersand.parse("&4&lREPORTS &7&l➤ &cError! No permission."));
                                                        return 0;
                                                    }

                                                    MinecraftServer server = ctx.getSource().getServer();
                                                    int id = IntegerArgumentType.getInteger(ctx, "id");
                                                    String name = StringArgumentType.getString(ctx, "staff");

                                                    ServerPlayer staff = server.getPlayerList().getPlayerByName(name);
                                                    if (staff == null) return 0;

                                                    return reports.assign(server, id, staff) ? 1 : 0;
                                                })
                                        )
                                )
                        )

                        .then(Commands.literal("unassign")
                                .requires(src -> src.getEntity() instanceof ServerPlayer p &&
                                        has(p, ReportsService.PERM_REPORT_ASSIGN))
                                .then(Commands.argument("id", IntegerArgumentType.integer(1))
                                        .executes(ctx -> {

                                            ServerPlayer p = ctx.getSource().getPlayerOrException();

                                            if (!has(p, ReportsService.PERM_REPORT_ASSIGN)) {
                                                p.sendSystemMessage(LegacyAmpersand.parse("&4&lREPORTS &7&l➤ &cError! No permission."));
                                                return 0;
                                            }

                                            int id = IntegerArgumentType.getInteger(ctx, "id");

                                            return reports.unassign(ctx.getSource().getServer(), id, p) ? 1 : 0;
                                        })
                                )
                        )

                        .then(Commands.literal("close")
                                .requires(src -> src.getEntity() instanceof ServerPlayer p &&
                                        has(p, ReportsService.PERM_REPORT_CLOSE))
                                .then(Commands.argument("id", IntegerArgumentType.integer(1))
                                        .then(Commands.argument("note", StringArgumentType.greedyString())
                                                .executes(ctx -> {

                                                    ServerPlayer p = ctx.getSource().getPlayerOrException();

                                                    if (!has(p, ReportsService.PERM_REPORT_CLOSE)) {
                                                        p.sendSystemMessage(LegacyAmpersand.parse("&4&lREPORTS &7&l➤ &cError! No permission."));
                                                        return 0;
                                                    }

                                                    int id = IntegerArgumentType.getInteger(ctx, "id");
                                                    String note = StringArgumentType.getString(ctx, "note");

                                                    return reports.close(ctx.getSource().getServer(), id, p, note) ? 1 : 0;
                                                })
                                        )
                                )
                        )

                        .then(Commands.literal("tp")
                                .requires(src -> src.getEntity() instanceof ServerPlayer p && has(p, ReportsService.PERM_REPORT_TELEPORT))
                                .then(Commands.argument("id", IntegerArgumentType.integer(1))
                                        .executes(ctx -> {

                                            ServerPlayer p = ctx.getSource().getPlayerOrException();

                                            if (!has(p, ReportsService.PERM_REPORT_TELEPORT)) {
                                                p.sendSystemMessage(LegacyAmpersand.parse("&4&lREPORTS &7&l➤ &cError! No permission."));
                                                return 0;
                                            }

                                            int id = IntegerArgumentType.getInteger(ctx, "id");
                                            ReportEntry r = reports.get(id);

                                            if (r == null) return 0;

                                            MinecraftServer server = ctx.getSource().getServer();
                                            ServerPlayer target = server.getPlayerList().getPlayerByName(r.targetName);

                                            if (target == null) return 0;

                                            p.teleportTo(target.serverLevel(),
                                                    target.getX(),
                                                    target.getY(),
                                                    target.getZ(),
                                                    target.getYRot(),
                                                    target.getXRot());
                                            return 1;
                                        })
                                )
                        )
        );

        d.register(
                Commands.literal("reports")
                        .requires(src -> src.getEntity() instanceof ServerPlayer p &&
                                has(p, ReportsService.PERM_REPORT_VIEW))

                        .executes(ctx -> { list(ctx.getSource(), ReportStatus.OPEN); return 1; })
                        .then(Commands.literal("open").executes(ctx -> { list(ctx.getSource(), ReportStatus.OPEN); return 1; }))
                        .then(Commands.literal("assigned").executes(ctx -> { list(ctx.getSource(), ReportStatus.ASSIGNED); return 1; }))
                        .then(Commands.literal("closed").executes(ctx -> { list(ctx.getSource(), ReportStatus.CLOSED); return 1; }))
                        .then(Commands.literal("all").executes(ctx -> { list(ctx.getSource(), null); return 1; }))
        );
    }

    private void list(CommandSourceStack src, ReportStatus status) {
        List<ReportEntry> list = reports.list(status);

        if (list.isEmpty()) {
            src.sendSuccess(() -> LegacyAmpersand.parse("&4&lREPORTS &7&l➤ &aNo reports."), false);
            return;
        }

        for (ReportEntry r : list) {
            src.sendSuccess(() ->
                            LegacyAmpersand.parse("&e#" + r.id + " &e" + r.targetName + " &e- &e" + r.reason),
                    false
            );
        }
    }
}