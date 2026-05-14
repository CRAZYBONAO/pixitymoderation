package org.howie.pixity.moderation.neoforge.worldboss;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import com.mojang.brigadier.arguments.StringArgumentType;

import java.util.Comparator;
import java.util.Map;
import java.util.UUID;

import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.chat.TextFormatter;
import org.howie.pixity.moderation.neoforge.rank.RankService;

public class WorldBossCommand {





    private static final RankService RANKS =
            new RankService();

    private static final String ADMIN_PERMISSION =
            "pixity.admin";





    public static void register(
            CommandDispatcher<CommandSourceStack> dispatcher
    ) {

        dispatcher.register(

                Commands.literal("worldboss")





                        .executes(ctx -> {

                            if (
                                    !(ctx.getSource()
                                            .getEntity()
                                            instanceof ServerPlayer player)
                            ) {
                                return 0;
                            }

                            if (!WorldBossManager.isActive()) {

                                player.sendSystemMessage(
                                        TextFormatter.parse(
                                                "<red>No active world boss.</red>"
                                        )
                                );

                                return 1;
                            }

                            WorldBossDefinition boss =
                                    WorldBossManager.getCurrent();

                            player.sendSystemMessage(

                                    TextFormatter.parse(
                                            "<red>&l👑 WORLD BOSS</red>\n\n"

                                                    + "<gold>"
                                                    + boss.display
                                                    + "</gold>\n\n"

                                                    + "<green>HP: "
                                                    + String.format(
                                                    "%,d",
                                                    WorldBossManager.getHealth()
                                            )
                                                    + "</green>"
                                    )
                            );

                            return 1;
                        })





                        .then(
                                Commands.literal("force")

                                        .executes(ctx -> {

                                            if (
                                                    !(ctx.getSource()
                                                            .getEntity()
                                                            instanceof ServerPlayer player)
                                            ) {
                                                return 0;
                                            }

                                            if (
                                                    !RANKS.hasPerm(
                                                            player,
                                                            ADMIN_PERMISSION
                                                    )
                                            ) {

                                                player.sendSystemMessage(
                                                        TextFormatter.parse(
                                                                "<red>No permission.</red>"
                                                        )
                                                );

                                                return 0;
                                            }

                                            try {

                                                WorldBossManager.start(
                                                        player
                                                );

                                                player.sendSystemMessage(
                                                        TextFormatter.parse(
                                                                "<green>Spawned world boss.</green>"
                                                        )
                                                );

                                            } catch (Exception e) {

                                                e.printStackTrace();

                                                player.sendSystemMessage(
                                                        TextFormatter.parse(
                                                                "<red>World boss failed to spawn. Check console.</red>"
                                                        )
                                                );
                                            }

                                            return 1;
                                        })
                        )





                        .then(
                                Commands.literal("end")

                                        .executes(ctx -> {

                                            if (
                                                    !(ctx.getSource()
                                                            .getEntity()
                                                            instanceof ServerPlayer player)
                                            ) {
                                                return 0;
                                            }

                                            if (
                                                    !RANKS.hasPerm(
                                                            player,
                                                            ADMIN_PERMISSION
                                                    )
                                            ) {

                                                player.sendSystemMessage(
                                                        TextFormatter.parse(
                                                                "<red>No permission.</red>"
                                                        )
                                                );

                                                return 0;
                                            }

                                            if (!WorldBossManager.isActive()) {

                                                player.sendSystemMessage(
                                                        TextFormatter.parse(
                                                                "<red>No active boss.</red>"
                                                        )
                                                );

                                                return 0;
                                            }

                                            WorldBossManager.kill(
                                                    player.server
                                            );

                                            player.sendSystemMessage(
                                                    TextFormatter.parse(
                                                            "<green>Force ended world boss.</green>"
                                                    )
                                            );

                                            return 1;
                                        })
                        )





                        .then(
                                Commands.literal("spawn")

                                        .then(
                                                Commands.argument(
                                                                "boss",
                                                                StringArgumentType.word()
                                                        )

                                                        .executes(ctx -> {

                                                            if (
                                                                    !(ctx.getSource()
                                                                            .getEntity()
                                                                            instanceof ServerPlayer player)
                                                            ) {
                                                                return 0;
                                                            }

                                                            if (
                                                                    !RANKS.hasPerm(
                                                                            player,
                                                                            ADMIN_PERMISSION
                                                                    )
                                                            ) {

                                                                player.sendSystemMessage(
                                                                        TextFormatter.parse(
                                                                                "<red>No permission.</red>"
                                                                        )
                                                                );

                                                                return 0;
                                                            }

                                                            String bossName =
                                                                    StringArgumentType.getString(
                                                                            ctx,
                                                                            "boss"
                                                                    );

                                                            WorldBossDefinition found =
                                                                    null;





                                                            for (WorldBossDefinition boss
                                                                    : WorldBossPool.getAll()) {

                                                                if (
                                                                        boss.species.equalsIgnoreCase(
                                                                                bossName
                                                                        )
                                                                ) {

                                                                    found = boss;
                                                                    break;
                                                                }
                                                            }





                                                            if (found == null) {

                                                                for (WorldBossDefinition boss
                                                                        : LegendaryWorldBossPool.getAll()) {

                                                                    if (
                                                                            boss.species.equalsIgnoreCase(
                                                                                    bossName
                                                                            )
                                                                    ) {

                                                                        found = boss;
                                                                        break;
                                                                    }
                                                                }
                                                            }





                                                            if (found == null) {

                                                                player.sendSystemMessage(

                                                                        TextFormatter.parse(

                                                                                "<red>Boss not found.</red>"
                                                                        )
                                                                );

                                                                return 0;
                                                            }





                                                            WorldBossManager.startSpecific(
                                                                    player,
                                                                    found
                                                            );

                                                            player.sendSystemMessage(

                                                                    TextFormatter.parse(

                                                                            "<green>Spawned:</green> "

                                                                                    + "<gold>"
                                                                                    + found.display
                                                                                    + "</gold>"
                                                                    )
                                                            );

                                                            return 1;
                                                        })
                                        )
                        )





                        .then(
                                Commands.literal("leaderboard")

                                        .executes(ctx -> {

                                            if (
                                                    !(ctx.getSource()
                                                            .getEntity()
                                                            instanceof ServerPlayer player)
                                            ) {
                                                return 0;
                                            }

                                            if (!WorldBossManager.isActive()) {

                                                player.sendSystemMessage(
                                                        TextFormatter.parse(
                                                                "<red>No active world boss.</red>"
                                                        )
                                                );

                                                return 0;
                                            }

                                            var damageMap =
                                                    WorldBossDamageTracker.getAll();

                                            if (damageMap.isEmpty()) {

                                                player.sendSystemMessage(
                                                        TextFormatter.parse(
                                                                "<red>No damage recorded.</red>"
                                                        )
                                                );

                                                return 0;
                                            }

                                            StringBuilder msg =
                                                    new StringBuilder();

                                            msg.append(
                                                    "<red>&l👑 RAID LEADERBOARD</red>\n\n"
                                            );

                                            int place = 1;

                                            for (Map.Entry<UUID, Long> entry
                                                    : damageMap.entrySet()
                                                    .stream()

                                                    .sorted(
                                                            Map.Entry.comparingByValue(
                                                                    Comparator.reverseOrder()
                                                            )
                                                    )

                                                    .limit(10)

                                                    .toList()) {

                                                ServerPlayer target =
                                                        player.server.getPlayerList()
                                                                .getPlayer(
                                                                        entry.getKey()
                                                                );

                                                if (target == null) {
                                                    continue;
                                                }

                                                msg.append(

                                                        "<gold>#"
                                                                + place
                                                                + "</gold> "

                                                                + "<white>"
                                                                + target.getGameProfile().getName()
                                                                + "</white>"

                                                                + "<gray> - </gray>"

                                                                + "<green>"
                                                                + String.format(
                                                                "%,d",
                                                                entry.getValue()
                                                        )
                                                                + "</green>\n"
                                                );

                                                place++;
                                            }

                                            msg.append("\n")

                                                    .append("<gray>Boss HP:</gray> ")

                                                    .append("<red>")
                                                    .append(
                                                            String.format(
                                                                    "%,d",
                                                                    WorldBossManager.getHealth()
                                                            )
                                                    )
                                                    .append("</red>");

                                            player.sendSystemMessage(
                                                    TextFormatter.parse(
                                                            msg.toString()
                                                    )
                                            );

                                            return 1;
                                        })
                        )





                        .then(
                                Commands.literal("setspawn")

                                        .executes(ctx -> {

                                            if (
                                                    !(ctx.getSource()
                                                            .getEntity()
                                                            instanceof ServerPlayer player)
                                            ) {
                                                return 0;
                                            }

                                            if (
                                                    !RANKS.hasPerm(
                                                            player,
                                                            ADMIN_PERMISSION
                                                    )
                                            ) {

                                                player.sendSystemMessage(
                                                        TextFormatter.parse(
                                                                "<red>No permission.</red>"
                                                        )
                                                );

                                                return 0;
                                            }

                                            WorldBossSpawnPoint.set(
                                                    player
                                            );

                                            player.sendSystemMessage(

                                                    TextFormatter.parse(
                                                            "<green>&l✔ World Boss spawn set!</green>\n\n"

                                                                    + "<gray>X:</gray> <gold>"
                                                                    + player.blockPosition().getX()
                                                                    + "</gold>\n"

                                                                    + "<gray>Y:</gray> <gold>"
                                                                    + player.blockPosition().getY()
                                                                    + "</gold>\n"

                                                                    + "<gray>Z:</gray> <gold>"
                                                                    + player.blockPosition().getZ()
                                                                    + "</gold>"
                                                    )
                                            );

                                            return 1;
                                        })
                        )
        );
    }
}