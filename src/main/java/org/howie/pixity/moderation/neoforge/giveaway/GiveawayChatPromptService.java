package org.howie.pixity.moderation.neoforge.giveaway;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class GiveawayChatPromptService {

    private static final class Pending {
        GiveawayService service;
        String rename;
        boolean saving;

        Pending(GiveawayService service) {
            this.service = service;
        }
    }

    private final Map<UUID, Pending> pending = new ConcurrentHashMap<>();





    public void requestSave(ServerPlayer p, GiveawayService service) {

        Pending pen = new Pending(service);
        pen.saving = true;

        pending.put(p.getUUID(), pen);

        p.sendSystemMessage(
                LegacyAmpersand.parse("&4&lGIVEAWAYS &7&l➤ §eType preset name...")
        );
    }





    public void requestRename(ServerPlayer p,
                              GiveawayService service,
                              String oldName) {

        Pending pen = new Pending(service);
        pen.rename = oldName;

        pending.put(p.getUUID(), pen);

        p.sendSystemMessage(
                LegacyAmpersand.parse("&4&lGIVEAWAYS &7&l➤ §eType new preset name...")
        );
    }





    public boolean tryConsume(MinecraftServer server,
                              ServerPlayer p,
                              String msg) {

        Pending pen = pending.remove(p.getUUID());
        if (pen == null) return false;

        if (pen.saving) {

            pen.service.savePreset(msg);

            p.sendSystemMessage(
                    LegacyAmpersand.parse("&4&lGIVEAWAYS &7&l➤ §aPreset saved: §e" + msg)
            );

            GiveawayGUI.open(p, pen.service, this);
            return true;
        }

        if (pen.rename != null) {

            pen.service.renamePreset(pen.rename, msg);

            p.sendSystemMessage(
                    LegacyAmpersand.parse("&4&lGIVEAWAYS &7&l➤ §aPreset renamed to §e" + msg)
            );

            GiveawayPresetMenu.open(p, pen.service, this);
            return true;
        }

        return false;
    }
}