package org.howie.pixity.moderation.neoforge.rules;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

public final class RulesCommands {

    private final RulesService rules;
    private final RankService ranks;

    public static final String PERM_VIEW = "pixity.rules.view";
    public static final String PERM_RELOAD = "pixity.rules.reload";

    public RulesCommands(final RulesService rules, final RankService ranks) {
        this.rules = rules;
        this.ranks = ranks;
    }

    private boolean has(ServerPlayer p, String perm) {
        return ranks != null && ranks.hasPerm(p, perm);
    }

    public void register(CommandDispatcher<CommandSourceStack> d) {

        d.register(Commands.literal("rules")
                .requires(cs -> cs.getEntity() instanceof ServerPlayer p && has(p, PERM_VIEW))
                .executes(ctx -> {
                    ServerPlayer p = ctx.getSource().getPlayer();
                    if (p == null) {
                        ctx.getSource().sendSuccess(
                                () -> LegacyAmpersand.parse("&4&lRULES &7&l➤ &cError! This command is for players."),
                                false
                        );
                        return 1;
                    }

                    rules.showRules(p);
                    return 1;
                })
        );

        d.register(Commands.literal("rulesreload")
                .requires(cs -> cs.getEntity() instanceof ServerPlayer p && has(p, PERM_RELOAD))
                .executes(ctx -> {
                    rules.reload();
                    ctx.getSource().sendSuccess(
                            () -> LegacyAmpersand.parse("&4&lRULES &7&l➤ &aReloaded rules configuration."),
                            false
                    );
                    return 1;
                })
        );
    }
}