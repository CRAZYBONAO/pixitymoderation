package org.howie.pixity.moderation.neoforge.util;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.neoforge.kits.KitManager;
import org.howie.pixity.moderation.neoforge.tp.TpService;
import org.howie.pixity.moderation.neoforge.jail.JailService;
import org.howie.pixity.moderation.neoforge.freeze.FreezeService;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public final class Suggest {

    private Suggest() {}

public static SuggestionProvider<CommandSourceStack> playersOnline() {
    return (ctx, b) -> {
        var srv = ctx.getSource().getServer();
        if (srv == null) return Suggestions.empty();
        return SharedSuggestionProvider.suggest(
                srv.getPlayerList().getPlayers().stream().map(p -> p.getGameProfile().getName()).toList(),
                b
        );
    };
}


public static com.mojang.brigadier.suggestion.SuggestionProvider<CommandSourceStack> frozenPlayers(final FreezeService freeze) {
    return (ctx, b) -> SharedSuggestionProvider.suggest(freeze.listFrozenNames(), b);
}

    public static SuggestionProvider<CommandSourceStack> jailedPlayers(final JailService jail) {
    return (ctx, b) -> SharedSuggestionProvider.suggest(jail.listJailedNames(), b);
}

    public static SuggestionProvider<CommandSourceStack> homes(final TpService tp) {
        return (ctx, b) -> {
            ServerPlayer p = ctx.getSource().getPlayer();
            if (p == null) return Suggestions.empty();
            List<String> list = tp.listHomes(p);
            return SharedSuggestionProvider.suggest(list, b);
        };
    }

    public static SuggestionProvider<CommandSourceStack> warps(final TpService tp) {
        return (ctx, b) -> SharedSuggestionProvider.suggest(tp.listWarps(), b);
    }

    public static SuggestionProvider<CommandSourceStack> pwarps(final TpService tp) {
        return (ctx, b) -> SharedSuggestionProvider.suggest(tp.listPlayerWarps(), b);
    }

    public static SuggestionProvider<CommandSourceStack> jails(final JailService jail) {
        return (ctx, b) -> SharedSuggestionProvider.suggest(jail.listJails(), b);
    }

    public static SuggestionProvider<CommandSourceStack> kits(final KitManager kits) {
        return (ctx, b) -> SharedSuggestionProvider.suggest(kits.allKits().stream().map(k -> k.name).toList(), b);
    }
}
