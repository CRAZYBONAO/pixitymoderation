package org.howie.pixity.moderation.neoforge.skills;

import com.cobblemon.mod.common.api.events.pokemon.LevelUpEvent;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.server.level.ServerPlayer;
import org.howie.pixity.moderation.PixityModerationNeoForge;
import org.howie.pixity.moderation.chat.TextFormatter;

import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.pokemon.stat.CobblemonStatProvider;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;

public class TrainerListener {

    private final SkillService skills;

    public TrainerListener(SkillService skills) {
        this.skills = skills;


        CobblemonEvents.LEVEL_UP_EVENT.subscribe(this::onLevelUp);
    }

    private void onLevelUp(LevelUpEvent event) {

        if (event.getPokemon().getOwnerPlayer() == null) return;

        var player = event.getPokemon().getOwnerPlayer();
        var pokemon = event.getPokemon();
        var abilities = PixityModerationNeoForge.ABILITY_ENGINE;
        var active = PixityModerationNeoForge.ACTIVE_ABILITIES;

        int level = skills.get(player.getUUID()).getLevel(SkillType.TRAINER);

        double multiplier = 1.0;

        if (PixityModerationNeoForge.ABILITY_ENGINE.isEnabled(player, AbilityType.TRAINERS_KEEP)) {
            multiplier += (level * 0.01);
        }




        if (PixityModerationNeoForge.ABILITY_ENGINE.isEnabled(player, AbilityType.TRAINERS_INSIGHT)) {
            multiplier += (level * 0.01);
        }




        if (PixityModerationNeoForge.ACTIVE_ABILITIES.isActive(player, AbilityType.ADRENALINE_RUSH)) {

            multiplier += 0.5;

            player.displayClientMessage(
                    TextFormatter.parse("<gradient:#EE8D8D:#FFFFFF:#EE8D8D>Adrenaline Rush Active!</gradient>"),
                    true
            );
        }




        if (abilities.isEnabled(player, AbilityType.INFUSION)
                && active.isActive(player, AbilityType.INFUSION)) {

            double chance = 0.02 + (level * 0.002);

            if (active.isActive(player, AbilityType.ADRENALINE_RUSH)) {
                chance *= 1.5;
            }

            if (Math.random() < chance) {

                String result = boostIVs(pokemon, level);

                if (result != null) {

                    org.howie.pixity.moderation.neoforge.stats.PlayerStatsDatabase.add(
                            player.getUUID(),
                            "trainer_infusion",
                            1
                    );

                    String[] split = result.split("\\|");

                    String stats = split[0];
                    int boost = Integer.parseInt(split[1]);

                    double percent = getIVPercent(pokemon);
                    String rank = getRank(percent);

                    player.sendSystemMessage(
                            TextFormatter.parse(
                                    "<gradient:#FF0000:#FFFFFF:#FF0000>&lPIXITY SKILLS</gradient> &7➤ " +
                                            "<gradient:#EE8D8D:#FFFFFF:#EE8D8D>TRAINER</gradient> &7Your Pokémon's &e" +
                                            stats +
                                            " &7increased by &a+" + boost +
                                            " &7IVs! &8(&f" + rank + " &7| &e" + String.format("%.1f", percent) + "%&8)"
                            )
                    );

                    announceRank(player, pokemon, percent);
                }

            }
        }

        SkillXpRouter.onPokemonLevelUp(
                player,
                event.getNewLevel(),
                skills,
                multiplier
        );




        if (Math.random() < (level * 0.002)) {

            player.sendSystemMessage(TextFormatter.parse(
                    "<gradient:#EE8D8D:#FFFFFF:#EE8D8D>Battle Instinct!</gradient> &7XP Surge!"
            ));

            skills.addXp(player, SkillType.TRAINER, 50);
        }

        if (level >= 35) {
            PermissionHelper.givePermission(player, "pixity.kits.claim.trainerskill");
        }
    }


    private String boostIVs(com.cobblemon.mod.common.pokemon.Pokemon pokemon, int trainerLevel) {

        int boost = 1 + (trainerLevel / 25);

        java.util.List<String> increased = new java.util.ArrayList<>();

        increaseStat(pokemon, Stats.HP, boost, "hp", increased);
        increaseStat(pokemon, Stats.ATTACK, boost, "attack", increased);
        increaseStat(pokemon, Stats.DEFENCE, boost, "defence", increased);
        increaseStat(pokemon, Stats.SPECIAL_ATTACK, boost, "special_attack", increased);
        increaseStat(pokemon, Stats.SPECIAL_DEFENCE, boost, "special_defence", increased);
        increaseStat(pokemon, Stats.SPEED, boost, "speed", increased);

        if (increased.isEmpty()) return null;

        return String.join(", ", increased) + "|" + boost;
    }

    private void increaseStat(com.cobblemon.mod.common.pokemon.Pokemon pokemon,
                              com.cobblemon.mod.common.api.pokemon.stats.Stat stat,
                              int boost,
                              String statName,
                              java.util.List<String> increased) {

        var ivs = pokemon.getIvs();

        int current = ivs.get(stat);

        if (current >= 31) return;

        int newVal = Math.min(31, current + boost);

        ivs.set(stat, newVal);

        increased.add(formatStat(statName));
    }

    private String formatStat(String stat) {
        return switch (stat) {
            case "hp" -> "&9HP";
            case "attack" -> "&cAttack";
            case "defence" -> "&aDefense";
            case "special_attack" -> "&4Sp. Attack";
            case "special_defence" -> "&2Sp. Defense";
            case "speed" -> "&eSpeed";
            default -> stat;
        };
    }


    private double getIVPercent(Pokemon pokemon) {

        var ivs = pokemon.getIvs();

        int total =
                ivs.get(Stats.HP) +
                        ivs.get(Stats.ATTACK) +
                        ivs.get(Stats.DEFENCE) +
                        ivs.get(Stats.SPECIAL_ATTACK) +
                        ivs.get(Stats.SPECIAL_DEFENCE) +
                        ivs.get(Stats.SPEED);

        return (total / 186.0) * 100.0;
    }

    private String getRank(double percent) {

        if (percent >= 100) return "&6&lSSS";
        if (percent >= 90) return "&d&lSS";
        if (percent >= 80) return "&a&lS";
        if (percent >= 70) return "&b&lA";
        if (percent >= 50) return "&e&lB";
        if (percent >= 30) return "&7&lC";
        if (percent >= 10) return "&8&lD";
        return "&8&lE";
    }

    private void announceRank(ServerPlayer player, com.cobblemon.mod.common.pokemon.Pokemon pokemon, double percent) {

        String rank = getRank(percent);


        if (!(rank.contains("SS"))) return;

        String name = pokemon.getSpecies().getName();

        player.getServer().getPlayerList().broadcastSystemMessage(
                TextFormatter.parse(
                        "<gradient:#FF0000:#FFFFFF:#FF0000>&lPIXITY SKILLS</gradient> &7➤ " +
                                "<gradient:#EE8D8D:#FFFFFF:#EE8D8D>" + player.getName().getString() + "</gradient> " +
                                "&7just obtained a " + rank +
                                " &7Pokémon! (&e" + String.format("%.1f", percent) + "%&7)"
                ),
                false
        );
    }
}