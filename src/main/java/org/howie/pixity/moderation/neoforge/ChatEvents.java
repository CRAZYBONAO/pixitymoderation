package org.howie.pixity.moderation.neoforge;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.ServerChatEvent;

import net.minecraft.network.chat.*;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.chat.*;
import org.howie.pixity.moderation.neoforge.afk.AfkService;
import org.howie.pixity.moderation.neoforge.automod.AutoModService;
import org.howie.pixity.moderation.neoforge.chatextras.ChatExtrasService;
import org.howie.pixity.moderation.neoforge.chatcontrol.ChatControlService;
import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.state.PlayerStateService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;
import org.howie.pixity.moderation.neoforge.tp.TpService;
import org.howie.pixity.moderation.neoforge.tp.gui.TpChatPromptService;


import org.howie.pixity.moderation.neoforge.economy.EconomyService;
import org.howie.pixity.moderation.neoforge.fly.FlyTimeService;
import org.howie.pixity.moderation.neoforge.util.PlayerHoverUtil;

public final class ChatEvents {

    private final ChatConfigManager cfgMgr;
    private final NickManager nick;
    private final MuteManager mutes;
    private final RankService ranks;
    private final TpChatPromptService tpPrompts;
    private final AfkService afk;
    private final AutoModService automod;
    private final ChatExtrasService extras;
    private final ChatControlService chatCtl;
    private final TpService perms;
    private final PlayerStateService state;


    private final EconomyService economy;
    private final FlyTimeService fly;

    public ChatEvents(ChatConfigManager cfgMgr,
                      NickManager nick,
                      MuteManager mutes,
                      RankService ranks,
                      TpChatPromptService tpPrompts,
                      AfkService afk,
                      AutoModService automod,
                      ChatExtrasService extras,
                      ChatControlService chatCtl,
                      TpService perms,
                      PlayerStateService state,
                      EconomyService economy,
                      FlyTimeService fly) {

        this.cfgMgr = cfgMgr;
        this.nick = nick;
        this.mutes = mutes;
        this.ranks = ranks;
        this.tpPrompts = tpPrompts;
        this.afk = afk;
        this.automod = automod;
        this.extras = extras;
        this.chatCtl = chatCtl;
        this.perms = perms;
        this.state = state;


        this.economy = economy;
        this.fly = fly;
    }

    @SubscribeEvent
    public void onChat(ServerChatEvent event) {

        ServerPlayer p = event.getPlayer();
        if (p == null) return;

        if (afk != null && afk.isAfk(p.getUUID())) {
            afk.setAfk(p.server, p, false, true);
        }

        if (!state.canChat(p.getUUID())) {
            p.sendSystemMessage(LegacyAmpersand.parse("&c&lSERVER &7&l➤ You cannot chat right now."));
            event.setCanceled(true);
            return;
        }

        if (tpPrompts != null && tpPrompts.tryConsume(p.server, p, event.getMessage().getString())) {
            event.setCanceled(true);
            return;
        }

        if (mutes != null && mutes.isMuted(p.getUUID())) {
            p.sendSystemMessage(LegacyAmpersand.parse("&c&lMUTES &7&l➤ &cYou are muted."));
            event.setCanceled(true);
            return;
        }

        if (chatCtl != null && chatCtl.isChatMuted()) {
            boolean bypass = (perms != null && perms.hasPerm(p, ChatControlService.PERM_BYPASS_MUTE)) || p.hasPermissions(2);

            if (!bypass) {
                p.sendSystemMessage(LegacyAmpersand.parse("&c&lCHAT CONTROL &7&l➤ &cChat is muted."));
                event.setCanceled(true);
                return;
            }
        }

        if (automod != null) {
            Component block = automod.check(p, event.getMessage().getString());
            if (block != null) {
                p.sendSystemMessage(block);
                event.setCanceled(true);
                return;
            }
        }

        String raw = event.getMessage().getString();

        if (extras != null) {
            raw = extras.apply(p.server, p, raw);
        }

        ChatConfig cfg = cfgMgr.config();





        Component name = DisplayFormatter.formatPlayer(p);

        Component hover = PlayerHoverUtil.buildHover(p, nick, economy, fly);

        name = name.copy().withStyle(style ->
                style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover))
        );





        Component prefix = LegacyAmpersand.parse(ranks.prefix(p));
        Component suffix = LegacyAmpersand.parse(ranks.suffix(p));







        boolean canColor =
                ranks != null &&
                        (ranks.hasPerm(p, "pixity.chat.color")
                                || ranks.hasPerm(p, "*"));

        Component msg = canColor
                ? LegacyAmpersand.parse(raw)
                : Component.literal(raw);

        TextColor msgColor = parseHex(cfg.defaultMessageHex);
        if (msgColor != null) msg = msg.copy().withStyle(s -> s.withColor(msgColor));





        String fmt = (cfg.rankFormat == null || cfg.rankFormat.isBlank())
                ? "{LP_PREFIX} {DISPLAYNAME} {LP_SUFFIX} 》 {MESSAGE}"
                : cfg.rankFormat;

        Component formatted = build(fmt, prefix, name, suffix, msg);

        event.setMessage(formatted);

        if (afk != null) afk.touch(p);
    }

    private static Component build(String fmt, Component prefix, Component name, Component suffix, Component msg) {
        MutableComponent out = Component.empty();

        int i = 0;
        while (i < fmt.length()) {

            if (fmt.startsWith("{LP_PREFIX}", i)) {
                out.append(prefix);
                i += 11;
                continue;
            }

            if (fmt.startsWith("{DISPLAYNAME}", i)) {
                out.append(name);
                i += 13;
                continue;
            }

            if (fmt.startsWith("{LP_SUFFIX}", i)) {
                out.append(suffix);
                i += 11;
                continue;
            }

            if (fmt.startsWith("{MESSAGE}", i)) {
                out.append(msg);
                i += 9;
                continue;
            }

            out.append(LegacyAmpersand.parse(String.valueOf(fmt.charAt(i))));
            i++;
        }

        return out;
    }

    private static TextColor parseHex(String hex) {
        try {
            if (hex == null || !hex.startsWith("#")) return null;
            return TextColor.fromRgb(Integer.parseInt(hex.substring(1), 16));
        } catch (Exception ignored) {
            return null;
        }
    }
}