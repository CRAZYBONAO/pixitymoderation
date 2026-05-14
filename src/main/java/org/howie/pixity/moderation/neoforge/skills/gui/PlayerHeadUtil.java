package org.howie.pixity.moderation.neoforge.skills.gui;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;

import java.util.UUID;

public class PlayerHeadUtil {

    public static ItemStack create(ServerPlayer viewer, UUID uuid, String fallbackName) {

        ItemStack head = new ItemStack(Items.PLAYER_HEAD);

        GameProfile profile = resolveProfile(viewer, uuid, fallbackName);


        head.set(DataComponents.PROFILE, new ResolvableProfile(profile));


        head.set(DataComponents.CUSTOM_NAME,
                Component.literal(profile.getName() != null ? profile.getName() : fallbackName)
                        .withStyle(style -> style.withItalic(false))
        );

        return head;
    }




    private static GameProfile resolveProfile(ServerPlayer viewer, UUID uuid, String fallbackName) {

        MinecraftServer server = viewer.getServer();
        if (server == null) return new GameProfile(uuid, fallbackName);


        var online = server.getPlayerList().getPlayer(uuid);
        if (online != null) {
            return online.getGameProfile();
        }


        var cached = server.getProfileCache().get(uuid).orElse(null);
        if (cached != null) {
            return cached;
        }


        return new GameProfile(uuid, fallbackName);
    }
}