package org.howie.pixity.moderation.neoforge.jail;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

public final class JailCheckCommands {

    private final JailService jail;
    private final RankService ranks;

    public JailCheckCommands(final JailService jail, final RankService ranks) {
        this.jail = jail;
        this.ranks = ranks;
    }

    public void register(CommandDispatcher<CommandSourceStack> d) {

        d.register(Commands.literal("jailcheck")
                .executes(ctx -> {

                    ServerPlayer p = ctx.getSource().getPlayer();
                    if (p == null) return 0;

                    if (!jail.isJailed(p.getUUID())) {
                        p.sendSystemMessage(
                                LegacyAmpersand.parse(
                                        "&c&lPUNISHMENTS &7&l➤ &aYou are not jailed."
                                )
                        );
                        return 1;
                    }

                    long left = jail.remainingSeconds(p.getUUID());

                    if (left == -1L) {
                        p.sendSystemMessage(
                                LegacyAmpersand.parse(
                                        "&c&lPUNISHMENTS &7&l➤ &cYou are jailed permanently."
                                )
                        );
                    }
                    else {
                        p.sendSystemMessage(
                                LegacyAmpersand.parse(
                                        "&c&lPUNISHMENTS &7&l➤ &cYour jail expires in &e" +
                                                left + "s"
                                )
                        );
                    }

                    return 1;
                })
        );
    }
}