package org.howie.pixity.moderation.neoforge.pokemon;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.chat.CachedText;
import org.howie.pixity.moderation.chat.TextFormatter;
import org.howie.pixity.moderation.neoforge.rank.RankService;

public class PokePartyCommand {

    private static final RankService RANK = new RankService();

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(
                Commands.literal("pokeparty")




                        .then(Commands.literal("setevent")
                                .requires(src -> has(src, "pixity.pokeparty.admin"))
                                .executes(ctx -> {

                                    var player = ctx.getSource().getPlayerOrException();
                                    var level = player.serverLevel();

                                    PokePartyManager.setEvent(level, player.blockPosition());

                                    player.sendSystemMessage(TextFormatter.parse("<rainbow>&l✦ POKE PARTY ✦</rainbow> &7&l➤ &aPokeParty location set!"));
                                    return 1;
                                })
                        )




                        .then(Commands.literal("vote")
                                .requires(src -> has(src, "pixity.pokeparty.vote"))
                                .executes(ctx -> {

                                    var player = ctx.getSource().getPlayerOrException();

                                    if (!PokePartyManager.hasEvent()) {
                                        player.sendSystemMessage(TextFormatter.parse("<rainbow>&l✦ POKE PARTY ✦</rainbow> &7&l➤ &cError! No event set!"));
                                        return 0;
                                    }

                                    if (!PokePartyManager.addVote(player.getUUID())) {
                                        player.sendSystemMessage(TextFormatter.parse("<rainbow>&l✦ POKE PARTY ✦</rainbow> &7&l➤ &cError! You already voted!"));
                                        return 0;
                                    }

                                    int votes = PokePartyManager.getVotes();
                                    int req = PokePartyManager.getRequiredVotes();

                                    player.sendSystemMessage(TextFormatter.parse(
                                            "<rainbow>&l✦ POKE PARTY ✦</rainbow> &7&l➤ &eVote added! &7(&e" + votes + "&7/&c" + req + "&7)"
                                    ));

                                    if (PokePartyManager.isReady()) {
                                        startEvent(player.serverLevel());
                                    }

                                    return 1;
                                })
                        )
        );
    }

    private static boolean has(CommandSourceStack src, String node) {
        try {
            return RANK.hasPerm(src.getPlayerOrException(), node);
        } catch (Exception e) {
            return false;
        }
    }

    private static void startEvent(ServerLevel level) {

        var pos = PokePartyManager.getLocation();


        PokePartySpawner.spawnEvent(level, pos);


        Component msg = TextFormatter.parse("<rainbow>&l✦ POKE PARTY STARTED ✦</rainbow> &7&l➤ &7Go to &e/warp pokeparty &7to participate and catch some pokemon!");

        for (ServerPlayer p : level.getServer().getPlayerList().getPlayers()) {
            p.sendSystemMessage(msg);
        }


        PokePartyManager.reset();
    }

    private static void send(ServerPlayer player, String msg) {
        player.sendSystemMessage(CachedText.of(msg));
    }

    private static void broadcast(net.minecraft.server.level.ServerLevel level, String msg) {
        var comp = CachedText.of(msg);

        for (var p : level.getServer().getPlayerList().getPlayers()) {
            p.sendSystemMessage(comp);
        }
    }
}