package org.howie.pixity.moderation.neoforge.tp;

import com.mojang.brigadier.CommandDispatcher;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

public final class BackCommands {


    private final TpService tp;
    private final RankService perms;

    public BackCommands(TpService tp, RankService perms) {
        this.tp = tp;
        this.perms = perms;
    }

    private boolean has(ServerPlayer p, String perm) {
        return perms != null && (perms.hasPerm(p, perm) || perms.hasPerm(p, "pixity.admin"));
    }

    @SubscribeEvent
    public void onRegister(RegisterCommandsEvent e) {

        CommandDispatcher<CommandSourceStack> d = e.getDispatcher();

        d.register(Commands.literal("back")
                .requires(src -> src.getEntity() instanceof ServerPlayer p && has(p, BackService.PERM_BACK))
                .executes(ctx -> {
                    ServerPlayer p = ctx.getSource().getPlayer();
                    BackService.back(p.server, p, tp);
                    p.sendSystemMessage(LegacyAmpersand.parse("&e&lBACK &7&l➤ &aYou have been taken to your past location."));
                    return 1;
                })
        );
    }


}
