package org.howie.pixity.moderation.neoforge.automod;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import org.howie.pixity.moderation.chat.MuteManager;
import org.howie.pixity.moderation.neoforge.punish.PunishmentManager;
import org.howie.pixity.moderation.neoforge.punish.PunishAction;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;
import org.howie.pixity.moderation.neoforge.tp.TpService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class AutoModService {


    public static final String PERM_NOTIFY = "pixity.automod.notify";

    private final AutoModConfig cfg;
    private final TpService perms;
    private final MuteManager mutes;
    private final PunishmentManager punish;

    private static final class MsgState {
        String lastMsg = "";
        long lastMsgAt = 0L;
        Deque<Long> recent = new ArrayDeque<>();
        Deque<Long> strikes = new ArrayDeque<>();
    }

    private final Map<UUID, MsgState> state = new ConcurrentHashMap<>();

    public AutoModService(AutoModConfig cfg, TpService perms, MuteManager mutes, PunishmentManager punish) {
        this.cfg = cfg;
        this.perms = perms;
        this.mutes = mutes;
        this.punish = punish;
    }




    public Component check(ServerPlayer p, String raw) {

        if (cfg == null || !cfg.enabled) return null;
        if (p == null || raw == null) return null;
        if (bypass(p)) return null;

        String msg = raw.trim();
        if (msg.isEmpty()) return null;

        long now = System.currentTimeMillis();
        MsgState st = state.computeIfAbsent(p.getUUID(), u -> new MsgState());


        if (cfg.bannedWords != null) {
            String lower = msg.toLowerCase(Locale.ROOT);
            for (String w : cfg.bannedWords) {
                if (w != null && !w.isBlank() && lower.contains(w.toLowerCase())) {
                    return punish(p, st, now, "Banned Word", cfg.bannedWordMessage);
                }
            }
        }


        if (cfg.blockLinks && looksLikeLink(msg)) {
            return punish(p, st, now, "Link", cfg.linkBlockedMessage);
        }


        if (cfg.capsFilter && msg.length() >= cfg.capsMinLength) {

            int letters = 0, caps = 0;

            for (char c : msg.toCharArray()) {
                if (Character.isLetter(c)) {
                    letters++;
                    if (Character.isUpperCase(c)) caps++;
                }
            }

            if (letters >= cfg.capsMinLength) {
                double ratio = (double) caps / letters;
                if (ratio >= cfg.capsRatio) {
                    return punish(p, st, now, "Caps", cfg.capsBlockedMessage);
                }
            }
        }


        if (cfg.repeatFilter) {
            long win = cfg.repeatWindowSeconds * 1000L;

            if (st.lastMsgAt > 0 && (now - st.lastMsgAt) <= win) {
                if (normalize(msg).equals(st.lastMsg)) {
                    return punish(p, st, now, "Repeat", cfg.repeatBlockedMessage);
                }
            }
        }


        if (cfg.rateFilter) {

            long win = cfg.rateWindowSeconds * 1000L;
            prune(st.recent, now - win);
            st.recent.addLast(now);

            if (st.recent.size() > cfg.rateMaxMessages) {
                return punish(p, st, now, "Spam", cfg.rateBlockedMessage);
            }
        }

        st.lastMsg = normalize(msg);
        st.lastMsgAt = now;

        return null;
    }




    private Component punish(ServerPlayer p, MsgState st, long now, String type, String msg) {

        log(p, type);
        strike(p, st, now, type);


        Component c = LegacyAmpersand.parse(msg);

        p.sendSystemMessage(c);
        p.sendSystemMessage(c, true);

        try {
            p.playNotifySound(
                    SoundEvents.NOTE_BLOCK_BASS.value(),
                    SoundSource.PLAYERS,
                    0.6f,
                    0.8f
            );
        } catch (Exception ignored) {}

        return c;
    }




    private void strike(ServerPlayer p, MsgState st, long now, String reason) {

        if (!cfg.escalationEnabled) return;

        long win = cfg.strikesWindowSeconds * 1000L;
        prune(st.strikes, now - win);
        st.strikes.addLast(now);

        if (cfg.muteSeconds > 0 && st.strikes.size() >= cfg.strikesToMute) {

            if (mutes != null) {
                mutes.tempMute(p.getUUID(), "AutoMod", cfg.muteSeconds * 1000L, cfg.muteReason);
            }

            if (punish != null) {
                try {
                    punish.logCustom(
                            PunishAction.MUTE,
                            null,
                            p.getUUID(),
                            p.getGameProfile().getName(),
                            (long) cfg.muteSeconds,
                            cfg.muteReason
                    );
                } catch (Exception ignored) {}
            }

            alertStaff(p);

            st.strikes.clear();
        }
    }

    private void alertStaff(ServerPlayer target) {

        MinecraftServer server = target.server;
        if (server == null) return;

        String name = target.getGameProfile().getName();

        Component msg = LegacyAmpersand.parse(
                "&c[AutoMod] &f" + name + " &7was muted automatically."
        );

        for (ServerPlayer sp : server.getPlayerList().getPlayers()) {

            if (sp == null || sp == target) continue;

            boolean canSee = sp.hasPermissions(2) ||
                    (perms != null && perms.hasPerm(sp, PERM_NOTIFY));

            if (!canSee) continue;

            sp.sendSystemMessage(msg);
        }
    }

    private void log(ServerPlayer p, String reason) {
        if (punish == null) return;

        try {
            punish.logCustom(
                    PunishAction.AUTOMOD,
                    null,
                    p.getUUID(),
                    p.getGameProfile().getName(),
                    null,
                    reason
            );
        } catch (Exception ignored) {}
    }

    private boolean bypass(ServerPlayer p) {
        return p.hasPermissions(2) ||
                (perms != null && perms.hasPerm(p, cfg.bypassPermission));
    }

    private static void prune(Deque<Long> dq, long min) {
        while (!dq.isEmpty() && dq.peekFirst() < min) dq.removeFirst();
    }

    private static String normalize(String s) {
        return s.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }


    private static boolean looksLikeLink(String s) {
        String lower = s.toLowerCase(Locale.ROOT);

        return lower.contains("http:") ||
                lower.contains("https:") ||
                lower.contains("www.") ||
                lower.matches(".*\\b[a-z0-9\\-]+\\.(com|net|org|gg|io|co|xyz|shop)\\b.*");
    }


}
