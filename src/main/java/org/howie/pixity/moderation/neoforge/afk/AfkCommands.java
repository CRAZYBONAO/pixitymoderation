package org.howie.pixity.moderation.neoforge.afk;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import org.howie.pixity.moderation.neoforge.rank.RankService;

public final class AfkCommands {

    private final AfkService afk;
    private final RankService ranks;

    public AfkCommands(final AfkService afk, final RankService ranks) {
        this.afk = afk;
        this.ranks = ranks;
    }

    private boolean has(ServerPlayer p, String perm) {
        return ranks.hasPerm(p, perm) || ranks.hasPerm(p, "pixity.admin");
    }

    @SubscribeEvent
    public void onRegister(final RegisterCommandsEvent e) {
        CommandDispatcher<CommandSourceStack> d = e.getDispatcher();

        d.register(Commands.literal("afk")
                .requires(src -> {
                    if (!(src.getEntity() instanceof ServerPlayer p)) return false;
                    return src.hasPermission(2) || has(p, AfkService.PERM_AFK);
                })
                .executes(ctx -> {
                    ServerPlayer p = ctx.getSource().getPlayerOrException();

                    afk.toggle(p);

                    return 1;
                })
        );
    }
}