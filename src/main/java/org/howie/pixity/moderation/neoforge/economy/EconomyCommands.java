package org.howie.pixity.moderation.neoforge.economy;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;
import org.howie.pixity.moderation.neoforge.util.Suggest;

import java.util.UUID;

public final class EconomyCommands {

    private final EconomyService econ;
    private final RankService ranks;

    public EconomyCommands(EconomyService econ, RankService ranks) {
        this.econ = econ;
        this.ranks = ranks;
    }

    private boolean has(ServerPlayer p, String perm) {
        return ranks.hasPerm(p, perm) || ranks.hasPerm(p, "pixity.admin");
    }

    public void register(CommandDispatcher<CommandSourceStack> d) {

        d.register(Commands.literal("balance")
                .requires(src -> src.getEntity() instanceof ServerPlayer p && has(p, "pixity.economy.balance"))
                .executes(ctx -> showAll(ctx.getSource().getPlayer()))
        );

        d.register(Commands.literal("bal")
                .requires(src -> src.getEntity() instanceof ServerPlayer p && has(p, "pixity.economy.balance"))
                .executes(ctx -> showAll(ctx.getSource().getPlayer()))
        );

        d.register(Commands.literal("money")
                .requires(src -> src.getEntity() instanceof ServerPlayer p && has(p, "pixity.economy.balance"))
                .executes(ctx -> showSingle(ctx.getSource().getPlayer(), CurrencyType.MONEY))
        );

        d.register(Commands.literal("coins")
                .requires(src -> src.getEntity() instanceof ServerPlayer p && has(p, "pixity.economy.balance"))
                .executes(ctx -> showSingle(ctx.getSource().getPlayer(), CurrencyType.COINS))
        );

        d.register(Commands.literal("tokens")
                .requires(src -> src.getEntity() instanceof ServerPlayer p && has(p, "pixity.economy.balance"))
                .executes(ctx -> showSingle(ctx.getSource().getPlayer(), CurrencyType.TOKENS))
        );

        d.register(Commands.literal("pay")
                .requires(src -> src.getEntity() instanceof ServerPlayer p && has(p, "pixity.economy.pay"))
                .then(Commands.argument("player", StringArgumentType.word())
                        .suggests(Suggest.playersOnline())
                        .then(Commands.argument("amount", DoubleArgumentType.doubleArg(0.01))
                                .executes(ctx -> pay(ctx, CurrencyType.MONEY))
                                .then(Commands.argument("type", StringArgumentType.word())
                                        .suggests((ctx, builder) -> {
                                            for (CurrencyType t : CurrencyType.values()) {
                                                builder.suggest(t.name().toLowerCase());
                                            }
                                            return builder.buildFuture();
                                        })
                                        .executes(ctx -> {
                                            String typeStr = StringArgumentType.getString(ctx, "type");

                                            try {
                                                return pay(ctx, CurrencyType.valueOf(typeStr.toUpperCase()));
                                            } catch (Exception ex) {
                                                ctx.getSource().getPlayer().sendSystemMessage(
                                                        LegacyAmpersand.parse("&e&lECONOMY &7&l➤ &cError! Invalid currency type")
                                                );
                                                return 0;
                                            }
                                        })
                                )
                        )
                )
        );

        d.register(Commands.literal("baltop")
                .requires(src -> src.getEntity() instanceof ServerPlayer p &&
                        has(p, "pixity.economy.baltop"))
                .executes(ctx -> {

                    ServerPlayer p = ctx.getSource().getPlayer();

                    p.openMenu(
                            BalTopMenu.provider(
                                    ctx.getSource().getServer(),
                                    econ,
                                    CurrencyType.MONEY
                            )
                    );

                    return 1;
                })

                .then(Commands.argument("type", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            for (CurrencyType t : CurrencyType.values()) {
                                builder.suggest(t.name().toLowerCase());
                            }
                            return builder.buildFuture();
                        })
                        .executes(ctx -> {

                            ServerPlayer p = ctx.getSource().getPlayer();

                            String typeStr =
                                    StringArgumentType.getString(ctx, "type");

                            CurrencyType type;

                            try {
                                type = CurrencyType.valueOf(typeStr.toUpperCase());
                            } catch (Exception e) {
                                p.sendSystemMessage(
                                        LegacyAmpersand.parse(
                                                "&e&lECONOMY &7&l➤ &cInvalid currency type"
                                        )
                                );
                                return 0;
                            }

                            p.openMenu(
                                    BalTopMenu.provider(
                                            ctx.getSource().getServer(),
                                            econ,
                                            type
                                    )
                            );

                            return 1;
                        })
                )
        );

