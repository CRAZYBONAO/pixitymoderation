package org.howie.pixity.moderation.neoforge.alts;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.Set;
import java.util.UUID;

import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;
import org.howie.pixity.moderation.neoforge.util.NameResolver;
import org.howie.pixity.moderation.neoforge.alts.smart.SmartAltService;

public final class AltsCommands {

    private final AltsService alts;
    private final SmartAltService smart;
    private final RankService ranks;

    public AltsCommands(final AltsService alts,
                        final SmartAltService smart,
                        final RankService ranks) {
        this.alts = alts;
        this.smart = smart;
        this.ranks = ranks;
    }

    private boolean has(ServerPlayer p, String perm) {
        return ranks.hasPerm(p, perm) || ranks.hasPerm(p, "pixity.admin");
    }

    public void register(CommandDispatcher<CommandSourceStack> d) {

        d.register(Commands.literal("alts")

                .requires(src -> {
                    if (!(src.getEntity() instanceof ServerPlayer p)) return false;
                    return src.hasPermission(2) || has(p, "pixity.alts");
                })


                .then(Commands.argument("player", StringArgumentType.word())
                        .executes(ctx -> {

                            MinecraftServer server = ctx.getSource().getServer();
                            ServerPlayer viewer = ctx.getSource().getPlayerOrException();

                            String name = StringArgumentType.getString(ctx, "player");

                            UUID targetId = NameResolver.uuid(server, name);
                            if (targetId == null) {
                                ctx.getSource().sendFailure(
                                        LegacyAmpersand.parse("&c&lALTS &7&l➤ &cPlayer not found.")
                                );
                                return 0;
                            }

                            ServerPlayer target =
                                    server.getPlayerList().getPlayer(targetId);


                            if (target != null) {

                                viewer.sendSystemMessage(
                                        LegacyAmpersand.parse(
                                                "&c&lALTS &7&l➤ &7Opening menu for &e" + name
                                        )
                                );

                                viewer.openMenu(
                                        AltsMenu.provider(
                                                server, null, alts, smart, viewer, target
                                        )
                                );

                                return 1;
                            }

                            Set<UUID> list = alts.altsOf(targetId);

                            ctx.getSource().sendSuccess(() ->
                                    LegacyAmpersand.parse(
                                            "&c&lALTS &7&l➤ &6Alts for &e" + name + "&6:"
                                    ), false);

                            if (list.isEmpty()) {
                                ctx.getSource().sendSuccess(() ->
                                        LegacyAmpersand.parse(
                                                "&c&lALTS &7&l➤ &7None"
                                        ), false);
                                return 1;
                            }

                            for (UUID u : list) {

                                String resolved =
                                        NameResolver.nameOrUuid(server, u);

                                final String user =
                                        (resolved != null) ? resolved : u.toString();

                                ctx.getSource().sendSuccess(() ->
                                                LegacyAmpersand.parse(
                                                        "&c&lALTS &7&l➤ &e- &f"
                                                                + user + " &7(" + u + ")"
                                                ),
                                        false
                                );
                            }

                            return 1;
                        })
                )

                .then(Commands.literal("link")
                        .then(Commands.argument("a", StringArgumentType.word())
                                .then(Commands.argument("b", StringArgumentType.word())
                                        .executes(ctx -> {

                                            MinecraftServer server =
                                                    ctx.getSource().getServer();

                                            UUID a = NameResolver.uuid(
                                                    server,
                                                    StringArgumentType.getString(ctx, "a")
                                            );

                                            UUID b = NameResolver.uuid(
                                                    server,
                                                    StringArgumentType.getString(ctx, "b")
                                            );

                                            if (a == null || b == null) {
                                                ctx.getSource().sendFailure(
                                                        LegacyAmpersand.parse(
                                                                "&c&lALTS &7&l➤ &cInvalid player(s)."
                                                        )
                                                );
                                                return 0;
                                            }

                                            ServerPlayer staff =
                                                    ctx.getSource().getPlayerOrException();

                                            alts.link(server, staff, a, b);

                                            ctx.getSource().sendSuccess(() ->
                                                    LegacyAmpersand.parse(
                                                            "&c&lALTS &7&l➤ &aLinked players."
                                                    ), false);

                                            return 1;
                                        })
                                )
                        )
                )

                .then(Commands.literal("unlink")
                        .then(Commands.argument("a", StringArgumentType.word())
                                .then(Commands.argument("b", StringArgumentType.word())
                                        .executes(ctx -> {

                                            MinecraftServer server =
                                                    ctx.getSource().getServer();

                                            UUID a = NameResolver.uuid(
                                                    server,
                                                    StringArgumentType.getString(ctx, "a")
                                            );

                                            UUID b = NameResolver.uuid(
                                                    server,
                                                    StringArgumentType.getString(ctx, "b")
                                            );

                                            if (a == null || b == null) {
                                                ctx.getSource().sendFailure(
                                                        LegacyAmpersand.parse(
                                                                "&c&lALTS &7&l➤ &cInvalid player(s)."
                                                        )
                                                );
                                                return 0;
                                            }

                                            ServerPlayer staff =
                                                    ctx.getSource().getPlayerOrException();

                                            alts.unlink(server, staff, a, b);

                                            ctx.getSource().sendSuccess(() ->
                                                    LegacyAmpersand.parse(
                                                            "&c&lALTS &7&l➤ &eUnlinked players."
                                                    ), false);

                                            return 1;
                                        })
                                )
                        )
                )
        );
    }
}