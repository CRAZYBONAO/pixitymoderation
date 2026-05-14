package org.howie.pixity.moderation.neoforge.crate;

import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.howie.pixity.moderation.neoforge.crate.gui.CrateAnimationMenu;
import org.howie.pixity.moderation.neoforge.crate.gui.CratePreviewMenu;

public class CrateListener {

    @SubscribeEvent
    public void onInteract(net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock e) {

        if (!(e.getEntity() instanceof ServerPlayer player)) return;

        var pos = e.getPos();
        var level = player.serverLevel();

        String crateId = CrateBlockManager.get(level, pos);

        if (crateId == null) return;

        ItemStack held = player.getMainHandItem();


        if (!CrateKeyService.isKey(held)) {
            player.sendSystemMessage(Component.literal("§cYou need a crate key."));
            return;
        }


        if (!CrateKeyService.getCrate(held).equalsIgnoreCase(crateId)) {
            player.sendSystemMessage(Component.literal("§cWrong key."));
            return;
        }

        held.shrink(1);

        CrateAnimationMenu.open(player, crateId);

        e.setCanceled(true);
    }

    @SubscribeEvent
    public void onLeftClick(net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.LeftClickBlock e) {

        if (!(e.getEntity() instanceof ServerPlayer player)) return;

        String crateId = CrateBlockManager.get(player.serverLevel(), e.getPos());

        if (crateId == null) return;

        CratePreviewMenu.open(player, crateId);

        e.setCanceled(true);
    }
}