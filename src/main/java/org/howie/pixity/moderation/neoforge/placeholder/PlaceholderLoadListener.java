package org.howie.pixity.moderation.neoforge.placeholder;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import net.neoforged.neoforge.event.server.ServerStartedEvent;

@EventBusSubscriber
public class PlaceholderLoadListener {

    @SubscribeEvent
    public static void serverStarted(
            ServerStartedEvent event
    ) {

        PlaceholderDefaults.register();
    }
}