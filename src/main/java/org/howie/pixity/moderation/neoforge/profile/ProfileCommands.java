package org.howie.pixity.moderation.neoforge.profile;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.neoforge.alts.AltsService;
import org.howie.pixity.moderation.neoforge.alts.smart.SmartAltService;
import org.howie.pixity.moderation.neoforge.freeze.FreezeService;
import org.howie.pixity.moderation.neoforge.notes.NotesService;
import org.howie.pixity.moderation.neoforge.reports.ReportsService;
import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;
import org.howie.pixity.moderation.neoforge.tp.gui.MenuProviderLike;
import org.howie.pixity.moderation.neoforge.util.Suggest;

public final class ProfileCommands {

    public static final String PERM_PROFILE = "pixity.profile.open";

    private final RankService ranks;
    private final FreezeService freeze;
    private final NotesService notes;
    private final ReportsService reports;
    private final AltsService alts;
    private final SmartAltService smart;

    public ProfileCommands(
            final RankService ranks,
            final FreezeService freeze,
            final NotesService notes,
            final ReportsService reports,
            final AltsService alts,
            final SmartAltService smart
    ) {
        this.ranks = ranks;
        this.freeze = freeze;
        this.notes = notes;
        this.reports = reports;
        this.alts = alts;
        this.smart = smart;
    }

    private boolean has(ServerPlayer p, String perm) {
        return ranks != null && ranks.hasPerm(p, perm);
    }

    public void register(CommandDispatcher<CommandSourceStack> d) {

        d.register(Commands.literal("profile")
                .requires(src -> src.getEntity() instanceof ServerPlayer p && has(p, PERM_PROFILE))

                .then(Commands.argument("player", StringArgumentType.word())
                        .suggests(Suggest.playersOnline())

                        .executes(ctx -> {

                            ServerPlayer viewer = ctx.getSource().getPlayer();
                            if (viewer == null) return 0;

                            MinecraftServer server = ctx.getSource().getServer();
                            String name = StringArgumentType.getString(ctx, "player");

                            ServerPlayer target =
                                    server.getPlayerList().getPlayerByName(name);

                            if (target == null) {
                                viewer.sendSystemMessage(
                                        LegacyAmpersand.parse("&e&lPROFILES &7&l➤ Player must be online.")
                                );
                                return 0;
                            }

                            MenuProviderLike.open(
                                    viewer,
                                    (id, inv) -> new ProfileMenu(
                                            id,
                                            inv,
                                            freeze,
                                            notes,
                                            reports,
                                            alts,
                                            smart,
                                            viewer,
                                            target
                                    ),
                                    LegacyAmpersand.parse(
                                            "&cProfile: &e" +
                                                    target.getGameProfile().getName()
                                    )
                            );

                            return 1;
                        })
                )
        );
    }
}