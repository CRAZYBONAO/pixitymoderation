package org.howie.pixity.moderation.neoforge.milestones.core;

public class MilestoneCommandReward {

    public final String command;

    public final int every;

    public MilestoneCommandReward(
            String command,
            int every
    ) {

        this.command = command;
        this.every = every;
    }

    public boolean shouldGive(int level) {
        return level % every == 0;
    }
}