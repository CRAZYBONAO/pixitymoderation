package org.howie.pixity.moderation.neoforge.kits;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.minecraft.server.level.ServerPlayer;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class KitJoinListener {

    private final KitManager kits;
    private final Set<UUID> joined = new HashSet<>();

    public KitJoinListener(final KitManager kits) {
        this.kits = kits;
    }

    @SubscribeEvent
    public void onJoin(PlayerEvent.PlayerLoggedInEvent e) {
        if (!(e.getEntity() instanceof ServerPlayer p)) return;

        UUID uuid = p.getUUID();

        if (joined.contains(uuid)) return;
        joined.add(uuid);

        kits.getKit("starter").ifPresent(kit -> {
            if (kits.canUseKit(p, kit)) {
                kits.tryClaimKit(p, kit);
                p.sendSystemMessage(LegacyAmpersand.parse("&c&lKITS &7&l➤ You received your starter kit!"));
            }
        });
    }
}