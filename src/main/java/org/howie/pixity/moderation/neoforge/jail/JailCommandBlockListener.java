package org.howie.pixity.moderation.neoforge.jail;

import net.minecraft.server.level.ServerPlayer;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.CommandEvent;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

public final class JailCommandBlockListener {


    private final JailService jail;

    public JailCommandBlockListener(final JailService jail) {
        this.jail = jail;
    }

    @SubscribeEvent
    public void onCommand(final CommandEvent e) {

        if (!(e.getParseResults().getContext().getSource().getEntity() instanceof ServerPlayer p)) return;

        if (!jail.isJailed(p.getUUID())) return;

        String input = e.getParseResults().getReader().getString().toLowerCase();

        if (input.startsWith("/")) input = input.substring(1);

        String base = input.split(" ")[0];

        if (!jail.getConfig().allowCommands.contains(base)) {
            e.setCanceled(true);
            p.sendSystemMessage(LegacyAmpersand.parse("&c&lPUNISHMENTS &7&l➤ &cError! You cannot use that command while jailed."));
        }
    }


}
