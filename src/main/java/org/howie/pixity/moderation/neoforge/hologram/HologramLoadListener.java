package org.howie.pixity.moderation.neoforge.hologram;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import net.neoforged.neoforge.event.server.ServerStartedEvent;
import org.howie.pixity.moderation.neoforge.hologram.template.HologramTemplateRegistry;
import org.howie.pixity.moderation.neoforge.placeholder.GlyphPlaceholders;
import org.howie.pixity.moderation.neoforge.placeholder.PlaceholderDefaults;

@EventBusSubscriber
public class HologramLoadListener {

    @SubscribeEvent
    public static void serverStarted(
            ServerStartedEvent event
    ) {
        PlaceholderDefaults.register();

        GlyphPlaceholders.register();
        HologramTemplateRegistry.registerDefaults();
        HologramStorageService.loadAll();
    }
}