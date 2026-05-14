package org.howie.pixity.moderation.neoforge.worldboss;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import net.minecraft.world.entity.LightningBolt;

import net.minecraft.world.phys.Vec3;

import org.howie.pixity.moderation.chat.TextFormatter;

import java.util.Random;

public class WorldBossMechanics {





    private static final Random RANDOM =
            new Random();





    public static void apply(

            MinecraftServer server,

            String species,

            int phase
    ) {

        switch (species.toLowerCase()) {





            case "gengar" -> gengar(
                    server,
                    phase
            );





            case "ampharos" -> ampharos(
                    server,
                    phase
            );





            case "milotic" -> milotic(
                    server,
                    phase
            );





            case "darkrai" -> darkrai(
                    server,
                    phase
            );





            case "rayquaza" -> rayquaza(
                    server,
                    phase
            );





            case "snorlax" -> snorlax(
                    server,
                    phase
            );
        }
    }





    private static void gengar(
            MinecraftServer server,
            int phase
    ) {

        if (phase != 25) {
            return;
        }

        for (ServerPlayer player
                : server.getPlayerList().getPlayers()) {

            player.addEffect(

                    new MobEffectInstance(

                            MobEffects.BLINDNESS,

                            200,

                            0
                    )
            );
        }

        WorldBossHazards.add(

                server,

                "toxic_spikes",

                "Toxic Spikes",

                "Poisonous spikes spread across the battlefield!"
        );

        broadcast(

                server,

                "<dark_purple>&l👻 NIGHTMARE VEIL</dark_purple>\n\n"

                        + "<gray>Gengar engulfed the arena in darkness!</gray>"
        );
    }





    private static void ampharos(
            MinecraftServer server,
            int phase
    ) {

        if (phase != 50) {
            return;
        }

        for (ServerPlayer player
                : server.getPlayerList().getPlayers()) {

            try {

                LightningBolt bolt =
                        new LightningBolt(

                                net.minecraft.world.entity.EntityType.LIGHTNING_BOLT,

                                player.level()
                        );

                bolt.moveTo(
                        player.position()
                );

                player.level()
                        .addFreshEntity(
                                bolt
                        );

            } catch (Exception ignored) {
            }
        }

        WorldBossHazards.add(

                server,

                "paralysis_field",

                "Paralysis Field",

                "Electric currents slow the raid team!"
        );

        broadcast(

                server,

                "<yellow>&l⚡ THUNDER WRATH</yellow>\n\n"

                        + "<gray>Lightning crashes across the arena!</gray>"
        );
    }





    private static void milotic(
            MinecraftServer server,
            int phase
    ) {

        if (phase != 75) {
            return;
        }

        long heal =
                WorldBossManager.getCurrent()
                        .maxHealth / 10;

        WorldBossManager.heal(
                heal
        );

        WorldBossHazards.add(

                server,

                "frozen_terrain",

                "Frozen Terrain",

                "The arena becomes dangerously slippery and cold!"
        );

        broadcast(

                server,

                "&b&l🌊 TIDAL RECOVERY\n\n"

                        + "<gray>Milotic restored part of its health!</gray>"
        );
    }





    private static void darkrai(
            MinecraftServer server,
            int phase
    ) {

        if (phase != 50) {
            return;
        }

        for (ServerPlayer player
                : server.getPlayerList().getPlayers()) {

            player.addEffect(

                    new MobEffectInstance(

                            MobEffects.DARKNESS,

                            300,

                            0
                    )
            );

            player.addEffect(

                    new MobEffectInstance(

                            MobEffects.MOVEMENT_SLOWDOWN,

                            300,

                            1
                    )
            );
        }

        broadcast(

                server,

                "<dark_gray>&l☠ NIGHTMARE DOMAIN</dark_gray>\n\n"

                        + "<gray>Darkrai trapped players in nightmares!</gray>"
        );
    }





    private static void rayquaza(
            MinecraftServer server,
            int phase
    ) {

        if (phase != 10) {
            return;
        }

        for (ServerPlayer player
                : server.getPlayerList().getPlayers()) {

            Vec3 push =
                    new Vec3(

                            RANDOM.nextDouble() - 0.5,

                            1.2,

                            RANDOM.nextDouble() - 0.5
                    );

            player.push(

                    push.x,

                    push.y,

                    push.z
            );
        }

        WorldBossHazards.add(

                server,

                "stealth_rock",

                "Stealth Rock",

                "Sharp floating stones surround the battlefield!"
        );

        broadcast(

                server,

                "<green>&l☄ SKY ASCENSION</green>\n\n"

                        + "<gray>Rayquaza unleashed hurricane-force winds!</gray>"
        );
    }





    private static void snorlax(
            MinecraftServer server,
            int phase
    ) {

        if (phase != 25) {
            return;
        }

        long heal =
                WorldBossManager.getCurrent()
                        .maxHealth / 5;

        WorldBossManager.heal(
                heal
        );

        WorldBossHazards.add(

                server,

                "sticky_web",

                "Sticky Web",

                "Thick webs slow down the raid team!"
        );

        broadcast(

                server,

                "<gold>&l💤 REST</gold>\n\n"

                        + "<gray>Snorlax restored a massive amount of health!</gray>"
        );
    }





    private static void broadcast(

            MinecraftServer server,

            String msg
    ) {

        server.getPlayerList()
                .broadcastSystemMessage(

                        TextFormatter.parse(
                                msg
                        ),

                        false
                );
    }
}