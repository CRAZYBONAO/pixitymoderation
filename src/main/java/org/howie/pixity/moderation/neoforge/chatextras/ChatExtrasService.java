package org.howie.pixity.moderation.neoforge.chatextras;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ChatExtrasService {


    private final ChatExtrasConfig cfg;
    private final RankService ranks;

    public ChatExtrasService(final ChatExtrasConfig cfg, final RankService ranks) {
        this.cfg = cfg;
        this.ranks = ranks;
    }

    public String apply(final MinecraftServer server, final ServerPlayer sender, final String raw) {
        if (cfg == null || !cfg.enabled) return raw;
        if (raw == null) return null;

        String msg = raw;

        if (cfg.emojiEnabled && cfg.emojis != null && !cfg.emojis.isEmpty()) {
            msg = applyEmojis(msg);
        }

        if (cfg.mentionsEnabled && server != null && sender != null) {
            msg = applyMentions(server, sender, msg);
        }

        return msg;
    }

    private String applyEmojis(final String msg) {
        int remaining = cfg.emojiMaxPerMessage;
        if (remaining == 0) return msg;

        String out = msg;

        List<String> keys = new ArrayList<>(cfg.emojis.keySet());
        keys.sort((a, b) -> Integer.compare(b.length(), a.length()));

        for (String k : keys) {
            if (remaining == 0) break;
            if (k == null || k.isEmpty()) continue;

            String v = cfg.emojis.get(k);
            if (v == null) continue;

            int idx = 0;
            while (remaining > 0) {
                idx = out.indexOf(k, idx);
                if (idx < 0) break;

                out = out.substring(0, idx) + v + out.substring(idx + k.length());
                idx += v.length();
                remaining--;
            }
        }

        return out;
    }

    private String applyMentions(final MinecraftServer server, final ServerPlayer sender, final String msg) {

        Map<String, ServerPlayer> online = new HashMap<>();
        for (ServerPlayer p : server.getPlayerList().getPlayers()) {
            online.put(p.getGameProfile().getName().toLowerCase(Locale.ROOT), p);
        }

        Pattern pat = Pattern.compile("(?i)(?<!\\w)@([A-Za-z0-9_]{3,16})(?!\\w)");
        Matcher m = pat.matcher(msg);

        StringBuffer sb = new StringBuffer();
        Set<UUID> pinged = new HashSet<>();

        while (m.find()) {
            String name = m.group(1);
            ServerPlayer target = online.get(name.toLowerCase(Locale.ROOT));
            if (target == null) continue;

            String repl = cfg.mentionColor + "@" + target.getGameProfile().getName() + "&r";
            m.appendReplacement(sb, Matcher.quoteReplacement(repl));

            if (!pinged.contains(target.getUUID()) && !hasMentionBypass(target)) {
                pinged.add(target.getUUID());
                ping(target, sender);
            }
        }

        m.appendTail(sb);
        return sb.toString();
    }

    private boolean hasMentionBypass(final ServerPlayer p) {
        return p.hasPermissions(2) ||
                (ranks != null && ranks.hasPerm(p, cfg.mentionBypassPermission));
    }

    private void ping(final ServerPlayer target, final ServerPlayer sender) {

        if (cfg.mentionActionbar) {
            String txt = cfg.mentionActionbarText.replace("{SENDER}", sender.getGameProfile().getName());
            Component c = LegacyAmpersand.parse(txt);
            target.sendSystemMessage(c, true);
        }

        if (cfg.mentionSound) {
            target.playNotifySound(SoundEvents.NOTE_BLOCK_PLING.value(), SoundSource.MASTER, 1f, 1f);
        }
    }


}
