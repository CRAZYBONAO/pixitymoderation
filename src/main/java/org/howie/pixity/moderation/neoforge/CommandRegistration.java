package org.howie.pixity.moderation.neoforge;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.chat.ChatConfigManager;
import org.howie.pixity.moderation.chat.MuteManager;
import org.howie.pixity.moderation.chat.NickManager;
import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

import java.util.UUID;

public final class CommandRegistration {

    private final ChatConfigManager chatCfg;
    private final NickManager nicks;
    private final MuteManager mutes;
    private final RankService ranks;

    public CommandRegistration(ChatConfigManager chatCfg, NickManager nicks, MuteManager mutes, RankService ranks) {
        this.chatCfg = chatCfg;
        this.nicks = nicks;
        this.mutes = mutes;
        this.ranks = ranks;
    }

    @SubscribeEvent
    public void onRegister(final RegisterCommandsEvent event) {
        final CommandDispatcher<CommandSourceStack> d = event.getDispatcher();

        d.register(Commands.literal("pixityreloadchat")
            .requires(cs -> cs.hasPermission(2))
            .executes(ctx -> {
                chatCfg.reload();
                ctx.getSource().sendSuccess(() -> LegacyAmpersand.parse("&c&lADMIN &7&l➤ &aReloaded chat.json"), false);
                return 1;
            })
        );

        d.register(Commands.literal("nick")
            .requires(cs -> true)
            .then(Commands.argument("nickname", StringArgumentType.greedyString())
                .executes(ctx -> {
                    ServerPlayer p = ctx.getSource().getPlayer();
                    if (p == null) return 0;
                    String nn = StringArgumentType.getString(ctx, "nickname");
                    nicks.setNick(p.getUUID(), nn);
                    ctx.getSource().sendSuccess(() -> LegacyAmpersand.parse("&e&lNICKNAME &7&l➤ &eSet nickname to: " + nn), false);
                    return 1;
                })
            )
            .executes(ctx -> {
                ServerPlayer p = ctx.getSource().getPlayer();
                if (p == null) return 0;
                nicks.clearNick(p.getUUID());
                ctx.getSource().sendSuccess(() -> LegacyAmpersand.parse("&e&lNICKNAME &7&l➤ &eCleared nickname."), false);
                return 1;
            })
        );

        d.register(Commands.literal("mute")
            .requires(cs -> cs.hasPermission(2))
            .then(Commands.argument("player", StringArgumentType.word())
                .then(Commands.argument("reason", StringArgumentType.greedyString())
                    .executes(ctx -> {
                        String name = StringArgumentType.getString(ctx, "player");
                        String reason = StringArgumentType.getString(ctx, "reason");
                        return muteByName(ctx.getSource(), name, reason);

                    })
                )
                .executes(ctx -> {
                    String name = StringArgumentType.getString(ctx, "player");
                    return muteByName(ctx.getSource(), name, "");
                })
            )
        );

        d.register(Commands.literal("tempmute")
            .requires(cs -> cs.hasPermission(2))
            .then(Commands.argument("player", StringArgumentType.word())
                .then(Commands.argument("seconds", LongArgumentType.longArg(1))
                    .then(Commands.argument("reason", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            String name = StringArgumentType.getString(ctx, "player");
                            long seconds = LongArgumentType.getLong(ctx, "seconds");
                            String reason = StringArgumentType.getString(ctx, "reason");
                            return tempMuteByName(ctx.getSource(), name, seconds, reason);
                        })

                    )
                    .executes(ctx -> {
                        String name = StringArgumentType.getString(ctx, "player");
                        long seconds = LongArgumentType.getLong(ctx, "seconds");
                        return tempMuteByName(ctx.getSource(), name, seconds, "");
                    })
                )
            )
        );

        d.register(Commands.literal("unmute")
            .requires(cs -> cs.hasPermission(2))
            .then(Commands.argument("player", StringArgumentType.word())
                .executes(ctx -> {
                    String name = StringArgumentType.getString(ctx, "player");
                    ServerPlayer target = ctx.getSource().getServer().getPlayerList().getPlayerByName(name);
                    if (target == null) {
                        ctx.getSource().sendFailure(LegacyAmpersand.parse("&4&lPUNISHMENTS &7&l➤ &cError! Player not online: " + name));
                        return 0;
                    }
                    boolean ok = mutes.unmute(target.getUUID());
                    ctx.getSource().sendSuccess(() -> LegacyAmpersand.parse(ok ? "&4&lPUNISHMENTS &7&l➤ &aUnmuted &e" + name : "&4&lPUNISHMENTS &7&l➤ &e" + name + "&c was not muted."), false);
                    if (ok) target.sendSystemMessage(LegacyAmpersand.parse("&4&lPUNISHMENTS &7&l➤ &aYou have been unmuted."));
                    return 1;
                })
            )
        );
    }

    private int muteByName(CommandSourceStack src, String name, String reason) {
        ServerPlayer target = src.getServer().getPlayerList().getPlayerByName(name);
        if (target == null) {
            src.sendFailure(LegacyAmpersand.parse("&4&lPUNISHMENTS &7&l➤ &cError! Player not online: " + name));
            return 0;
        }
        mutes.mute(target.getUUID(), src.getTextName(), reason);
        src.sendSuccess(() -> LegacyAmpersand.parse("&4&lPUNISHMENTS &7&l➤ &aMuted &e" + name + (reason.isBlank() ? "" : " (" + reason + ")")), false);
        target.sendSystemMessage(LegacyAmpersand.parse("&4&lPUNISHMENTS &7&l➤ &cYou have been muted."));
        return 1;
    }

    private int tempMuteByName(CommandSourceStack src, String name, long seconds, String reason) {
        ServerPlayer target = src.getServer().getPlayerList().getPlayerByName(name);
        if (target == null) {
            src.sendFailure(LegacyAmpersand.parse("&4&lPUNISHMENTS &7&l➤ &cError! Player not online: " + name));
            return 0;
        }
        long ms = seconds * 1000L;
        mutes.tempMute(target.getUUID(), src.getTextName(), ms, reason);
        src.sendSuccess(() -> LegacyAmpersand.parse("&4&lPUNISHMENTS &7&l➤ &aTemporarily muted &e" + name + " &afor &e" + seconds + "s"), false);
        target.sendSystemMessage(LegacyAmpersand.parse("&4&lPUNISHMENTS &7&l➤ &cYou have been temporarily muted (&e" + seconds + "s&c)."));
        return 1;
    }
}
