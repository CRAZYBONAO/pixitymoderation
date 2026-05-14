package org.howie.pixity.moderation.neoforge.outbreaks;

import java.util.List;
import java.util.Random;

public class OutbreakPool {





    public final OutbreakPoolType type;

    public final String display;

    public final List<String> species;





    public OutbreakPool(
            OutbreakPoolType type,
            String display,
            List<String> species
    ) {

        this.type = type;

        this.display = display;

        this.species = species;
    }





    public String randomSpecies() {

        return species.get(
                new Random()
                        .nextInt(
                                species.size()
                        )
        );
    }
}