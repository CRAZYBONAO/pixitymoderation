package org.howie.pixity.moderation.chat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.Logger;
import org.howie.pixity.moderation.neoforge.punish.PunishmentManager;
import org.howie.pixity.moderation.neoforge.punish.PunishEntry;
import org.howie.pixity.moderation.neoforge.punish.PunishAction;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public final class MuteManager {

    private final Logger logger;
    private final Path file;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private final Map<UUID, MuteRecord> mutes = new ConcurrentHashMap<>();

    public static final class MuteRecord {
        public String by;
        public String reason;
        public long atMillis;
        public long untilMillis;

        public MuteRecord() {}

        public MuteRecord(final String by, final String reason, final long atMillis, final long untilMillis) {
            this.by = by;
            this.reason = reason;
            this.atMillis = atMillis;
            this.untilMillis = untilMillis;
        }

        public boolean expired(final long now) {
            return untilMillis > 0 && now >= untilMillis;
        }
    }

    public MuteManager(final Logger logger, final Path file) {
        this.logger = logger;
        this.file = file;
        load();
        cleanupExpired();
    }

    public Optional<MuteRecord> record(final UUID uuid) {
        if (uuid == null) return Optional.empty();
        cleanupExpired();
        return Optional.ofNullable(mutes.get(uuid));
    }

    public boolean isMuted(final UUID uuid) {
        if (uuid == null) return false;
        cleanupExpired();
        return mutes.containsKey(uuid);
    }

    public long remainingMillis(final UUID uuid) {
        if (uuid == null) return 0L;
        cleanupExpired();

        MuteRecord r = mutes.get(uuid);
        if (r == null) return 0L;

        if (r.untilMillis <= 0) return Long.MAX_VALUE;
        long now = System.currentTimeMillis();
        return Math.max(0L, r.untilMillis - now);
    }

    public void mute(final UUID uuid, final String by, final String reason) {
        if (uuid == null) return;

        final long now = System.currentTimeMillis();
        final String who = (by == null || by.isBlank()) ? "Unknown" : by;
        final String why = (reason == null) ? "" : reason;

        mutes.put(uuid, new MuteRecord(who, why, now, 0L));
        save();
    }

    public void tempMute(final UUID uuid, final String by, final long durationMillis, final String reason) {
        if (uuid == null) return;

        final long now = System.currentTimeMillis();
        final long dur = Math.max(1_000L, durationMillis);
        final long until = now + dur;

        mutes.put(uuid, new MuteRecord(by, reason, now, until));
        save();
    }

    public boolean unmute(final UUID uuid, final String by, final String reason) {
        return unmute(uuid);
    }

    public boolean unmute(final UUID uuid) {
        if (uuid == null) return false;
        boolean had = mutes.remove(uuid) != null;
        if (had) save();
        return had;
    }

    public boolean expireNow(final UUID uuid) {
        return unmute(uuid);
    }

    public int activeCount() {
        cleanupExpired();
        return mutes.size();
    }

    public void cleanupExpired() {
        cleanupExpired(null);
    }


    public void cleanupExpired(final PunishmentManager punishments) {
        final long now = System.currentTimeMillis();
        boolean changed = false;

        for (UUID id : new ArrayList<>(mutes.keySet())) {
            MuteRecord r = mutes.get(id);
            if (r == null) {
                mutes.remove(id);
                changed = true;
                continue;
            }
            if (r.expired(now)) {
                mutes.remove(id);
                changed = true;

                try {
                    punishments.logCustom(
                            PunishAction.MUTE,
                            null,
                            id,
                            "Unknown",
                            null,
                            "Temporary mute expired"
                    );
                } catch (Throwable ignored) {}
            }
        }

        if (changed) save();
    }


    private void load() {
        if (!Files.exists(file)) return;
        try {
            Type type = new TypeToken<Map<UUID, MuteRecord>>() {}.getType();
            Map<UUID, MuteRecord> data = gson.fromJson(Files.readString(file), type);
            mutes.clear();
            if (data != null) mutes.putAll(data);
        } catch (Throwable t) {
            try { logger.error("[PixityModeration] Failed to load mutes: " + file, t); } catch (Throwable ignored) {}
        }
    }

    private void save() {
        try {
            Files.createDirectories(file.getParent());
            Files.writeString(file, gson.toJson(mutes));
        } catch (IOException e) {
            try { logger.error("[PixityModeration] Failed to save mutes: " + file, e); } catch (Throwable ignored) {}
        }
    }
}
