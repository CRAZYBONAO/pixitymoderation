package org.howie.pixity.moderation.neoforge.notes;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;
import org.howie.pixity.moderation.neoforge.util.Suggest;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public final class NotesCommands {

    private static final DateTimeFormatter TS =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                    .withZone(ZoneId.systemDefault());

    private final NotesService notes;
    private final RankService ranks;

    public NotesCommands(final NotesService notes, final RankService ranks) {
        this.notes = notes;
        this.ranks = ranks;
    }

    private boolean has(ServerPlayer p, String perm) {
        return ranks.hasPerm(p, perm) || ranks.hasPerm(p, "pixity.admin");
    }

    public void register(CommandDispatcher<CommandSourceStack> d) {


        d.register(Commands.literal("note")

                .requires(cs -> {
                    if (!(cs.getEntity() instanceof ServerPlayer p)) return false;
                    return cs.hasPermission(2) || has(p, NotesService.PERM_NOTE_ADD);
                })

                .then(Commands.argument("player", StringArgumentType.word())
                        .suggests(Suggest.playersOnline())

                        .then(Commands.argument("text", StringArgumentType.greedyString())
                                .executes(ctx -> {

                                    MinecraftServer server = ctx.getSource().getServer();
                                    ServerPlayer staff = ctx.getSource().getPlayerOrException();

                                    String name = StringArgumentType.getString(ctx, "player");
                                    UUID uuid = resolveUuid(server, name);

                                    if (uuid == null) {
                                        ctx.getSource().sendFailure(LegacyAmpersand.parse("&e&lNOTES &7&l➤ &cError! Unknown player: " + name));
                                        return 0;
                                    }

                                    String text = StringArgumentType.getString(ctx, "text");
                                    NoteEntry n = notes.add(uuid, staff, text);

                                    ctx.getSource().sendSuccess(() ->
                                                    LegacyAmpersand.parse("&e&lNOTES &7&l➤ &aNote added to &e" + name + "&a (#" + n.id + ")."),
                                            false
                                    );

                                    return 1;
                                })
                        )
                )

                .then(Commands.literal("remove")

                        .requires(cs -> {
                            if (!(cs.getEntity() instanceof ServerPlayer p)) return false;
                            return cs.hasPermission(2) || has(p, NotesService.PERM_NOTE_REMOVE);
                        })

                        .then(Commands.argument("player", StringArgumentType.word())
                                .suggests(Suggest.playersOnline())

                                .then(Commands.argument("id", StringArgumentType.word())
                                        .executes(ctx -> {

                                            MinecraftServer server = ctx.getSource().getServer();
                                            ServerPlayer staff = ctx.getSource().getPlayerOrException();

                                            String name = StringArgumentType.getString(ctx, "player");
                                            UUID uuid = resolveUuid(server, name);

                                            if (uuid == null) {
                                                ctx.getSource().sendFailure(LegacyAmpersand.parse("&e&lNOTES &7&l➤ &cError! Unknown player: " + name));
                                                return 0;
                                            }

                                            String id = StringArgumentType.getString(ctx, "id");
                                            boolean ok = notes.remove(uuid, id);

                                            if (ok) {
                                                ctx.getSource().sendSuccess(() ->
                                                                LegacyAmpersand.parse("&e&lNOTES &7&l➤ &aRemoved note &e#" + id + " &afrom &e" + name + "&c."),
                                                        false
                                                );
                                            } else {
                                                ctx.getSource().sendFailure(
                                                        LegacyAmpersand.parse("&e&lNOTES &7&l➤ &cError! No note &e#" + id + " &cfound for &e" + name + "&c.")
                                                );
                                            }

                                            return ok ? 1 : 0;
                                        })
                                )
                        )
                )
        );



        d.register(Commands.literal("notes")

                .requires(cs -> {
                    if (!(cs.getEntity() instanceof ServerPlayer p)) return false;
                    return cs.hasPermission(2) || has(p, NotesService.PERM_NOTE_VIEW);
                })

                .then(Commands.argument("player", StringArgumentType.word())
                        .suggests(Suggest.playersOnline())

                        .executes(ctx -> {

                            MinecraftServer server = ctx.getSource().getServer();
                            ServerPlayer viewer = ctx.getSource().getPlayerOrException();

                            String name = StringArgumentType.getString(ctx, "player");
                            UUID uuid = resolveUuid(server, name);

                            if (uuid == null) {
                                ctx.getSource().sendFailure(LegacyAmpersand.parse("&c&lSTAFF &7&l➤ Unknown player: " + name));
                                return 0;
                            }

                            List<NoteEntry> list = notes.list(uuid);

                            if (list.isEmpty()) {
                                viewer.sendSystemMessage(LegacyAmpersand.parse("&c&lSTAFF &7&l➤ No notes for " + name + "."));
                                return 1;
                            }

                            viewer.sendSystemMessage(LegacyAmpersand.parse("&c&lSTAFF &7&l➤ Notes for " + name + ":"));

                            int shown = 0;
                            for (NoteEntry n : list) {
                                if (shown++ >= 20) break;

                                String when = TS.format(Instant.ofEpochMilli(n.ts));

                                viewer.sendSystemMessage(LegacyAmpersand.parse(
                                        "&a#" + n.id + " &7[" + when + "&7] &e" + n.staffName + "&c: &e" + n.text
                                ));
                            }

                            return 1;
                        })
                )
        );
    }

    private static UUID resolveUuid(final MinecraftServer server, final String name) {

        ServerPlayer online = server.getPlayerList().getPlayerByName(name);
        if (online != null) return online.getUUID();

        try {
            var cache = server.getProfileCache();
            if (cache != null) {
                var opt = cache.get(name);
                if (opt.isPresent()) return opt.get().getId();
            }
        } catch (Throwable ignored) {}

        return null;
    }
}