package org.howie.pixity.moderation.neoforge.punish.gui;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.chat.MuteManager;
import org.howie.pixity.moderation.neoforge.freeze.FreezeService;
import org.howie.pixity.moderation.neoforge.jail.JailService;
import org.howie.pixity.moderation.neoforge.punish.PunishmentManager;
import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;
import org.howie.pixity.moderation.neoforge.util.Suggest;

public final class PunishGuiCommands {

    public static final String PERM_PUNISH_GUI = "pixity.punish.gui";

    private final PunishmentManager punishments;
    private final MuteManager mutes;
    private final FreezeService freeze;
    private final JailService jail;
    private final RankService ranks;

    public PunishGuiCommands(final PunishmentManager punishments,
                            final MuteManager mutes,
                            final FreezeService freeze,
                            final JailService jail,
                            final RankService ranks) {
        this.punishments = punishments;
        this.mutes = mutes;
        this.freeze = freeze;
        this.jail = jail;
        this.ranks = ranks;
    }

    @SubscribeEvent
    public void register(CommandDispatcher<CommandSourceStack> d) {


        d.register(Commands.literal("punishreload")
                .requires(src -> src.hasPermission(2) || (src.getEntity() instanceof ServerPlayer p && hasPerm(p)))
                .executes(ctx -> {
                    PunishGuiConfigStore.reload();
                    ctx.getSource().sendSuccess(() -> LegacyAmpersand.parse("&4&lSTAFF &7&l➤ Punish GUI config reloaded."), true);
                    return 1;
                })
        );

        d.register(Commands.literal("punish")
                .requires(src -> src.hasPermission(2) || (src.getEntity() instanceof ServerPlayer p && hasPerm(p)))
                .then(Commands.argument("player", StringArgumentType.word()).suggests(Suggest.playersOnline())
                        .executes(ctx -> {
                            ServerPlayer viewer = ctx.getSource().getPlayer();
                            if (viewer == null) { ctx.getSource().sendFailure(Component.literal("Players only.")); return 0; }

                            String name = StringArgumentType.getString(ctx, "player");
                            ServerPlayer target = ctx.getSource().getServer().getPlayerList().getPlayerByName(name);
                            if (target == null) { viewer.sendSystemMessage(LegacyAmpersand.parse("&c&lPUNISHMENTS &7&l➤ &cError! Player not online: &e" + name)); return 0; }

                            PunishGui.open(viewer, target, punishments, mutes, freeze, jail, ranks);
                            return 1;
                        })
                )
        );
    }

    private boolean hasPerm(final ServerPlayer p) {
        if (p == null) return false;
        if (p.hasPermissions(2)) return true;
        if (ranks == null) return false;
        return ranks.hasPerm(p, PERM_PUNISH_GUI);
    }
}
