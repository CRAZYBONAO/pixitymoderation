package org.howie.pixity.moderation.neoforge.staff;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

public final class StaffModeCommands {

    private static final String PERM = "pixity.staff.mode";

    private final StaffModeService staff;
    private final RankService ranks;

    public StaffModeCommands(StaffModeService staff, RankService ranks) {
        this.staff = staff;
        this.ranks = ranks;
    }

    private boolean has(CommandSourceStack src) {
        if (!(src.getEntity() instanceof ServerPlayer p))
            return src.hasPermission(2);

        return ranks.hasPerm(p, PERM);
    }

    public void register(CommandDispatcher<CommandSourceStack> d) {

        d.register(Commands.literal("staff")
                .requires(this::has)
                .executes(ctx -> {

                    ServerPlayer p = ctx.getSource().getPlayer();

                    if (p == null) {
                        ctx.getSource().sendFailure(LegacyAmpersand.parse("&c&lSTAFFMODE &7&l➤ &cError! Players only."));
                        return 0;
                    }

                    staff.toggle(ctx.getSource().getServer(), p);
                    return 1;
                })
        );
    }
}