package org.howie.pixity.moderation.neoforge.mail;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import org.apache.logging.log4j.Logger;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

import java.util.*;

public final class MailService {

    private final Logger logger;
    private final MailConfig cfg;
    private final SQLiteMailStore store;

    public MailService(final Logger logger,
                       final MailConfig cfg,
                       final SQLiteMailStore store) {

        this.logger = logger;
        this.cfg = cfg;
        this.store = store;
    }

    public void send(ServerPlayer from, UUID toUuid, String toName, String msg) {

        MailMessage m = new MailMessage();

        m.id = String.valueOf(store.nextId(toUuid));
        m.ts = System.currentTimeMillis();

        if (from == null) {
            m.fromUuid = "CONSOLE";
            m.fromName = "CONSOLE";
        } else {
            m.fromUuid = from.getUUID().toString();
            m.fromName = from.getGameProfile().getName();
        }

        m.toUuid = toUuid.toString();
        m.toName = toName;
        m.message = msg;
        m.read = false;

        store.insert(m);
    }

    public List<MailMessage> inbox(UUID to) {
        return store.getInbox(to);
    }

    public MailMessage get(UUID to, String id) {
        return store.get(to, id);
    }

    public boolean markRead(UUID to, String id) {
        store.markRead(to, id);
        return true;
    }

    public boolean delete(UUID to, String id) {
        return store.delete(to, id);
    }

    public int clear(UUID to) {
        return store.clear(to);
    }

    public int unreadCount(UUID to) {
        return store.unread(to);
    }

    public void notifyOnJoin(MinecraftServer server, ServerPlayer p) {

        if (!cfg.enabled || !cfg.notifyOnJoin) return;

        int unread = unreadCount(p.getUUID());
        if (unread <= 0) return;

        p.sendSystemMessage(LegacyAmpersand.parse(""));
        p.sendSystemMessage(
                LegacyAmpersand.parse(
                        cfg.notifyMessage.replace("{COUNT}", String.valueOf(unread))
                )
        );
        p.sendSystemMessage(LegacyAmpersand.parse(""));
    }
}