package org.howie.pixity.moderation.neoforge.hologram.packet;

import net.minecraft.network.chat.Component;

import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import net.minecraft.world.entity.decoration.ArmorStand;
import org.howie.pixity.moderation.neoforge.hologram.HologramPlaceholderService;

public class PacketHologramRenderer {

    public static void spawn(

            ServerPlayer player,

            FakeArmorStand fake
    ) {

        ServerLevel level =
                player.serverLevel();

        ArmorStand stand =
                new ArmorStand(
                        level,
                        fake.x(),
                        fake.y(),
                        fake.z()
                );

        stand.setId(
                fake.entityId()
        );

        stand.setInvisible(true);

        stand.setNoGravity(true);

        stand.setCustomNameVisible(true);

        stand.setCustomName(

                HologramPlaceholderService.parse(
                        player,
                        fake.text()
                )
        );

        player.connection.send(

                new ClientboundAddEntityPacket(

                        stand.getId(),

                        stand.getUUID(),

                        stand.getX(),
                        stand.getY(),
                        stand.getZ(),

                        stand.getXRot(),
                        stand.getYRot(),

                        stand.getType(),

                        0,

                        stand.getDeltaMovement(),

                        stand.getYHeadRot()
                )
        );

        var data =
                stand.getEntityData()
                        .getNonDefaultValues();

        if (data != null) {

            player.connection.send(

                    new ClientboundSetEntityDataPacket(

                            stand.getId(),

                            data
                    )
            );
        }
    }

    public static void destroy(

            ServerPlayer player,

            FakeArmorStand stand
    ) {

        player.connection.send(

                new ClientboundRemoveEntitiesPacket(

                        stand.entityId()
                )
        );
    }

    public static void updateText(

            ServerPlayer player,

            FakeArmorStand fake
    ) {

        ServerLevel level =
                player.serverLevel();

        ArmorStand stand =
                new ArmorStand(
                        level,
                        fake.x(),
                        fake.y(),
                        fake.z()
                );

        stand.setId(
                fake.entityId()
        );

        stand.setInvisible(true);

        stand.setNoGravity(true);

        stand.setCustomNameVisible(true);

        stand.setCustomName(

                HologramPlaceholderService.parse(
                        player,
                        fake.text()
                )
        );

        var data =
                stand.getEntityData()
                        .getNonDefaultValues();

        if (data != null) {

            player.connection.send(

                    new ClientboundSetEntityDataPacket(

                            stand.getId(),

                            data
                    )
            );
        }
    }
}