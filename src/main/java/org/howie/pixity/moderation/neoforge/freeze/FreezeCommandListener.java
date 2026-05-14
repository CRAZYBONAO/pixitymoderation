package org.howie.pixity.moderation.neoforge.freeze;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.CommandEvent;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

public final class FreezeCommandListener {


    private final FreezeService freeze;

    public FreezeCommandListener(final FreezeService freeze) {
        this.freeze = freeze;
    }

    @SubscribeEvent
    public void onCommand(CommandEvent e) {

        if (!(e.getParseResults().getContext().getSource().getEntity() instanceof ServerPlayer p)) return;

        if (!freeze.isFrozen(p.getUUID())) return;

        String input = e.getParseResults().getReader().getString();

        if (input.startsWith("/")) input = input.substring(1);

        String cmd = input.split(" ")[0].toLowerCase();

        if (freeze.config().allowCommands.contains(cmd)) return;

        e.setCanceled(true);

        p.sendSystemMessage(LegacyAmpersand.parse("&c&lPUNISHMENTS &7&l➤ §cYou cannot use commands other than /r and /reports while frozen."));
    }


}
