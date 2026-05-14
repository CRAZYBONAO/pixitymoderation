package org.howie.pixity.moderation.neoforge.announce;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;
import org.howie.pixity.moderation.neoforge.tp.TpService;

public final class AnnouncementsCommands {

    public interface Reloader { void reload(); }

    private final AnnouncementsService svc;
    private final TpService perms;
    private final Reloader reloader;

    public AnnouncementsCommands(
            AnnouncementsService svc,
            TpService perms,
            Reloader reloader
    ) {
        this.svc = svc;
        this.perms = perms;
        this.reloader = reloader;
    }

    public void register(CommandDispatcher<CommandSourceStack> d) {

        d.register(Commands.literal("announcereload")
                .requires(src -> src.hasPermission(2) ||
                        (src.getEntity() instanceof ServerPlayer p &&
                                perms.hasPerm(p, "pixity.announce.reload")))
                .executes(ctx -> {
                    if (reloader != null) {
                        reloader.reload();
                        ctx.getSource().sendSuccess(
                                () -> LegacyAmpersand.parse("&6&lANNOUNCEMENTS &7&l➤ &aAnnouncements reloaded."),
                                false
                        );
                        return 1;
                    }
                    return 0;
                })
        );

        var base = Commands.literal("announce")
                .requires(src -> src.hasPermission(2) ||
                        (src.getEntity() instanceof ServerPlayer p &&
                                perms.hasPerm(p, AnnouncementsService.PERM_ANNOUNCE)));

        base.then(Commands.argument("message", StringArgumentType.greedyString())
                .executes(ctx -> {
                    svc.broadcastNow(
                            ctx.getSource().getServer(),
                            StringArgumentType.getString(ctx, "message")
                    );
                    return 1;
                }));

        base.then(Commands.literal("title")
                .then(Commands.argument("message", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            svc.config().mode = "TITLE";
                            svc.broadcastNow(
                                    ctx.getSource().getServer(),
                                    StringArgumentType.getString(ctx, "message")
                            );
                            return 1;
                        }))
        );

        base.then(Commands.literal("actionbar")
                .then(Commands.argument("message", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            svc.config().mode = "ACTIONBAR";
                            svc.broadcastNow(
                                    ctx.getSource().getServer(),
                                    StringArgumentType.getString(ctx, "message")
                            );
                            return 1;
                        }))
        );

        d.register(base);
    }
}