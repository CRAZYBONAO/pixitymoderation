package org.howie.pixity.moderation.neoforge.kits.firstjoin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.neoforge.kits.Kit;
import org.howie.pixity.moderation.neoforge.kits.KitManager;
import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

import java.util.*;

public final class FirstJoinService {

    private final KitManager kits;
    private final RankService ranks;
    private final FirstJoinStore store;

    private final Set<UUID> given;

    public FirstJoinService(final KitManager kits,
                            final RankService ranks,
                            final FirstJoinStore store) {

        this.kits = kits;
        this.ranks = ranks;
        this.store = store;

        this.given = store.load();
    }

    public void handleJoin(final MinecraftServer server, final ServerPlayer player) {
        if (player == null) return;

        UUID uuid = player.getUUID();

        if (given.contains(uuid)) return;

        Kit kit = resolveKit(player);

        if (kit != null) {
            kits.tryClaimKit(player, kit);

            given.add(uuid);
            store.save(given);

            player.sendSystemMessage(
                    LegacyAmpersand.parse("§c&lKITS &7&l➤ &aYou have received your &estarter &akit!")
            );
        }
    }

    private Kit resolveKit(final ServerPlayer player) {

        List<String> priority = List.of(
                "starter.owner",
                "starter.admin",
                "starter.mod",
                "starter.mvp",
                "starter.vip",
                "starter"
        );

        for (String key : priority) {

            var opt = kits.getKit(key);
            if (opt.isEmpty()) continue;

            Kit kit = opt.get();

            if (ranks.hasPerm(player, "pixitymoderation.kit." + kit.name)) {
                return kit;
            }

            if (key.equals("starter")) {
                return kit;
            }
        }

        return null;
    }
}