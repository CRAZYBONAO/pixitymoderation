package org.howie.pixity.moderation.neoforge.fishing;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.entity.animal.Squid;
import org.howie.pixity.moderation.neoforge.fishing.events.FishingEventManager;

public class FishingMobListener {

    @SubscribeEvent
    public void onDeath(LivingDeathEvent e) {

        Entity entity = e.getEntity();




        if (!(e.getSource().getEntity() instanceof ServerPlayer player)) {
            return;
        }




        FishingManager.handleCrabDrop(entity);
        FishingManager.handleMobDrops(entity);




        var uuid = player.getUUID();


        if (entity.getPersistentData().getBoolean("pixity_crab")) {
            FishingDatabase.incrementStat(uuid, "crabs_killed");
        }


        if (entity instanceof Dolphin) {
            FishingDatabase.incrementStat(uuid, "dolphins_killed");

            FishingEventManager.onDolphinKill(player);
        }


        if (entity instanceof Squid) {
            FishingDatabase.incrementStat(uuid, "squids_killed");

            FishingEventManager.onSquidKill(player);
        }
    }
}