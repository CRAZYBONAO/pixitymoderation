package org.howie.pixity.moderation.neoforge.milestones.core;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.chat.TextFormatter;
import org.howie.pixity.moderation.neoforge.stats.PlayerStatsDatabase;

import java.text.NumberFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MilestoneLoreBuilder {





    public static List<Component> build(
            ServerPlayer player,
            MilestoneEntry entry
    ) {

        List<Component> lore =
                new ArrayList<>();





        long value =
                PlayerStatsDatabase.get(
                        player.getUUID(),
                        entry.statColumn
                );





        int place =
                MilestoneLeaderboardService.getPlace(
                        player.getUUID(),
                        entry.statColumn
                );





        var top =
                MilestoneLeaderboardService.getTop(
                        entry.statColumn,
                        3
                );





        String formattedValue =
                format(value);





        lore.add(TextFormatter.parse(""));

        lore.add(TextFormatter.parse(
                "<gradient:#FFD700:#FFF8DC:#FFD700>&lLEADERBOARDS</gradient>"
        ));

        lore.add(TextFormatter.parse(""));





        lore.add(TextFormatter.parse(
                "<gray>Your Progress:</gray> " +
                        "<green>" + formattedValue + "</green>"
        ));

        lore.add(TextFormatter.parse(
                "<gray>Your Placement:</gray> " +
                        "<gold>#" + place + "</gold>"
        ));

        lore.add(TextFormatter.parse(""));





        lore.add(TextFormatter.parse(
                "<gradient:#FFD700:#FFF8DC:#FFD700>&lTOP PLAYERS</gradient>"
        ));

        lore.add(TextFormatter.parse(""));





        if (top.size() >= 1) {

            var first = top.get(0);

            String name =
                    first.username() == null ||
                            first.username().isBlank()
                            ? "Unknown"
                            : first.username();

            lore.add(TextFormatter.parse(
                    "&#f2c4611st: " +
                            "<white>" + name +
                            "</white> <gray>-</gray> " +
                            "<green>" +
                            format(first.value()) +
                            "</green>"
            ));

        } else {

            lore.add(TextFormatter.parse(
                    "&#f2c4611st: <gray>None</gray>"
            ));
        }





        if (top.size() >= 2) {

            var second = top.get(1);

            String name =
                    second.username() == null ||
                            second.username().isBlank()
                            ? "Unknown"
                            : second.username();

            lore.add(TextFormatter.parse(
                    "&#9c9c9c2nd: " +
                            "<white>" + name +
                            "</white> <gray>-</gray> " +
                            "<green>" +
                            format(second.value()) +
                            "</green>"
            ));

        } else {

            lore.add(TextFormatter.parse(
                    "&#9c9c9c2nd: <gray>None</gray>"
            ));
        }






        if (top.size() >= 3) {

            var third = top.get(2);

            String name =
                    third.username() == null ||
                            third.username().isBlank()
                            ? "Unknown"
                            : third.username();

            lore.add(TextFormatter.parse(
                    "&#4a3a233rd: " +
                            "<white>" + name +
                            "</white> <gray>-</gray> " +
                            "<green>" +
                            format(third.value()) +
                            "</green>"
            ));

        } else {

            lore.add(TextFormatter.parse(
                    "&#4a3a233rd: <gray>None</gray>"
            ));
        }

        lore.add(TextFormatter.parse(""));





        lore.add(TextFormatter.parse(
                "<yellow>Click to view milestones</yellow>"
        ));

        return lore;
    }





    private static String format(
            long number
    ) {

        return NumberFormat
                .getNumberInstance(Locale.US)
                .format(number);
    }
}