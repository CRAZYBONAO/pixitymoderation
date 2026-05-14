package org.howie.pixity.moderation.neoforge.punish.gui;

import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.chat.MuteManager;
import org.howie.pixity.moderation.neoforge.freeze.FreezeService;
import org.howie.pixity.moderation.neoforge.jail.JailService;
import org.howie.pixity.moderation.neoforge.punish.PunishmentManager;
import org.howie.pixity.moderation.neoforge.rank.RankService;

public final class PunishGui {

    private PunishGui() {}

    public static void open(final ServerPlayer viewer,
                            final ServerPlayer target,
                            final PunishmentManager punishments,
                            final MuteManager mutes,
                            final FreezeService freeze,
                            final JailService jail,
                            final RankService ranks) {

        viewer.openMenu(
                PunishMenu.provider(
                        target.getUUID(),
                        target.getGameProfile().getName(),
                        punishments,
                        mutes,
                        freeze,
                        jail,
                        ranks,
                        viewer
                )
        );
    }
}
