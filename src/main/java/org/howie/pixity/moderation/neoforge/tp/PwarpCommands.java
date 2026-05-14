package org.howie.pixity.moderation.neoforge.tp;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

public final class PwarpCommands {


    private final TpService tp;
    private final RankService perms;

    public PwarpCommands(TpService tp, RankService perms) {
        this.tp = tp;
        this.perms = perms;
    }

    private boolean has(ServerPlayer p, String perm) {
        return perms != null && (perms.hasPerm(p, perm) || perms.hasPerm(p, "pixity.admin"));
    }

    private ServerPlayer getPlayer(CommandSourceStack src, String name) {
        return src.getServer().getPlayerList().getPlayerByName(name);
    }

    @SubscribeEvent
    public void onRegister(RegisterCommandsEvent e) {

        CommandDispatcher<CommandSourceStack> d = e.getDispatcher();

        d.register(Commands.literal("pwarp")
                .requires(src -> src.getEntity() instanceof ServerPlayer p && has(p, TpService.PERM_PWARP))
                .then(Commands.argument("name", StringArgumentType.word())
                        .executes(ctx -> {
                            String name = StringArgumentType.getString(ctx, "name");
                            ServerPlayer p = ctx.getSource().getPlayer();
                            tp.teleportPlayerWarp(p.server, p,
                                    StringArgumentType.getString(ctx, "name"));
                            p.sendSystemMessage(LegacyAmpersand.parse("&d&lPLAYER WARPS &7&l➤ &aYou have been warped to &e " + name));
                            return 1;
                        })
                )
        );

        d.register(Commands.literal("setpwarp")
                .requires(src -> src.getEntity() instanceof ServerPlayer p && has(p, TpService.PERM_SETPWARP))
                .then(Commands.argument("name", StringArgumentType.word())
                        .executes(ctx -> {
                            String name = StringArgumentType.getString(ctx, "name");
                            ServerPlayer p = ctx.getSource().getPlayer();
                            tp.setPlayerWarp(ctx.getSource().getPlayer(),
                                    StringArgumentType.getString(ctx, "name"));
                            p.sendSystemMessage(LegacyAmpersand.parse("&d&lPLAYER WARPS &7&l➤ &aPlayer warp &e" + name + " &ahas been created."));
                            return 1;
                        })
                )
        );

        d.register(Commands.literal("delpwarp")
                .requires(src -> src.getEntity() instanceof ServerPlayer p && has(p, TpService.PERM_DELPWARP))
                .then(Commands.argument("name", StringArgumentType.word())
                        .executes(ctx -> {
                            String name = StringArgumentType.getString(ctx, "name");
                            ServerPlayer p = ctx.getSource().getPlayer();
                            tp.delPlayerWarp(ctx.getSource().getPlayer(),
                                    StringArgumentType.getString(ctx, "name"));
                            p.sendSystemMessage(LegacyAmpersand.parse("&d&lPLAYER WARPS &7&l➤ &cPlayer warp &e" + name + " &chas been deleted."));
                            return 1;
                        })
                )
        );
    }


}
