package org.howie.pixity.moderation.neoforge.pokemon;

import com.mojang.brigadier.CommandDispatcher;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.chat.CachedText;
import org.howie.pixity.moderation.chat.TextFormatter;

import java.sql.ResultSet;

public class PokedexCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(
                Commands.literal("pokedex")




                        .executes(ctx -> {

                            ServerPlayer player = ctx.getSource().getPlayerOrException();

                            int count = PokedexDatabase.getCount(player.getUUID());
                            int shiny = PokedexDatabase.getShinyCount(player.getUUID());

                            player.sendSystemMessage(CachedText.of(
                                    "<rainbow>&l📖 POKEDEX</rainbow>\n"
                                            + "<gold>Normal: </gold>&e" + count + "\n"
                                            + "<light_purple>Shiny: </light_purple>&e" + shiny
                            ));

                            return 1;
                        })

                        .then(Commands.literal("holo")


                                .then(Commands.literal("set")
                                        .then(Commands.argument("type", StringArgumentType.word())
                                                .executes(ctx -> {

                                                    var player = ctx.getSource().getPlayerOrException();
                                                    var pos = player.blockPosition();
                                                    var level = player.serverLevel();

                                                    String type = StringArgumentType.getString(ctx, "type");

                                                    String world = level.dimension().location().toString();

                                                    PokedexDatabase.addHologram(world, pos.getX(), pos.getY(), pos.getZ(), type);

                                                    player.sendSystemMessage(CachedText.of(
                                                            "<rainbow>&lLEADERBOARDS</rainbow> &7>> <green>Hologram saved!</green>"
                                                    ));

                                                    return 1;
                                                })
                                        )
                                )


                                .then(Commands.literal("remove")
                                        .executes(ctx -> {

                                            var player = ctx.getSource().getPlayerOrException();
                                            var pos = player.blockPosition();
                                            var level = player.serverLevel();

                                            double bestDist = Double.MAX_VALUE;
                                            int bestId = -1;

                                            for (var holo : PokedexDatabase.getHolograms()) {

                                                if (!holo.world.equals(level.dimension().location().toString())) continue;

                                                double dist = pos.distSqr(new BlockPos(holo.x, holo.y, holo.z));

                                                if (dist < bestDist) {
                                                    bestDist = dist;
                                                    bestId = holo.id;
                                                }
                                            }

                                            if (bestId != -1) {
                                                PokedexDatabase.removeHologram(bestId);
                                                HologramManager.remove(bestId);

                                                player.sendSystemMessage(CachedText.of("<rainbow>&lLEADERBOARDS</rainbow> &7>> &cRemoved hologram."));
                                            }

                                            return 1;
                                        })
                                )
                        )




                        .then(Commands.literal("top")
                                .executes(ctx -> {

                                    var src = ctx.getSource();

                                    src.sendSuccess(() ->
                                                    CachedText.of("<rainbow>&l🏆 TOP POKEDEX</rainbow>"),
                                            false
                                    );

                                    int i = 1;

                                    for (var entry : PokedexDatabase.getTopDex()) {

                                        final int rank = i;
                                        final String name = PokedexDatabase.getName(entry.getKey());
                                        final int total = entry.getValue();

                                        src.sendSuccess(() ->
                                                        CachedText.of("<gray>#" + rank + " </gray><gold>" + name + "</gold>: &e" + total),
                                                false
                                        );

                                        i++;
                                    }

                                    return 1;
                                })
                        )




                        .then(Commands.literal("shinytop")
                                .executes(ctx -> {

                                    var src = ctx.getSource();

                                    src.sendSuccess(() ->
                                                    CachedText.of("<light_purple>&l✨ TOP SHINY DEX ✨</light_purple>"),
                                            false
                                    );

                                    int i = 1;

                                    for (var entry : PokedexDatabase.getTopShinyDex()) {

                                        final int rank = i;
                                        final String name = PokedexDatabase.getName(entry.getKey());
                                        final int total = entry.getValue();

                                        src.sendSuccess(() ->
                                                        CachedText.of("<gray>#" + rank + " </gray><light_purple>" + name + "</light_purple>: &e" + total),
                                                false
                                        );

                                        i++;
                                    }

                                    return 1;
                                })
                        )
        );
    }
}