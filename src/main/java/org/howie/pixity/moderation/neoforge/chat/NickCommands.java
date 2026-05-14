package org.howie.pixity.moderation.neoforge.chat;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.chat.NickHolder;
import org.howie.pixity.moderation.neoforge.chat.cosmetics.ChatCosmeticsService;
import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

public final class NickCommands {

    private static final String PERM_USE = "pixity.chat.nickname";
    private static final String PERM_COLOR = "pixity.chat.nickname.colors";
    private static final String PERM_OTHERS = "pixity.chat.nickname.others";

    private final RankService perms;
    private final ChatCosmeticsService cosmetics;

    public NickCommands(RankService perms,
                        ChatCosmeticsService cosmetics) {

        this.perms = perms;
        this.cosmetics = cosmetics;
    }

    private boolean has(CommandSourceStack src, String perm) {
        if (!(src.getEntity() instanceof ServerPlayer p))
            return src.hasPermission(2);

        return perms.hasPerm(p, perm);
    }

    public void register(CommandDispatcher<CommandSourceStack> d) {

        d.register(Commands.literal("nick")

                .requires(src -> has(src, PERM_USE))


                .then(Commands.argument("name", StringArgumentType.greedyString())
                        .executes(ctx -> {

                            ServerPlayer player = ctx.getSource().getPlayer();
                            if (player == null) return 0;

                            String input =
                                    StringArgumentType.getString(ctx, "name");

                            if (input.equalsIgnoreCase("off")
                                    || input.equalsIgnoreCase("reset")) {

                                NickHolder.INSTANCE.clearNick(player.getUUID());

                                player.sendSystemMessage(
                                        LegacyAmpersand.parse("&e&lNICKNAMES &7&l➤ &aNickname removed.")
                                );

                                return 1;
                            }

                            boolean canColor =
                                    perms.hasPerm(player, PERM_COLOR);

                            String finalNick =
                                    canColor ? input : "§f" + stripColors(input);

                            if (finalNick.length() > 32) {
                                finalNick = finalNick.substring(0, 32);
                            }

                            for (ServerPlayer p :
                                    player.server.getPlayerList().getPlayers()) {

                                String existing =
                                        NickHolder.INSTANCE.getDisplayName(p);

                                if (stripColors(existing)
                                        .equalsIgnoreCase(stripColors(finalNick))) {

                                    player.sendSystemMessage(
                                            LegacyAmpersand.parse(
                                                    "&e&lNICKNAMES &7&l➤ &cError! That nickname is already in use."
                                            )
                                    );
                                    return 0;
                                }
                            }

                            if (cosmetics != null &&
                                    (finalNick.contains("&")
                                            || finalNick.contains("§")
                                            || finalNick.contains("<gradient"))) {

                                cosmetics.clearName(player.getUUID());
                            }

                            NickHolder.INSTANCE.setNick(
                                    player.getUUID(),
                                    finalNick
                            );

                            player.sendSystemMessage(
                                    Component.empty()
                                            .append(LegacyAmpersand.parse("&e&lNICKNAMES &7&l➤ &aNickname set to: &r"))
                                            .append(LegacyAmpersand.parse(finalNick))
                            );

                            return 1;
                        })
                )


                .then(Commands.literal("reset")
                        .requires(src -> has(src, PERM_OTHERS))
                        .then(Commands.argument("player", StringArgumentType.word())
                                .executes(ctx -> {

                                    ServerPlayer sender =
                                            ctx.getSource().getPlayer();
                                    if (sender == null) return 0;

                                    String name =
                                            StringArgumentType.getString(ctx, "player");

                                    ServerPlayer target =
                                            ctx.getSource().getServer()
                                                    .getPlayerList()
                                                    .getPlayerByName(name);

                                    if (target == null) {
                                        sender.sendSystemMessage(
                                                LegacyAmpersand.parse("&e&lNICKNAMES &7&l➤ &cError! Player not found.")
                                        );
                                        return 0;
                                    }

                                    NickHolder.INSTANCE.clearNick(target.getUUID());

                                    sender.sendSystemMessage(
                                            LegacyAmpersand.parse("&e&lNICKNAMES &7&l➤ &aNickname reset.")
                                    );

                                    return 1;
                                })
                        )
                )


                .then(Commands.argument("player", StringArgumentType.word())
                        .requires(src -> has(src, PERM_OTHERS))
                        .then(Commands.argument("name", StringArgumentType.greedyString())
                                .executes(ctx -> {

                                    ServerPlayer sender =
                                            ctx.getSource().getPlayer();
                                    if (sender == null) return 0;

                                    String targetName =
                                            StringArgumentType.getString(ctx, "player");

                                    ServerPlayer target =
                                            ctx.getSource().getServer()
                                                    .getPlayerList()
                                                    .getPlayerByName(targetName);

                                    if (target == null) {
                                        sender.sendSystemMessage(
                                                LegacyAmpersand.parse("&e&lNICKNAMES &7&l➤ &cError! Player not found.")
                                        );
                                        return 0;
                                    }

                                    String input =
                                            StringArgumentType.getString(ctx, "name");

                                    if (input.equalsIgnoreCase("off")
                                            || input.equalsIgnoreCase("reset")) {

                                        NickHolder.INSTANCE.clearNick(target.getUUID());

                                        sender.sendSystemMessage(
                                                LegacyAmpersand.parse("&e&lNICKNAMES &7&l➤ &aNickname cleared.")
                                        );

                                        return 1;
                                    }

                                    boolean canColor =
                                            perms.hasPerm(sender, PERM_COLOR);

                                    String finalNick =
                                            canColor ? input : "§f" + stripColors(input);

                                    if (finalNick.length() > 32) {
                                        finalNick = finalNick.substring(0, 32);
                                    }

                                    for (ServerPlayer p :
                                            sender.server.getPlayerList().getPlayers()) {

                                        String existing =
                                                NickHolder.INSTANCE.getDisplayName(p);

                                        if (stripColors(existing)
                                                .equalsIgnoreCase(stripColors(finalNick))) {

                                            sender.sendSystemMessage(
                                                    LegacyAmpersand.parse(
                                                            "&e&lNICKNAMES &7&l➤ &cError! That nickname is already in use."
                                                    )
                                            );
                                            return 0;
                                        }
                                    }

                                    if (cosmetics != null &&
                                            (finalNick.contains("&")
                                                    || finalNick.contains("§")
                                                    || finalNick.contains("<gradient"))) {

                                        cosmetics.clearName(target.getUUID());
                                    }

                                    NickHolder.INSTANCE.setNick(
                                            target.getUUID(),
                                            finalNick
                                    );

                                    sender.sendSystemMessage(
                                            LegacyAmpersand.parse("&e&lNICKNAMES &7&l➤ Nickname set.")
                                    );

                                    return 1;
                                })
                        )
                )
        );
    }

    private String stripColors(String input) {
        return input
                .replaceAll("(?i)&[0-9A-FK-OR]", "")
                .replaceAll("(?i)&#[0-9A-F]{6}", "");
    }
}