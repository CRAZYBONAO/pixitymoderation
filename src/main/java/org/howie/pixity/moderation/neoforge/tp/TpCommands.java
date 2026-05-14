package org.howie.pixity.moderation.neoforge.tp;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

public final class TpCommands {


    private final TpService tp;
    private final RankService perms;
    private final TeleportWarmupManager warmup;

    public TpCommands(TpService tp, TeleportWarmupManager warmup, RankService perms) {
        this.tp = tp;
        this.perms = perms;
        this.warmup = warmup;
    }

    private boolean has(ServerPlayer p, String perm) {
        return perms != null && (perms.hasPerm(p, perm) || perms.hasPerm(p, "pixity.admin"));
    }

    private boolean req(CommandSourceStack src, String perm) {
        return src.getEntity() instanceof ServerPlayer p && has(p, perm);
    }

    public void register(CommandDispatcher<CommandSourceStack> d) {

        d.register(Commands.literal("spawn")
                .requires(src -> req(src, TpService.PERM_SPAWN))
                .executes(ctx -> {
                    ServerPlayer p = ctx.getSource().getPlayer();

                    WarpPos pos = tp.getSpawnPos();
                    if (pos != null) {
                        warmup.request(p.server, p, pos, "spawn");
                        p.sendSystemMessage(LegacyAmpersand.parse("&e&lSPAWNS &7&l➤ &aTeleported to spawn."));
                    } else {
                        p.sendSystemMessage(LegacyAmpersand.parse("&e&lSPAWNS &7&l➤ &cError! Spawn not set."));
                    }
                    return 1;
                })
        );


        d.register(Commands.literal("setspawn")
                .requires(src -> src.getEntity() instanceof ServerPlayer p && has(p, TpService.PERM_SETSPAWN))
                .executes(ctx -> {
                    ServerPlayer p = ctx.getSource().getPlayer();
                    tp.setSpawn(p);
                    p.sendSystemMessage(LegacyAmpersand.parse("&e&lSPAWNS &7&l➤ &aSpawn set."));
                    return 1;
                })
        );


        d.register(Commands.literal("warp")
                .requires(src -> req(src, TpService.PERM_WARP))
                .then(Commands.argument("name", StringArgumentType.word())
                        .executes(ctx -> {
                            ServerPlayer p = ctx.getSource().getPlayer();
                            String name = StringArgumentType.getString(ctx, "name");

                            WarpPos pos = tp.getWarpPos(name);
                            if (pos != null) {
                                warmup.request(p.server, p, pos, "warp " + name);
                                p.sendSystemMessage(LegacyAmpersand.parse("&c&lWARPS &7&l➤ &aYou have been warped to &e" + name));
                            } else {
                                p.sendSystemMessage(LegacyAmpersand.parse("&c&lWARPS &7&l➤ &cError! Warp not found."));
                            }
                            return 1;
                        })
                )
        );


        d.register(Commands.literal("setwarp")
                .requires(src -> req(src, TpService.PERM_SETWARP))
                .then(Commands.argument("name", StringArgumentType.word())
                        .executes(ctx -> {
                            String name = StringArgumentType.getString(ctx, "name");
                            ServerPlayer p = ctx.getSource().getPlayer();
                            tp.setWarp(ctx.getSource().getPlayer(),
                                    StringArgumentType.getString(ctx, "name"));
                            p.sendSystemMessage(LegacyAmpersand.parse("&c&lWARPS &7&l➤ &aWarp &e" + name + " &ahas been created!"));
                            return 1;
                        })
                )
        );


        d.register(Commands.literal("delwarp")
                .requires(src -> req(src, TpService.PERM_DELWARP))
                .then(Commands.argument("name", StringArgumentType.word())

                        .executes(ctx -> {
                            String name = StringArgumentType.getString(ctx, "name");
                            ServerPlayer p = ctx.getSource().getPlayer();
                            tp.delWarp(StringArgumentType.getString(ctx, "name"));
                            p.sendSystemMessage(LegacyAmpersand.parse("&c&lWARPS &7&l➤ &cWarp &e" + name + " &chas been deleted."));
                            return 1;
                        })
                )
        );


        d.register(Commands.literal("home")
                .requires(src -> req(src, TpService.PERM_HOME))

                .executes(ctx -> {
                    ServerPlayer p = ctx.getSource().getPlayer();

                    WarpPos pos = tp.getHomePos(p, "home");
                    if (pos != null) {
                        warmup.request(p.server, p, pos, "home");
                    } else {
                        p.sendSystemMessage(LegacyAmpersand.parse("&a&lHOMES &7&l➤ &cHome not found."));
                    }
                    return 1;
                })

                .then(Commands.argument("name", StringArgumentType.word())
                        .executes(ctx -> {
                            ServerPlayer p = ctx.getSource().getPlayer();
                            String name = StringArgumentType.getString(ctx, "name");

                            WarpPos pos = tp.getHomePos(p, name);
                            if (pos != null) {
                                warmup.request(p.server, p, pos, "home " + name);
                            } else {
                                p.sendSystemMessage(LegacyAmpersand.parse("&a&lHOMES &7&l➤ &cHome not found."));
                            }
                            return 1;
                        })
                )
        );


        d.register(Commands.literal("sethome")
                .requires(src -> req(src, TpService.PERM_SETHOME))
                .then(Commands.argument("name", StringArgumentType.word())
                        .executes(ctx -> {
                            String name = StringArgumentType.getString(ctx, "name");
                            ServerPlayer p = ctx.getSource().getPlayer();
                            tp.setHome(ctx.getSource().getPlayer(),
                                    StringArgumentType.getString(ctx, "name"));
                            p.sendSystemMessage(LegacyAmpersand.parse("&a&lHOMES &7&l➤ &aHome &e" + name + " &aset."));
                            return 1;
                        })
                )
        );

        d.register(Commands.literal("delhome")
                .requires(src -> req(src, TpService.PERM_DELHOME))
                .then(Commands.argument("name", StringArgumentType.word())
                        .executes(ctx -> {
                            String name = StringArgumentType.getString(ctx, "name");
                            ServerPlayer p = ctx.getSource().getPlayer();
                            tp.delHome(ctx.getSource().getPlayer(),
                                    StringArgumentType.getString(ctx, "name"));
                            p.sendSystemMessage(LegacyAmpersand.parse("&a&lHOMES &7&l➤ &cHome &e" + name + "&c has been deleted."));
                            return 1;
                        })
                )
        );
    }


}
