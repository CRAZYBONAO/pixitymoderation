package org.howie.pixity.moderation.neoforge.pokemon;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;

import org.howie.pixity.moderation.chat.CachedText;
import org.howie.pixity.moderation.chat.TextFormatter;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

public class LegendarySpawnListener {

    @SubscribeEvent
    public void onSpawn(EntityJoinLevelEvent e) {

        if (!(e.getLevel() instanceof ServerLevel level)) return;
        if (!(e.getEntity() instanceof PokemonEntity entity)) return;




        if (entity.getPersistentData().getBoolean("pixity_announce")) return;
        entity.getPersistentData().putBoolean("pixity_announce", true);




        level.getServer().tell(new net.minecraft.server.TickTask(1, () -> {

            try {
                var pokemon = entity.getPokemon();

                boolean isLegendary = pokemon.isLegendary();
                boolean isMythical = pokemon.isMythical();
                boolean isUltra = pokemon.isUltraBeast();
                boolean isShiny = pokemon.getShiny();


                if (!isLegendary && !isMythical && !isUltra && !isShiny) return;




                String rawName = pokemon.getSpecies().getName();
                String name = rawName.substring(0,1).toUpperCase() + rawName.substring(1);





                if (isMythical) {
                    broadcast(level,
                            "<rainbow>&l✦ MYTHICAL SPAWN ✦</rainbow>",
                            "&d&l" + name,
                            isShiny
                    );
                    return;
                }

                if (isLegendary) {
                    broadcast(level,
                            "<rainbow>&l✦ LEGENDARY SPAWN ✦</rainbow>",
                            "&6&l" + name,
                            isShiny
                    );
                    return;
                }

                if (isUltra) {
                    broadcast(level,
                            "<rainbow>&l✦ ULTRA BEAST SPAWN ✦</rainbow>",
                            "&a&l" + name,
                            isShiny
                    );
                    return;
                }


                if (isShiny) {
                    broadcast(level,
                            "<rainbow>&l✦ SHINY SPAWN ✦</rainbow>",
                            "&d&l" + name,
                            true
                    );
                }

            } catch (Exception ignored) {}

        }));
    }

    private void broadcast(ServerLevel level, String header, String name, boolean shiny) {

        String shinyTag = shiny ? " &d&l✨ SHINY ✨" : "";

        String message = header
                + " &8»"
                + shinyTag
                + " " + name
                + " &fhas appeared!";

        Component headerComp = TextFormatter.rainbow("✦ SHINY SPAWN ✦")
                .copy()
                .withStyle(net.minecraft.ChatFormatting.BOLD);

        Component rest = LegacyAmpersand.parse(
                " &8»"
                        + (shiny ? " &d&l✨ SHINY ✨" : "")
                        + " " + name
                        + " &fhas appeared!"
        );

        Component msg = Component.empty()
                .append(headerComp)
                .append(rest);

        for (ServerPlayer p : level.getServer().getPlayerList().getPlayers()) {
            p.sendSystemMessage(msg);
        }
    }
}