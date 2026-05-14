package org.howie.pixity.moderation.neoforge.milestones.core;

import net.minecraft.server.level.ServerPlayer;
import org.howie.pixity.moderation.neoforge.stats.PlayerStatsDatabase;

public class MilestoneProgressService {





    public record ProgressData(

            int level,

            int currentValue,

            int currentRequirement,

            int nextRequirement,

            double percent,

            boolean maxed
    ) {}





    public static ProgressData getProgress(
            ServerPlayer player,
            MilestoneDefinition definition
    ) {

        int value = PlayerStatsDatabase.get(
                player.getUUID(),
                definition.statColumn
        );

        int level = calculateLevel(
                definition,
                value
        );





        if (level >= definition.maxLevel) {

            return new ProgressData(
                    definition.maxLevel,
                    value,
                    definition.getRequired(definition.maxLevel),
                    definition.getRequired(definition.maxLevel),
                    100.0,
                    true
            );
        }

        int currentReq =
                level <= 0
                        ? 0
                        : definition.getRequired(level);

        int nextReq =
                definition.getRequired(level + 1);

        int needed =
                Math.max(
                        1,
                        nextReq - currentReq
                );

        int progress =
                value - currentReq;

        double percent =
                Math.max(
                        0.0,
                        Math.min(
                                100.0,
                                (progress / (double) needed) * 100.0
                        )
                );

        return new ProgressData(
                level,
                value,
                currentReq,
                nextReq,
                percent,
                false
        );
    }





    public static int calculateLevel(
            MilestoneDefinition definition,
            int value
    ) {

        int level = 0;

        for (int i = 1; i <= definition.maxLevel; i++) {

            int req = definition.getRequired(i);

            if (value >= req) {
                level = i;
            }
            else {
                break;
            }
        }

        return level;
    }
}