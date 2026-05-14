package org.howie.pixity.moderation.neoforge.skills;

import com.cobblemon.mod.common.api.events.pokemon.PokemonCapturedEvent;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import org.howie.pixity.moderation.PixityModerationNeoForge;
import org.howie.pixity.moderation.chat.TextFormatter;

public class ProfessorListener {

    private final SkillService skills;

    public ProfessorListener(SkillService skills) {
        this.skills = skills;


        CobblemonEvents.POKEMON_CAPTURED.subscribe(this::onCatch);
    }

    private void onCatch(PokemonCapturedEvent event) {

        if (event.getPlayer() == null) return;

        var player = event.getPlayer();
        var pokemon = event.getPokemon();

        var abilities = PixityModerationNeoForge.ABILITY_ENGINE;
        var active = PixityModerationNeoForge.ACTIVE_ABILITIES;

        int level = skills.get(player.getUUID()).getLevel(SkillType.PROFESSOR);




        if (abilities.isEnabled(player, AbilityType.GOTTA_CATCH_EM_ALL)) {

            double chance = 0.01 + (level * 0.002);

            if (active.isActive(player, AbilityType.CAPTURE_AURA)) {
                chance *= 2;
            }

            if (Math.random() < chance) {

                player.sendSystemMessage(
                        TextFormatter.parse(
                                "<gradient:#8DD6EE:#FFFFFF:#8DD6EE>Perfect Capture!</gradient> &7Instant success!"
                        )
                );
            }
        }




        SkillXpRouter.onPokemonCatch(
                player,
                pokemon.getShiny(),
                pokemon.isLegendary(),
                pokemon.isMythical(),
                skills,
                1.0
        );
    }
}