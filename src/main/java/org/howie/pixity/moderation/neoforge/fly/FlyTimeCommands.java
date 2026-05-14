package org.howie.pixity.moderation.neoforge.fly;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.neoforge.economy.EconomyService;
import org.howie.pixity.moderation.neoforge.fly.gui.FlyTimeGui;
import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;
import org.howie.pixity.moderation.neoforge.util.Suggest;
import org.howie.pixity.moderation.neoforge.util.TimeUtil;

import java.util.UUID;

public final class FlyTimeCommands {

    private final FlyTimeService fly;
    private final EconomyService economy;
    private final RankService ranks;

    public FlyTimeCommands(FlyTimeService fly, EconomyService economy, RankService ranks) {
        this.fly = fly;
        this.economy = economy;
        this.ranks = ranks;
    }

    private boolean has(ServerPlayer p, String perm) {
        return p.hasPermissions(2) || (ranks != null && ranks.hasPerm(p, perm));
    }

    public void register(CommandDispatcher<CommandSourceStack> d) {


        d.register(Commands.literal("tempfly")
                .requires(src -> src.getEntity() instanceof ServerPlayer p && has(p,"pixity.fly.use"))
                .executes(ctx -> {
                    ServerPlayer p = ctx.getSource().getPlayerOrException();

                    boolean on = fly.toggleFlight(p);

                    p.sendSystemMessage(LegacyAmpersand.parse(
                            "&6&lFLIGHT &7&l➤" + (on ? " &aENABLED" : " &cDISABLED")
                    ));
                    return 1;
                })
                .then(Commands.literal("shop")
                        .requires(src -> src.getEntity() instanceof ServerPlayer p && has(p,"pixity.fly.shop"))
                        .executes(ctx -> {
                            ServerPlayer p = ctx.getSource().getPlayerOrException();
                            FlyTimeGui.open(p, fly, economy);
                            return 1;
                        })
                )
        );


        d.register(Commands.literal("flytime")

                .then(Commands.literal("pay")
                        .requires(src -> src.getEntity() instanceof ServerPlayer)
                        .then(Commands.argument("player", StringArgumentType.word()).suggests(Suggest.playersOnline())
                                .then(Commands.argument("time", StringArgumentType.greedyString())
                                        .executes(ctx -> {

                                            ServerPlayer sender = ctx.getSource().getPlayerOrException();
                                            MinecraftServer server = ctx.getSource().getServer();

                                            String name = StringArgumentType.getString(ctx,"player");
                                            String timeStr = StringArgumentType.getString(ctx,"time");

                                            long seconds = TimeUtil.parse(timeStr);

                                            if (seconds <= 0) {
                                                sender.sendSystemMessage(LegacyAmpersand.parse("&6&lFLIGHT &7&l➤ &cInvalid time"));
                                                return 0;
                                            }

                                            UUID targetId = resolveUuid(server,name);
                                            if (targetId == null) return 0;

                                            long balance = fly.getTime(sender.getUUID());
                                            if (balance < seconds) return 0;

                                            fly.set(sender.getUUID(), balance - seconds);
                                            fly.give(targetId, seconds);

                                            sender.sendSystemMessage(LegacyAmpersand.parse(
                                                    "&6&lFLIGHT &7&l➤ &aSent &e" + timeStr + " &ato &e" + name
                                            ));

                                            return 1;
                                        })
                                )
                        )
                )

                .then(adminNode("give","pixity.fly.admin.give"))
                .then(adminNode("take","pixity.fly.admin.take"))
                .then(adminNode("set","pixity.fly.admin.set"))

                .then(Commands.literal("check")
                        .requires(src -> src.getEntity() instanceof ServerPlayer p && has(p,"pixity.fly.check"))
                        .then(Commands.argument("player", StringArgumentType.word()).suggests(Suggest.playersOnline())
                                .executes(ctx -> {

                                    MinecraftServer server = ctx.getSource().getServer();
                                    String name = StringArgumentType.getString(ctx,"player");

                                    UUID uuid = resolveUuid(server,name);
                                    if (uuid == null) return 0;

                                    long time = fly.getTime(uuid);

                                    ctx.getSource().sendSuccess(() ->
                                            LegacyAmpersand.parse("&6&lFLIGHT &7&l➤ &e"+name+" &ahas &e"+time+"s"), false);

                                    return 1;
                                })
                        )
                )
        );
    }

    private com.mojang.brigadier.builder.ArgumentBuilder<CommandSourceStack,?> adminNode(String action,String perm) {
        return Commands.literal(action)
                .requires(src -> {
                    if (src.getEntity() == null) return true;
                    if (src.getEntity() instanceof ServerPlayer p) return has(p, perm);
                    return false;
                })
                .then(Commands.argument("player",StringArgumentType.word()).suggests(Suggest.playersOnline())
                        .then(Commands.argument("time",StringArgumentType.greedyString())
                                .executes(ctx -> modify(ctx,action))
                        )
                );
    }

    private int modify(com.mojang.brigadier.context.CommandContext<CommandSourceStack> ctx,String action) {

        MinecraftServer server = ctx.getSource().getServer();

        String name = StringArgumentType.getString(ctx,"player");
        String timeStr = StringArgumentType.getString(ctx,"time");

        long seconds = TimeUtil.parse(timeStr);
        if (seconds <= 0) return 0;

        UUID uuid = resolveUuid(server,name);
        if (uuid == null) return 0;

        switch (action) {
            case "give" -> fly.give(uuid,seconds);
            case "take" -> fly.set(uuid, Math.max(0, fly.getTime(uuid)-seconds));
            case "set" -> fly.set(uuid,seconds);
        }

        return 1;
    }

    private UUID resolveUuid(MinecraftServer server,String name) {

        ServerPlayer online = server.getPlayerList().getPlayerByName(name);
        if (online != null) return online.getUUID();

        return null;
    }
}