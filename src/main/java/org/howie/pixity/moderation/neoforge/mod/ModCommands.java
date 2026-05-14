package org.howie.pixity.moderation.neoforge.mod;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;
import org.howie.pixity.moderation.neoforge.util.Suggest;

public final class ModCommands {

    public static final String PERM_OPEN = "pixity.mod.open";

    private final ModGui gui;
    private final RankService ranks;

    public ModCommands(ModGui gui, RankService ranks) {
        this.gui = gui;
        this.ranks = ranks;
    }

    private boolean has(ServerPlayer p, String perm) {
        return ranks != null && ranks.hasPerm(p, perm);
    }

    public void register(CommandDispatcher<CommandSourceStack> d) {

        d.register(Commands.literal("mod")
                .requires(src ->
                        src.getEntity() instanceof ServerPlayer p
                                && has(p, PERM_OPEN)
                )

                .then(Commands.argument("player", StringArgumentType.word())
                        .suggests(Suggest.playersOnline())
                        .executes(ctx -> {

                            ServerPlayer staff = ctx.getSource().getPlayer();
                            if (staff == null) return 0;

                            String name = StringArgumentType.getString(ctx, "player");

                            ServerPlayer target =
                                    ctx.getSource()
                                            .getServer()
                                            .getPlayerList()
                                            .getPlayerByName(name);

                            if (target == null) {
                                staff.sendSystemMessage(
                                        LegacyAmpersand.parse(
                                                "&4&lSTAFF &7&l➤ &cError! Player not found."
                                        )
                                );
                                return 0;
                            }

                            gui.open(staff, target);
                            return 1;
                        })
                )
        );
    }
}