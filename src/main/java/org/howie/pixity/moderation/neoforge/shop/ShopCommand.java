package org.howie.pixity.moderation.neoforge.shop;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.shop.gui.ShopMainMenu;
import org.howie.pixity.moderation.neoforge.shop.gui.ShopSearchMenu;

public class ShopCommand {

    private final ShopService shopService;
    private final RankService ranks;
    private final EconomyBridge economyBridge;


    public ShopCommand(ShopService shopService,
                       RankService ranks,
                       EconomyBridge economyBridge
    ) {
        this.shopService = shopService;
        this.ranks = ranks;
        this.economyBridge = economyBridge;
    }

    private boolean has(ServerPlayer p, String perm) {
        return ranks != null && (ranks.hasPerm(p, perm) || ranks.hasPerm(p, "pixity.admin"));
    }

    public void register(CommandDispatcher<CommandSourceStack> d) {

        d.register(Commands.literal("shop")

                .requires(src -> src.getEntity() instanceof ServerPlayer p && ranks.hasPerm(p, "pixity.shop"))

                .executes(ctx -> {

                    ServerPlayer p = ctx.getSource().getPlayerOrException();

                    ShopMainMenu.open(p, shopService, economyBridge, ranks);

                    return 1;
                })
                .then(Commands.literal("reload")
                        .requires(src -> src.getEntity() instanceof ServerPlayer p && ranks.hasPerm(p, "pixity.shop.reload"))
                        .executes(ctx -> {
                            shopService.load();
                            ctx.getSource().sendSuccess(() ->
                                    net.minecraft.network.chat.Component.literal("§aShops reloaded."), false);
                            return 1;
                        })
                )
                .then(Commands.literal("search")
                        .then(Commands.argument("query", StringArgumentType.greedyString())
                                .executes(ctx -> {

                                    ServerPlayer p = ctx.getSource().getPlayerOrException();

                                    String query = StringArgumentType.getString(ctx, "query");

                                    ShopSearchMenu.open(p, shopService, economyBridge, query, 0);

                                    return 1;
                                })
                        )
                )
        );
    }
}