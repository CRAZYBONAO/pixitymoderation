package org.howie.pixity.moderation.neoforge.msg;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import org.apache.logging.log4j.Logger;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;
import org.howie.pixity.moderation.neoforge.util.Suggest;

import java.util.UUID;

public final class MsgCommands {

    public static final String PERM_SEND   = "pixity.msg.send";
    public static final String PERM_REPLY  = "pixity.msg.reply";
    public static final String PERM_IGNORE = "pixity.msg.ignore";
    public static final String PERM_SPY    = "pixity.msg.socialspy";

    private final Logger logger;
    private final MsgService msg;

    public MsgCommands(Logger logger, MsgService msg) {
        this.logger = logger;
        this.msg = msg;
    }

    private boolean has(ServerPlayer p, String perm) {
        try {
            var lp = net.luckperms.api.LuckPermsProvider.get();

            var user = lp.getUserManager().getUser(p.getUUID());
            if (user == null) {
                user = lp.getUserManager().loadUser(p.getUUID()).join();
            }

            if (user == null) return false;

            var contextManager = lp.getContextManager();

            var optionsOpt = contextManager.getQueryOptions(user);

            net.luckperms.api.query.QueryOptions options =
                    optionsOpt.orElse(contextManager.getStaticQueryOptions());

            return user.getCachedData()
                    .getPermissionData(options)
                    .checkPermission(perm)
                    .asBoolean();

        } catch (Throwable ignored) {}

        return false;
    }

    public void register(CommandDispatcher<CommandSourceStack> d) {


        var msgRoot = d.register(Commands.literal("msg")
                .requires(src ->
                        src.getEntity() instanceof ServerPlayer p &&
                                has(p, PERM_SEND)
                )
                .then(Commands.argument("player", StringArgumentType.word())
                        .suggests(Suggest.playersOnline())
                        .then(Commands.argument("message", StringArgumentType.greedyString())
                                .executes(ctx -> {

                                    ServerPlayer from = ctx.getSource().getPlayer();
                                    if (from == null) return 0;

                                    String name = StringArgumentType.getString(ctx, "player");
                                    String text = StringArgumentType.getString(ctx, "message");

                                    ServerPlayer to =
                                            ctx.getSource().getServer()
                                                    .getPlayerList()
                                                    .getPlayerByName(name);

                                    if (to == null) {
                                        from.sendSystemMessage(
                                                LegacyAmpersand.parse("&e&lMESSAGES &7&l➤ &cError! That player is not online: " + name));
                                        return 0;
                                    }

                                    return msg.sendPrivate(
                                            ctx.getSource().getServer(),
                                            from,
                                            to,
                                            text
                                    ) ? 1 : 0;
                                })
                        )
                )
        );

        d.register(Commands.literal("pmsg").redirect(msgRoot));
        d.register(Commands.literal("tell").redirect(msgRoot));
        d.register(Commands.literal("w").redirect(msgRoot));
        d.register(Commands.literal("dm").redirect(msgRoot));

        var replyRoot = d.register(Commands.literal("r")
                .requires(src ->
                        src.getEntity() instanceof ServerPlayer p &&
                                has(p, PERM_REPLY)
                )
                .then(Commands.argument("message", StringArgumentType.greedyString())
                        .executes(ctx -> reply(
                                ctx.getSource(),
                                StringArgumentType.getString(ctx, "message")
                        )))
        );

        d.register(Commands.literal("reply").redirect(replyRoot));


        d.register(Commands.literal("ignore")
                .requires(src ->
                        src.getEntity() instanceof ServerPlayer p &&
                                has(p, PERM_IGNORE)
                )
                .then(Commands.argument("player", StringArgumentType.word())
                        .suggests(Suggest.playersOnline())
                        .executes(ctx -> {

                            ServerPlayer p = ctx.getSource().getPlayer();
                            if (p == null) return 0;

                            String name = StringArgumentType.getString(ctx, "player");

                            ServerPlayer target =
                                    ctx.getSource()
                                            .getServer()
                                            .getPlayerList()
                                            .getPlayerByName(name);

                            if (target == null) {
                                p.sendSystemMessage(LegacyAmpersand.parse("&e&lMESSAGES &7&l➤ &cError! That player is not online: " + name));
                                return 0;
                            }

                            boolean now = msg.toggleIgnore(p.getUUID(), target.getUUID());

                            p.sendSystemMessage(LegacyAmpersand.parse(
                                    (now ? "&e&lMESSAGES &7&l➤ &aNow ignoring &c" : "&e&lMESSAGES &7&l➤ &aNo longer ignoring &c") + name
                            ));

                            return 1;
                        })
                )
        );


        d.register(Commands.literal("socialspy")
                .requires(src ->
                        src.getEntity() instanceof ServerPlayer p &&
                                has(p, PERM_SPY)
                )
                .executes(ctx -> {

                    ServerPlayer p = ctx.getSource().getPlayer();
                    if (p == null) return 0;

                    boolean on = msg.toggleSpy(p.getUUID());

                    p.sendSystemMessage(LegacyAmpersand.parse(
                            "&e&lMESSAGES &7&l➤ &eSocialSpy: " + (on ? "&aON" : "&cOFF")
                    ));

                    return 1;
                })
        );
    }

    private int reply(CommandSourceStack src, String text) {

        ServerPlayer from = src.getPlayer();
        if (from == null) return 0;

        UUID targetId = msg.getReplyTarget(from.getUUID());

        if (targetId == null) {
            from.sendSystemMessage(LegacyAmpersand.parse("&e&lMESSAGES &7&l➤ &cError! Nobody to reply to."));
            return 0;
        }

        ServerPlayer to =
                src.getServer()
                        .getPlayerList()
                        .getPlayer(targetId);

        if (to == null) {
            from.sendSystemMessage(LegacyAmpersand.parse("&e&lMESSAGES &7&l➤ &cError! That player is not online."));
            return 0;
        }

        return msg.sendPrivate(
                src.getServer(),
                from,
                to,
                text
        ) ? 1 : 0;
    }
}