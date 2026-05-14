package org.howie.pixity.moderation.neoforge.joinleave;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

public final class JoinLeaveCommands {

    private final JoinLeaveService svc;
    private final RankService ranks;

    public static final String PERM_SET = "pixity.joinleave.set";
    public static final String PERM_SET_OTHERS = "pixity.joinleave.set.others";
    public static final String PERM_CLEAR = "pixity.joinleave.clear";
    public static final String PERM_RELOAD = "pixity.joinleave.reload";

    public JoinLeaveCommands(final JoinLeaveService svc, RankService ranks) {
        this.svc = svc;
        this.ranks = ranks;
    }

    private boolean has(ServerPlayer p, String perm) {
        return ranks != null && ranks.hasPerm(p, perm);
    }

    public void register(CommandDispatcher<CommandSourceStack> d) {

        d.register(Commands.literal("setjoinmsg")
                .requires(src -> src.getEntity() instanceof ServerPlayer p && has(p, PERM_SET))
                .then(Commands.argument("message", StringArgumentType.greedyString())
                        .requires(src -> src.getEntity() instanceof ServerPlayer p && has(p, PERM_SET))
                        .executes(ctx -> {
                            ServerPlayer p = ctx.getSource().getPlayerOrException();
                            svc.setJoin(p, StringArgumentType.getString(ctx, "message"));
                            p.sendSystemMessage(LegacyAmpersand.parse("&9&lCOSMETICS &7&l➤ &aSet your join message."));
                            return 1;
                        })
                )

                .then(Commands.argument("player", StringArgumentType.word())
                        .requires(src -> src.getEntity() instanceof ServerPlayer p && has(p, PERM_SET_OTHERS))
                        .then(Commands.argument("message", StringArgumentType.greedyString())
                                .requires(src -> src.getEntity() instanceof ServerPlayer p && has(p, PERM_SET_OTHERS))
                                .executes(ctx -> {

                                    ServerPlayer sender = ctx.getSource().getPlayerOrException();
                                    String name = StringArgumentType.getString(ctx, "player");
                                    String msg = StringArgumentType.getString(ctx, "message");

                                    ServerPlayer target =
                                            ctx.getSource().getServer()
                                                    .getPlayerList()
                                                    .getPlayerByName(name);

                                    if (target == null) {
                                        sender.sendSystemMessage(
                                                LegacyAmpersand.parse("&9&lCOSMETICS &7&l➤ &cError! Player must be online."));
                                        return 0;
                                    }

                                    svc.setJoin(target.getUUID(), msg);

                                    sender.sendSystemMessage(
                                            LegacyAmpersand.parse("&9&lCOSMETICS &7&l➤ &aSet join message for " + name));
                                    return 1;
                                })
                        )
                )
        );

        d.register(Commands.literal("setleavemsg")
                .requires(src -> src.getEntity() instanceof ServerPlayer p && has(p, PERM_SET))
                .then(Commands.argument("message", StringArgumentType.greedyString())
                        .requires(src -> src.getEntity() instanceof ServerPlayer p && has(p, PERM_SET))
                        .executes(ctx -> {
                            ServerPlayer p = ctx.getSource().getPlayerOrException();
                            svc.setLeave(p, StringArgumentType.getString(ctx, "message"));
                            p.sendSystemMessage(LegacyAmpersand.parse("&9&lCOSMETICS &7&l➤ &aSet your leave message."));
                            return 1;
                        })
                )

                .then(Commands.argument("player", StringArgumentType.word())
                        .then(Commands.argument("message", StringArgumentType.greedyString())
                                .requires(src -> src.getEntity() instanceof ServerPlayer p && has(p, PERM_SET_OTHERS))
                                .executes(ctx -> {

                                    ServerPlayer sender = ctx.getSource().getPlayerOrException();

                                    String name = StringArgumentType.getString(ctx, "player");
                                    String msg = StringArgumentType.getString(ctx, "message");

                                    ServerPlayer target =
                                            ctx.getSource().getServer()
                                                    .getPlayerList()
                                                    .getPlayerByName(name);

                                    if (target == null) {
                                        sender.sendSystemMessage(
                                                LegacyAmpersand.parse("&9&lCOSMETICS &7&l➤ &cError! Player must be online."));
                                        return 0;
                                    }

                                    svc.setLeave(target.getUUID(), msg);

                                    sender.sendSystemMessage(
                                            LegacyAmpersand.parse("&9&lCOSMETICS &7&l➤ &aSet leave message for &e" + name));
                                    return 1;
                                })
                        )
                )
        );

        d.register(Commands.literal("clearjoinmsg")
                .requires(src -> src.getEntity() instanceof ServerPlayer p && has(p, PERM_CLEAR))
                .executes(ctx -> {
                    ServerPlayer p = ctx.getSource().getPlayerOrException();
                    svc.clearJoin(p);
                    p.sendSystemMessage(LegacyAmpersand.parse("&9&lCOSMETICS &7&l➤ &aCleared your join message."));
                    return 1;
                })
        );

        d.register(Commands.literal("clearleavemsg")
                .requires(src -> src.getEntity() instanceof ServerPlayer p && has(p, PERM_CLEAR))
                .executes(ctx -> {
                    ServerPlayer p = ctx.getSource().getPlayerOrException();
                    svc.clearLeave(p);
                    p.sendSystemMessage(LegacyAmpersand.parse("&9&lCOSMETICS &7&l➤ &aCleared your leave message."));
                    return 1;
                })
        );

        d.register(Commands.literal("joinleavereload")
                .requires(src -> {
                    if (!(src.getEntity() instanceof ServerPlayer p)) return true;
                    return has(p, PERM_RELOAD);
                })
                .executes(ctx -> {
                    svc.reload();
                    ctx.getSource().sendSuccess(
                            () -> LegacyAmpersand.parse("&9&lCOSMETICS &7&l➤ Reloaded join/leave config."),
                            false);
                    return 1;
                })
        );
    }
}