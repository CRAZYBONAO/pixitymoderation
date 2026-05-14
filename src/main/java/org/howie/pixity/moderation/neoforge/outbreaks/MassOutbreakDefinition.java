package org.howie.pixity.moderation.neoforge.outbreaks;

public class MassOutbreakDefinition {





    public final String species;

    public final String biomeName;

    public final OutbreakTier tier;

    public final int durationMinutes;

    public final OutbreakPool pool;








    public MassOutbreakDefinition(
            String species,
            String biomeName,
            OutbreakTier tier,
            OutbreakPool pool,
            int durationMinutes
    ) {

        this.species = species;

        this.biomeName = biomeName;

        this.tier = tier;

        this.pool = pool;

        this.durationMinutes = durationMinutes;

    }





    public String getDisplayName() {

        return capitalize(species);
    }





    private String capitalize(
            String s
    ) {

        if (s == null || s.isEmpty()) {
            return s;
        }

        return s.substring(0, 1)
                .toUpperCase()
                + s.substring(1)
                .toLowerCase();
    }
}