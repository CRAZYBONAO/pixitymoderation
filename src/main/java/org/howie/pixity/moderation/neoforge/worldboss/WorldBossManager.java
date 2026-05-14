package org.howie.pixity.moderation.neoforge.worldboss;

import net.minecraft.server.MinecraftServer;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.api.events.battles.BattleFaintedEvent;


import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.chat.TextFormatter;

public class WorldBossManager {

    private static net.minecraft.server.MinecraftServer server;





    private static WorldBossDefinition current;





    private static long health;





    private static long endTime;

    private static PokemonEntity activeBoss;





    public static void start(
            ServerPlayer player
    ) {

        current =
                WorldBossPool.randomBoss();

        WorldBossManager.server =
                player.server;





        boolean legendary =
                current.maxHealth
                        >= 4_000_000L;

        health =
                current.maxHealth;

        WorldBossHazards.reset();

        WorldBossEffects.reset();

        WorldBossPhaseManager.reset();

        WorldBossBattleAttempts.clear();





        activeBoss =
                WorldBossSpawner.spawn(
                        player,
                        current
                );

        if (activeBoss == null) {

            current = null;

            return;
        }

        endTime =
                System.currentTimeMillis()
                        + (
                        current.durationMinutes
                                * 60_000L
                );

        WorldBossPersistence.save(
                player.server
        );

        WorldBossBossBar.remove();

        WorldBossBossBar.create(player.server);





        player.server.getPlayerList()
                .broadcastSystemMessage(

                        TextFormatter.parse(
                                (
                                        legendary

                                                ?

                                                "<dark_purple>&l☄ LEGENDARY RAID BOSS</dark_purple>\n\n"

                                                :

                                                "<red>&l👑 WORLD BOSS SPAWNED</red>\n\n"
                                )

                                        + "<gold>"
                                        + current.display
                                        + "</gold>"

                                        + "<gray> has appeared!</gray>\n\n"

                                        + "<green>HP: "
                                        + String.format(
                                        "%,d",
                                        current.maxHealth
                                )
                                        + "</green>\n\n"

                                        + "<yellow>Rewards:</yellow>\n"

                                        + "<aqua>"
                                        + current.rewardTokens
                                        + " Tokens</aqua>\n"

                                        + "<green>$"
                                        + String.format(
                                        "%,d",
                                        current.rewardMoney
                                )
                                        + "</green>"
                        ),
                        false
                );
    }





    public static boolean damage(
            MinecraftServer server,
            long amount
    ) {


        health -= amount;
        WorldBossPhaseManager.tick(
                server
        );
        WorldBossPersistence.save(server);

        if (health < 0) {
            health = 0;
        }

        if (activeBoss != null) {

            WorldBossBossBar.update(
                    activeBoss.getServer()
            );
        }





        if (health <= 0) {

            kill(server);

            return true;
        }

        return false;
    }





    public static void kill(
            MinecraftServer server
    ) {

        if (current == null) {
            return;
        }

        server.getPlayerList()
                .broadcastSystemMessage(

                        TextFormatter.parse(
                                "<gold>&l👑 WORLD BOSS DEFEATED!</gold>\n\n"

                                        + "<yellow>"
                                        + current.display
                                        + "</yellow>"

                                        + "<gray> has been defeated!</gray>"
                        ),
                        false
                );





        if (activeBoss != null) {

            activeBoss.discard();

            activeBoss = null;
        }

        WorldBossRewards.distribute(
                server
        );

        WorldBossDamageTracker.clear();

        current = null;

        health = 0;

        activeBoss = null;

        WorldBossBossBar.remove();

        WorldBossPersistence.save(server);
    }





    public static WorldBossDefinition getCurrent() {
        return current;
    }

    public static long getHealth() {
        return health;
    }

    public static long getEndTime() {
        return endTime;
    }

    public static PokemonEntity getActiveBoss() {
        return activeBoss;
    }

    public static boolean isActive() {
        return current != null;
    }

