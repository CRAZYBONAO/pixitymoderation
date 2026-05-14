package org.howie.pixity.moderation.neoforge.chatgames;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import org.howie.pixity.moderation.chat.TextFormatter;
import org.howie.pixity.moderation.neoforge.chatgames.gui.ChatGamesMilestonesGui;
import org.howie.pixity.moderation.neoforge.chatgames.gui.ChatGamesStreakGui;

import java.util.UUID;

public class ChatGamesCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(
                Commands.literal("chatgames")




                        .then(Commands.literal("top")
                                .executes(ctx -> {

                                    ServerPlayer player = ctx.getSource().getPlayerOrException();

                                    var top = ChatGamesDatabase.getTop(10);

                                    player.sendSystemMessage(TextFormatter.parse("<rainbow>&l✦ CHATGAMES LEADERBOARD ✦"));

                                    int rank = 1;

                                    for (var entry : top) {

                                        String name = ctx.getSource().getServer()
                                                .getProfileCache()
                                                .get(entry.getKey())
                                                .map(p -> p.getName())
                                                .orElse("Unknown");

                                        player.sendSystemMessage(TextFormatter.parse(
                                                "<yellow>#"+rank+" </yellow><white>"+name+" </white><gray>- </gray><green>"+entry.getValue()+"x"
                                        ));

                                        rank++;
                                    }

                                    return 1;
                                })
                        )




                        .then(Commands.literal("stats")
                                .executes(ctx -> {

                                    ServerPlayer player = ctx.getSource().getPlayerOrException();

                                    return showStats(player, player.getUUID());
                                })
                        )




                        .then(Commands.literal("stats")
                                .then(Commands.argument("target", net.minecraft.commands.arguments.EntityArgument.player())
                                        .executes(ctx -> {

                                            ServerPlayer viewer = ctx.getSource().getPlayerOrException();
                                            ServerPlayer target = net.minecraft.commands.arguments.EntityArgument.getPlayer(ctx, "target");

                                            return showStats(viewer, target.getUUID());
                                        })
                                )
                        )




                        .then(Commands.literal("reload")
                                .requires(src -> src.hasPermission(2))
                                .executes(ctx -> {

                                    ChatGamesConfig.load();

                                    ctx.getSource().sendSuccess(
                                            () -> TextFormatter.parse("&c&lCHATGAMES &7&l➤ &aChatGames config reloaded."),
                                            false
                                    );

                                    return 1;
                                })
                        )
                        .then(Commands.literal("streak")
                                .executes(ctx -> {

                                    ServerPlayer player = ctx.getSource().getPlayerOrException();

                                    ChatGamesStreakGui.open(player);

                                    return 1;
                                })
                        )
                        .then(Commands.literal("milestones")
                                .executes(ctx -> {

                                    ServerPlayer player = ctx.getSource().getPlayerOrException();

                                    ChatGamesMilestonesGui.open(player, 0);

                                    return 1;
                                })
                        )


        );
    }


    private static int showStats(ServerPlayer viewer, UUID uuid) {

        var stats = ChatGamesDatabase.getStats(uuid);

        int best = stats.getOrDefault("best", 0);
        int current = stats.getOrDefault("current", 0);
        int wins = stats.getOrDefault("wins", 0);

        viewer.sendSystemMessage(TextFormatter.parse("<rainbow>✦ CHATGAMES STATS ✦</rainbow>"));
        viewer.sendSystemMessage(TextFormatter.parse("<gray>Wins: </gray><green>" + wins + "</green>"));
        viewer.sendSystemMessage(TextFormatter.parse("<gray>Best Streak:</gray> <gold>" + best + "</gold>"));
        viewer.sendSystemMessage(TextFormatter.parse("<gray>Current Streak:</gray> <yellow>" + current + "</yellow>"));

        return 1;
    }
}