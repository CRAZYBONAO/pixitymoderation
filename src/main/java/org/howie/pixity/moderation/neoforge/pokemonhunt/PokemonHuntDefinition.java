package org.howie.pixity.moderation.neoforge.pokemonhunt;

public class PokemonHuntDefinition {





    public final PokemonHuntType type;

    public final String target;

    public final int required;





    public final int tokens;

    public final int money;





    public PokemonHuntDefinition(
            PokemonHuntType type,
            String target,
            int required,
            int tokens,
            int money
    ) {

        this.type = type;

        this.target = target;

        this.required = required;

        this.tokens = tokens;

        this.money = money;
    }





    public String getDisplay() {

        return switch (type) {

            case SPECIES ->
                    "Catch "
                            + required
                            + " "
                            + capitalize(target);

            case TYPE ->
                    "Catch "
                            + required
                            + " "
                            + capitalize(target)
                            + " Pokémon";

            case SHINY ->
                    "Catch "
                            + required
                            + " Shiny Pokémon";

            case HIDDEN_ABILITY ->
                    "Catch "
                            + required
                            + " Hidden Ability Pokémon";
        };
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