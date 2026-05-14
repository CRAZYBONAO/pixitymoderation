package org.howie.pixity.moderation.neoforge.kits;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.neoforge.economy.EconomyService;
import org.howie.pixity.moderation.neoforge.kits.gui.KitsCategoryGui;
import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;
import org.howie.pixity.moderation.neoforge.util.Suggest;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class KitsCommands {

    public static final String PERM_CREATE = "pixity.kits.create";
    public static final String PERM_DELETE = "pixity.kits.delete";
    public static final String PERM_OPEN = "pixity.kits.open";
    public static final String PERM_CLAIM = "pixity.kits.claim";

    private final KitManager kits;
    private final EconomyService economy;
    private final RankService ranks;

    public KitsCommands(final KitManager kits,
                        final EconomyService economy,
                        final RankService ranks) {

        this.kits = kits;
        this.economy = economy;
        this.ranks = ranks;
    }

    private boolean has(ServerPlayer p, String perm) {
        return ranks != null && ranks.hasPerm(p, perm);
    }

    public void register(CommandDispatcher<CommandSourceStack> d) {


        d.register(Commands.literal("kits")
                .requires(src -> {
                    if (!(src.getEntity() instanceof ServerPlayer p)) return true;
                    return has(p, PERM_OPEN);
                })
                .executes(ctx -> {
                    ServerPlayer p = ctx.getSource().getPlayer();
                    if (p != null) {
                        KitsCategoryGui.open(p, kits, economy);
                        return 1;
                    }
                    return listToSource(ctx.getSource());
                })
        );

        d.register(Commands.literal("editkit")
                .requires(src -> src.getEntity() instanceof ServerPlayer p && has(p, PERM_CREATE))
                .then(Commands.argument("kit", StringArgumentType.word())
                        .suggests(Suggest.kits(kits))
                        .executes(ctx -> {

                            ServerPlayer p = ctx.getSource().getPlayerOrException();
                            String kit = StringArgumentType.getString(ctx, "kit");

                            var opt = kits.getKit(kit);

                            if (opt.isEmpty()) {
                                p.sendSystemMessage(LegacyAmpersand.parse("&c&lKITS &7&l➤ &cError! Kit not found"));
                                return 0;
                            }

                            kits.createFromPlayerInventory(
                                    p,
                                    kit,
                                    opt.get().cooldownSeconds,
                                    opt.get().displayNameRaw,
                                    opt.get().category,
                                    opt.get().price,
                                    opt.get().currency
                            );

                            p.sendSystemMessage(
                                    LegacyAmpersand.parse("&c&lKITS &7&l➤ &aEdited kit &e" + kit)
                            );

                            return 1;
                        })
                )
        );

        d.register(Commands.literal("setkitprice")
                .requires(src -> src.getEntity() instanceof ServerPlayer p && has(p, PERM_CREATE))
                .then(Commands.argument("kit", StringArgumentType.word())
                        .suggests(Suggest.kits(kits))
                        .then(Commands.argument("price", com.mojang.brigadier.arguments.DoubleArgumentType.doubleArg(0))
                                .then(Commands.argument("currency", StringArgumentType.word())
                                        .executes(ctx -> {

                                            ServerPlayer p = ctx.getSource().getPlayerOrException();

                                            String kit = StringArgumentType.getString(ctx,"kit");
                                            double price = com.mojang.brigadier.arguments.DoubleArgumentType.getDouble(ctx,"price");
                                            String currency = StringArgumentType.getString(ctx,"currency");

                                            kits.setPrice(kit, price, currency);

                                            p.sendSystemMessage(
                                                    LegacyAmpersand.parse("&c&lKITS &7&l➤&aSet price for &e" + kit)
                                            );

                                            return 1;
                                        })
                                )
                                .executes(ctx -> {

                                    ServerPlayer p = ctx.getSource().getPlayerOrException();

                                    String kit = StringArgumentType.getString(ctx,"kit");
                                    double price = com.mojang.brigadier.arguments.DoubleArgumentType.getDouble(ctx,"price");

                                    kits.setPrice(kit, price, "MONEY");

                                    p.sendSystemMessage(
                                            LegacyAmpersand.parse("&c&lKITS &7&l➤ &aSet price for &e" + kit)
                                    );

                                    return 1;
                                })
                        )
                )
        );

        d.register(Commands.literal("setkitdisplay")
                .requires(src -> src.getEntity() instanceof ServerPlayer p && has(p, PERM_CREATE))
                .then(Commands.argument("kit", StringArgumentType.word())
                        .suggests(Suggest.kits(kits))
                        .then(Commands.argument("display", StringArgumentType.greedyString())
                                .executes(ctx -> {

                                    ServerPlayer p = ctx.getSource().getPlayerOrException();

                                    String kit = StringArgumentType.getString(ctx,"kit");
                                    String display = StringArgumentType.getString(ctx,"display");

                                    kits.setDisplay(kit, display);

                                    p.sendSystemMessage(
                                            LegacyAmpersand.parse("&c&lKITS &7&l➤ &aUpdated display for &e" + kit)
                                    );

                                    return 1;
                                })
                        )
                )
        );

        d.register(Commands.literal("setkiticon")
                .requires(src -> src.getEntity() instanceof ServerPlayer p && has(p, PERM_CREATE))
                .then(Commands.argument("kit", StringArgumentType.word())
                        .suggests(Suggest.kits(kits))
                        .executes(ctx -> {

                            ServerPlayer p = ctx.getSource().getPlayerOrException();
                            String kit = StringArgumentType.getString(ctx,"kit");

                            if (!kits.setIcon(p, kit)) {
                                p.sendSystemMessage(
                                        LegacyAmpersand.parse("&c&lKITS &7&l➤ &cError! Hold item first")
                                );
                                return 0;
                            }

                            p.sendSystemMessage(
                                    LegacyAmpersand.parse("&c&lKITS &7&l➤ &aUpdated icon for &e" + kit)
                            );

                            return 1;
                        })
                )
        );

        d.register(Commands.literal("kitpreview")
                .then(Commands.argument("kit", StringArgumentType.word())
                        .suggests(Suggest.kits(kits))
                        .executes(ctx -> {

                            ServerPlayer p = ctx.getSource().getPlayerOrException();
                            String kit = StringArgumentType.getString(ctx,"kit");

                            var opt = kits.getKit(kit);

                            if (opt.isEmpty()) {
                                p.sendSystemMessage(LegacyAmpersand.parse("&c&lKITS &7&l➤ &cError! Kit not found"));
                                return 0;
                            }

                            org.howie.pixity.moderation.neoforge.kits.gui.KitPreviewGui.open(
                                    p,
                                    kits,
                                    economy,
                                    opt.get()
                            );

                            return 1;
                        })
                )
        );


        d.register(Commands.literal("kit")
                .then(Commands.argument("name", StringArgumentType.word())
                        .suggests(Suggest.kits(kits))
                        .executes(ctx -> {

                            ServerPlayer p = ctx.getSource().getPlayerOrException();
                            String name = StringArgumentType.getString(ctx, "name");

                            var opt = kits.getKit(name);

                            if (opt.isEmpty()) {
                                p.sendSystemMessage(
                                        LegacyAmpersand.parse("&c&lKITS &7&l➤ &cKit not found")
                                );
                                return 0;
                            }

                            kits.tryClaimKit(p, opt.get());
                            return 1;
                        })
                )
        );


        d.register(Commands.literal("createkit")
                .requires(src -> src.getEntity() instanceof ServerPlayer p && has(p, PERM_CREATE))

                .then(Commands.argument("name", StringArgumentType.word())

                        .then(Commands.argument("cooldown", LongArgumentType.longArg(0))

                                .then(Commands.argument("category", StringArgumentType.word())

                                        .then(Commands.argument("display", StringArgumentType.greedyString())
                                                .executes(ctx -> {

                                                    ServerPlayer p = ctx.getSource().getPlayerOrException();

                                                    String name = StringArgumentType.getString(ctx, "name");
                                                    long cooldown = LongArgumentType.getLong(ctx, "cooldown");
                                                    String category = StringArgumentType.getString(ctx, "category");
                                                    String display = StringArgumentType.getString(ctx, "display");

                                                    kits.createFromPlayerInventory(
                                                            p,
                                                            name,
                                                            cooldown,
                                                            display,
                                                            category,
                                                            0,
                                                            "MONEY"
                                                    );

                                                    p.sendSystemMessage(
                                                            LegacyAmpersand.parse(
                                                                    "&c&lKITS &7&l➤ &aCreated kit &e" + name
                                                            )
                                                    );

                                                    return 1;
                                                })
                                        )

                                        .executes(ctx -> {

                                            ServerPlayer p = ctx.getSource().getPlayerOrException();

                                            String name = StringArgumentType.getString(ctx, "name");
                                            long cooldown = LongArgumentType.getLong(ctx, "cooldown");
                                            String category = StringArgumentType.getString(ctx, "category");

                                            kits.createFromPlayerInventory(
                                                    p,
                                                    name,
                                                    cooldown,
                                                    name,
                                                    category,
                                                    0,
                                                    "MONEY"
                                            );

                                            p.sendSystemMessage(
                                                    LegacyAmpersand.parse(
                                                            "&c&lKITS &7&l➤ &aCreated kit &e" + name
                                                    )
                                            );

                                            return 1;
                                        })
                                )
                        )
                )
        );


        d.register(Commands.literal("deletekit")
                .requires(src -> src.getEntity() instanceof ServerPlayer p && has(p, PERM_DELETE))
                .then(Commands.argument("name", StringArgumentType.word())
                        .suggests(Suggest.kits(kits))
                        .executes(ctx -> {

                            ServerPlayer p = ctx.getSource().getPlayerOrException();
                            String name = StringArgumentType.getString(ctx, "name");

                            if (kits.deleteKit(name)) {
                                p.sendSystemMessage(
                                        LegacyAmpersand.parse("&c&lKITS &7&l➤ &aDeleted kit &e" + name)
                                );
                            } else {
                                p.sendSystemMessage(
                                        LegacyAmpersand.parse("&c&lKITS &7&l➤ &cKit not found")
                                );
                            }

                            return 1;
                        })
                )
        );
    }

    private int listToSource(final CommandSourceStack src) {

        List<Kit> all = kits.allKits().stream()
                .sorted(Comparator.comparing(k -> k.name))
                .collect(Collectors.toList());

        if (all.isEmpty()) {
            src.sendSuccess(() -> LegacyAmpersand.parse("&c&lKITS &7&l➤ &cError! No kits configured."), false);
            return 1;
        }

        src.sendSuccess(() -> LegacyAmpersand.parse("&c&lKITS &7&l➤ &aKits:"), false);

        for (Kit k : all) {
            Component line = Component.literal(" - ")
                    .append(kits.renderDisplayName(k.displayNameRaw))
                    .append(Component.literal(" (" + k.name + ")"));

            src.sendSuccess(() -> line, false);
        }

        return 1;
    }
}