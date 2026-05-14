package org.howie.pixity.moderation.neoforge.worldboss;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;

import net.minecraft.server.MinecraftServer;

import org.howie.pixity.moderation.chat.TextFormatter;

public class WorldBossTransformationManager {





    private static boolean transformed =
            false;





    public static void reset() {

        transformed = false;
    }





    public static void tryTransform(
            MinecraftServer server
    ) {

        if (transformed) {
            return;
        }

        if (!WorldBossManager.isActive()) {
            return;
        }

        WorldBossDefinition boss =
                WorldBossManager.getCurrent();

        if (boss == null) {
            return;
        }

        double percent =
                WorldBossManager.getHealth()

                        / (double) boss.maxHealth;





        if (percent > 0.25) {
            return;
        }

        transformed = true;

        PokemonEntity entity =
                WorldBossManager.getActiveBoss();

        if (entity == null) {
            return;
        }





        try {

            entity.getPokemon()
                    .setScaleModifier(
                            boss.scale + 2.0F
                    );

        } catch (Exception ignored) {
        }





        entity.setGlowingTag(true);





        entity.setCustomName(

                TextFormatter.parse(

                        "<dark_red>&l☄ ASCENDED "

                                + boss.display.toUpperCase()

                                + "</dark_red>"
                )
        );





        WorldBossEffects.setDamageMultiplier(
                2.5
        );





        server.getPlayerList()
                .broadcastSystemMessage(

                        TextFormatter.parse(

                                "<dark_purple>&l☄ RAID TRANSFORMATION</dark_purple>\n\n"

                                        + "<gold>"
                                        + boss.display
                                        + "</gold>\n\n"

                                        + "<red>The boss has transformed into an ascended form!</red>"
                        ),

                        false
                );
    }
}