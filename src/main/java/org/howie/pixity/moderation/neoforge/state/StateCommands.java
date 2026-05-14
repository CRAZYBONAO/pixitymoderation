package org.howie.pixity.moderation.neoforge.state;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.neoforge.rank.RankService;

public final class StateCommands {

    private final PlayerStateManager states;
    private final RankService ranks;

    public StateCommands(PlayerStateManager states, RankService ranks) {
        this.states = states;
        this.ranks = ranks;
    }

    private boolean has(ServerPlayer p, String perm) {
        return ranks != null && ranks.hasPerm(p, perm);
    }

    public void register(CommandDispatcher<CommandSourceStack> d) {

        d.register(Commands.literal("vanish")
                .requires(src -> src.getEntity() instanceof ServerPlayer p && has(p, PlayerStateManager.PERM_VANISH))
                .executes(ctx -> {
                    ServerPlayer p = ctx.getSource().getPlayer();
                    if (p == null) {
                        ctx.getSource().sendFailure(Component.literal("Players only."));
                        return 0;
                    }

                    states.toggleVanish(ctx.getSource().getServer(), p);
                    return 1;
                })
        );

        d.register(Commands.literal("fly")
                .requires(src -> src.getEntity() instanceof ServerPlayer p && has(p, PlayerStateManager.PERM_FLY))
                .executes(ctx -> {
                    ServerPlayer p = ctx.getSource().getPlayer();
                    if (p == null) {
                        ctx.getSource().sendFailure(Component.literal("Players only."));
                        return 0;
                    }

                    states.toggleFly(p);
                    return 1;
                })
        );

        d.register(Commands.literal("god")
                .requires(src -> src.getEntity() instanceof ServerPlayer p && has(p, PlayerStateManager.PERM_GOD))
                .executes(ctx -> {
                    ServerPlayer p = ctx.getSource().getPlayer();
                    if (p == null) {
                        ctx.getSource().sendFailure(Component.literal("Players only."));
                        return 0;
                    }

                    states.toggleGod(p);
                    return 1;
                })
        );
    }
}