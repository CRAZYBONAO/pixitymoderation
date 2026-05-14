package org.howie.pixity.moderation.neoforge.afk;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;

public final class AfkService {

    public static final String PERM_AFK = "pixity.afk";
    public static final String PERM_AFK_BYPASS = "pixity.afk.bypass";

    private final AfkConfig config;
    private final RankService ranks;

    private final SQLiteAfkStore store;
    private final Map<UUID, SQLiteAfkStore.AfkData> data = new ConcurrentHashMap<>();

    public AfkService(final AfkConfig config, final RankService ranks, SQLiteAfkStore store) {
        this.config = config;
        this.ranks = ranks;
        this.store = store;

        this.data.putAll(store.load());
    }

    private SQLiteAfkStore.AfkData get(UUID u) {
        return data.computeIfAbsent(u, k -> new SQLiteAfkStore.AfkData());
    }

    public AfkConfig config() {
        return config;
    }



    public void touch(final ServerPlayer p) {
        if (p == null) return;

        var d = get(p.getUUID());
        d.lastActive = System.currentTimeMillis();

        if (d.isAfk) {
            setAfk(p.getServer(), p, false, false);
        }

        store.save(new HashMap<>(data));
    }



    public boolean isAfk(final UUID u) {
        return get(u).isAfk;
    }

    public long lastActive(final UUID u) {
        return get(u).lastActive;
    }

    public long afkSince(final UUID u) {
        return get(u).afkSince;
    }



    public void setAfk(final MinecraftServer server,
                       final ServerPlayer p,
                       final boolean value,
                       final boolean silent) {

        if (p == null) return;

        UUID u = p.getUUID();
        var d = get(u);

        boolean was = d.isAfk;
        if (was == value) return;

        d.isAfk = value;

        if (value) d.afkSince = System.currentTimeMillis();
        else d.afkSince = 0;

        store.save(new HashMap<>(data));


        if (!silent) {
            p.sendSystemMessage(LegacyAmpersand.parse(
                    value ? "&e&lAFK &7&l➤ &eYou are now &cAFK." : "&e&lAFK &7&l➤ &eYou are no longer &cAFK."
            ));
        }



        if (server == null || config == null || !config.broadcastMessages) return;

        String tmpl = value ? config.afkMessage : config.returnMessage;
        if (tmpl == null || tmpl.isBlank()) return;

        MutableComponent display = buildDisplayName(p);

        MutableComponent msg;

        if (tmpl.contains("{DISPLAYNAME}")) {

            String[] parts = tmpl.split("\\{DISPLAYNAME}", -1);

            MutableComponent left = parts.length > 0 && !parts[0].isEmpty()
                    ? LegacyAmpersand.parse(parts[0]).copy()
                    : Component.empty().copy();

            MutableComponent right = parts.length > 1 && !parts[1].isEmpty()
                    ? LegacyAmpersand.parse(parts[1]).copy()
                    : Component.empty().copy();

            msg = left.append(display).append(right);

        } else {
            msg = display.append(Component.literal(" "))
                    .append(LegacyAmpersand.parse(tmpl));
        }

        for (ServerPlayer sp : server.getPlayerList().getPlayers()) {
            sp.sendSystemMessage(msg);
        }
    }



    public void toggle(final ServerPlayer p) {
        if (p == null) return;
        setAfk(p.server, p, !isAfk(p.getUUID()), false);
    }


    private MutableComponent buildDisplayName(final ServerPlayer p) {

        String name =
                org.howie.pixity.moderation.chat.NickHolder.INSTANCE
                        .getDisplayName(p);

        MutableComponent out =
                LegacyAmpersand.parse(name).copy();

        if (ranks != null) {
            String pre = ranks.prefix(p);
            String suf = ranks.suffix(p);

            if (pre != null && !pre.isEmpty()) {
                out = LegacyAmpersand.parse(pre).copy()
                        .append(Component.literal(" "))
                        .append(out);
            }

            if (suf != null && !suf.isEmpty()) {
                out = out
                        .append(Component.literal(" "))
                        .append(LegacyAmpersand.parse(suf).copy());
            }
        }

        return out;
    }


    public static String formatAfkTime(final long ms) {
        if (ms <= 0) return "";

        long sec = ms / 1000L;

        long h = sec / 3600;
        long m = (sec % 3600) / 60;
        long s = sec % 60;

        if (h > 0) return h + "h " + m + "m";
        if (m > 0) return m + "m " + s + "s";
        return s + "s";
    }
}