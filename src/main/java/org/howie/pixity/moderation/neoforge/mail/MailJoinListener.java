package org.howie.pixity.moderation.neoforge.mail;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.minecraft.server.level.ServerPlayer;

public final class MailJoinListener {

    private final MailService mail;

    public MailJoinListener(final MailService mail) {
        this.mail = mail;
    }

    @SubscribeEvent
    public void onJoin(final PlayerEvent.PlayerLoggedInEvent e) {
        if (e.getEntity() instanceof ServerPlayer p) {
            mail.notifyOnJoin(p.server, p);
        }
    }
}
