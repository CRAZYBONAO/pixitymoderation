package org.howie.pixity.moderation.neoforge.placeholder;

import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.neoforge.rank.RankHolder;
import org.howie.pixity.moderation.neoforge.rank.RankService;

public class RankPlaceholders {

    public static void register() {

        registerPrefix();

        registerSuffix();
    }

    private static void registerPrefix() {

        PlaceholderRegistry.registerPlayer(

                "%player_rank_prefix%",

                context -> {

                    ServerPlayer player =
                            context.player();

                    if (player == null)
                        return "";

                    RankService ranks =
                            RankHolder.INSTANCE;

                    if (ranks == null)
                        return "";

                    return ranks.prefix(player);
                }
        );
    }

    private static void registerSuffix() {

        PlaceholderRegistry.registerPlayer(

                "%player_rank_suffix%",

                context -> {

                    ServerPlayer player =
                            context.player();

                    if (player == null)
                        return "";

                    RankService ranks =
                            RankHolder.INSTANCE;

                    if (ranks == null)
                        return "";

                    return ranks.suffix(player);
                }
        );
    }
}