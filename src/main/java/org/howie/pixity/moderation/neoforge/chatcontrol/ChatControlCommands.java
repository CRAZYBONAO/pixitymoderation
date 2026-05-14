package org.howie.pixity.moderation.neoforge.chatcontrol;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.chat.NickHolder;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;
import org.howie.pixity.moderation.neoforge.tp.TpService;

public final class ChatControlCommands {

    private final ChatControlService chatCtl;
    private final TpService perms;

    public ChatControlCommands(ChatControlService chatCtl, TpService perms) {
        this.chatCtl = chatCtl;
        this.perms = perms;
    }

    public void register(CommandDispatcher<CommandSourceStack> d) {

        d.register(Commands.literal("chatmute")
                .requires(src -> src.hasPermission(2) ||
                        (src.getEntity() instanceof ServerPlayer p &&
                                perms.hasPerm(p, ChatControlService.PERM_CHATMUTE)))
                .then(Commands.literal("on").executes(ctx -> {
                    setMuted(ctx.getSource().getServer(), true);
                    return 1;
                }))
                .then(Commands.literal("off").executes(ctx -> {
                    setMuted(ctx.getSource().getServer(), false);
                    return 1;
                }))
                .executes(ctx -> {
                    setMuted(ctx.getSource().getServer(), !chatCtl.isChatMuted());
                    return 1;
                })
        );

        d.register(Commands.literal("slowchat")
                .requires(src -> src.hasPermission(2) ||
                        (src.getEntity() instanceof ServerPlayer p &&
                                perms.hasPerm(p, ChatControlService.PERM_SLOWCHAT)))
                .then(Commands.literal("off")
                        .executes(ctx -> {
                            setSlow(ctx.getSource().getServer(), 0);
                            return 1;
                        }))
                .then(Commands.argument("seconds", IntegerArgumentType.integer(1, 3600))
                        .executes(ctx -> {
                            int sec = IntegerArgumentType.getInteger(ctx, "seconds");
                            setSlow(ctx.getSource().getServer(), sec);
                            return 1;
                        })
                )
                .executes(ctx -> {
                    int sec = (chatCtl.config() != null)
                            ? chatCtl.config().defaultSlowchatSeconds
                            : 5;

                    setSlow(ctx.getSource().getServer(), sec);
                    return 1;
                })
        );

        d.register(Commands.literal("clearchat")
                .requires(src -> src.hasPermission(2) ||
                        (src.getEntity() instanceof ServerPlayer p &&
                                perms.hasPerm(p, ChatControlService.PERM_CLEARCHAT)))
                .executes(ctx -> {
                    MinecraftServer server = ctx.getSource().getServer();

                    for (ServerPlayer p : server.getPlayerList().getPlayers()) {
                        for (int i = 0; i < 120; i++)
                            p.sendSystemMessage(Component.literal(" "));

                        p.sendSystemMessage(
                                LegacyAmpersand.parse("&4&lSTAFF &7&l➤ &4&lCHAT HAS BEEN CLEARED BY" + NickHolder.INSTANCE.getDisplayName(p))
                        );
                    }

                    return 1;
                })
        );
    }

    private void setMuted(MinecraftServer server, boolean v) {
        chatCtl.setChatMuted(v);

        String msg = v
                ? "&4&lSTAFF &7&l➤ &cChat has been muted."
                : "&4&lSTAFF &7&l➤ &aChat has been unmuted.";

        broadcast(server, msg);
    }

    private void setSlow(MinecraftServer server, int sec) {
        chatCtl.setSlowchatSeconds(sec);

        String msg = (sec <= 0)
                ? "&4&lSTAFF &7&l➤ &aSlowchat disabled."
                : ("&4&lSTAFF &7&l➤ &eSlowchat enabled: &f" + sec + "&e seconds.");

        broadcast(server, msg);
    }

    private static void broadcast(MinecraftServer server, String msg) {
        Component c = LegacyAmpersand.parse(msg);
        for (ServerPlayer p : server.getPlayerList().getPlayers())
            p.sendSystemMessage(c);
    }
}