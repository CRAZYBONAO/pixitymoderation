package org.howie.pixity.moderation.neoforge.milestones.listeners;

import net.minecraft.server.level.ServerPlayer;

import net.minecraft.world.entity.Entity;

import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.Sheep;

import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;

import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.Zombie;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

import org.howie.pixity.moderation.neoforge.stats.PlayerStatsDatabase;

@EventBusSubscriber
public class MobMilestoneListener {





    @SubscribeEvent
    public static void death(
            LivingDeathEvent event
    ) {





        Entity source =
                event.getSource().getEntity();

        if (!(source instanceof ServerPlayer sp)) {
            return;
        }





        Entity entity =
                event.getEntity();





        PlayerStatsDatabase.add(
                sp.getUUID(),
                "total_mob_kills",
                1
        );





        if (entity instanceof Chicken) {

            add(
                    sp,
                    "chicken_kills"
            );

            return;
        }

        if (entity instanceof Cow) {

            add(
                    sp,
                    "cow_kills"
            );

            return;
        }

        if (entity instanceof Pig) {

            add(
                    sp,
                    "pig_kills"
            );

            return;
        }

        if (entity instanceof Sheep) {

            add(
                    sp,
                    "sheep_kills"
            );

            return;
        }





        if (entity instanceof Zombie) {

            add(
                    sp,
                    "zombie_kills"
            );

            return;
        }




        if (entity instanceof WitherSkeleton) {

            add(
                    sp,
                    "wither_skeleton_kills"
            );

            return;
        }

        if (entity instanceof Skeleton) {

            add(
                    sp,
                    "skeleton_kills"
            );

            return;
        }

        if (entity instanceof Spider) {

            add(
                    sp,
                    "spider_kills"
            );

            return;
        }

        if (entity instanceof Creeper) {

            add(
                    sp,
                    "creeper_kills"
            );

            return;
        }

        if (entity instanceof Blaze) {

            add(
                    sp,
                    "blaze_kills"
            );

            return;
        }

        if (entity instanceof MagmaCube) {

            add(
                    sp,
                    "magma_cube_kills"
            );

            return;
        }

        if (entity instanceof EnderMan) {

            add(
                    sp,
                    "enderman_kills"
            );

            return;
        }

        if (entity instanceof Phantom) {

            add(
                    sp,
                    "phantom_kills"
            );

            return;
        }





        if (
                entity instanceof WitherBoss ||
                        entity instanceof EnderDragon
        ) {

            add(
                    sp,
                    "boss_kills"
            );
        }
    }





    private static void add(
            ServerPlayer player,
            String column
    ) {

        PlayerStatsDatabase.add(
                player.getUUID(),
                column,
                1
        );
    }
}