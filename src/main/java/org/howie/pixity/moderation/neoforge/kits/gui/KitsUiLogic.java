package org.howie.pixity.moderation.neoforge.kits.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.neoforge.kits.KitManager;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

public final class KitsUiLogic {

    private KitsUiLogic() {}

    public static void tryClaim(final ServerPlayer p,
                                final KitManager kits,
                                final String key) {

        kits.getKit(key).ifPresentOrElse(
                kit -> kits.tryClaimKit(p, kit),
                () -> p.sendSystemMessage(LegacyAmpersand.parse("&c&lKITS &7&l➤ &cError! Kit not found: " + key))
        );
    }
}