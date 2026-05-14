package org.howie.pixity.moderation.neoforge.giveaway;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;

import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

public final class GiveawayCommands {

    private final GiveawayService service;
    private final GiveawayChatPromptService prompts;
    private final RankService ranks;

    public GiveawayCommands(GiveawayService service,
                            GiveawayChatPromptService prompts,
                            RankService ranks) {

        this.service = service;
        this.prompts = prompts;
        this.ranks = ranks;
    }

    private boolean has(ServerPlayer p, String perm) {
        return ranks.hasPerm(p, perm)
                || ranks.hasPerm(p, "pixity.giveaway.admin")
                || p.hasPermissions(2);
    }

    public void register(CommandDispatcher<CommandSourceStack> d) {

        d.register(Commands.literal("giveaway")

                .executes(ctx -> {
                    ServerPlayer p = ctx.getSource().getPlayer();

                    if (!has(p,"pixity.giveaway.gui")) {
                        p.sendSystemMessage(
                                LegacyAmpersand.parse("&4&lGIVEAWAYS &7&l➤ &cError! No permission.")
                        );
                        return 0;
                    }

                    GiveawayGUI.open(p, service, prompts);
                    return 1;
                })

                .then(Commands.literal("winners")
                        .executes(ctx -> {

                            ServerPlayer p = ctx.getSource().getPlayer();

                            var winners = service.getStoredWinners();

                            if (winners.isEmpty()) {
                                p.sendSystemMessage(
                                        LegacyAmpersand.parse("&4&lGIVEAWAYS &7&l➤ &cError! There have been no winners yet.")
                                );
                                return 1;
                            }

                            p.sendSystemMessage(
                                    LegacyAmpersand.parse("&4&lGIVEAWAYS &7&l➤ §ePast Giveaway Winners")
                            );

                            for (String w : winners) {
                                p.sendSystemMessage(
                                        Component.literal("§e• §f" + w)
                                );
                            }

                            return 1;
                        })
                )

                .then(Commands.literal("enter")
                        .executes(ctx -> {

                            ServerPlayer p = ctx.getSource().getPlayer();
                            service.enter(p);

                            p.sendSystemMessage(
                                    LegacyAmpersand.parse("&4&lGIVEAWAYS &7&l➤ §aYou entered the giveaway!")
                            );

                            return 1;
                        })
                )
        );

        d.register(Commands.literal("enter")
                .executes(ctx -> {

                    ServerPlayer p = ctx.getSource().getPlayer();
                    service.enter(p);

                    p.sendSystemMessage(
                            LegacyAmpersand.parse("&4&lGIVEAWAYS &7&l➤ §aYou entered the giveaway!")
                    );

                    return 1;
                })
        );
    }
}