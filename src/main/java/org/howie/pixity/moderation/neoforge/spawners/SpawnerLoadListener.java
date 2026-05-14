package org.howie.pixity.moderation.neoforge.spawners;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

public class SpawnerLoadListener {

    private final SpawnerStackService stacks;

    public SpawnerLoadListener(SpawnerStackService stacks) {
        this.stacks = stacks;
    }

    @SubscribeEvent
    public void onStart(ServerStartedEvent e) {

        MinecraftServer server = e.getServer();

        for (ServerLevel level : server.getAllLevels()) {


            if (!level.dimension()
                    .location()
                    .toString()
                    .equals("minecraft:overworld"))
                continue;

            stacks.loadAll(level);
        }
    }
}