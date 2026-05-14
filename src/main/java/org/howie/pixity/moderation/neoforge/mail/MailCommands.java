package org.howie.pixity.moderation.neoforge.mail;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;
import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.util.Suggest;

import java.text.SimpleDateFormat;
import java.util.*;

public final class MailCommands {

    public static final String PERM_SEND = "pixity.mail.send";
    public static final String PERM_READ = "pixity.mail.read";

    private final MailService mail;
    private final RankService ranks;

    public MailCommands(final MailService mail, final RankService ranks) {
        this.mail = mail;
        this.ranks = ranks;
    }

    private boolean has(ServerPlayer p, String perm) {
        return ranks != null && ranks.hasPerm(p, perm);
    }

    public void register(CommandDispatcher<CommandSourceStack> d) {

        d.register(Commands.literal("mail")

                .then(Commands.literal("send")
                        .requires(src -> src.getEntity() instanceof ServerPlayer p && has(p, PERM_SEND))

                        .then(Commands.argument("player", StringArgumentType.word())
                                .then(Commands.argument("message", StringArgumentType.greedyString())
                                        .executes(ctx -> {

                                            ServerPlayer from = ctx.getSource().getPlayer();
                                            MinecraftServer server = ctx.getSource().getServer();

                                            String name = StringArgumentType.getString(ctx, "player");
                                            String message = StringArgumentType.getString(ctx, "message");

                                            UUID uuid = null;
                                            String realName = name;


                                            ServerPlayer online =
                                                    server.getPlayerList().getPlayerByName(name);

                                            if (online != null) {
                                                uuid = online.getUUID();
                                                realName = online.getGameProfile().getName();
                                            }
                                            else {


                                                var cache = server.getProfileCache();
                                                var opt = cache.get(name);

                                                if (opt.isPresent()) {
                                                    var profile = opt.get();
                                                    uuid = profile.getId();
                                                    realName = profile.getName();
                                                }
                                            }


                                            if (uuid == null) {
                                                ctx.getSource().sendFailure(
                                                        LegacyAmpersand.parse(
                                                                "&9&lMAIL &7&l➤ &cPlayer has never joined."
                                                        )
                                                );
                                                return 0;
                                            }


                                            mail.send(from, uuid, realName, message);

                                            ctx.getSource().sendSystemMessage(
                                                    LegacyAmpersand.parse(
                                                            "&9&lMAIL &7&l➤ &aMail sent to &f" + realName
                                                    )
                                            );

                                            return 1;
                                        })
                                )
                        )
                )

                .then(Commands.literal("delete")
                        .requires(src -> src.getEntity() instanceof ServerPlayer p && has(p, PERM_READ))
                        .then(Commands.argument("id", StringArgumentType.word())
                                .executes(ctx -> {

                                    ServerPlayer p = ctx.getSource().getPlayer();
                                    String id = StringArgumentType.getString(ctx, "id");

                                    boolean removed = mail.delete(p.getUUID(), id);

                                    if (!removed) {
                                        p.sendSystemMessage(
                                                LegacyAmpersand.parse(
                                                        "&9&lMAIL &7&l➤ &cMail not found."
                                                )
                                        );
                                        return 0;
                                    }

                                    p.sendSystemMessage(
                                            LegacyAmpersand.parse(
                                                    "&9&lMAIL &7&l➤ &aDeleted mail &e" + id
                                            )
                                    );

                                    return 1;
                                })
                        )
                )

                .then(Commands.literal("inbox")
                        .requires(src -> src.getEntity() instanceof ServerPlayer p && has(p, PERM_READ))
                        .executes(ctx -> inbox(ctx.getSource(), 1))
                        .then(Commands.argument("page", IntegerArgumentType.integer(1))
                                .executes(ctx -> inbox(ctx.getSource(),
                                        IntegerArgumentType.getInteger(ctx, "page"))))
                )

                .then(Commands.literal("read")
                        .requires(src -> src.getEntity() instanceof ServerPlayer p && has(p, PERM_READ))
                        .then(Commands.argument("id", StringArgumentType.word())
                                .executes(ctx -> {
                                    ServerPlayer p = ctx.getSource().getPlayer();
                                    String id = StringArgumentType.getString(ctx, "id");

                                    MailMessage m = mail.get(p.getUUID(), id);
                                    if (m == null) return 0;

                                    mail.markRead(p.getUUID(), id);

                                    ctx.getSource().sendSystemMessage(LegacyAmpersand.parse(""));
                                    ctx.getSource().sendSystemMessage(
                                            LegacyAmpersand.parse("&6&lMail (" + m.id + ")"));
                                    ctx.getSource().sendSystemMessage(
                                            LegacyAmpersand.parse("&7From: &f" + m.fromName));
                                    ctx.getSource().sendSystemMessage(
                                            LegacyAmpersand.parse("&7Date: &f" + fmt(m.ts)));
                                    ctx.getSource().sendSystemMessage(LegacyAmpersand.parse(""));
                                    ctx.getSource().sendSystemMessage(
                                            LegacyAmpersand.parse("&f" + m.message));
                                    ctx.getSource().sendSystemMessage(LegacyAmpersand.parse(""));
                                    return 1;
                                })
                        )
                )

                .then(Commands.literal("clear")
                        .executes(ctx -> {
                            ServerPlayer p = ctx.getSource().getPlayer();
                            int count = mail.clear(p.getUUID());
                            p.sendSystemMessage(
                                    LegacyAmpersand.parse("&9&lMAIL &7&l➤ &aCleared &e" + count + "&a messages."));
                            return 1;
                        })
                )

                .then(Commands.literal("unread")
                        .executes(ctx -> {
                            ServerPlayer p = ctx.getSource().getPlayer();
                            int count = mail.unreadCount(p.getUUID());
                            p.sendSystemMessage(
                                    LegacyAmpersand.parse("&9&lMAIL &7&l➤ Unread: " + count));
                            return 1;
                        })
                )
        );
    }

    private int inbox(final CommandSourceStack src, final int page) {
        ServerPlayer p = src.getPlayer();
        List<MailMessage> list = mail.inbox(p.getUUID());

        src.sendSystemMessage(LegacyAmpersand.parse(""));
        src.sendSystemMessage(LegacyAmpersand.parse("&6&lInbox"));

        for (MailMessage m : list) {
            MutableComponent line = Component.literal("");

            line.append(LegacyAmpersand.parse("&e" + m.id + " &7from "))
                    .append(LegacyAmpersand.parse(m.fromName))
                    .append(LegacyAmpersand.parse(" &8- &7" + m.message));

            line.withStyle(style -> style
                    .withClickEvent(new ClickEvent(
                            ClickEvent.Action.RUN_COMMAND,
                            "/mail read " + m.id
                    ))
                    .withHoverEvent(new HoverEvent(
                            HoverEvent.Action.SHOW_TEXT,
                            LegacyAmpersand.parse("&9&lMAIL &7&l➤ &aClick to read")
                    ))
            );

            src.sendSystemMessage(line);
        }

        src.sendSystemMessage(LegacyAmpersand.parse(""));
        return 1;
    }

    private static String fmt(long ts) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(ts));
    }
}