package org.howie.pixity.moderation.neoforge.freeze;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;
import org.howie.pixity.moderation.neoforge.util.Suggest;

import java.util.UUID;

public final class FreezeCommands {

    private final FreezeService freeze;
    private final RankService ranks;

    public FreezeCommands(final FreezeService freeze, final RankService ranks) {
        this.freeze = freeze;
        this.ranks = ranks;
    }

    private boolean has(CommandSourceStack src, String perm) {
        if (!(src.getEntity() instanceof ServerPlayer p)) return src.hasPermission(2);
        return ranks.hasPerm(p, perm);
    }

    public void register(CommandDispatcher<CommandSourceStack> d) {

        d.register(Commands.literal("freeze")
                .requires(src -> has(src, "pixity.freeze"))
                .then(Commands.argument("player", StringArgumentType.word())
                        .suggests(Suggest.playersOnline())
                        .executes(ctx -> freezeNow(
                                ctx.getSource(),
                                StringArgumentType.getString(ctx, "player"),
                                null
                        ))
                        .then(Commands.argument("reason", StringArgumentType.greedyString())
                                .executes(ctx -> freezeNow(
                                        ctx.getSource(),
                                        StringArgumentType.getString(ctx, "player"),
                                        StringArgumentType.getString(ctx, "reason")
                                ))
                        )
                )
        );

        d.register(Commands.literal("unfreeze")
                .requires(src -> has(src, "pixity.unfreeze"))
                .then(Commands.argument("player", StringArgumentType.word())
                        .suggests(Suggest.frozenPlayers(freeze))
                        .executes(ctx -> unfreezeNow(
                                ctx.getSource(),
                                StringArgumentType.getString(ctx, "player"),
                                null
                        ))
                        .then(Commands.argument("reason", StringArgumentType.greedyString())
                                .executes(ctx -> unfreezeNow(
                                        ctx.getSource(),
                                        StringArgumentType.getString(ctx, "player"),
                                        StringArgumentType.getString(ctx, "reason")
                                ))
                        )
                )
        );
    }

    private int freezeNow(CommandSourceStack src, String targetName, String reason) {

        MinecraftServer server = src.getServer();
        ServerPlayer staff = src.getPlayer();

        if (staff == null) return 0;

        ServerPlayer target = server.getPlayerList().getPlayerByName(targetName);
        if (target == null) return 0;

        freeze.freeze(server, staff, target, reason);
        return 1;
    }

    private int unfreezeNow(CommandSourceStack src, String name, String reason) {

        MinecraftServer server = src.getServer();
        ServerPlayer staff = src.getPlayer();

        if (staff == null) return 0;

        UUID uuid = resolveUuid(server, name);
        if (uuid == null) {
            staff.sendSystemMessage(
                    LegacyAmpersand.parse("&4&lSTAFF &7&l➤ Player not found or not frozen.")
            );
            return 0;
        }

        boolean success =
                freeze.unfreeze(server, staff, uuid, name, reason);

        if (!success) {
            staff.sendSystemMessage(
                    LegacyAmpersand.parse("&4&lSTAFF &7&l➤That player is not frozen.")
            );
            return 0;
        }

        return 1;
    }

    private UUID resolveUuid(MinecraftServer server, String name) {

        for (FreezeRecord r : freeze.listFrozenRecords()) {
            if (r.playerName.equalsIgnoreCase(name)) {
                return r.player;
            }
        }

        ServerPlayer p = server.getPlayerList().getPlayerByName(name);
        return p != null ? p.getUUID() : null;
    }
}