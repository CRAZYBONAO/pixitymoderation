package org.howie.pixity.moderation.neoforge.punish;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.UUID;

import org.howie.pixity.moderation.chat.MuteManager;
import org.howie.pixity.moderation.neoforge.freeze.FreezeCommands;
import org.howie.pixity.moderation.neoforge.freeze.FreezeService;
import org.howie.pixity.moderation.neoforge.jail.JailService;
import org.howie.pixity.moderation.neoforge.notes.NotesService;
import org.howie.pixity.moderation.neoforge.reports.ReportsService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;
import org.howie.pixity.moderation.neoforge.util.Suggest;
import org.howie.pixity.moderation.neoforge.rank.RankService;

public final class PunishCommands {

    private final PunishmentManager punish;
    private final NotesService notes;
    private final ReportsService reports;
    private final MuteManager mutes;
    private final RankService ranks;
    private final FreezeService freeze;
    private final JailService jail;

    public PunishCommands(
            final PunishmentManager punish,
            final NotesService notes,
            final ReportsService reports,
            final MuteManager mutes,
            final RankService ranks,
            final FreezeService freeze,
            final JailService jail
    ) {
        this.punish = punish;
        this.notes = notes;
        this.reports = reports;
        this.mutes = mutes;
        this.freeze = freeze;
        this.jail = jail;
        this.ranks = ranks;
    }

    private boolean has(CommandSourceStack src, String perm) {
        if (!(src.getEntity() instanceof ServerPlayer p)) return src.hasPermission(2);
        return ranks.hasPerm(p, perm);
    }

