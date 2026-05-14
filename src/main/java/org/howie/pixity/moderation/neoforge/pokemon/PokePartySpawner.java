package org.howie.pixity.moderation.neoforge.pokemon;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.ArrayList;
import java.util.List;

public class PokePartySpawner {

    private static final String[] RARE_POOL = {
            "dratini", "larvitar", "gible", "beldum",
            "bagon", "deino", "goomy", "jangmo-o",
            "larvesta", "riolu", "zorua", "feebas",
            "abra", "nidorina", "nidorino", "munchlax", "gastly", "bulbasaur", "charmander", "squirtle", "eevee",
            "mareep", "heracross", "skarmory", "houndour", "chikorita", "cyndaquil", "totodile",
            "aaron", "electrike", "trapinch", "carvanha", "ralts", "shroomish", "treecko", "torchic", "mudkip",
            "gligar", "budew", "bidoof", "starly", "piplup", "chimchar", "turtwig",
            "drilbur", "darumaka", "litwick", "axew", "sandile", "timburr", "joltik", "ferroseed", "tepig", "oshawott", "snivy",
            "froakie", "honedge", "hawlucha", "clauncher", "flabebe", "chespin", "fennekin",
            "mimikyu", "wimpod", "Mareanie", "ninetales alolan", "grubbin", "rockruff", "salandit", "muk alolan", "dewpider", "mudbray", "cutiefly", "rowlet", "litten", "popplio",
            "dreepy", "rookidee", "toxel", "impidimp", "hatenna", "mrmime galarian", "sizzlipede",
            "nacli", "glimmet", "mankey", "charcadet", "tinkatink", "greavard",
    };

    private static final String[] LEGEND_POOL = {
            "mewtwo", "mew", "rayquaza", "kyogre", "groudon",
            "dialga", "palkia", "giratina", "articuno", "moltres",
            "zapdos", "raikou", "entei", "suicune", "regirock", "registeel",
            "regice", "latias", "latios", "uxie", "mespirit", "azelf", "heatran",
            "regigigas", "cresselia", "cobalion", "terrakion", "virizion", "tornadus",
            "thundurus", "landorus", "typenull", "silvally", "tapukoko", "tapulele", "tapubulu",
            "tapufini", "nihilego", "buzzwole", "pheromosa", "xurkitree", "celestella", "kartana",
            "guzzlord", "poipole", "naganadel", "stakataka", "blacephalon", "kubfu", "urshifu",
            "regieleki", "regidraco", "glastrier", "spectrier", "enamorus", "wo-chien", "chien-pao",
            "ting-lu", "chi-yu", "okidogi", "munkidori", "fezandipity", "ogerpon", "deoxys",
            "articuno galarian", "moltres galarian", "zapdos galarian"
    };

    public static void spawnEvent(ServerLevel level, BlockPos center) {

        var config = PokePartyConfig.get();

        List<ServerPlayer> players = getNearbyPlayers(level, center, config.radius);

        if (players.isEmpty()) return;

        int waves = 5;
        int spawnsPerPlayer = config.spawnsPerPlayer;

        for (int w = 0; w < waves; w++) {

            int delay = w * 40;

            level.getServer().tell(new net.minecraft.server.TickTask(delay, () -> {

                for (ServerPlayer player : players) {


                    for (int i = 0; i < spawnsPerPlayer / waves; i++) {

                        BlockPos pos = getSpawnNearPlayer(level, player, config.spawnRadiusPerPlayer);

                        boolean spawnLegend = level.random.nextDouble() < config.legendaryChance;

                        String species = spawnLegend
                                ? LEGEND_POOL[level.random.nextInt(LEGEND_POOL.length)]
                                : RARE_POOL[level.random.nextInt(RARE_POOL.length)];

                        spawnPokemon(level, pos, species, config.shinyBoost);
                    }
                }

            }));
        }
    }




    private static BlockPos getSpawnNearPlayer(ServerLevel level, ServerPlayer player, int radius) {

        int x = player.blockPosition().getX() + level.random.nextInt(radius * 2) - radius;
        int z = player.blockPosition().getZ() + level.random.nextInt(radius * 2) - radius;
        int y = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);

        return new BlockPos(x, y, z);
    }




    private static void spawnPokemon(ServerLevel level, BlockPos pos, String species, double configBoost) {

        try {
            double finalChance = configBoost;

            if (finalChance > 1.0) finalChance = 1.0;

            StringBuilder args = new StringBuilder();

            args.append(species);
            args.append(" level=50");

            if (level.random.nextDouble() < finalChance) {
                args.append(" shiny=true");
            }

            String cmd =
                    "execute positioned " +
                            pos.getX() + " " + pos.getY() + " " + pos.getZ() +
                            " run pokespawn " + args;

            level.getServer().getCommands().performPrefixedCommand(
                    level.getServer().createCommandSourceStack(),
                    cmd
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    private static List<ServerPlayer> getNearbyPlayers(ServerLevel level, BlockPos center, int radius) {

        List<ServerPlayer> list = new ArrayList<>();

        for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {

            if (player.level() != level) continue;

            if (player.blockPosition().distSqr(center) <= (radius * radius)) {
                list.add(player);
            }
        }

        return list;
    }
}