    public static void clearActiveEntity() {

        activeBoss = null;
    }

    public static void restore(

            MinecraftServer server,

            WorldBossDefinition definition,

            long restoredHealth,

            long restoredEndTime
    ) {

        try {

            current = definition;

            health = restoredHealth;

            endTime = restoredEndTime;

            WorldBossManager.server = server;

            recoverExistingBoss();

            WorldBossBossBar.remove();

            WorldBossBossBar.create(server);

            WorldBossBossBar.update(server);

            System.out.println(
                    "[WorldBoss] Restored active raid boss."
            );

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public static void findExistingBoss(
            MinecraftServer server
    ) {

        try {

            for (var level : server.getAllLevels()) {

                for (var entity : level.getAllEntities()) {

                    if (!(entity instanceof PokemonEntity pokemon)) {
                        continue;
                    }

                    if (
                            pokemon.getPersistentData()
                                    .getBoolean(
                                            "pixity_worldboss"
                                    )
                    ) {

                        activeBoss = pokemon;

                        System.out.println(
                                "[WorldBoss] Re-linked existing boss entity."
                        );

                        return;
                    }
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public static void recoverExistingBoss() {

        try {

            if (server == null) {
                return;
            }

            if (current == null) {
                return;
            }

            var level =
                    server.getLevel(
                            net.minecraft.world.level.Level.OVERWORLD
                    );

            if (level == null) {
                return;
            }

            for (var entity : level.getAllEntities()) {

                if (!(entity instanceof PokemonEntity pokemon)) {
                    continue;
                }

                try {

                    boolean isBoss =
                            pokemon.getPersistentData()
                                    .getBoolean(
                                            "pixity_worldboss"
                                    );

                    if (!isBoss) {
                        continue;
                    }

                    activeBoss = pokemon;

                    System.out.println(
                            "[WorldBoss] Recovered existing raid boss entity."
                    );

                    return;

                } catch (Exception ignored) {
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }





    public static void startSpecific(

            ServerPlayer player,

            WorldBossDefinition definition
    ) {

        current = definition;

        WorldBossPhaseManager.reset();

        WorldBossTransformationManager.reset();

        health = current.maxHealth;

        WorldBossHazards.reset();

        WorldBossEffects.reset();

        WorldBossPhaseManager.reset();

        WorldBossBattleAttempts.clear();

        WorldBossManager.server =
                player.server;

        endTime =
                System.currentTimeMillis()

                        + (
                        current.durationMinutes
                                * 60_000L
                );

        WorldBossPersistence.save(
                player.server
        );

        activeBoss =
                WorldBossSpawner.spawn(
                        player,
                        current
                );

        WorldBossBossBar.create(
                player.server
        );

        player.server.getPlayerList()
                .broadcastSystemMessage(

                        TextFormatter.parse(
                                "<red>&l👑 WORLD BOSS SPAWNED</red>\n\n"

                                        + "<gold>"
                                        + current.display
                                        + "</gold>\n\n"

                                        + "<gray>HP:</gray> "

                                        + "<red>"
                                        + String.format(
                                        "%,d",
                                        current.maxHealth
                                )
                                        + "</red>"
                        ),

                        false
                );
    }





    public static void heal(
            long amount
    ) {

        if (!isActive()) {
            return;
        }

        if (current == null) {
            return;
        }

        health += amount;

        if (health > current.maxHealth) {

            health = current.maxHealth;
        }
    }





    public static void refreshBoss() {

        if (activeBoss == null) {
            return;
        }

        try {

            activeBoss.getPokemon()
                    .heal();

        } catch (Exception ignored) {
        }
    }





    public static void respawnBoss() {

        try {





            if (current == null) {
                return;
            }

            if (server == null) {
                return;
            }





            if (activeBoss != null) {

                try {

                    activeBoss.discard();

                } catch (Exception ignored) {
                }

                activeBoss = null;
            }





            var players =
                    server.getPlayerList()
                            .getPlayers();

            if (players.isEmpty()) {
                return;
            }

            var level =
                    WorldBossSpawnPoint.getLevel(
                            players.getFirst()
                    );

            if (level == null) {
                return;
            }

            var spawnPos =
                    WorldBossSpawnPoint.getPosition();





            var species =
                    com.cobblemon.mod.common.api.pokemon.PokemonSpecies
                            .getByIdentifier(

                                    net.minecraft.resources.ResourceLocation.parse(
                                            "cobblemon:"
                                                    + current.species
                                    )
                            );

            if (species == null) {

                System.out.println(
                        "[WorldBoss] Failed to respawn species."
                );

                return;
            }





            var pokemon =
                    new com.cobblemon.mod.common.pokemon.Pokemon();

            pokemon.setSpecies(species);





            pokemon.setLevel(100);





            try {

                pokemon.setScaleModifier(
                        current.scale
                );

            } catch (Exception ignored) {
            }





            try {

                pokemon.initializeMoveset(true);

            } catch (Exception ignored) {
            }





            try {

                var ivs =
                        pokemon.getIvs();

                ivs.set(
                        com.cobblemon.mod.common.api.pokemon.stats.Stats.HP,
                        31
                );

                ivs.set(
                        com.cobblemon.mod.common.api.pokemon.stats.Stats.ATTACK,
                        31
                );

                ivs.set(
                        com.cobblemon.mod.common.api.pokemon.stats.Stats.DEFENCE,
                        31
                );

                ivs.set(
                        com.cobblemon.mod.common.api.pokemon.stats.Stats.SPECIAL_ATTACK,
                        31
                );

                ivs.set(
                        com.cobblemon.mod.common.api.pokemon.stats.Stats.SPECIAL_DEFENCE,
                        31
                );

                ivs.set(
                        com.cobblemon.mod.common.api.pokemon.stats.Stats.SPEED,
                        31
                );

            } catch (Exception ignored) {
            }





            try {

                var evs =
                        pokemon.getEvs();

                evs.set(
                        com.cobblemon.mod.common.api.pokemon.stats.Stats.HP,
                        252
                );

                evs.set(
                        com.cobblemon.mod.common.api.pokemon.stats.Stats.ATTACK,
                        252
                );

                evs.set(
                        com.cobblemon.mod.common.api.pokemon.stats.Stats.DEFENCE,
                        252
                );

                evs.set(
                        com.cobblemon.mod.common.api.pokemon.stats.Stats.SPECIAL_ATTACK,
                        252
                );

                evs.set(
                        com.cobblemon.mod.common.api.pokemon.stats.Stats.SPECIAL_DEFENCE,
                        252
                );

                evs.set(
                        com.cobblemon.mod.common.api.pokemon.stats.Stats.SPEED,
                        252
                );

            } catch (Exception ignored) {
            }





            pokemon.heal();





            activeBoss =
                    pokemon.sendOut(

                            level,

                            new net.minecraft.world.phys.Vec3(

                                    spawnPos.getX() + 0.5,

                                    spawnPos.getY() + 1,

                                    spawnPos.getZ() + 0.5
                            ),

                            null,

                            entity -> {





                                entity.setNoAi(true);





                                entity.setGlowingTag(true);





                                entity.setPersistenceRequired();





                                entity.setCustomName(

                                        org.howie.pixity.moderation.chat.TextFormatter.parse(

                                                "<red><bold>👑 "
                                                        + current.display
                                                        + "</bold></red>"
                                        )
                                );

                                entity.setCustomNameVisible(true);





                                entity.getPersistentData()
                                        .putBoolean(
                                                "pixity_worldboss",
                                                true
                                        );

                                return kotlin.Unit.INSTANCE;
                            }
                    );

            System.out.println(
                    "[WorldBoss] Respawned raid boss."
            );

        } catch (Exception e) {

            e.printStackTrace();
        }
    }



}