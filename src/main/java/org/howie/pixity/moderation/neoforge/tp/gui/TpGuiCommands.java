package org.howie.pixity.moderation.neoforge.tp.gui;

import com.mojang.brigadier.CommandDispatcher;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;
import org.howie.pixity.moderation.neoforge.tp.TeleportWarmupManager;
import org.howie.pixity.moderation.neoforge.tp.TpService;

import java.util.List;

public final class TpGuiCommands {

    private final TpService tp;
    private final TpChatPromptService prompts;
    private final RankService ranks;

    public TpGuiCommands(final TpService tp,
                         final TeleportWarmupManager warmup,
                         final TpChatPromptService prompts,
                         final RankService ranks) {
        this.tp = tp;
        this.prompts = prompts;
        this.ranks = ranks;
    }

    private boolean has(ServerPlayer p, String perm) {
        return ranks.hasPerm(p, perm) || ranks.hasPerm(p, "pixity.admin");
    }

    public void register(CommandDispatcher<CommandSourceStack> d) {



        d.register(Commands.literal("homes")

                .requires(src -> {
                    if (!(src.getEntity() instanceof ServerPlayer p)) return false;
                    return src.hasPermission(2) || has(p, TpService.PERM_HOME);
                })

                .executes(ctx -> {

                    ServerPlayer p = ctx.getSource().getPlayerOrException();

                    List<String> homes = tp.listHomes(p);

                    if (homes.isEmpty()) {
                        p.sendSystemMessage(LegacyAmpersand.parse("&e&lHOMES &7&l➤ &cError! No homes."));
                        return 1;
                    }

                    prompts.openHomes(p, homes, "");
                    return 1;
                })
        );



        d.register(Commands.literal("pwarps")

                .requires(src -> {
                    if (!(src.getEntity() instanceof ServerPlayer p)) return false;
                    return src.hasPermission(2) || has(p, TpService.PERM_PWARP);
                })

                .executes(ctx -> {

                    ServerPlayer p = ctx.getSource().getPlayerOrException();

                    List<String> list = tp.listPlayerWarps();

                    if (list.isEmpty()) {
                        p.sendSystemMessage(LegacyAmpersand.parse("&d&lPLAYER WARPS &7&l➤ &cError! No player warps."));
                        return 1;
                    }

                    prompts.openPWarps(p, list, "");
                    return 1;
                })
        );
    }
}