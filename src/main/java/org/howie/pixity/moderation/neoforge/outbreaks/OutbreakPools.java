package org.howie.pixity.moderation.neoforge.outbreaks;

import java.util.List;
import java.util.Random;

public class OutbreakPools {





    public static final OutbreakPool STANDARD =
            new OutbreakPool(

                    OutbreakPoolType.STANDARD,

                    "Standard",

                    List.of(
                            "pikachu",
                            "eevee",
                            "riolu",
                            "gastly",
                            "dratini",
                            "magikarp",
                            "ralts",
                            "zorua",
                            "shinx",
                            "growlithe",
                            "vulpix",
                            "abra"
                    )
            );





    public static final OutbreakPool STARTERS =
            new OutbreakPool(

                    OutbreakPoolType.STARTERS,

                    "Starter Frenzy",

                    List.of(
                            "bulbasaur",
                            "charmander",
                            "squirtle",
                            "treecko",
                            "torchic",
                            "mudkip",
                            "froakie",
                            "fuecoco"
                    )
            );





    public static final OutbreakPool SPOOKY =
            new OutbreakPool(

                    OutbreakPoolType.SPOOKY,

                    "Spooky",

                    List.of(
                            "gastly",
                            "haunter",
                            "mimikyu",
                            "zorua",
                            "litwick",
                            "spiritomb",
                            "absol"
                    )
            );





    public static final OutbreakPool ANCIENT =
            new OutbreakPool(

                    OutbreakPoolType.ANCIENT,

                    "Ancient",

                    List.of(
                            "omanyte",
                            "kabuto",
                            "aerodactyl",
                            "cranidos",
                            "shieldon",
                            "tyrunt"
                    )
            );





    public static final OutbreakPool DRAGON =
            new OutbreakPool(

                    OutbreakPoolType.DRAGON,

                    "Dragon",

                    List.of(
                            "dratini",
                            "bagon",
                            "gible",
                            "axew",
                            "deino",
                            "jangmoo"
                    )
            );





    public static final OutbreakPool OCEAN =
            new OutbreakPool(

                    OutbreakPoolType.OCEAN,

                    "Ocean",

                    List.of(
                            "magikarp",
                            "lapras",
                            "feebas",
                            "clamperl",
                            "totodile",
                            "squirtle"
                    )
            );





    public static final List<OutbreakPool> ALL =
            List.of(
                    STANDARD,
                    STARTERS,
                    SPOOKY,
                    ANCIENT,
                    DRAGON,
                    OCEAN
            );





    public static OutbreakPool random() {

        return ALL.get(
                new Random()
                        .nextInt(
                                ALL.size()
                        )
        );
    }
}