package org.howie.pixity.moderation.neoforge.jail;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.util.Suggest;

import java.util.UUID;

public final class JailCommands {

    private final JailService jail;
    private final RankService ranks;

    public static final String UNJAIL_PERM = "pixity.jails.unjail";
    public static final String JAIL_PERM = "pixity.jails.jail";


    public JailCommands(JailService jail, RankService ranks) {
        this.jail = jail;
        this.ranks = ranks;
    }

    private boolean has(CommandSourceStack src, String perm) {
        if (!(src.getEntity() instanceof ServerPlayer p)) return src.hasPermission(2);
        return ranks.hasPerm(p, perm);
    }

    public void register(CommandDispatcher<CommandSourceStack> d) {

        d.register(Commands.literal("jail")
                .requires(src -> has(src,"pixity.jail"))
                .then(Commands.argument("player", StringArgumentType.word())
                        .suggests(Suggest.playersOnline())
                        .then(Commands.argument("jail", StringArgumentType.word())
                                .suggests(Suggest.jails(jail))
                                .executes(ctx -> jailNow(
                                        ctx.getSource(),
                                        StringArgumentType.getString(ctx,"player"),
                                        StringArgumentType.getString(ctx,"jail"),
                                        null,
                                        null
                                ))
                                .then(Commands.argument("time", LongArgumentType.longArg())
                                        .executes(ctx -> jailNow(
                                                ctx.getSource(),
                                                StringArgumentType.getString(ctx,"player"),
                                                StringArgumentType.getString(ctx,"jail"),
                                                LongArgumentType.getLong(ctx,"time"),
                                                null
                                        ))
                                )
                        )
                )
        );

        d.register(Commands.literal("unjail")
                .requires(src -> has(src,"pixity.unjail"))
                .then(Commands.argument("player", StringArgumentType.word())
                        .suggests(Suggest.jailedPlayers(jail))
                        .executes(ctx -> unjailNow(
                                ctx.getSource(),
                                StringArgumentType.getString(ctx,"player"),
                                null
                        ))
                )
        );
    }

    private int jailNow(CommandSourceStack src, String player, String jailName,
                        Long time, String reason) {

        MinecraftServer server = src.getServer();
        ServerPlayer staff = src.getPlayer();

        if (staff == null) return 0;

        ServerPlayer target = server.getPlayerList().getPlayerByName(player);
        if (target == null) return 0;

        jail.jail(server, staff, target, jailName, time, reason);
        return 1;
    }

    private int unjailNow(CommandSourceStack src, String player, String reason) {

        MinecraftServer server = src.getServer();
        ServerPlayer staff = src.getPlayer();

        if (staff == null) return 0;

        UUID uuid = resolveUuid(server, player);
        if (uuid == null) return 0;

        jail.unjail(server, staff, uuid, player, reason);
        return 1;
    }

    private static UUID resolveUuid(MinecraftServer server, String name) {
        ServerPlayer p = server.getPlayerList().getPlayerByName(name);
        return p != null ? p.getUUID() : null;
    }
}