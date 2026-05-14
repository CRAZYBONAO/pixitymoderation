package org.howie.pixity.moderation.neoforge.milestones.listeners;

import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.api.Priority;

import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.neoforge.stats.PlayerStatsDatabase;

public class ProfessorMilestoneListener {





    public static void register() {

        CobblemonEvents.POKEMON_CAPTURED.subscribe(event -> {





            if (!(event.getPlayer() instanceof ServerPlayer player)) {
                return;
            }





            Pokemon pokemon =
                    event.getPokemon();





            PlayerStatsDatabase.add(
                    player.getUUID(),
                    "pokemon_caught",
                    1
            );





            boolean shiny =
                    pokemon.getShiny();

            if (shiny) {

                PlayerStatsDatabase.add(
                        player.getUUID(),
                        "pokemon_shiny_caught",
                        1
                );

                PlayerStatsDatabase.add(
                        player.getUUID(),
                        "pokemon_special_caught",
                        1
                );
            }





            boolean hiddenAbility = false;

            try {

                hiddenAbility =
                        pokemon.getAbility()
                                .getPriority()
                                .name()
                                .equalsIgnoreCase("LOW");

            } catch (Exception ignored) {}

            if (hiddenAbility) {

                PlayerStatsDatabase.add(
                        player.getUUID(),
                        "pokemon_hidden_ability_caught",
                        1
                );

                PlayerStatsDatabase.add(
                        player.getUUID(),
                        "pokemon_special_caught",
                        1
                );
            }





            String species =
                    pokemon.getSpecies()
                            .getName()
                            .toLowerCase();





            if (isLegendary(species)) {

                PlayerStatsDatabase.add(
                        player.getUUID(),
                        "pokemon_legendary_caught",
                        1
                );

                PlayerStatsDatabase.add(
                        player.getUUID(),
                        "pokemon_special_caught",
                        1
                );
            }





            if (isMythical(species)) {

                PlayerStatsDatabase.add(
                        player.getUUID(),
                        "pokemon_mythical_caught",
                        1
                );

                PlayerStatsDatabase.add(
                        player.getUUID(),
                        "pokemon_special_caught",
                        1
                );
            }
        });
    }





    private static boolean isLegendary(
            String species
    ) {

        return switch (species) {


            case "articuno",
                 "zapdos",
                 "moltres",
                 "mewtwo" -> true;


            case "raikou",
                 "entei",
                 "suicune",
                 "lugia",
                 "ho-oh" -> true;


            case "regirock",
                 "regice",
                 "registeel",
                 "latias",
                 "latios",
                 "kyogre",
                 "groudon",
                 "rayquaza" -> true;


            case "uxie",
                 "mesprit",
                 "azelf",
                 "dialga",
                 "palkia",
                 "heatran",
                 "regigigas",
                 "giratina",
                 "cresselia" -> true;


            case "cobalion",
                 "terrakion",
                 "virizion",
                 "tornadus",
                 "thundurus",
                 "landorus",
                 "reshiram",
                 "zekrom",
                 "kyurem" -> true;


            case "xerneas",
                 "yveltal",
                 "zygarde" -> true;


            case "tapu koko",
                 "tapu lele",
                 "tapu bulu",
                 "tapu fini",
                 "cosmog",
                 "cosmoem",
                 "solgaleo",
                 "lunala",
                 "necrozma" -> true;


            case "zacian",
                 "zamazenta",
                 "eternatus",
                 "kubfu",
                 "urshifu",
                 "regieleki",
                 "regidrago",
                 "glastrier",
                 "spectrier",
                 "calyrex" -> true;


            case "wo-chien",
                 "chien-pao",
                 "ting-lu",
                 "chi-yu",
                 "koraidon",
                 "miraidon" -> true;

            default -> false;
        };
    }





    private static boolean isMythical(
            String species
    ) {

        return switch (species) {

            case "mew",
                 "celebi",
                 "jirachi",
                 "deoxys",
                 "phione",
                 "manaphy",
                 "darkrai",
                 "shaymin",
                 "arceus",
                 "victini",
                 "keldeo",
                 "meloetta",
                 "genesect",
                 "diancie",
                 "hoopa",
                 "volcanion",
                 "magearna",
                 "marshadow",
                 "zeraora",
                 "meltan",
                 "melmetal",
                 "zarude",
                 "pecharunt" -> true;

            default -> false;
        };
    }
}