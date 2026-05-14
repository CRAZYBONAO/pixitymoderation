package org.howie.pixity.moderation.neoforge.jail;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

public final class JailAdminCommands {

    private final JailService jail;
    private final RankService ranks;

    public JailAdminCommands(JailService jail, RankService ranks) {
        this.jail = jail;
        this.ranks = ranks;
    }

    private boolean has(CommandSourceStack src, String perm) {
        if (!(src.getEntity() instanceof ServerPlayer p)) return src.hasPermission(2);
        return ranks.hasPerm(p, perm);
    }

    public void register(CommandDispatcher<CommandSourceStack> d) {

        d.register(Commands.literal("setjail")
                .requires(src -> has(src,"pixity.jail.set"))
                .then(Commands.argument("name", StringArgumentType.word())
                        .executes(ctx -> {

                            ServerPlayer p = ctx.getSource().getPlayer();
                            if (p == null) return 0;

                            jail.setJail(p,
                                    StringArgumentType.getString(ctx, "name")
                            );

                            return 1;
                        })
                )
        );

        d.register(Commands.literal("jails")
                .requires(src -> has(src,"pixity.jail.list"))
                .executes(ctx -> {

                    ctx.getSource().sendSuccess(() ->
                            LegacyAmpersand.parse(
                                    "&c&lPUNISHMENTS &7&l➤ &cJails:" + String.join(", ", jail.listJails())
                            ), false);

                    return 1;
                })
        );

        d.register(Commands.literal("deljail")
                .requires(src -> has(src,"pixity.jail.delete"))
                .then(Commands.argument("name", StringArgumentType.word())
                        .executes(ctx -> {

                            jail.deleteJail(
                                    StringArgumentType.getString(ctx, "name")
                            );

                            return 1;
                        })
                )
        );
    }
}