        d.register(Commands.literal("eco")
                .requires(src -> {
                    if (src.getEntity() == null) return true;
                    if (src.getEntity() instanceof ServerPlayer p) return has(p, "pixity.economy.admin");
                    return false;
                })
                .then(Commands.literal("give").then(modifyNode("give")))
                .then(Commands.literal("take").then(modifyNode("take")))
                .then(Commands.literal("set").then(modifyNode("set")))
        );
    }

    private com.mojang.brigadier.builder.ArgumentBuilder<CommandSourceStack, ?> modifyNode(String action) {
        return Commands.argument("player", StringArgumentType.word())
                .suggests(Suggest.playersOnline())
                .then(Commands.argument("type", StringArgumentType.word())
                        .then(Commands.argument("amount", DoubleArgumentType.doubleArg(0.01))
                                .executes(ctx -> modify(ctx, action))
                        )
                );
    }

    private int modify(com.mojang.brigadier.context.CommandContext<CommandSourceStack> ctx, String action) {

        CommandSourceStack src = ctx.getSource();
        MinecraftServer server = src.getServer();

        String name = StringArgumentType.getString(ctx, "player");
        String typeStr = StringArgumentType.getString(ctx, "type");
        double amount = DoubleArgumentType.getDouble(ctx, "amount");

        CurrencyType type;

        try {
            type = CurrencyType.valueOf(typeStr.toUpperCase());
        } catch (Exception e) {
            src.sendFailure(LegacyAmpersand.parse("&e&lECONOMY &7&l➤ &cError! Invalid currency type"));
            return 0;
        }

        UUID uuid = resolveUuid(server, name);
        if (uuid == null) {
            src.sendFailure(LegacyAmpersand.parse("&e&lECONOMY &7&l➤ &cError! Player not found"));
            return 0;
        }

        switch (action) {
            case "give" -> econ.add(uuid, type, amount);
            case "take" -> econ.remove(uuid, type, amount);
            case "set" -> econ.set(uuid, type, amount);
        }

        src.sendSuccess(() -> LegacyAmpersand.parse("&e&lECONOMY &7&l➤ &e" + name + "'s &7balance has been updated."), false);
        return 1;
    }

    private int showAll(ServerPlayer p) {
        p.sendSystemMessage(LegacyAmpersand.parse(
                "&aMoney: &e" + econ.get(p.getUUID(), CurrencyType.MONEY)
                        + " &eCoins: " + econ.get(p.getUUID(), CurrencyType.COINS)
                        + " &bTokens: " + econ.get(p.getUUID(), CurrencyType.TOKENS)
        ));
        return 1;
    }

    private int showSingle(ServerPlayer p, CurrencyType type) {
        p.sendSystemMessage(Component.literal(
                type.name() + ": " + econ.get(p.getUUID(), type)
        ));
        return 1;
    }

    private int pay(com.mojang.brigadier.context.CommandContext<CommandSourceStack> ctx,
                    CurrencyType type) {

        ServerPlayer sender = ctx.getSource().getPlayer();
        MinecraftServer server = ctx.getSource().getServer();

        String targetName = StringArgumentType.getString(ctx, "player");
        double amount = DoubleArgumentType.getDouble(ctx, "amount");

        ServerPlayer target = server.getPlayerList().getPlayerByName(targetName);

        if (target == null) return 0;
        if (!econ.remove(sender.getUUID(), type, amount)) return 0;

        econ.add(target.getUUID(), type, amount);

        return 1;
    }

    private UUID resolveUuid(MinecraftServer server, String name) {
        ServerPlayer online = server.getPlayerList().getPlayerByName(name);
        if (online != null) return online.getUUID();
        return null;
    }
}