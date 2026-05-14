package org.howie.pixity.moderation.neoforge.playtime;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

import net.minecraft.commands.arguments.EntityArgument;

import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;
import org.howie.pixity.moderation.neoforge.util.TimeUtil;

public final class PlaytimeCommands {

    private final PlaytimeService playtime;

    public PlaytimeCommands(PlaytimeService playtime) {
        this.playtime = playtime;
    }

    public void register(CommandDispatcher<CommandSourceStack> d) {

        d.register(Commands.literal("playtime")





                .executes(ctx -> {

                    ServerPlayer p =
                            ctx.getSource().getPlayerOrException();

                    long time =
                            playtime.getPlaytime(p.getUUID());

                    ctx.getSource().sendSuccess(() ->
                            LegacyAmpersand.parse(
                                    "&e&lPLAYTIME &7&l➤ &f" +
                                            TimeUtil.formatDuration(time)
                            ), false);

                    return 1;
                })





                .then(Commands.literal("set")
                        .requires(src -> src.hasPermission(2))
                        .then(Commands.argument("player",
                                        EntityArgument.player())
                                .then(Commands.argument("time",
                                                StringArgumentType.word())
                                        .executes(ctx -> {

                                            ServerPlayer target =
                                                    EntityArgument.getPlayer(
                                                            ctx,
                                                            "player"
                                                    );

                                            String input =
                                                    StringArgumentType.getString(
                                                            ctx,
                                                            "time"
                                                    );

                                            long seconds =
                                                    parseTime(input);

                                            playtime.set(
                                                    target.getUUID(),
                                                    seconds
                                            );

                                            ctx.getSource().sendSuccess(() ->
                                                    LegacyAmpersand.parse(
                                                            "&aSet playtime for &f"
                                                                    + target.getGameProfile().getName()
                                                                    + " &ato "
                                                                    + TimeUtil.formatDuration(seconds)
                                                    ), true);

                                            return 1;
                                        }))))





                .then(Commands.literal("add")
                        .requires(src -> src.hasPermission(2))
                        .then(Commands.argument("player",
                                        EntityArgument.player())
                                .then(Commands.argument("time",
                                                StringArgumentType.word())
                                        .executes(ctx -> {

                                            ServerPlayer target =
                                                    EntityArgument.getPlayer(
                                                            ctx,
                                                            "player"
                                                    );

                                            String input =
                                                    StringArgumentType.getString(
                                                            ctx,
                                                            "time"
                                                    );

                                            long seconds =
                                                    parseTime(input);

                                            playtime.add(
                                                    target.getUUID(),
                                                    seconds
                                            );

                                            ctx.getSource().sendSuccess(() ->
                                                    LegacyAmpersand.parse(
                                                            "&aAdded &f"
                                                                    + TimeUtil.formatDuration(seconds)
                                                                    + " &ato "
                                                                    + target.getGameProfile().getName()
                                                    ), true);

                                            return 1;
                                        }))))





                .then(Commands.literal("remove")
                        .requires(src -> src.hasPermission(2))
                        .then(Commands.argument("player",
                                        EntityArgument.player())
                                .then(Commands.argument("time",
                                                StringArgumentType.word())
                                        .executes(ctx -> {

                                            ServerPlayer target =
                                                    EntityArgument.getPlayer(
                                                            ctx,
                                                            "player"
                                                    );

                                            String input =
                                                    StringArgumentType.getString(
                                                            ctx,
                                                            "time"
                                                    );

                                            long seconds =
                                                    parseTime(input);

                                            playtime.remove(
                                                    target.getUUID(),
                                                    seconds
                                            );

                                            ctx.getSource().sendSuccess(() ->
                                                    LegacyAmpersand.parse(
                                                            "&cRemoved &f"
                                                                    + TimeUtil.formatDuration(seconds)
                                                                    + " &cfrom "
                                                                    + target.getGameProfile().getName()
                                                    ), true);

                                            return 1;
                                        }))))





                .then(Commands.literal("clear")
                        .requires(src -> src.hasPermission(2))
                        .then(Commands.argument("player",
                                        EntityArgument.player())
                                .executes(ctx -> {

                                    ServerPlayer target =
                                            EntityArgument.getPlayer(
                                                    ctx,
                                                    "player"
                                            );

                                    playtime.set(target.getUUID(), 0);

                                    ctx.getSource().sendSuccess(() ->
                                            LegacyAmpersand.parse(
                                                    "&cCleared playtime for &f"
                                                            + target.getGameProfile().getName()
                                            ), true);

                                    return 1;
                                })))
        );
    }

    private long parseTime(String input) {

        input = input.toLowerCase();

        long total = 0;

        String number = "";

        for (char c : input.toCharArray()) {

            if (Character.isDigit(c)) {
                number += c;
                continue;
            }

            if (number.isEmpty()) continue;

            long value = Long.parseLong(number);

            switch (c) {
                case 'd' -> total += value * 86400;
                case 'h' -> total += value * 3600;
                case 'm' -> total += value * 60;
                case 's' -> total += value;
            }

            number = "";
        }

        if (!number.isEmpty()) {
            total += Long.parseLong(number);
        }

        return total;
    }
}