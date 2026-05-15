package org.howie.pixity.moderation.neoforge.hologram;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;

public class HologramCommands {

    public static void register(
            com.mojang.brigadier.CommandDispatcher<CommandSourceStack> dispatcher
    ) {

        dispatcher.register(

                Commands.literal("hologram")

                        .requires(s -> s.hasPermission(2))

                        .then(

                                Commands.literal("create")

                                        .then(
                                                Commands.argument(
                                                                "id",
                                                                StringArgumentType.word()
                                                        )

                                                        .then(
                                                                Commands.argument(
                                                                                "line",
                                                                                StringArgumentType.greedyString()
                                                                        )

                                                                        .executes(ctx -> {

                                                                            ServerPlayer player =
                                                                                    ctx.getSource()
                                                                                            .getPlayerOrException();

                                                                            String id =
                                                                                    StringArgumentType.getString(
                                                                                            ctx,
                                                                                            "id"
                                                                                    );

                                                                            String line =
                                                                                    StringArgumentType.getString(
                                                                                            ctx,
                                                                                            "line"
                                                                                    );

                                                                            HologramManager.create(
                                                                                    id,
                                                                                    player.serverLevel(),
                                                                                    player.blockPosition(),
                                                                                    line
                                                                            );

                                                                            ctx.getSource().sendSuccess(
                                                                                    () -> Component.literal(
                                                                                            "Created hologram " + id
                                                                                    ),
                                                                                    false
                                                                            );

                                                                            return 1;
                                                                        })
                                                        )
                                        )
                        )

                        .then(

                                Commands.literal("movehere")

                                        .then(
                                                Commands.argument(
                                                                "id",
                                                                StringArgumentType.word()
                                                        )

                                                        .executes(ctx -> {

                                                            ServerPlayer player =
                                                                    ctx.getSource()
                                                                            .getPlayerOrException();

                                                            String id =
                                                                    StringArgumentType.getString(
                                                                            ctx,
                                                                            "id"
                                                                    );

                                                            Hologram hologram =
                                                                    HologramManager.get(id);

                                                            if (hologram == null) {

                                                                ctx.getSource().sendFailure(
                                                                        Component.literal(
                                                                                "Unknown hologram."
                                                                        )
                                                                );

                                                                return 0;
                                                            }

                                                            hologram.setPos(
                                                                    player.blockPosition()
                                                            );

                                                            hologram.respawn();

                                                            HologramStorageService.saveAll();

                                                            ctx.getSource().sendSuccess(
                                                                    () -> Component.literal(
                                                                            "Moved hologram " + id
                                                                    ),
                                                                    false
                                                            );

                                                            return 1;
                                                        })
                                        )
                        )

                        .then(

                                Commands.literal("teleport")

                                        .then(
                                                Commands.argument(
                                                                "id",
                                                                StringArgumentType.word()
                                                        )

                                                        .executes(ctx -> {

                                                            ServerPlayer player =
                                                                    ctx.getSource()
                                                                            .getPlayerOrException();

                                                            String id =
                                                                    StringArgumentType.getString(
                                                                            ctx,
                                                                            "id"
                                                                    );

                                                            Hologram hologram =
                                                                    HologramManager.get(id);

                                                            if (hologram == null) {

                                                                ctx.getSource().sendFailure(
                                                                        Component.literal(
                                                                                "Unknown hologram."
                                                                        )
                                                                );

                                                                return 0;
                                                            }

                                                            BlockPos pos =
                                                                    hologram.pos();

                                                            player.teleportTo(

                                                                    player.serverLevel(),

                                                                    pos.getX() + 0.5,
                                                                    pos.getY(),
                                                                    pos.getZ() + 0.5,

                                                                    player.getYRot(),
                                                                    player.getXRot()
                                                            );

                                                            ctx.getSource().sendSuccess(
                                                                    () -> Component.literal(
                                                                            "Teleported to hologram " + id
                                                                    ),
                                                                    false
                                                            );

                                                            return 1;
                                                        })
                                        )
                        )

                        .then(

                                Commands.literal("distance")

                                        .then(
                                                Commands.argument(
                                                                "id",
                                                                StringArgumentType.word()
                                                        )

                                                        .then(
                                                                Commands.argument(
                                                                                "distance",
                                                                                IntegerArgumentType.integer(1)
                                                                        )

                                                                        .executes(ctx -> {

                                                                            String id =
                                                                                    StringArgumentType.getString(
                                                                                            ctx,
                                                                                            "id"
                                                                                    );

                                                                            int distance =
                                                                                    IntegerArgumentType.getInteger(
                                                                                            ctx,
                                                                                            "distance"
                                                                                    );

                                                                            Hologram hologram =
                                                                                    HologramManager.get(id);

                                                                            if (hologram == null)
                                                                                return 0;

                                                                            hologram.setViewDistance(
                                                                                    distance
                                                                            );

                                                                            ctx.getSource().sendSuccess(
                                                                                    () -> Component.literal(
                                                                                            "Set hologram distance to "
                                                                                                    + distance
                                                                                    ),
                                                                                    false
                                                                            );

                                                                            return 1;
                                                                        })
                                                        )
                                        )
                        )

                        .then(

                                Commands.literal("reload")

                                        .executes(ctx -> {

                                            HologramManager.reload();

                                            ctx.getSource().sendSuccess(
                                                    () -> Component.literal(
                                                            "Reloaded holograms."
                                                    ),
                                                    false
                                            );

                                            return 1;
                                        })
                        )

                        .then(

                                Commands.literal("spacing")

                                        .then(
                                                Commands.argument(
                                                                "id",
                                                                StringArgumentType.word()
                                                        )

                                                        .then(
                                                                Commands.argument(
                                                                                "spacing",
                                                                                com.mojang.brigadier.arguments.DoubleArgumentType.doubleArg(0.05)
                                                                        )

                                                                        .executes(ctx -> {

                                                                            String id =
                                                                                    StringArgumentType.getString(
                                                                                            ctx,
                                                                                            "id"
                                                                                    );

                                                                            double spacing =
                                                                                    com.mojang.brigadier.arguments.DoubleArgumentType.getDouble(
                                                                                            ctx,
                                                                                            "spacing"
                                                                                    );

                                                                            Hologram hologram =
                                                                                    HologramManager.get(id);

                                                                            if (hologram == null) {

                                                                                ctx.getSource().sendFailure(
                                                                                        Component.literal(
                                                                                                "Unknown hologram."
                                                                                        )
                                                                                );

                                                                                return 0;
                                                                            }

                                                                            hologram.setLineSpacing(
                                                                                    spacing
                                                                            );

                                                                            ctx.getSource().sendSuccess(
                                                                                    () -> Component.literal(
                                                                                            "Set spacing to "
                                                                                                    + spacing
                                                                                    ),
                                                                                    false
                                                                            );

                                                                            return 1;
                                                                        })
                                                        )
                                        )
                        )

                        .then(

                                Commands.literal("animation")

                                        .then(
                                                Commands.argument(
                                                                "id",
                                                                StringArgumentType.word()
                                                        )

                                                        .then(
                                                                Commands.argument(
                                                                                "line",
                                                                                IntegerArgumentType.integer(1)
                                                                        )

                                                                        .then(
                                                                                Commands.argument(
                                                                                                "type",
                                                                                                StringArgumentType.word()
                                                                                        )

                                                                                        .executes(ctx -> {

                                                                                            String id =
                                                                                                    StringArgumentType.getString(
                                                                                                            ctx,
                                                                                                            "id"
                                                                                                    );

                                                                                            int line =
                                                                                                    IntegerArgumentType.getInteger(
                                                                                                            ctx,
                                                                                                            "line"
                                                                                                    ) - 1;

                                                                                            String type =
                                                                                                    StringArgumentType.getString(
                                                                                                            ctx,
                                                                                                            "type"
                                                                                                    );

                                                                                            Hologram hologram =
                                                                                                    HologramManager.get(id);

                                                                                            if (hologram == null)
                                                                                                return 0;

                                                                                            try {

                                                                                                hologram.setAnimation(

                                                                                                        line,

                                                                                                        org.howie.pixity.moderation.neoforge.hologram.animation.HologramAnimationType.valueOf(
                                                                                                                type.toUpperCase()
                                                                                                        )
                                                                                                );

                                                                                            } catch (Exception e) {

                                                                                                ctx.getSource().sendFailure(
                                                                                                        Component.literal(
                                                                                                                "Invalid animation."
                                                                                                        )
                                                                                                );

                                                                                                return 0;
                                                                                            }

                                                                                            ctx.getSource().sendSuccess(
                                                                                                    () -> Component.literal(
                                                                                                            "Updated hologram animation."
                                                                                                    ),
                                                                                                    false
                                                                                            );

                                                                                            return 1;
                                                                                        })
                                                                        )
                                                        )
                                        )
                        )

                        .then(

                                Commands.literal("template")

                                        .then(
                                                Commands.argument(
                                                                "id",
                                                                StringArgumentType.word()
                                                        )

                                                        .then(
                                                                Commands.argument(
                                                                                "template",
                                                                                StringArgumentType.word()
                                                                        )

                                                                        .executes(ctx -> {

                                                                            String id =
                                                                                    StringArgumentType.getString(
                                                                                            ctx,
                                                                                            "id"
                                                                                    );

                                                                            String template =
                                                                                    StringArgumentType.getString(
                                                                                            ctx,
                                                                                            "template"
                                                                                    );

                                                                            Hologram hologram =
                                                                                    HologramManager.get(id);

                                                                            if (hologram == null) {

                                                                                ctx.getSource().sendFailure(
                                                                                        Component.literal(
                                                                                                "Unknown hologram."
                                                                                        )
                                                                                );

                                                                                return 0;
                                                                            }

                                                                            var t =
                                                                                    org.howie.pixity.moderation.neoforge.hologram.template.HologramTemplateRegistry.get(
                                                                                            template
                                                                                    );

                                                                            if (t == null) {

                                                                                ctx.getSource().sendFailure(
                                                                                        Component.literal(
                                                                                                "Unknown template."
                                                                                        )
                                                                                );

                                                                                return 0;
                                                                            }

                                                                            t.apply(hologram);

                                                                            hologram.respawn();

                                                                            ctx.getSource().sendSuccess(
                                                                                    () -> Component.literal(
                                                                                            "Applied template "
                                                                                                    + template
                                                                                    ),
                                                                                    false
                                                                            );

                                                                            return 1;
                                                                        })
                                                        )
                                        )
                        )

                        .then(

                                Commands.literal("delete")

                                        .then(
                                                Commands.argument(
                                                                "id",
                                                                StringArgumentType.word()
                                                        )

                                                        .executes(ctx -> {

                                                            String id =
                                                                    StringArgumentType.getString(
                                                                            ctx,
                                                                            "id"
                                                                    );

                                                            HologramManager.delete(id);

                                                            ctx.getSource().sendSuccess(
                                                                    () -> Component.literal(
                                                                            "Deleted hologram " + id
                                                                    ),
                                                                    false
                                                            );

                                                            return 1;
                                                        })
                                        )
                        )

                        .then(

                                Commands.literal("addline")

                                        .then(
                                                Commands.argument(
                                                                "id",
                                                                StringArgumentType.word()
                                                        )

                                                        .then(
                                                                Commands.argument(
                                                                                "line",
                                                                                StringArgumentType.greedyString()
                                                                        )

                                                                        .executes(ctx -> {

                                                                            String id =
                                                                                    StringArgumentType.getString(
                                                                                            ctx,
                                                                                            "id"
                                                                                    );

                                                                            String line =
                                                                                    StringArgumentType.getString(
                                                                                            ctx,
                                                                                            "line"
                                                                                    );

                                                                            Hologram hologram =
                                                                                    HologramManager.get(id);

                                                                            if (hologram == null)
                                                                                return 0;

                                                                            hologram.addLine(line);

                                                                            return 1;
                                                                        })
                                                        )
                                        )
                        )

                        .then(

                                Commands.literal("deleteline")

                                        .then(
                                                Commands.argument(
                                                                "id",
                                                                StringArgumentType.word()
                                                        )

                                                        .then(
                                                                Commands.argument(
                                                                                "line",
                                                                                IntegerArgumentType.integer(1)
                                                                        )

                                                                        .executes(ctx -> {

                                                                            String id =
                                                                                    StringArgumentType.getString(
                                                                                            ctx,
                                                                                            "id"
                                                                                    );

                                                                            int line =
                                                                                    IntegerArgumentType.getInteger(
                                                                                            ctx,
                                                                                            "line"
                                                                                    );

                                                                            Hologram hologram =
                                                                                    HologramManager.get(id);

                                                                            if (hologram == null)
                                                                                return 0;

                                                                            hologram.removeLine(
                                                                                    line - 1
                                                                            );

                                                                            return 1;
                                                                        })
                                                        )
                                        )
                        )

                        .then(

                                Commands.literal("editline")

                                        .then(
                                                Commands.argument(
                                                                "id",
                                                                StringArgumentType.word()
                                                        )

                                                        .then(
                                                                Commands.argument(
                                                                                "line",
                                                                                IntegerArgumentType.integer(1)
                                                                        )

                                                                        .then(
                                                                                Commands.argument(
                                                                                                "text",
                                                                                                StringArgumentType.greedyString()
                                                                                        )

                                                                                        .executes(ctx -> {

                                                                                            String id =
                                                                                                    StringArgumentType.getString(
                                                                                                            ctx,
                                                                                                            "id"
                                                                                                    );

                                                                                            int line =
                                                                                                    IntegerArgumentType.getInteger(
                                                                                                            ctx,
                                                                                                            "line"
                                                                                                    );

                                                                                            String text =
                                                                                                    StringArgumentType.getString(
                                                                                                            ctx,
                                                                                                            "text"
                                                                                                    );

                                                                                            Hologram hologram =
                                                                                                    HologramManager.get(id);

                                                                                            if (hologram == null)
                                                                                                return 0;

                                                                                            hologram.setLine(
                                                                                                    line - 1,
                                                                                                    text
                                                                                            );

                                                                                            return 1;
                                                                                        })
                                                                        )
                                                        )
                                        )
                        )

                        .then(

                                Commands.literal("list")

                                        .executes(ctx -> {

                                            ctx.getSource().sendSuccess(
                                                    () -> Component.literal(
                                                            "Holograms:"
                                                    ),
                                                    false
                                            );

                                            for (Hologram hologram :
                                                    HologramManager.all()) {

                                                ctx.getSource().sendSuccess(
                                                        () -> Component.literal(
                                                                "- " + hologram.id()
                                                        ),
                                                        false
                                                );
                                            }

                                            return 1;
                                        })
                        )
        );
    }
}