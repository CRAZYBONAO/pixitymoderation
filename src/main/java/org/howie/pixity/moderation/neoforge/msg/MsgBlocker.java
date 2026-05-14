package org.howie.pixity.moderation.neoforge.msg;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.CommandEvent;

public final class MsgBlocker {

    @SubscribeEvent
    public void onCommand(CommandEvent e) {

        String raw = e.getParseResults()
                .getReader()
                .getString()
                .toLowerCase();

        if (raw.startsWith("/msg ")
                || raw.equals("/msg")
                || raw.startsWith("/tell ")
                || raw.equals("/tell")
                || raw.startsWith("/w ")
                || raw.equals("/w")) {

            e.setCanceled(true);
        }
    }
}