package org.howie.pixity.moderation.neoforge.worldboss;

import com.cobblemon.mod.common.api.events.CobblemonEvents;

import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.chat.TextFormatter;

public class WorldBossBattleListener {





    public static void register() {

        CobblemonEvents.BATTLE_STARTED_PRE.subscribe(event -> {

            try {

                for (var actor : event.getBattle().getActors()) {

                    try {

                        Object entity =
                                actor.getClass()
                                        .getMethod("getEntity")
                                        .invoke(actor);

                        if (!(entity instanceof ServerPlayer player)) {
                            continue;
                        }

                        int attempts =
                                WorldBossBattleAttempts.getAttempts(
                                        player.getUUID()
                                );

                        if (attempts >= 5) {

                            player.sendSystemMessage(

                                    TextFormatter.parse(

                                            "<red>&l❌ RAID LIMIT</red>\n\n"

                                                    + "<gray>You already used all 5 raid battles.</gray>"
                                    )
                            );

                            event.cancel();

                            return;
                        }

                    } catch (Exception ignored) {
                    }
                }

            } catch (Exception e) {

                e.printStackTrace();
            }
        });





        CobblemonEvents.BATTLE_STARTED_POST.subscribe(event -> {

            try {

                if (!WorldBossManager.isActive()) {
                    return;
                }

            } catch (Exception e) {

                e.printStackTrace();
            }
        });





        CobblemonEvents.LOOT_DROPPED.subscribe(event -> {

            try {

                Object entity =
                        event.getClass()
                                .getMethod("getEntity")
                                .invoke(event);

                if (entity == null) {
                    return;
                }

                boolean worldBoss =
                        (boolean) entity.getClass()
                                .getMethod("getPersistentData")
                                .invoke(entity)
                                .getClass()
                                .getMethod(
                                        "getBoolean",
                                        String.class
                                )
                                .invoke(

                                        entity.getClass()
                                                .getMethod("getPersistentData")
                                                .invoke(entity),

                                        "pixity_worldboss"
                                );

                if (!worldBoss) {
                    return;
                }





                try {

                    event.getClass()
                            .getMethod("getDrops")
                            .invoke(event);

                    ((java.util.List<?>)
                            event.getClass()
                                    .getMethod("getDrops")
                                    .invoke(event)
                    ).clear();

                } catch (Exception ignored) {
                }

                System.out.println(
                        "[WorldBoss] Prevented Cobblemon loot."
                );

            } catch (Exception e) {

                e.printStackTrace();
            }
        });





        CobblemonEvents.BATTLE_FAINTED.subscribe(event -> {

            try {





                if (!WorldBossManager.isActive()) {
                    return;
                }





                ServerPlayer player = null;

                try {

                    Object context =
                            event.getContext();

                    Object origin =
                            context.getClass()
                                    .getMethod("getOrigin")
                                    .invoke(context);

                    Object actor =
                            origin.getClass()
                                    .getMethod("getActor")
                                    .invoke(origin);

                    Object entity =
                            actor.getClass()
                                    .getMethod("getEntity")
                                    .invoke(actor);

                    if (entity instanceof ServerPlayer sp) {

                        player = sp;
                    }

                } catch (Exception ignored) {
                }

                if (player == null) {
                    return;
                }






                long raidDamage =
                        25_000L;

                try {

                    Object killed =
                            event.getKilled();





                    try {

                        Object entity =
                                killed.getClass()
                                        .getMethod("getEntity")
                                        .invoke(killed);

                        if (entity == null) {
                            return;
                        }

                        boolean isBoss =
                                (boolean) entity.getClass()
                                        .getMethod("getPersistentData")
                                        .invoke(entity)
                                        .getClass()
                                        .getMethod(
                                                "getBoolean",
                                                String.class
                                        )
                                        .invoke(

                                                entity.getClass()
                                                        .getMethod("getPersistentData")
                                                        .invoke(entity),

                                                "pixity_worldboss"
                                        );

                        if (!isBoss) {
                            return;
                        }

                    } catch (Exception ignored) {

                        return;
                    }

                    int currentHp =
                            (int) killed.getClass()
                                    .getMethod("getHealth")
                                    .invoke(killed);

                    int maxHp =
                            (int) killed.getClass()
                                    .getMethod("getMaxHealth")
                                    .invoke(killed);

                    double percentRemoved =
                            1D - (
                                    currentHp
                                            / (double) maxHp
                            );





                    long maxRaidDamage;

                    long bossHp =
                            WorldBossManager.getCurrent()
                                    .maxHealth;

                    if (bossHp >= 4_000_000L) {

                        maxRaidDamage = 1_000_000L;

                    } else if (bossHp >= 1_000_000L) {

                        maxRaidDamage = 250_000L;

                    } else {

                        maxRaidDamage = 100_000L;
                    }

                    raidDamage =
                            (long) (
                                    percentRemoved
                                            * maxRaidDamage
                            );

                    raidDamage =
                            Math.max(
                                    5_000L,
                                    raidDamage
                            );

                } catch (Exception e) {

                    e.printStackTrace();
                }






                WorldBossDamageTracker.addDamage(

                        player.getUUID(),

                        raidDamage
                );





                WorldBossBattleAttempts
                        .addAttempt(
                                player.getUUID()
                        );







                boolean wasKilled =
                        WorldBossManager.damage(
                                player.server,
                                raidDamage
                        );





                if (
                        !wasKilled
                                &&
                                WorldBossManager.isActive()
                                &&
                                WorldBossManager.getHealth() > 0
                ) {

                    player.server.tell(

                            new TickTask(

                                    player.server.getTickCount() + 60,

                                    () -> {

                                        try {

                                            WorldBossManager.clearActiveEntity();

                                            WorldBossManager.respawnBoss();

                                            System.out.println(
                                                    "[WorldBoss] Respawned raid boss."
                                            );

                                        } catch (Exception e) {

                                            e.printStackTrace();
                                        }
                                    }
                            )
                    );
                }







                player.sendSystemMessage(

                        TextFormatter.parse(

                                "<red>&l👑 RAID DAMAGE</red>\n\n"

                                        + "<yellow>You dealt </yellow>"

                                        + "<green>"
                                        + String.format(
                                        "%,d",
                                        raidDamage
                                )
                                        + "</green>"

                                        + "<yellow> raid damage!</yellow>\n\n"

                                        + "<gray>Attempts:</gray> "

                                        + "<red>"
                                        + WorldBossBattleAttempts.getAttempts(
                                        player.getUUID()
                                )
                                        + "/5</red>\n\n"

                                        + "<gray>Boss HP Remaining:</gray>\n"

                                        + "<red>"
                                        + String.format(
                                        "%,d",
                                        WorldBossManager.getHealth()
                                )
                                        + "</red>"
                        )
                );

            } catch (Exception e) {

                e.printStackTrace();
            }
        });
    }
}