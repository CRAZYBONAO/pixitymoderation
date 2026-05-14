package org.howie.pixity.moderation.neoforge.skills;

import net.minecraft.server.level.ServerPlayer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.PermissionNode;

import java.util.UUID;

public class PermissionHelper {

    public static void givePermission(ServerPlayer player, String permission) {

        LuckPerms api = LuckPermsProvider.get();

        UUID uuid = player.getUUID();

        User user = api.getUserManager().getUser(uuid);
        if (user == null) return;

        if (user.getCachedData().getPermissionData().checkPermission(permission).asBoolean()) {
            return;
        }

        user.data().add(PermissionNode.builder(permission).build());
        api.getUserManager().saveUser(user);
    }
}