package org.howie.pixity.moderation.neoforge.rollback;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;
import org.howie.pixity.moderation.neoforge.util.Suggest;

import java.util.Locale;
import java.util.UUID;

public final class RollbackCommands {

    private final RollbackService svc;

    public RollbackCommands(final RollbackService svc) {
        this.svc = svc;
    }

    public void register(CommandDispatcher<CommandSourceStack> d) {

        d.register(Commands.literal("rollback")
                .requires(src -> src.getEntity() instanceof ServerPlayer p && svc.canUse(p))

                .then(Commands.argument("player", StringArgumentType.word())
                        .suggests(Suggest.playersOnline())

                        .then(Commands.argument("time", StringArgumentType.word())

                                .executes(ctx -> {

                                    MinecraftServer server = ctx.getSource().getServer();

                                    String playerName =
                                            StringArgumentType.getString(ctx, "player");

                                    String timeStr =
                                            StringArgumentType.getString(ctx, "time");

                                    long ms = parseDurationMs(timeStr);

                                    if (ms <= 0) {
                                        ctx.getSource().sendFailure(
                                                LegacyAmpersand.parse("&4&lROLLBACKS &7&l➤ &cError! Invalid time. Examples: 10m, 2h, 1d"));
                                        return 0;
                                    }

                                    UUID target = resolveUuid(server, playerName);

                                    if (target == null) {
                                        ctx.getSource().sendFailure(
                                                LegacyAmpersand.parse("&4&lROLLBACKS &7&l➤ &cError! Unknown player."));
                                        return 0;
                                    }

                                    int applied = svc.rollback(server, target, ms);

                                    ctx.getSource().sendSuccess(() ->
                                                    LegacyAmpersand.parse(
                                                            "&4&lROLLBACKS &7&l➤ &aRollback complete: &e"
                                                                    + applied
                                                                    + "&a actions reverted."
                                                    ),
                                            false
                                    );

                                    return 1;
                                })
                        )
                )
        );
    }

    private static UUID resolveUuid(final MinecraftServer server, final String name) {

        ServerPlayer online =
                server.getPlayerList().getPlayerByName(name);

        if (online != null)
            return online.getUUID();

        try {
            GameProfile gp =
                    server.getProfileCache().get(name).orElse(null);

            if (gp != null)
                return gp.getId();

        } catch (Throwable ignored) {}

        return null;
    }

    private static long parseDurationMs(final String s) {

        if (s == null) return -1;

        String t = s.trim().toLowerCase(Locale.ROOT);

        if (t.isEmpty()) return -1;

        long mult = 60_000L;
        char last = t.charAt(t.length() - 1);
        String num = t;

        if (last == 's') { mult = 1000L; num = t.substring(0, t.length()-1); }
        else if (last == 'm') { mult = 60_000L; num = t.substring(0, t.length()-1); }
        else if (last == 'h') { mult = 3_600_000L; num = t.substring(0, t.length()-1); }
        else if (last == 'd') { mult = 86_400_000L; num = t.substring(0, t.length()-1); }

        try {
            long v = Long.parseLong(num);
            return v > 0 ? v * mult : -1;
        } catch (Exception e) {
            return -1;
        }
    }
}