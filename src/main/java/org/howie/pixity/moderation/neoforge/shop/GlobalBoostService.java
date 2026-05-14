package org.howie.pixity.moderation.neoforge.shop;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GlobalBoostService {

    private static double globalMultiplier = 1.0;
    private static long endTime = 0;

    private static final ConcurrentHashMap<UUID, Double> contributors = new ConcurrentHashMap<>();



    public static void activate(double multiplier, long durationMillis, UUID activator) {

        long now = System.currentTimeMillis();

        if (now < endTime) {
            endTime += durationMillis;
        } else {
            globalMultiplier = multiplier;
            endTime = now + durationMillis;
        }

        contributors.put(activator, multiplier);
    }


    public static double getMultiplier() {

        if (System.currentTimeMillis() > endTime) {
            globalMultiplier = 1.0;
        }

        return globalMultiplier;
    }

    public static void announce(ServerPlayer activator, double mult, int minutes) {

        var server = activator.getServer();
        if (server == null) return;

        String name = activator.getName().getString();

        var message = LegacyAmpersand.parse(
                "§6§lGLOBAL BOOST §7>> §e" + name +
                        " §7activated a §a" + mult + "x §7boost for §e" + minutes + " minutes!"
        );


        server.getPlayerList().broadcastSystemMessage(message, false);


        server.getPlayerList().getPlayers().forEach(p -> {
            p.playNotifySound(
                    net.minecraft.sounds.SoundEvents.UI_TOAST_CHALLENGE_COMPLETE,
                    net.minecraft.sounds.SoundSource.MASTER,
                    1.0f,
                    1.2f
            );
        });


        server.getPlayerList().getPlayers().forEach(p -> {

            var level = p.serverLevel();
            var pos = p.position();

            level.sendParticles(
                    net.minecraft.core.particles.ParticleTypes.TOTEM_OF_UNDYING,
                    pos.x, pos.y + 1, pos.z,
                    20, 0.5, 0.5, 0.5, 0.2
            );
        });
    }

    public static long getRemaining() {
        return Math.max(0, endTime - System.currentTimeMillis());
    }

    public static boolean isActive() {
        return System.currentTimeMillis() < endTime;
    }
}

