package org.howie.pixity.moderation.neoforge.milestones.core;

import java.util.ArrayList;
import java.util.List;

public class MilestoneDefinition {





    public final String id;

    public final String statColumn;

    public final int maxLevel;





    public final int startingValue;

    public final int tier1Increase;
    public final int tier2Increase;
    public final int tier3Increase;
    public final int tier4Increase;
    public final int tier5Increase;
    public final int tier6Increase;





    public final int moneyPerLevel;

    public final int tokensPerLevel;





    public final List<MilestoneCommandReward> commandRewards =
            new ArrayList<>();






    public MilestoneDefinition(
            String id,
            String statColumn,
            int maxLevel,
            int startingValue,
            int moneyPerLevel,
            int tokensPerLevel
    ) {

        this(
                id,
                statColumn,
                maxLevel,

                startingValue,

                startingValue,
                startingValue * 2,
                startingValue * 4,
                startingValue * 8,
                startingValue * 16,
                startingValue * 32,

                moneyPerLevel,
                tokensPerLevel
        );
    }






    public MilestoneDefinition(
            String id,
            String statColumn,
            int maxLevel,

            int startingValue,

            int tier1Increase,
            int tier2Increase,
            int tier3Increase,
            int tier4Increase,
            int tier5Increase,
            int tier6Increase,

            int moneyPerLevel,
            int tokensPerLevel
    ) {

        this.id = id;
        this.statColumn = statColumn;

        this.maxLevel = maxLevel;

        this.startingValue = startingValue;

        this.tier1Increase = tier1Increase;
        this.tier2Increase = tier2Increase;
        this.tier3Increase = tier3Increase;
        this.tier4Increase = tier4Increase;
        this.tier5Increase = tier5Increase;
        this.tier6Increase = tier6Increase;

        this.moneyPerLevel = moneyPerLevel;
        this.tokensPerLevel = tokensPerLevel;
    }





    public int getRequired(int level) {

        if (level <= 1) {
            return startingValue;
        }

        int total = startingValue;

        for (int i = 2; i <= level; i++) {

            if (i <= 5) {
                total += tier1Increase;
            }
            else if (i <= 15) {
                total += tier2Increase;
            }
            else if (i <= 25) {
                total += tier3Increase;
            }
            else if (i <= 50) {
                total += tier4Increase;
            }
            else if (i <= 75) {
                total += tier5Increase;
            }
            else {
                total += tier6Increase;
            }
        }

        return total;
    }





    public int getMoney(int level) {
        return moneyPerLevel * level;
    }

    public int getTokens(int level) {
        return tokensPerLevel * level;
    }
}