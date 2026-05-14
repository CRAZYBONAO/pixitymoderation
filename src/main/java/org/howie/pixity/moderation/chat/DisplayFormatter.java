package org.howie.pixity.moderation.chat;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.neoforge.afk.AfkHolder;
import org.howie.pixity.moderation.neoforge.rank.RankHolder;
import org.howie.pixity.moderation.neoforge.tab.TabFormatting;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

public final class DisplayFormatter {

    private DisplayFormatter() {}

    public static Component formatPlayer(ServerPlayer player) {
        if (player == null) return Component.literal("Unknown");

        String name =
                NickHolder.INSTANCE.getDisplayName(player);

        String prefix =
                TabFormatting.buildPrefix(player);

        String suffix = "";
        if (RankHolder.INSTANCE != null) {
            suffix = RankHolder.INSTANCE.suffix(player);
        }

        String full =
                prefix + " " + name + " " + suffix;

        if (AfkHolder.INSTANCE != null &&
                AfkHolder.INSTANCE.isAfk(player.getUUID())) {

            full = "§8[§7AFK§8] §r" + full;
        }

        return LegacyAmpersand.parse(full);
    }

    public static Component formatPlayerChat(ServerPlayer player) {
        if (player == null) return Component.literal("Unknown");

        String name =
                NickHolder.INSTANCE.getDisplayName(player);

        String prefix =
                TabFormatting.buildPrefix(player);

        String suffix = "";
        if (RankHolder.INSTANCE != null) {
            suffix = RankHolder.INSTANCE.suffix(player);
        }

        String full =
                prefix + " " + name + " " + suffix;

        if (AfkHolder.INSTANCE != null &&
                AfkHolder.INSTANCE.isAfk(player.getUUID())) {

            full = "§8[§7AFK§8] §r" + full;
        }

        return LegacyAmpersand.parse(full);
    }
}