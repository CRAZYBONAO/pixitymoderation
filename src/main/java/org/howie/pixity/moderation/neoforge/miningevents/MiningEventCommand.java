package org.howie.pixity.moderation.neoforge.miningevents;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;

import net.minecraft.network.chat.Component;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.chat.TextFormatter;
import org.howie.pixity.moderation.neoforge.rank.RankService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MiningEventCommand {





    private static final RankService RANKS =
            new RankService();

    private static final String ADMIN_PERMISSION =
            "pixity.admin";





    public static void register(
            com.mojang.brigadier.CommandDispatcher<net.minecraft.commands.CommandSourceStack> dispatcher
    ) {

        dispatcher.register(

                Commands.literal("mining")





                        .then(
                                Commands.literal("event")

                                        .executes(ctx -> {

                                            if (
                                                    !(ctx.getSource().getEntity()
                                                            instanceof ServerPlayer sp)
                                            ) {
                                                return 0;
                                            }





                                            if (!MiningEventManager.isActive()) {

                                                long remaining =
                                                        MiningEventManager.getNextEventTime()
                                                                - System.currentTimeMillis();

                                                long hours =
                                                        remaining / (1000L * 60L * 60L);

                                                long minutes =
                                                        (remaining / (1000L * 60L))
                                                                % 60L;

                                                sp.sendSystemMessage(
                                                        TextFormatter.parse(
                                                                "<red>No mining event is currently active.</red>\n"
                                                                        + "<gray>Next event starts in </gray>"
                                                                        + "<yellow>"
                                                                        + hours
                                                                        + "h "
                                                                        + minutes
                                                                        + "m</yellow>"
                                                        )
                                                );

                                                return 1;
                                            }





                                            MiningEventOre ore =
                                                    MiningEventManager.getCurrentOre();

                                            long remaining =
                                                    MiningEventManager.getEndTime()
                                                            - System.currentTimeMillis();

                                            long minutes =
                                                    remaining / (1000L * 60L);

                                            long seconds =
                                                    (remaining / 1000L) % 60L;

                                            sp.sendSystemMessage(
                                                    TextFormatter.parse(
                                                            "<gold>🏆</gold><rainbow>MINING EVENT</rainbow><gold>🏆</gold>\n\n"

                                                                    + "<gray>Current Ore: </gray>"
                                                                    + ore.color
                                                                    + ore.display
                                                                    + "</gold>\n"

                                                                    + "<gray>Time Remaining: </gray>"
                                                                    + "<yellow>"
                                                                    + minutes
                                                                    + "m "
                                                                    + seconds
                                                                    + "s</yellow>\n"
                                                    )
                                            );

                                            var standings =
                                                    MiningEventManager.getStandings();

                                            if (standings.isEmpty()) {

                                                sp.sendSystemMessage(
                                                        TextFormatter.parse(
                                                                "<gray>No players have mined any ore yet.</gray>"
                                                        )
                                                );

                                                return 1;
                                            }

                                            int place = 1;

                                            for (Map.Entry<UUID, Integer> e : standings) {

                                                String prefix =
                                                        switch (place) {

                                                            case 1 ->
                                                                    "<gold>1st:</gold> ";

                                                            case 2 ->
                                                                    "<gray>2nd:</gray> ";

                                                            default ->
                                                                    "<#cd7f32>3rd:</#cd7f32> ";
                                                        };

                                                String name =
                                                        "Unknown";

                                                ServerPlayer target =
                                                        sp.server
                                                                .getPlayerList()
                                                                .getPlayer(
                                                                        e.getKey()
                                                                );

                                                if (target != null) {
                                                    name =
                                                            target.getGameProfile()
                                                                    .getName();
                                                }

                                                sp.sendSystemMessage(
                                                        TextFormatter.parse(
                                                                prefix
                                                                        + "<white>"
                                                                        + name
                                                                        + "</white>"
                                                                        + "<gray> - </gray>"
                                                                        + "<yellow>"
                                                                        + e.getValue()
                                                                        + "</yellow>"
                                                        )
                                                );

                                                place++;

                                                if (place > 3) {
                                                    break;
                                                }
                                            }

                                            return 1;
                                        })
                        )





                        .then(
                                Commands.literal("forcestartevent")

                                        .then(
                                                Commands.argument(
                                                                "ore",
                                                                StringArgumentType.word()
                                                        )

                                                        .suggests((ctx, builder) ->
                                                                suggestOres(builder)
                                                        )

                                                        .executes(ctx -> {

                                                            if (
                                                                    !(ctx.getSource()
                                                                            .getEntity()
                                                                            instanceof ServerPlayer sp)
                                                            ) {
                                                                return 0;
                                                            }





                                                            if (
                                                                    !RANKS.hasPerm(
                                                                            sp,
                                                                            ADMIN_PERMISSION
                                                                    )
                                                            ) {

                                                                sp.sendSystemMessage(
                                                                        TextFormatter.parse(
                                                                                "<red>You do not have permission.</red>"
                                                                        )
                                                                );

                                                                return 0;
                                                            }

                                                            String oreName =
                                                                    StringArgumentType.getString(
                                                                            ctx,
                                                                            "ore"
                                                                    );

                                                            MiningEventOre ore =
                                                                    MiningEventOre.fromString(
                                                                            oreName
                                                                    );

                                                            if (ore == null) {

                                                                sp.sendSystemMessage(
                                                                        TextFormatter.parse(
                                                                                "<red>Invalid ore.</red>"
                                                                        )
                                                                );

                                                                return 0;
                                                            }

                                                            MiningEventManager.startEvent(
                                                                    sp.server,
                                                                    ore
                                                            );

                                                            sp.sendSystemMessage(
                                                                    TextFormatter.parse(
                                                                            "<green>Started mining event for "
                                                                                    + ore.display
                                                                                    + ".</green>"
                                                                    )
                                                            );

                                                            return 1;
                                                        })
                                        )
                        )





                        .then(
                                Commands.literal("forceendevent")

                                        .executes(ctx -> {

                                            if (
                                                    !(ctx.getSource()
                                                            .getEntity()
                                                            instanceof ServerPlayer sp)
                                            ) {
                                                return 0;
                                            }





                                            if (
                                                    !RANKS.hasPerm(
                                                            sp,
                                                            ADMIN_PERMISSION
                                                    )
                                            ) {

                                                sp.sendSystemMessage(
                                                        TextFormatter.parse(
                                                                "<red>You do not have permission.</red>"
                                                        )
                                                );

                                                return 0;
                                            }

                                            if (!MiningEventManager.isActive()) {

                                                sp.sendSystemMessage(
                                                        TextFormatter.parse(
                                                                "<red>No mining event is active.</red>"
                                                        )
                                                );

                                                return 0;
                                            }

                                            MiningEventManager.forceEnd(
                                                    sp.server
                                            );

                                            sp.sendSystemMessage(
                                                    TextFormatter.parse(
                                                            "<green>Mining event force ended.</green>"
                                                    )
                                            );

                                            return 1;
                                        })
                        )
        );
    }





    private static CompletableFuture<com.mojang.brigadier.suggestion.Suggestions> suggestOres(
            SuggestionsBuilder builder
    ) {

        for (MiningEventOre ore : MiningEventOre.values()) {

            builder.suggest(
                    ore.name()
                            .toLowerCase()
            );
        }

        return builder.buildFuture();
    }
}