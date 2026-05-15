package org.howie.pixity.moderation.neoforge.contribution;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import net.minecraft.commands.arguments.EntityArgument;

import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

public final class ContributionCommands {

    private final ContributionService contributions;

    public ContributionCommands(
            ContributionService contributions
    ) {

        this.contributions = contributions;
    }

    public void register(
            CommandDispatcher<CommandSourceStack> d
    ) {

        d.register(

                Commands.literal("contribution")

                        /*
                         * /contribution
                         */

                        .executes(ctx -> {

                            ServerPlayer player =
                                    ctx.getSource()
                                            .getPlayerOrException();

                            ContributionData data =
                                    contributions.get(
                                            player.getUUID()
                                    );

                            ctx.getSource().sendSuccess(

                                    () -> LegacyAmpersand.parse(

                                            "&6&lCONTRIBUTIONS &7&l➤ "
                                                    + "&eCurrent: &f$"
                                                    + format(
                                                    data.getCurrent()
                                            )
                                                    + "\n"
                                                    + "&eLifetime: &f$"
                                                    + format(
                                                    data.getLifetime()
                                            )
                                    ),

                                    false
                            );

                            return 1;
                        })

                        /*
                         * /contribution view
                         */

                        .then(

                                Commands.literal("view")

                                        .then(

                                                Commands.argument(
                                                                "player",
                                                                EntityArgument.player()
                                                        )

                                                        .executes(ctx -> {

                                                            ServerPlayer target =
                                                                    EntityArgument.getPlayer(
                                                                            ctx,
                                                                            "player"
                                                                    );

                                                            ContributionData data =
                                                                    contributions.get(
                                                                            target.getUUID()
                                                                    );

                                                            ctx.getSource().sendSuccess(

                                                                    () -> LegacyAmpersand.parse(

                                                                            "&6&lCONTRIBUTIONS\n"
                                                                                    + "&ePlayer: &f"
                                                                                    + target.getGameProfile().getName()
                                                                                    + "\n"
                                                                                    + "&eCurrent: &f$"
                                                                                    + format(
                                                                                    data.getCurrent()
                                                                            )
                                                                                    + "\n"
                                                                                    + "&eLifetime: &f$"
                                                                                    + format(
                                                                                    data.getLifetime()
                                                                            )
                                                                    ),

                                                                    false
                                                            );

                                                            return 1;
                                                        })
                                        )
                        )

                        /*
                         * /contribution add
                         */

                        .then(

                                Commands.literal("add")

                                        .requires(src ->
                                                src.hasPermission(2)
                                        )

                                        .then(

                                                Commands.argument(
                                                                "player",
                                                                EntityArgument.player()
                                                        )

                                                        .then(

                                                                Commands.argument(
                                                                                "amount",
                                                                                DoubleArgumentType.doubleArg(
                                                                                        0
                                                                                )
                                                                        )

                                                                        .executes(ctx -> {

                                                                            ServerPlayer target =
                                                                                    EntityArgument.getPlayer(
                                                                                            ctx,
                                                                                            "player"
                                                                                    );

                                                                            double amount =
                                                                                    DoubleArgumentType.getDouble(
                                                                                            ctx,
                                                                                            "amount"
                                                                                    );

                                                                            contributions.add(
                                                                                    target,
                                                                                    amount
                                                                            );

                                                                            ctx.getSource().sendSuccess(

                                                                                    () -> LegacyAmpersand.parse(

                                                                                            "&aAdded &f$"
                                                                                                    + format(amount)
                                                                                                    + " &ato "
                                                                                                    + target.getGameProfile().getName()
                                                                                    ),

                                                                                    true
                                                                            );

                                                                            return 1;
                                                                        })
                                                        )
                                        )
                        )

                        /*
                         * /contribution remove
                         */

                        .then(

                                Commands.literal("remove")

                                        .requires(src ->
                                                src.hasPermission(2)
                                        )

                                        .then(

                                                Commands.argument(
                                                                "player",
                                                                EntityArgument.player()
                                                        )

                                                        .then(

                                                                Commands.argument(
                                                                                "amount",
                                                                                DoubleArgumentType.doubleArg(
                                                                                        0
                                                                                )
                                                                        )

                                                                        .executes(ctx -> {

                                                                            ServerPlayer target =
                                                                                    EntityArgument.getPlayer(
                                                                                            ctx,
                                                                                            "player"
                                                                                    );

                                                                            double amount =
                                                                                    DoubleArgumentType.getDouble(
                                                                                            ctx,
                                                                                            "amount"
                                                                                    );

                                                                            contributions.remove(
                                                                                    target.getUUID(),
                                                                                    amount
                                                                            );

                                                                            ctx.getSource().sendSuccess(

                                                                                    () -> LegacyAmpersand.parse(

                                                                                            "&cRemoved &f$"
                                                                                                    + format(amount)
                                                                                                    + " &cfrom "
                                                                                                    + target.getGameProfile().getName()
                                                                                    ),

                                                                                    true
                                                                            );

                                                                            return 1;
                                                                        })
                                                        )
                                        )
                        )

                        /*
                         * /contribution set
                         */

                        .then(

                                Commands.literal("set")

                                        .requires(src ->
                                                src.hasPermission(2)
                                        )

                                        .then(

                                                Commands.argument(
                                                                "player",
                                                                EntityArgument.player()
                                                        )

                                                        .then(

                                                                Commands.argument(
                                                                                "amount",
                                                                                DoubleArgumentType.doubleArg(
                                                                                        0
                                                                                )
                                                                        )

                                                                        .executes(ctx -> {

                                                                            ServerPlayer target =
                                                                                    EntityArgument.getPlayer(
                                                                                            ctx,
                                                                                            "player"
                                                                                    );

                                                                            double amount =
                                                                                    DoubleArgumentType.getDouble(
                                                                                            ctx,
                                                                                            "amount"
                                                                                    );

                                                                            contributions.set(
                                                                                    target.getUUID(),
                                                                                    amount
                                                                            );

                                                                            ctx.getSource().sendSuccess(

                                                                                    () -> LegacyAmpersand.parse(

                                                                                            "&eSet "
                                                                                                    + target.getGameProfile().getName()
                                                                                                    + "'s contribution to &f$"
                                                                                                    + format(amount)
                                                                                    ),

                                                                                    true
                                                                            );

                                                                            return 1;
                                                                        })
                                                        )
                                        )
                        )

                        .then(

                                Commands.literal("top")

                                        .executes(ctx -> {

                                            java.util.List<
                                                    java.util.Map.Entry<
                                                            java.util.UUID,
                                                            ContributionData
                                                            >
                                                    > top =
                                                    sorted();

                                            StringBuilder msg =
                                                    new StringBuilder();

                                            msg.append(
                                                    "§6§lTOP CONTRIBUTORS\n"
                                            );

                                            int max =
                                                    Math.min(
                                                            10,
                                                            top.size()
                                                    );

                                            for (int i = 0; i < max; i++) {

                                                var entry =
                                                        top.get(i);

                                                java.util.UUID uuid =
                                                        entry.getKey();

                                                ContributionData data =
                                                        entry.getValue();

                                                String name =
                                                        uuid.toString();

                                                if (ctx.getSource()
                                                        .getServer()
                                                        .getProfileCache() != null) {

                                                    name =
                                                            ctx.getSource()
                                                                    .getServer()
                                                                    .getProfileCache()
                                                                    .get(uuid)
                                                                    .map(p -> p.getName())
                                                                    .orElse(name);
                                                }

                                                msg.append(

                                                        "§e#"
                                                                + (i + 1)
                                                                + " §f"
                                                                + name
                                                                + " §7- §a$"
                                                                + format(
                                                                data.getCurrent()
                                                        )
                                                                + "\n"
                                                );
                                            }

                                            ctx.getSource().sendSuccess(

                                                    () -> LegacyAmpersand.parse(
                                                            msg.toString()
                                                    ),

                                                    false
                                            );

                                            return 1;
                                        })
                        )
        );
    }

    private String format(
            double value
    ) {

        return String.format(
                "%,.2f",
                value
        );
    }

    private java.util.List<
            java.util.Map.Entry<
                    java.util.UUID,
                    ContributionData
                    >
            > sorted() {

        java.util.List<
                java.util.Map.Entry<
                        java.util.UUID,
                        ContributionData
                        >
                > list =
                new java.util.ArrayList<>(

                        contributions.getAll()
                                .entrySet()
                );

        list.sort(

                java.util.Comparator.comparingDouble(

                        (java.util.Map.Entry<
                                java.util.UUID,
                                ContributionData
                                > entry)

                                -> entry.getValue()
                                .getCurrent()

                ).reversed()
        );

        return list;
    }
}