package org.howie.pixity.moderation.neoforge.worldboss;

import net.minecraft.commands.CommandSourceStack;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.Random;

public class WorldBossLootTable {





    private static final Random RANDOM =
            new Random();





    public static void giveLoot(

            MinecraftServer server,

            ServerPlayer player,

            long damage,

            WorldBossDefinition boss
    ) {





        double scale =
                Math.min(
                        1.0,
                        damage / 1_000_000D
                );





        if (boss.maxHealth >= 4_000_000L) {

            legendaryLoot(
                    server,
                    player,
                    scale
            );

            return;
        }





        if (boss.maxHealth >= 1_500_000L) {

            dualLoot(
                    server,
                    player,
                    scale
            );

            return;
        }





        singleLoot(
                server,
                player,
                scale
        );
    }





    private static void singleLoot(

            MinecraftServer server,

            ServerPlayer player,

            double scale
    ) {

        giveItem(
                server,
                player,
                "cobblemon:exp_candy_xs",
                randomAmount(32, scale)
        );

        giveItem(
                server,
                player,
                "cobblemon:exp_candy_s",
                randomAmount(24, scale)
        );

        giveItem(
                server,
                player,
                "cobblemon:exp_candy_m",
                randomAmount(8, scale)
        );

        giveItem(
                server,
                player,
                "cobblemon:exp_candy_l",
                randomAmount(3, scale)
        );

        chanceItem(
                server,
                player,
                "cobblemon:exp_candy_xl",
                1,
                scale
        );

        giveItem(
                server,
                player,
                "minecraft:rare_candy",
                randomAmount(4, scale)
        );

        giveItem(
                server,
                player,
                "cobblemon:poke_ball",
                randomAmount(32, scale)
        );

        giveItem(
                server,
                player,
                "cobblemon:great_ball",
                randomAmount(16, scale)
        );

        giveItem(
                server,
                player,
                "cobblemon:ultra_ball",
                randomAmount(4, scale)
        );

        chanceItem(
                server,
                player,
                "cobblemon:beast_ball",
                1,
                scale * 0.25
        );





        chanceCommand(
                server,
                "crates give vote "
                        + player.getGameProfile().getName()
                        + " 1",

                scale * 0.5
        );

        chanceCommand(
                server,
                "crates give elite "
                        + player.getGameProfile().getName()
                        + " 1",

                scale * 0.2
        );
    }





    private static void dualLoot(

            MinecraftServer server,

            ServerPlayer player,

            double scale
    ) {

        giveItem(
                server,
                player,
                "cobblemon:exp_candy_xs",
                randomAmount(48, scale)
        );

        giveItem(
                server,
                player,
                "cobblemon:exp_candy_s",
                randomAmount(36, scale)
        );

        giveItem(
                server,
                player,
                "cobblemon:exp_candy_m",
                randomAmount(12, scale)
        );

        giveItem(
                server,
                player,
                "cobblemon:exp_candy_l",
                randomAmount(6, scale)
        );

        giveItem(
                server,
                player,
                "cobblemon:exp_candy_xl",
                randomAmount(2, scale)
        );

        giveItem(
                server,
                player,
                "minecraft:rare_candy",
                randomAmount(8, scale)
        );

        giveItem(
                server,
                player,
                "cobblemon:poke_ball",
                randomAmount(48, scale)
        );

        giveItem(
                server,
                player,
                "cobblemon:great_ball",
                randomAmount(24, scale)
        );

        giveItem(
                server,
                player,
                "cobblemon:ultra_ball",
                randomAmount(6, scale)
        );

        chanceItem(
                server,
                player,
                "cobblemon:beast_ball",
                2,
                scale * 0.4
        );

        chanceCommand(
                server,
                "crates give elite "
                        + player.getGameProfile().getName()
                        + " 1",

                scale * 0.5
        );

        chanceCommand(
                server,
                "crates give mystic "
                        + player.getGameProfile().getName()
                        + " 1",

                scale * 0.2
        );
    }





    private static void legendaryLoot(

            MinecraftServer server,

            ServerPlayer player,

            double scale
    ) {

        giveItem(
                server,
                player,
                "cobblemon:exp_candy_xs",
                randomAmount(64, scale)
        );

        giveItem(
                server,
                player,
                "cobblemon:exp_candy_s",
                randomAmount(48, scale)
        );

        giveItem(
                server,
                player,
                "cobblemon:exp_candy_m",
                randomAmount(16, scale)
        );

        giveItem(
                server,
                player,
                "cobblemon:exp_candy_l",
                randomAmount(12, scale)
        );

        giveItem(
                server,
                player,
                "cobblemon:exp_candy_xl",
                randomAmount(4, scale)
        );

        giveItem(
                server,
                player,
                "minecraft:rare_candy",
                randomAmount(16, scale)
        );

        giveItem(
                server,
                player,
                "cobblemon:poke_ball",
                randomAmount(64, scale)
        );

        giveItem(
                server,
                player,
                "cobblemon:great_ball",
                randomAmount(48, scale)
        );

        giveItem(
                server,
                player,
                "cobblemon:ultra_ball",
                randomAmount(16, scale)
        );

        giveItem(
                server,
                player,
                "cobblemon:beast_ball",
                randomAmount(4, scale)
        );

        chanceItem(
                server,
                player,
                "cobblemon:master_ball",
                1,
                scale * 0.1
        );

        chanceCommand(
                server,
                "crates give mystic "
                        + player.getGameProfile().getName()
                        + " 1",

                scale * 0.6
        );

        chanceCommand(
                server,
                "crates give master "
                        + player.getGameProfile().getName()
                        + " 1",

                scale * 0.2
        );
    }





    private static int randomAmount(
            int max,
            double scale
    ) {

        return Math.max(

                1,

                (int) (
                        RANDOM.nextInt(max + 1)
                                * scale
                )
        );
    }





    private static void giveItem(

            MinecraftServer server,

            ServerPlayer player,

            String item,

            int amount
    ) {

        if (amount <= 0) {
            return;
        }

        runCommand(

                server,

                "give "
                        + player.getGameProfile().getName()
                        + " "
                        + item
                        + " "
                        + amount
        );
    }





    private static void chanceItem(

            MinecraftServer server,

            ServerPlayer player,

            String item,

            int amount,

            double chance
    ) {

        if (RANDOM.nextDouble() > chance) {
            return;
        }

        giveItem(
                server,
                player,
                item,
                amount
        );
    }





    private static void chanceCommand(

            MinecraftServer server,

            String command,

            double chance
    ) {

        if (RANDOM.nextDouble() > chance) {
            return;
        }

        runCommand(
                server,
                command
        );
    }





    private static void runCommand(

            MinecraftServer server,

            String command
    ) {

        try {

            CommandSourceStack source =
                    server.createCommandSourceStack();

            server.getCommands()
                    .performPrefixedCommand(
                            source,
                            command
                    );

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}