package org.howie.pixity.moderation.neoforge.worldboss;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import org.howie.pixity.moderation.chat.TextFormatter;

public class WorldBossEffects {





    private static double damageMultiplier =
            1.0;





    public static void reset() {

        damageMultiplier = 1.0;
    }





    public static double getDamageMultiplier() {
        return damageMultiplier;
    }





    public static void setDamageMultiplier(
            double value
    ) {

        damageMultiplier = value;
    }






    public static void weatherPhase(
            MinecraftServer server
    ) {





        server.getAllLevels().forEach(level -> {

            level.setWeatherParameters(

                    0,

                    12000,

                    true,

                    false
            );
        });





        broadcast(

                server,

                "&b&l⚠ RAID PHASE: 75%\n\n"

                        + "<gold>"
                        + WorldBossManager.getCurrent().display
                        + "</gold>\n\n"

                        + "<gray>The battlefield has become stormy!</gray>\n"

                        + "<blue>Rain intensifies across the arena...</blue>"
        );
    }






    public static void phase50(
            MinecraftServer server
    ) {

        damageMultiplier = 1.5;





        broadcast(

                server,

                "&c&l⚠ RAID PHASE: 50%\n\n"

                        + "<gold>"
                        + WorldBossManager.getCurrent().display
                        + "</gold>\n\n"

                        + "<dark_red>The boss's attacks became stronger!</dark_red>\n"

                        + "<gray>Raid damage effectiveness reduced!</gray>"
        );
    }






    public static void phase25(
            MinecraftServer server
    ) {





        for (ServerPlayer player
                : server.getPlayerList().getPlayers()) {

            player.setRemainingFireTicks(
                    100
            );

            player.addEffect(

                    new MobEffectInstance(

                            MobEffects.WEAKNESS,

                            200,

                            0,

                            false,

                            true
                    )
            );
        }





        broadcast(

                server,

                "<dark_red>&l⚠ RAID PHASE: 25%</dark_red>\n\n"

                        + "<gold>"
                        + WorldBossManager.getCurrent().display
                        + "</gold>\n\n"

                        + "<red>The arena erupts into flames!</red>\n"

                        + "<gray>Raiders are being scorched!</gray>"
        );
    }






    public static void phase10(
            MinecraftServer server
    ) {

        damageMultiplier = 2.0;





        broadcast(

                server,

                "<dark_purple>&l☄ FINAL ENRAGE: 10%</dark_purple>\n\n"

                        + "<gold>"
                        + WorldBossManager.getCurrent().display
                        + "</gold>\n\n"

                        + "<light_purple>The boss has gone berserk!</light_purple>\n"

                        + "<red>Raid damage effectiveness heavily reduced!</red>"
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