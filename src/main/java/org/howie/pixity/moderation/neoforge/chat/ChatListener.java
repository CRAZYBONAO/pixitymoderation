package org.howie.pixity.moderation.neoforge.chat;

import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.level.ServerPlayer;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.ServerChatEvent;

import org.howie.pixity.moderation.chat.*;
import org.howie.pixity.moderation.neoforge.afk.AfkService;
import org.howie.pixity.moderation.neoforge.auctionhouse.gui.AuctionInputHandler;
import org.howie.pixity.moderation.neoforge.automod.AutoModService;
import org.howie.pixity.moderation.neoforge.chat.cosmetics.AnimatedChatManager;
import org.howie.pixity.moderation.neoforge.chat.cosmetics.ChatCosmeticsService;
import org.howie.pixity.moderation.neoforge.chat.cosmetics.CosmeticService;
import org.howie.pixity.moderation.neoforge.chatcontrol.ChatControlService;
import org.howie.pixity.moderation.neoforge.chatextras.ChatExtrasService;
import org.howie.pixity.moderation.neoforge.economy.CurrencyType;
import org.howie.pixity.moderation.neoforge.economy.EconomyService;
import org.howie.pixity.moderation.neoforge.fly.FlyTimeService;
import org.howie.pixity.moderation.neoforge.freeze.FreezeService;
import org.howie.pixity.moderation.neoforge.giveaway.GiveawayChatPromptService;
import org.howie.pixity.moderation.neoforge.jail.JailService;
import org.howie.pixity.moderation.neoforge.msg.IgnoreManager;
import org.howie.pixity.moderation.neoforge.playtime.PlaytimeService;
import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;
import org.howie.pixity.moderation.neoforge.util.TimeUtil;

public final class ChatListener {

    private final ChatConfigManager cfgMgr;
    private final MuteManager mutes;
    private final AfkService afk;
    private final AutoModService automod;
    private final ChatControlService chatCtl;
    private final ChatExtrasService extras;
    private final FreezeService freeze;
    private final JailService jail;
    private final EconomyService economy;
    private final FlyTimeService fly;
    private final PlaytimeService playtime;
    private final IgnoreManager ignoreManager;
    private final RankService ranks;
    private final ChatCosmeticsService cosmetics;



    public ChatListener(ChatConfigManager cfgMgr,
                        MuteManager mutes,
                        AfkService afk,
                        AutoModService automod,
                        ChatControlService chatCtl,
                        ChatExtrasService extras,
                        FreezeService freeze,
                        JailService jail,
                        EconomyService economy,
                        FlyTimeService fly,
                        PlaytimeService playtime,
                        IgnoreManager ignoreManager,
                        RankService ranks,
                        ChatCosmeticsService cosmetics
    ) {

        this.cfgMgr = cfgMgr;
        this.mutes = mutes;
        this.afk = afk;
        this.automod = automod;
        this.chatCtl = chatCtl;
        this.extras = extras;
        this.freeze = freeze;
        this.jail = jail;
        this.ignoreManager = ignoreManager;
        this.ranks = ranks;
        this.cosmetics = cosmetics;


        this.economy = economy;
        this.fly = fly;
        this.playtime = playtime;


    }


