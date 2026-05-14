package org.howie.pixity.moderation.neoforge.invsee;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;
import org.howie.pixity.moderation.neoforge.util.Suggest;

public final class InvSeeCommands {

    public static final String PERM_INVSEE_VIEW = "pixity.invsee";
    public static final String PERM_INVSEE_EDIT = "pixity.invsee.edit";

    private final RankService ranks;

    public InvSeeCommands(final RankService ranks) {
        this.ranks = ranks;
    }

    private boolean has(ServerPlayer p, String perm) {
        return ranks != null && ranks.hasPerm(p, perm);
    }

    public void register(CommandDispatcher<CommandSourceStack> d) {


        d.register(Commands.literal("invsee")
                .requires(src -> {
                    if (!(src.getEntity() instanceof ServerPlayer p)) return false;
                    return has(p, PERM_INVSEE_VIEW);
                })
                .then(Commands.argument("player", StringArgumentType.word())
                        .suggests(Suggest.playersOnline())
                        .executes(ctx -> {

                            ServerPlayer viewer = ctx.getSource().getPlayerOrException();
                            MinecraftServer server = viewer.server;

                            String name = StringArgumentType.getString(ctx, "player");
                            ServerPlayer target = server.getPlayerList().getPlayerByName(name);

                            if (target == null) {
                                ctx.getSource().sendFailure(
                                        LegacyAmpersand.parse("&4&lSTAFF &7&l➤ &cError! Player must be online.")
                                );
                                return 0;
                            }

                            boolean editable = has(viewer, PERM_INVSEE_EDIT);

                            InvSeeGui.openInv(viewer, target, editable);
                            return 1;
                        })
                )
        );

        d.register(Commands.literal("endersee")
                .requires(src -> {
                    if (!(src.getEntity() instanceof ServerPlayer p)) return false;
                    return has(p, PERM_INVSEE_VIEW);
                })
                .then(Commands.argument("player", StringArgumentType.word())
                        .suggests(Suggest.playersOnline())
                        .executes(ctx -> {

                            ServerPlayer viewer = ctx.getSource().getPlayerOrException();
                            MinecraftServer server = viewer.server;

                            String name = StringArgumentType.getString(ctx, "player");
                            ServerPlayer target = server.getPlayerList().getPlayerByName(name);

                            if (target == null) {
                                ctx.getSource().sendFailure(
                                        LegacyAmpersand.parse("&4&lSTAFF &7&l➤ &cError! Player must be online.")
                                );
                                return 0;
                            }

                            boolean editable = has(viewer, PERM_INVSEE_EDIT);

                            InvSeeGui.openEnder(viewer, target, editable);
                            return 1;
                        })
                )
        );
    }
}