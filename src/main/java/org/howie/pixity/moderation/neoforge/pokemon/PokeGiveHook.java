package org.howie.pixity.moderation.neoforge.pokemon;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.CommandEvent;

import net.minecraft.server.level.ServerPlayer;

public class PokeGiveHook {

    @SubscribeEvent
    public void onCommand(CommandEvent e) {

        String cmd = e.getParseResults().getReader().getString();


        if (!cmd.startsWith("pokegive") && !cmd.startsWith("/pokegive")) return;

        try {



            String[] parts = cmd.split(" ");

            if (parts.length < 3) return;

            String playerName = parts[1];
            String species = parts[2];

            var server = e.getParseResults().getContext().getSource().getServer();
            ServerPlayer target = server.getPlayerList().getPlayerByName(playerName);

            if (target == null) return;


            server.tell(new net.minecraft.server.TickTask(1, () -> {

                boolean isNew = PokedexDatabase.addCatch(target.getUUID(), species);

                if (isNew) {
                    int count = PokedexDatabase.getCount(target.getUUID());

                    target.sendSystemMessage(
                            org.howie.pixity.moderation.chat.CachedText.of(
                                    "<green>New Pokémon added! (" + count + ")</green>"
                            )
                    );

                    PokedexRewardManager.checkRewards(target, count);
                    HologramManager.markDirty("normal");
                }


                HologramManager.queueRefresh(target.server);
            }));

        } catch (Exception ignored) {}
    }
}