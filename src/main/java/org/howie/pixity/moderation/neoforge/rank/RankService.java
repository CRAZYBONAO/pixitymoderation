package org.howie.pixity.moderation.neoforge.rank;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.ModList;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;

public final class RankService {

    private final boolean lpPresent;

    public RankService() {
        this.lpPresent = ModList.get().isLoaded("luckperms");
    }



    public boolean hasPerm(final ServerPlayer player, final String node) {
        if (player == null || node == null || node.isBlank()) return false;

        if (player.hasPermissions(4)) return true;

        if (!lpPresent) return false;

        try {
            LuckPerms lp = LuckPermsProvider.get();

            User user = lp.getUserManager().getUser(player.getUUID());
            if (user == null) {
                user = lp.getUserManager().loadUser(player.getUUID()).join();
            }

            if (user == null) return false;

            QueryOptions options =
                    lp.getContextManager().getQueryOptions(player);

            if (options == null) {
                options = lp.getContextManager().getStaticQueryOptions();
            }

            return user.getCachedData()
                    .getPermissionData(options)
                    .checkPermission(node)
                    .asBoolean();

        } catch (Throwable ignored) {}

        return false;
    }

    public void addPermission(ServerPlayer player, String node) {

        if (!lpPresent) return;

        try {
            LuckPerms lp = LuckPermsProvider.get();

            User user = lp.getUserManager().getUser(player.getUUID());
            if (user == null) {
                user = lp.getUserManager().loadUser(player.getUUID()).join();
            }

            if (user == null) return;

            user.data().add(
                    net.luckperms.api.node.types.PermissionNode.builder(node).build()
            );

            lp.getUserManager().saveUser(user);

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }



    public String prefix(final ServerPlayer player) {
        if (player == null) return "";

        if (!lpPresent) return "";

        try {
            LuckPerms lp = LuckPermsProvider.get();

            User user = lp.getUserManager().getUser(player.getUUID());
            if (user == null) {
                user = lp.getUserManager().loadUser(player.getUUID()).join();
            }

            if (user == null) return "";

            QueryOptions options =
                    lp.getContextManager().getQueryOptions(player);

            if (options == null) {
                options = lp.getContextManager().getStaticQueryOptions();
            }

            String prefix =
                    user.getCachedData()
                            .getMetaData(options)
                            .getPrefix();

            return prefix == null ? "" : prefix;

        } catch (Throwable ignored) {}

        return "";
    }


    public String suffix(final ServerPlayer player) {
        if (player == null) return "";

        if (!lpPresent) return "";

        try {
            LuckPerms lp = LuckPermsProvider.get();

            User user = lp.getUserManager().getUser(player.getUUID());
            if (user == null) {
                user = lp.getUserManager().loadUser(player.getUUID()).join();
            }

            if (user == null) return "";

            QueryOptions options =
                    lp.getContextManager().getQueryOptions(player);

            if (options == null) {
                options = lp.getContextManager().getStaticQueryOptions();
            }

            String suffix =
                    user.getCachedData()
                            .getMetaData(options)
                            .getSuffix();

            return suffix == null ? "" : suffix;

        } catch (Throwable ignored) {}

        return "";
    }

    public int weight(ServerPlayer player) {
        if (player == null) return 0;

        if (!lpPresent) return 0;

        try {
            LuckPerms lp = LuckPermsProvider.get();

            User user = lp.getUserManager().getUser(player.getUUID());
            if (user == null) {
                user = lp.getUserManager().loadUser(player.getUUID()).join();
            }

            if (user == null) return 0;

            String primary = user.getPrimaryGroup();

            var group = lp.getGroupManager().getGroup(primary);

            if (group == null) return 0;

            Integer weight = group.getWeight().orElse(0);

            return weight;

        } catch (Throwable ignored) {}

        return 0;
    }
}