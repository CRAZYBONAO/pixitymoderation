package org.howie.pixity.moderation.chat;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;

import java.util.UUID;

public final class LuckPermsHook {

    private static LuckPerms api;

    private static LuckPerms api() {
        if (api == null) {
            try {
                api = LuckPermsProvider.get();
            } catch (IllegalStateException ignored) {}
        }
        return api;
    }

    public static String getPrimaryGroup(UUID uuid) {
        LuckPerms lp = api();
        if (lp == null || uuid == null) return "default";

        User user = lp.getUserManager().getUser(uuid);
        if (user == null) return "default";

        return user.getPrimaryGroup().toLowerCase();
    }

    public static String getPrefix(UUID uuid) {
        LuckPerms lp = api();
        if (lp == null || uuid == null) return "";

        User user = lp.getUserManager().getUser(uuid);
        if (user == null) return "";

        QueryOptions query = lp.getContextManager().getStaticQueryOptions();
        CachedMetaData meta = user.getCachedData().getMetaData(query);

        String prefix = meta.getPrefix();
        return prefix == null ? "" : prefix;
    }

    public static String getSuffix(UUID uuid) {
        LuckPerms lp = api();
        if (lp == null || uuid == null) return "";

        User user = lp.getUserManager().getUser(uuid);
        if (user == null) return "";

        QueryOptions query = lp.getContextManager().getStaticQueryOptions();
        CachedMetaData meta = user.getCachedData().getMetaData(query);

        String suffix = meta.getSuffix();
        return suffix == null ? "" : suffix;
    }
}