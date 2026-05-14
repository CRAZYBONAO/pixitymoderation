package org.howie.pixity.moderation.neoforge.outbreaks;

public enum OutbreakTier {





    COMMON(

            "Common",

            "<green><bold>COMMON OUTBREAK</bold></green>",

            1024,

            32,

            8,

            0
    ),

    RARE(

            "Rare",

            "<aqua><bold>RARE OUTBREAK</bold></aqua>",

            512,

            16,

            10,

            0
    ),

    EPIC(

            "Epic",

            "<light_purple><bold>EPIC OUTBREAK</bold></light_purple>",

            256,

            8,

            12,

            1
    ),

    LEGENDARY(

            "Legendary",

            "<gold><bold>LEGENDARY OUTBREAK</bold></gold>",

            128,

            4,

            16,

            5
    );





    public final String display;

    public final String formatted;

    public final int shinyOdds;

    public final int hiddenAbilityOdds;

    public final int spawnAttempts;

    public final int alphaChance;





    OutbreakTier(
            String display,
            String formatted,
            int shinyOdds,
            int hiddenAbilityOdds,
            int spawnAttempts,
            int alphaChance
    ) {

        this.display = display;

        this.formatted = formatted;

        this.shinyOdds = shinyOdds;

        this.hiddenAbilityOdds = hiddenAbilityOdds;

        this.spawnAttempts = spawnAttempts;

        this.alphaChance = alphaChance;
    }





    public static OutbreakTier random() {

        int roll =
                new java.util.Random()
                        .nextInt(100);





        if (roll < 60) {
            return COMMON;
        }





        if (roll < 85) {
            return RARE;
        }





        if (roll < 95) {
            return EPIC;
        }





        return LEGENDARY;
    }
}