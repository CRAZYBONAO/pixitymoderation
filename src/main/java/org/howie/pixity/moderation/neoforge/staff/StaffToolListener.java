package org.howie.pixity.moderation.neoforge.staff;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import org.howie.pixity.moderation.neoforge.freeze.FreezeService;
import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

import java.util.UUID;

public final class StaffToolListener {


    private final StaffModeService staff;
    private final FreezeService freeze;
    private final RankService ranks;

    public StaffToolListener(final StaffModeService staff,
                             final FreezeService freeze,
                             final RankService ranks) {

        this.staff = staff;
        this.freeze = freeze;
        this.ranks = ranks;
    }

    @SubscribeEvent
    public void onEntityInteract(final PlayerInteractEvent.EntityInteract e) {

        if (!(e.getEntity() instanceof ServerPlayer p)) return;
        if (!(e.getTarget() instanceof ServerPlayer target)) return;
        if (e.getHand() != InteractionHand.MAIN_HAND) return;

        if (!staff.isEnabled(p.getUUID())) return;
        if (ranks == null || !ranks.hasPerm(p, StaffModeService.PERM_TOOL)) return;

        ItemStack it = p.getMainHandItem();
        if (it.isEmpty()) return;

        if (it.getItem() == Items.BLAZE_ROD) {
            toggleFreeze(p, target);
            e.setCanceled(true);
            return;
        }

        if (it.getItem() == Items.COMPASS) {
            run(p, "tpo " + target.getGameProfile().getName());
            e.setCanceled(true);
            return;
        }

        if (it.getItem() == Items.LEAD) {
            run(p, "tpohere " + target.getGameProfile().getName());
            e.setCanceled(true);
            return;
        }

        if (it.getItem() == Items.PAPER) {
            run(p, "history " + target.getGameProfile().getName());
            e.setCanceled(true);
            return;
        }

        if (it.getItem() == Items.BOOK) {
            inspect(p, target);
            e.setCanceled(true);
        }
    }

    private void toggleFreeze(final ServerPlayer p, final ServerPlayer target) {
        UUID tu = target.getUUID();

        if (freeze.isFrozen(tu)) {
            if (!ranks.hasPerm(p, FreezeService.PERM_UNFREEZE)) {
                p.sendSystemMessage(LegacyAmpersand.parse("&c&lSTAFFMODE &7&l➤ &cError! No permission to unfreeze."));
                return;
            }
            freeze.unfreeze(p.server, p, tu, target.getGameProfile().getName(), "Staff tool");
            return;
        }

        if (!ranks.hasPerm(p, FreezeService.PERM_FREEZE)) {
            p.sendSystemMessage(LegacyAmpersand.parse("&c&lSTAFFMODE &7&l➤ &cError! No permission to freeze."));
            return;
        }

        if (ranks.hasPerm(target, FreezeService.PERM_BYPASS)) {
            p.sendSystemMessage(LegacyAmpersand.parse("&c&lSTAFFMODE &7&l➤ &cError! That player cannot be frozen."));
            return;
        }

        freeze.freeze(p.server, p, target, "Staff tool");
    }

    private static void run(final ServerPlayer p, final String cmd) {
        p.getServer().getCommands().performPrefixedCommand(p.createCommandSourceStack(), cmd);
    }

    private static void inspect(final ServerPlayer staff, final ServerPlayer target) {
        staff.sendSystemMessage(LegacyAmpersand.parse("&c&lSTAFFMODE &7&l➤ &aInspect: " + target.getGameProfile().getName()));
    }


}