    @SubscribeEvent
    public void onChat(ServerChatEvent e) {

        if (e.isCanceled()) return;



        ServerPlayer p = e.getPlayer();

        String nameGradKey = cosmetics.getNameGradientKey(p.getUUID());
        String gradKey = cosmetics.getGradientKey(p.getUUID());



        if (afk != null && afk.isAfk(p.getUUID())) {
            afk.setAfk(p.server, p, false, true);
        }

        String raw = e.getMessage().getString();
        String msg = raw;





        if (freeze != null && freeze.isFrozen(p.getUUID())) {
            if (freeze.config().blockChat) {
                e.setCanceled(true);

                p.sendSystemMessage(
                        LegacyAmpersand.parse(
                                "&c&lPUNISHMENTS &7&l➤ &cYou cannot chat while frozen."
                        )
                );
                return;
            }
        }


        if (jail != null && jail.isJailed(p.getUUID())) {
            e.setCanceled(true);

            p.sendSystemMessage(
                    LegacyAmpersand.parse(
                            "&c&lPUNISHMENTS &7&l➤ &cYou cannot chat while jailed."
                    )
            );
            return;
        }



        if (mutes.isMuted(p.getUUID())) {

            var rec = mutes.record(p.getUUID()).orElse(null);

            String reason = rec != null ? rec.reason : "No reason";
            String staff = rec != null ? rec.by : "Console";

            long remaining =
                    mutes.remainingMillis(p.getUUID());

            e.setCanceled(true);

            p.sendSystemMessage(
                    LegacyAmpersand.parse(
                            "&c&lPUNISHMENTS &7&l➤ &cYou were muted for &e" +
                                    reason +
                                    " &cby &e" +
                                    staff +
                                    " &cyour mute will expire in &e" +
                                    TimeUtil.formatDuration(remaining / 1000)
                    )
            );

            return;
        }



        if (automod != null) {
            Component blocked = automod.check(p, msg);
            if (blocked != null) {
                e.setCanceled(true);
                p.sendSystemMessage(blocked);
                return;
            }
        }

        if (AuctionInputHandler.handle(p, msg)) {
            e.setCanceled(true);
            return;
        }



        if (chatCtl != null && chatCtl.isChatMuted()) {
            if (!p.hasPermissions(2)) {
                e.setCanceled(true);
                p.sendSystemMessage(
                        LegacyAmpersand.parse(chatCtl.config().chatMutedMessage)
                );
                return;
            }
        }



        if (chatCtl != null && !chatCtl.checkSlowchat(p)) {

            long sec = chatCtl.secondsRemaining(p);

            e.setCanceled(true);
            p.sendSystemMessage(
                    LegacyAmpersand.parse(
                            chatCtl.config().slowchatWaitMessage
                                    .replace("{SECONDS}", String.valueOf(sec))
                    )
            );
            return;
        }

        if (extras != null) {
            msg = extras.apply(p.server, p, msg);
        }

        if (chatCtl != null) {
            chatCtl.noteChat(p);
        }



        ChatConfig cfg = cfgMgr.config();
        if (!cfg.enabled) return;

        String group = LuckPermsHook.getPrimaryGroup(p.getUUID());

        String prefix = LuckPermsHook.getPrefix(p.getUUID());
        if (prefix == null) prefix = "";

        ChatConfig.Gradient nameGrad = cfg.nameGradients.getOrDefault(group,
                new ChatConfig.Gradient(cfg.defaultNameHex, cfg.defaultNameHex));

        ChatConfig.Gradient msgGrad = cfg.messageGradients.getOrDefault(group,
                new ChatConfig.Gradient(cfg.defaultMessageHex, cfg.defaultMessageHex));

        String username = NickHolder.INSTANCE.getDisplayName(p)
                .replaceAll("<gradient:.*?>", "")
                .replace("</gradient>", "")
                .replaceAll("\\{#.*?\\}", "")
                .replace("{/}", "");
        String rank = group.substring(0,1).toUpperCase() + group.substring(1);

        long play = playtime.getPlaytime(p.getUUID());
        long flyTime = fly.getTime(p.getUUID());

        double money = economy.get(p.getUUID(), CurrencyType.MONEY);
        double coins = economy.get(p.getUUID(), CurrencyType.COINS);
        double tokens = economy.get(p.getUUID(), CurrencyType.TOKENS);

        Component hover = LegacyAmpersand.parse(
                "&eUsername: &f" + username + "\n" +
                        "&eRank: &f" + rank + "\n" +
                        "&ePlaytime: &f" + TimeUtil.formatDuration(play) + "\n" +
                        "&eFlytime: &f" + TimeUtil.formatDuration(flyTime) + "\n\n" +
                        "&eMoney: &a" + money + "\n" +
                        "&eCoins: &6" + coins + "\n" +
                        "&eTokens: &b" + tokens + "\n\n" +
                        "&aClick to message the player"
        );

        if (prefix == null) prefix = "";

        var cosmeticNameColor = cosmetics.getNameColor(p.getUUID());
        var cosmeticNameGrad  = cosmetics.getNameGradient(p.getUUID());
        var cosmeticNameAnim  = cosmetics.getNameAnimated(p.getUUID());

        String styledName = username;

        if (cosmeticNameAnim != null) {

            String[] frame = cosmetics.nextNameFrame(p.getUUID());

            String start = frame[0];
            String end   = frame[1];

            styledName =
                    "<gradient:" + start + ":" + end + ">"
                            + username +
                            "</gradient>";
        }
        else if (cosmeticNameGrad != null &&
                nameGradKey != null &&
                ranks.hasPerm(p, "pixity.cosmetics.namegradient." + nameGradKey)) {

            String start = cosmeticNameGrad.start;
            String end   = cosmeticNameGrad.end;

            styledName =
                    "<gradient:" + start + ":" + end + ">"
                            + username +
                            "</gradient>";
        }
        else if (cosmeticNameColor != null &&
                ranks.hasPerm(p, "pixity.cosmetics.namecolor." + cosmeticNameColor)) {

            styledName = cosmeticNameColor + username;
        }

        String original = NickHolder.INSTANCE.getDisplayName(p);
        NickHolder.INSTANCE.setNick(p.getUUID(), styledName);

        Component nameComp = DisplayFormatter.formatPlayerChat(p);

        NickHolder.INSTANCE.setNick(p.getUUID(), original);

        nameComp = nameComp.copy()
                .withStyle(style -> style
                        .withHoverEvent(new HoverEvent(
                                HoverEvent.Action.SHOW_TEXT,
                                hover
                        ))
                        .withClickEvent(new ClickEvent(
                                ClickEvent.Action.SUGGEST_COMMAND,
                                "/msg " + username + " "
                        ))
                );

        boolean canColor =
                p.hasPermissions(2) ||
                        (ranks != null && (
                                ranks.hasPerm(p, "pixity.chat.color") ||
                                        ranks.hasPerm(p, "pixity.chat.*") ||
                                        ranks.hasPerm(p, "*")
                        ));

        var cosmeticColor = cosmetics.getColor(p.getUUID());
        var cosmeticGrad = cosmetics.getGradient(p.getUUID());
        var cosmeticAnim = cosmetics.getAnimated(p.getUUID());

        Component msgComp;

        if (cosmeticColor != null &&
                ranks.hasPerm(p, "pixity.cosmetics.chatcolor." + cosmeticColor)) {
            msgComp = LegacyAmpersand.parse(cosmeticColor + msg + "&r");
        }
        else if (cosmeticAnim != null) {

            String[] frame = cosmetics.nextFrame(p.getUUID());

            msgComp = TextFormatter.gradient(
                    msg,
                    frame[0],
                    frame[1]
            );
        }
        else if (cosmeticGrad != null &&
                gradKey != null &&
                ranks.hasPerm(p, "pixity.cosmetics.gradient." + gradKey)) {
            msgComp = TextFormatter.gradient(
                    msg,
                    cosmeticGrad.start,
                    cosmeticGrad.end
            );
        }
        else if (canColor) {
            msgComp = LegacyAmpersand.parse(raw);
        }
        else {
            msgComp = TextFormatter.gradient(msg, msgGrad.start, msgGrad.end);
        }
        Component tagComp = CosmeticService.buildTag(p);

        if (!tagComp.equals(Component.empty())) {
            tagComp = Component.literal(" ").append(tagComp);
        }

        Component finalMsg = Component.empty()
                .append(nameComp)
                .append(tagComp)
                .append(Component.literal(" » "))
                .append(msgComp);

        e.setCanceled(true);

        for (ServerPlayer sp : p.server.getPlayerList().getPlayers()) {

            if (ignoreManager != null &&
                    ignoreManager.isIgnoring(sp.getUUID(), p.getUUID())) {
                continue;
            }

            sp.sendSystemMessage(finalMsg);
        }
    }
}