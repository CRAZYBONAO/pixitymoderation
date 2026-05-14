package org.howie.pixity.moderation.neoforge.msg;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.chat.MuteManager;
import org.howie.pixity.moderation.chat.NickManager;
import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

import java.util.UUID;

public final class MsgService {

    private final IgnoreManager ignores;
    private final SocialSpyManager spy;
    private final ReplyManager reply;
    private final MuteManager mutes;
    private final RankService perms;
    private final NickManager nick;

    public MsgService(final IgnoreManager ignores,
                      final SocialSpyManager spy,
                      final ReplyManager reply,
                      final MuteManager mutes,
                      final RankService perms,
                      final NickManager nick) {

        this.ignores = ignores;
        this.spy = spy;
        this.reply = reply;
        this.mutes = mutes;
        this.perms = perms;
        this.nick = nick;
    }

    public boolean sendPrivate(final MinecraftServer server,
                               final ServerPlayer from,
                               final ServerPlayer to,
                               final String message) {

        if (server == null || from == null || to == null) return false;

        if (from.getUUID().equals(to.getUUID())) {
            from.sendSystemMessage(LegacyAmpersand.parse("&c&lSERVER &7&l➤ &cError! You cannot message yourself."));
            return false;
        }

        if (mutes.isMuted(from.getUUID())) {
            long rem = mutes.remainingMillis(from.getUUID());

            String time = (rem == Long.MAX_VALUE)
                    ? "forever"
                    : (Math.max(1, (rem + 999) / 1000) + "s");

            from.sendSystemMessage(LegacyAmpersand.parse("&c&lMUTES &7&l➤ &cYou are muted (" + time + ")."));
            return false;
        }

        boolean bypass = perms != null && perms.hasPerm(from, "pixity.ignore.bypass");

        if (!bypass && ignores.isIgnoring(to.getUUID(), from.getUUID())) {
            from.sendSystemMessage(LegacyAmpersand.parse("&c&lSERVER &7&l➤ &cThat player is ignoring you."));
            return false;
        }

        String clean = message.replace("§", "");

        boolean canColor = perms != null && perms.hasPerm(from, "pixity.msg.colored");

        Component formatted = canColor
                ? LegacyAmpersand.parse(clean)
                : Component.literal(clean);

        Component fromName = formatName(from);
        Component toName = formatName(to);

        Component toMsg = Component.empty()
                .append(LegacyAmpersand.parse("\n&8[&aFROM&8] &f"))
                .append(fromName)
                .append(LegacyAmpersand.parse(" &7» &f"))
                .append(formatted)
                .append(Component.literal("\n"));

        Component fromMsg = Component.empty()
                .append(LegacyAmpersand.parse("\n&8[&bTO&8] &f"))
                .append(toName)
                .append(LegacyAmpersand.parse(" &7» &f"))
                .append(formatted)
                .append(Component.literal("\n"));

        to.sendSystemMessage(toMsg);
        from.sendSystemMessage(fromMsg);

        reply.setLast(from.getUUID(), to.getUUID());
        reply.setLast(to.getUUID(), from.getUUID());

        for (ServerPlayer online : server.getPlayerList().getPlayers()) {

            if (online == null) continue;

            UUID u = online.getUUID();

            if (!spy.isEnabled(u)) continue;
            if (u.equals(from.getUUID()) || u.equals(to.getUUID())) continue;

            if (perms == null || !perms.hasPerm(online, "pixity.socialspy")) continue;

            Component spyMsg = Component.empty()
                    .append(LegacyAmpersand.parse("&8[&cSPY&8] &7"))
                    .append(fromName)
                    .append(LegacyAmpersand.parse(" &8→ &7"))
                    .append(toName)
                    .append(LegacyAmpersand.parse(" &7» &f"))
                    .append(formatted);

            online.sendSystemMessage(spyMsg);
        }

        return true;
    }

    private Component formatName(ServerPlayer p) {
        return org.howie.pixity.moderation.chat.DisplayFormatter.formatPlayer(p);
    }

    public UUID getReplyTarget(final UUID sender) {
        return reply.getLast(sender);
    }

    public boolean toggleIgnore(final UUID owner, final UUID target) {
        return ignores.toggleIgnore(owner, target);
    }

    public boolean toggleSpy(final UUID uuid) {
        return spy.toggle(uuid);
    }
}