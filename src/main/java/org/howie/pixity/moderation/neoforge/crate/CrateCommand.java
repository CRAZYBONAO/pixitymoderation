package org.howie.pixity.moderation.neoforge.crate;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class CrateCommand {

    public static void register(CommandDispatcher<CommandSourceStack> d) {

        d.register(Commands.literal("crates")

                .then(Commands.literal("give")

                        .then(Commands.argument("crate", StringArgumentType.word())

                                .then(Commands.argument("player", EntityArgument.player())

                                        .then(Commands.argument(
                                                                "amount",
                                                                IntegerArgumentType.integer(1)
                                                        )

                                                        .executes(ctx -> {

                                                            String id =
                                                                    StringArgumentType.getString(
                                                                            ctx,
                                                                            "crate"
                                                                    );

                                                            ServerPlayer player =
                                                                    EntityArgument.getPlayer(
                                                                            ctx,
                                                                            "player"
                                                                    );

                                                            int amount =
                                                                    IntegerArgumentType.getInteger(
                                                                            ctx,
                                                                            "amount"
                                                                    );

                                                            var crate =
                                                                    CrateManager.get(id);

                                                            if (crate == null) {
                                                                return 0;
                                                            }

                                                            var key =
                                                                    CrateKeyService.create(id);

                                                            key.setCount(amount);

                                                            player.getInventory()
                                                                    .add(key);

                                                            ctx.getSource().sendSuccess(

                                                                    () -> Component.literal(

                                                                            "§aGave "
                                                                                    + amount
                                                                                    + "x "
                                                                                    + id
                                                                                    + " crate key(s) to "
                                                                                    + player.getName().getString()
                                                                    ),

                                                                    true
                                                            );

                                                            return 1;
                                                        })
                                        )
                                )
                        )
                )

                .then(Commands.literal("aura")
                        .then(Commands.argument("crate", StringArgumentType.word())
                                .then(Commands.argument("preset", StringArgumentType.word())
                                        .executes(ctx -> {

                                            String crate = StringArgumentType.getString(ctx, "crate");
                                            var aura = new CrateAuraManager.Aura();

                                            CrateAuraManager.set(crate, aura);

                                            ctx.getSource().sendSuccess(() ->
                                                    Component.literal("§aAura set for crate: " + crate), true);

                                            return 1;
                                        })
                                )
                        )
                )

                .then(Commands.literal("set")
                        .then(Commands.argument("crate", StringArgumentType.word())
                                .executes(ctx -> {

                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                    String id = StringArgumentType.getString(ctx, "crate");

                                    var hit = player.pick(5, 0, false);

                                    if (!(hit instanceof net.minecraft.world.phys.BlockHitResult bhr)) {
                                        ctx.getSource().sendFailure(Component.literal("§cLook at a block."));
                                        return 0;
                                    }

                                    var pos = bhr.getBlockPos();
                                    var level = player.serverLevel();

                                    if (CrateManager.get(id) == null) {
                                        ctx.getSource().sendFailure(Component.literal("§cCrate not found."));
                                        return 0;
                                    }

                                    CrateBlockManager.set(level, pos, id);
                                    CrateHologramService.create(level, pos, id);

                                    ctx.getSource().sendSuccess(() ->
                                            Component.literal("§aCrate set at block."), true);

                                    return 1;
                                })
                        )
                )

                .then(Commands.literal("remove")
                        .executes(ctx -> {

                            ServerPlayer player = ctx.getSource().getPlayerOrException();

                            var hit = player.pick(5, 0, false);

                            if (!(hit instanceof net.minecraft.world.phys.BlockHitResult bhr)) {
                                ctx.getSource().sendFailure(Component.literal("§cLook at a block."));
                                return 0;
                            }

                            CrateBlockManager.remove(player.serverLevel(), bhr.getBlockPos());
                            CrateHologramService.remove(player.serverLevel(), bhr.getBlockPos());

                            ctx.getSource().sendSuccess(() ->
                                    Component.literal("§aCrate removed."), true);

                            return 1;
                        })
                )

                .then(Commands.literal("reload")
                        .executes(ctx -> {
                            CrateManager.load();
                            CrateBlockManager.load();

                            var server = ctx.getSource().getServer();

                            server.getAllLevels().forEach(level ->
                                    CrateHologramService.reloadAll(level)
                            );

                            return 1;
                        })
                )
        );
    }
}