package org.howie.pixity.moderation.neoforge.hologram;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.howie.pixity.moderation.neoforge.hologram.packet.PacketHologramManager;

@EventBusSubscriber
public class HologramTicker {

    private static int tick = 0;

    @SubscribeEvent
    public static void serverTick(ServerTickEvent.Post event) {

        tick++;

        if (tick < 20)
            return;

        tick = 0;

        for (Hologram hologram :
                HologramManager.all()) {

            PacketHologramManager.tick(
                    hologram
            );
        }
    }
}