    public void register(final CommandDispatcher<CommandSourceStack> d) {





        d.register(Commands.literal("warn")
                .requires(src -> has(src, "pixity.warn"))
                .then(Commands.argument("player", StringArgumentType.word())
                        .suggests(Suggest.playersOnline())
                        .then(Commands.argument("reason", StringArgumentType.greedyString())
                                .executes(ctx -> {

                                    ServerPlayer staff = ctx.getSource().getPlayer();
                                    MinecraftServer server = ctx.getSource().getServer();
                                    if (staff == null) return 0;

                                    String name =
                                            StringArgumentType.getString(ctx, "player");

                                    ServerPlayer target =
                                            server.getPlayerList().getPlayerByName(name);

                                    if (target == null) return 0;

                                    String reason =
                                            StringArgumentType.getString(ctx, "reason");

                                    punish.warn(staff, target, reason);

                                    return 1;
                                })
                        )
                )
        );

        d.register(Commands.literal("mute")
                .requires(src -> has(src, "pixity.mute"))
                .then(Commands.argument("player", StringArgumentType.word())
                        .suggests(Suggest.playersOnline())
                        .then(Commands.argument("seconds", LongArgumentType.longArg(1))
                                .then(Commands.argument("reason", StringArgumentType.greedyString())
                                        .executes(ctx -> {

                                            ServerPlayer staff = ctx.getSource().getPlayer();
                                            if (staff == null) return 0;

                                            String name =
                                                    StringArgumentType.getString(ctx, "player");

                                            ServerPlayer target =
                                                    ctx.getSource().getServer()
                                                            .getPlayerList()
                                                            .getPlayerByName(name);

                                            if (target == null) return 0;

                                            long secs =
                                                    LongArgumentType.getLong(ctx, "seconds");

                                            String reason =
                                                    StringArgumentType.getString(ctx, "reason");

                                            mutes.tempMute(
                                                    target.getUUID(),
                                                    staff.getName().getString(),
                                                    secs * 1000L,
                                                    reason
                                            );

                                            punish.logCustom(
                                                    PunishAction.MUTE,
                                                    staff,
                                                    target.getUUID(),
                                                    target.getName().getString(),
                                                    secs,
                                                    reason
                                            );

                                            ctx.getSource().getServer()
                                                    .getPlayerList()
                                                    .broadcastSystemMessage(
                                                            LegacyAmpersand.parse(
                                                                    "&c&lPUNISHMENTS &7&l➤ &e" +
                                                                            staff.getName().getString() +
                                                                            " &chas muted &e" +
                                                                            target.getName().getString() +
                                                                            " &cfor &e" +
                                                                            reason +
                                                                            " &cfor &e" +
                                                                            secs + "s"
                                                            ),
                                                            false
                                                    );

                                            return 1;
                                        })
                                )
                        )
                )
        );

        d.register(Commands.literal("unmute")
                .requires(src -> has(src, "pixity.unmute"))
                .then(Commands.argument("player", StringArgumentType.word())
                        .suggests(Suggest.playersOnline())
                        .executes(ctx -> {

                            ServerPlayer staff = ctx.getSource().getPlayer();
                            if (staff == null) return 0;

                            String name =
                                    StringArgumentType.getString(ctx, "player");

                            ServerPlayer target =
                                    ctx.getSource().getServer()
                                            .getPlayerList()
                                            .getPlayerByName(name);

                            if (target == null) return 0;

                            mutes.unmute(
                                    target.getUUID(),
                                    staff.getName().getString(),
                                    "Unmuted"
                            );

                            punish.logCustom(
                                    PunishAction.UNMUTE,
                                    staff,
                                    target.getUUID(),
                                    target.getName().getString(),
                                    null,
                                    "Unmuted"
                            );

                            ctx.getSource().getServer()
                                    .getPlayerList()
                                    .broadcastSystemMessage(
                                            LegacyAmpersand.parse(
                                                    "&c&lPUNISHMENTS &7&l➤ &e" +
                                                            staff.getName().getString() +
                                                            " &chas unmuted &e" +
                                                            target.getName().getString()
                                            ),
                                            false
                                    );

                            return 1;
                        })
                )
        );





        d.register(Commands.literal("pkick")
                .requires(src -> has(src, "pixity.kick"))
                .then(Commands.argument("player", StringArgumentType.word())
                        .suggests(Suggest.playersOnline())
                        .then(Commands.argument("reason", StringArgumentType.greedyString())
                                .executes(ctx -> {

                                    MinecraftServer server =
                                            ctx.getSource().getServer();

                                    ServerPlayer staff =
                                            ctx.getSource().getPlayer();

                                    if (staff == null) return 0;

                                    String name =
                                            StringArgumentType.getString(ctx, "player");

                                    ServerPlayer target =
                                            server.getPlayerList()
                                                    .getPlayerByName(name);

                                    if (target == null) return 0;

                                    String reason =
                                            StringArgumentType.getString(ctx, "reason");

                                    punish.kick(
                                            server,
                                            staff,
                                            target,
                                            reason
                                    );

                                    return 1;
                                })
                        )
                )
        );





        d.register(Commands.literal("pban")
                .requires(src -> has(src, "pixity.ban"))
                .then(Commands.argument("player", StringArgumentType.word())
                        .suggests(Suggest.playersOnline())
                        .then(Commands.argument("reason", StringArgumentType.greedyString())
                                .executes(ctx -> {

                                    MinecraftServer server =
                                            ctx.getSource().getServer();

                                    ServerPlayer staff =
                                            ctx.getSource().getPlayer();

                                    if (staff == null) return 0;

                                    String name =
                                            StringArgumentType.getString(ctx, "player");

                                    UUID uuid =
                                            resolveUuid(server, name);

                                    if (uuid == null) return 0;

                                    String reason =
                                            StringArgumentType.getString(ctx, "reason");

                                    punish.ban(
                                            server,
                                            staff,
                                            uuid,
                                            name,
                                            null,
                                            reason
                                    );

                                    return 1;
                                })
                        )
                )
        );





        d.register(Commands.literal("tempban")
                .requires(src -> has(src, "pixity.tempban"))
                .then(Commands.argument("player", StringArgumentType.word())
                        .suggests(Suggest.playersOnline())
                        .then(Commands.argument("seconds", LongArgumentType.longArg(1))
                                .then(Commands.argument("reason", StringArgumentType.greedyString())
                                        .executes(ctx -> {

                                            MinecraftServer server =
                                                    ctx.getSource().getServer();

                                            ServerPlayer staff =
                                                    ctx.getSource().getPlayer();

                                            if (staff == null) return 0;

                                            String name =
                                                    StringArgumentType.getString(ctx, "player");

                                            long secs =
                                                    LongArgumentType.getLong(ctx, "seconds");

                                            UUID uuid =
                                                    resolveUuid(server, name);

                                            if (uuid == null) return 0;

                                            String reason =
                                                    StringArgumentType.getString(ctx, "reason");

                                            punish.ban(
                                                    server,
                                                    staff,
                                                    uuid,
                                                    name,
                                                    secs,
                                                    reason
                                            );

                                            return 1;
                                        })
                                )
                        )
                )
        );





        d.register(Commands.literal("ipban")
                .requires(src -> has(src, "pixity.ipban"))
                .then(Commands.argument("player", StringArgumentType.word())
                        .suggests(Suggest.playersOnline())
                        .then(Commands.argument("reason", StringArgumentType.greedyString())
                                .executes(ctx -> {

                                    ServerPlayer staff =
                                            ctx.getSource().getPlayer();

                                    if (staff == null) return 0;

                                    String name =
                                            StringArgumentType.getString(ctx, "player");

                                    ServerPlayer target =
                                            ctx.getSource()
                                                    .getServer()
                                                    .getPlayerList()
                                                    .getPlayerByName(name);

                                    if (target == null) return 0;

                                    String reason =
                                            StringArgumentType.getString(ctx, "reason");

                                    punish.ipBan(
                                            ctx.getSource().getServer(),
                                            staff,
                                            target,
                                            reason
                                    );

                                    return 1;
                                })
                        )
                )
        );





        d.register(Commands.literal("unban")
                .requires(src -> has(src, "pixity.unban"))
                .then(Commands.argument("player", StringArgumentType.word())
                        .executes(ctx -> {

                            MinecraftServer server =
                                    ctx.getSource().getServer();

                            ServerPlayer staff =
                                    ctx.getSource().getPlayer();

                            String name =
                                    StringArgumentType.getString(ctx, "player");

                            UUID uuid =
                                    resolveUuid(server, name);

                            if (uuid == null) {
                                ctx.getSource().sendFailure(
                                        LegacyAmpersand.parse("&c&lPUNISHMENTS &7&l➤ &cError! Player not found.")
                                );
                                return 0;
                            }

                            punish.unban(
                                    server,
                                    staff,
                                    uuid,
                                    name
                            );

                            ctx.getSource().sendSuccess(
                                    () -> LegacyAmpersand.parse("&c&lPUNISHMENTS &7&l➤ &aYou have succesfully unbanned &e" + name),
                                    false
                            );

                            return 1;
                        })
                )
        );





        d.register(Commands.literal("history")
                .requires(src -> has(src, "pixity.history"))
                .then(Commands.argument("player", StringArgumentType.word())
                        .executes(ctx -> {

                            MinecraftServer server =
                                    ctx.getSource().getServer();

                            String name =
                                    StringArgumentType.getString(ctx, "player");

                            UUID uuid =
                                    resolveUuid(server, name);

                            if (uuid == null) {
                                ctx.getSource().sendFailure(
                                        LegacyAmpersand.parse("&c&lPUNISHMENTS &7&l➤ &cError! Player not found.")
                                );
                                return 0;
                            }

                            List<PunishEntry> list =
                                    punish.historyFor(uuid);

                            ctx.getSource().sendSuccess(
                                    () -> LegacyAmpersand.parse(
                                            "&cHistory for &e" + name
                                    ),
                                    false
                            );

                            for (PunishEntry e : list) {

                                String staff =
                                        e.staffName == null ? "Console" : e.staffName;

                                String reason =
                                        e.reason == null ? "No reason" : e.reason;

                                String line;

                                switch (e.action) {

                                    case WARN -> line =
                                            "&9Warning - &9" + reason +
                                                    " &eby " + staff;

                                    case KICK -> line =
                                            "&aKick - &c" + reason +
                                                    " &eby " + staff;

                                    case BAN -> line =
                                            "&4Ban - &c" + reason +
                                                    " &eby " + staff;

                                    case IPBAN -> line =
                                            "&4IPBan - &c" + reason +
                                                    " &eby " + staff;

                                    case MUTE -> line =
                                            "&6Mute - &c" + reason +
                                                    " &eby " + staff;

                                    case UNMUTE -> line =
                                            "&6Unmute &eby " + staff;

                                    default -> {

                                        if (e.durationSeconds != null) {

                                            line =
                                                    "&cTempban - &c" + reason +
                                                            " &eby " + staff +
                                                            " &cfor &e" +
                                                            formatDuration(e.durationSeconds);

                                        } else {

                                            line =
                                                    "&7" + e.action +
                                                            " - " + reason +
                                                            " &eby " + staff;
                                        }
                                    }
                                }

                                ctx.getSource().sendSuccess(
                                        () -> LegacyAmpersand.parse(line),
                                        false
                                );
                            }

                            return 1;
                        })
                )
        );
    }

    private static UUID resolveUuid(
            final MinecraftServer server,
            final String name
    ) {


        ServerPlayer online =
                server.getPlayerList().getPlayerByName(name);

        if (online != null)
            return online.getUUID();


        try {
            var cache = server.getProfileCache();
            if (cache == null) return null;

            var opt = cache.get(name);

            if (opt.isPresent())
                return opt.get().getId();

        } catch (Throwable ignored) {}

        return null;
    }

    private static String formatDuration(long seconds) {

        long s = seconds % 60;
        long m = (seconds / 60) % 60;
        long h = (seconds / 3600) % 24;
        long d = seconds / 86400;

        if (d > 0) return d + "d " + h + "h";
        if (h > 0) return h + "h " + m + "m";
        if (m > 0) return m + "m " + s + "s";
        return s + "s";
    }
}