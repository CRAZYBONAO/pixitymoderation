package org.howie.pixity.moderation.neoforge.skills;

import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.pokeball.PokeBallCaptureCalculatedEvent;
import net.minecraft.server.level.ServerPlayer;
import org.howie.pixity.moderation.PixityModerationNeoForge;
import org.howie.pixity.moderation.chat.TextFormatter;

public class ProfessorCatchListener {

    private final SkillService skills;

    public ProfessorCatchListener(SkillService skills) {

        this.skills = skills;

        CobblemonEvents.POKE_BALL_CAPTURE_CALCULATED.subscribe(this::onCapture);
    }

    private void onCapture(PokeBallCaptureCalculatedEvent event) {

        if (!(event.getThrower() instanceof ServerPlayer player)) return;

        var abilities = PixityModerationNeoForge.ABILITY_ENGINE;
        var active = PixityModerationNeoForge.ACTIVE_ABILITIES;




        if (!abilities.isEnabled(player, AbilityType.GOTTA_CATCH_EM_ALL)) {
            return;
        }

        int level = skills.get(player.getUUID()).getLevel(SkillType.PROFESSOR);







        double procChance = 0.01 + (level * 0.002);

        if (active.isActive(player, AbilityType.CAPTURE_AURA)) {
            procChance += 0.10;
        }

        if (event.getPokemonEntity().getPokemon().isLegendary()) {
            procChance *= 0.50;
        }

        if (event.getPokemonEntity().getPokemon().getShiny()) {
            procChance *= 0.75;
        }

        if (Math.random() < procChance) {

            var result = event.getCaptureResult();


            result.isSuccessfulCapture();

            event.setCaptureResult(result);

            player.displayClientMessage(
                    TextFormatter.parse(
                            "<gradient:#8DD6EE:#FFFFFF:#8DD6EE>Gotta Catch 'Em All!</gradient> &7Capture overridden!"
                    ),
                    true
            );
        }
    }
}