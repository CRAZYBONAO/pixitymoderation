package org.howie.pixity.moderation.neoforge.tp;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.neoforge.rank.RankService;

public final class TpaCommands {


    private final TpaService tpa;
    private final RankService perms;
    private final TeleportWarmupManager warmup;

    public TpaCommands(TpaService tpa, TeleportWarmupManager warmup, RankService perms) {
        this.tpa = tpa;
        this.perms = perms;
        this.warmup = warmup;
    }

    private boolean has(ServerPlayer p, String perm) {
        return perms != null && (perms.hasPerm(p, perm) || perms.hasPerm(p, "pixity.admin"));
    }

    private ServerPlayer getPlayer(CommandSourceStack src, String name) {
        return src.getServer().getPlayerList().getPlayerByName(name);
    }

    public void register(CommandDispatcher<CommandSourceStack> d)  {


        d.register(Commands.literal("tpa")
                .requires(src -> src.getEntity() instanceof ServerPlayer p && has(p, TpaService.PERM_TPA))
                .then(Commands.argument("player", StringArgumentType.word())
                        .executes(ctx -> {
                            ServerPlayer from = ctx.getSource().getPlayer();
                            ServerPlayer to = getPlayer(ctx.getSource(),
                                    StringArgumentType.getString(ctx, "player"));

                            if (to != null) {
                                tpa.sendRequest(from.server, from, to, TpaRequest.Type.TO);
                            }
                            return 1;
                        })
                )
        );

        d.register(Commands.literal("tpahere")
                .requires(src -> src.getEntity() instanceof ServerPlayer p && has(p, TpaService.PERM_TPAHERE))
                .then(Commands.argument("player", StringArgumentType.word())
                        .executes(ctx -> {
                            ServerPlayer from = ctx.getSource().getPlayer();
                            ServerPlayer to = getPlayer(ctx.getSource(),
                                    StringArgumentType.getString(ctx, "player"));

                            if (to != null) {
                                tpa.sendRequest(from.server, from, to, TpaRequest.Type.HERE);
                            }
                            return 1;
                        })
                )
        );

        d.register(Commands.literal("tpaccept")
                .requires(src -> src.getEntity() instanceof ServerPlayer p && has(p, TpaService.PERM_TPACCEPT))
                .executes(ctx -> {
                    tpa.accept(ctx.getSource().getPlayer().server, ctx.getSource().getPlayer());
                    return 1;
                })
        );

        d.register(Commands.literal("tpdeny")
                .requires(src -> src.getEntity() instanceof ServerPlayer p && has(p, TpaService.PERM_TPDENY))
                .executes(ctx -> {
                    tpa.deny(ctx.getSource().getPlayer().server, ctx.getSource().getPlayer());
                    return 1;
                })
        );

        d.register(Commands.literal("tptoggle")
                .requires(src -> src.getEntity() instanceof ServerPlayer p && has(p, TpaService.PERM_TPTOGGLE))
                .executes(ctx -> {
                    tpa.toggleRequests(ctx.getSource().getPlayer());
                    return 1;
                })
        );

        d.register(Commands.literal("tpo")
                .requires(src -> src.getEntity() instanceof ServerPlayer p && has(p, TpaService.PERM_TPO))
                .then(Commands.argument("player", StringArgumentType.word())
                        .executes(ctx -> {
                            ServerPlayer actor = ctx.getSource().getPlayer();
                            ServerPlayer target = getPlayer(ctx.getSource(),
                                    StringArgumentType.getString(ctx, "player"));

                            if (target != null) {
                                tpa.forceTeleportTo(actor.server, actor, target);
                            }
                            return 1;
                        })
                )
        );

        d.register(Commands.literal("tpohere")
                .requires(src -> src.getEntity() instanceof ServerPlayer p && has(p, TpaService.PERM_TPOHERE))
                .then(Commands.argument("player", StringArgumentType.word())
                        .executes(ctx -> {
                            ServerPlayer actor = ctx.getSource().getPlayer();
                            ServerPlayer target = getPlayer(ctx.getSource(),
                                    StringArgumentType.getString(ctx, "player"));

                            if (target != null) {
                                tpa.forceTeleportHere(actor.server, actor, target);
                            }
                            return 1;
                        })
                )
        );
    }


}
