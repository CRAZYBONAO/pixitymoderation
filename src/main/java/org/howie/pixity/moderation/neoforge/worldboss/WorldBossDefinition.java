package org.howie.pixity.moderation.neoforge.worldboss;

public class WorldBossDefinition {





    public final String species;

    public final String display;

    public final long maxHealth;

    public final float scale;

    public final int rewardTokens;

    public final int rewardMoney;

    public final int durationMinutes;





    public WorldBossDefinition(

            String species,

            String display,

            long maxHealth,

            float scale,

            int rewardTokens,

            int rewardMoney,

            int durationMinutes
    ) {

        this.species = species;

        this.display = display;

        this.maxHealth = maxHealth;

        this.scale = scale;

        this.rewardTokens = rewardTokens;

        this.rewardMoney = rewardMoney;

        this.durationMinutes = durationMinutes;
    }
}