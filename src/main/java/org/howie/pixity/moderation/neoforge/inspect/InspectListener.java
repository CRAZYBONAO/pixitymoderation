package org.howie.pixity.moderation.neoforge.inspect;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public final class InspectListener {

    private final InspectService svc;

    public InspectListener(final InspectService svc) {
        this.svc = svc;
    }


    @SubscribeEvent
    public void onServerTick(final ServerTickEvent.Post e) {
        svc.onServerTick();
    }

    @SubscribeEvent
    public void onPlace(final BlockEvent.EntityPlaceEvent e) {
        if (!(e.getEntity() instanceof ServerPlayer p)) return;
        if (!(e.getLevel() instanceof ServerLevel lvl)) return;

        BlockPos pos = e.getPos();
        BlockState st = e.getPlacedBlock();
        String id = st.getBlock().builtInRegistryHolder().key().location().toString();
        svc.recordPlace(lvl, pos, p, id);
    }

    @SubscribeEvent
    public void onBreak(final BlockEvent.BreakEvent e) {
        if (!(e.getPlayer() instanceof ServerPlayer p)) return;
        if (!(e.getLevel() instanceof ServerLevel lvl)) return;

        BlockPos pos = e.getPos();
        BlockState st = e.getState();
        String id = st.getBlock().builtInRegistryHolder().key().location().toString();
        svc.recordBreak(lvl, pos, p, id);
    }

    @SubscribeEvent
    public void onInteract(final PlayerInteractEvent.RightClickBlock e) {
        if (!(e.getEntity() instanceof ServerPlayer p)) return;
        if (!(e.getLevel() instanceof ServerLevel lvl)) return;

        BlockPos pos = e.getPos();
        BlockState st = lvl.getBlockState(pos);
        String id = st.getBlock().builtInRegistryHolder().key().location().toString();

        svc.recordInteract(lvl, pos, p, id);

        if (!svc.isInspecting(p.getUUID())) return;
        if (!svc.canUse(p)) return;

        List<BlockLogEntry> list = svc.get(lvl, pos);

        p.sendSystemMessage(LegacyAmpersand.parse(
                "&6&lInspect &7(" + id + ") &8@" + pos.getX() + " " + pos.getY() + " " + pos.getZ()
        ));

        if (list.isEmpty()) {
            p.sendSystemMessage(LegacyAmpersand.parse("&7No history for this block yet."));
            return;
        }

        int shown = 0;
        for (BlockLogEntry le : list) {
            if (le == null) continue;

            String when = fmt(le.ts);

            p.sendSystemMessage(LegacyAmpersand.parse(
                    "&e" + le.action + " &7by &f" + le.playerName + " &8(" + when + ")"
            ));

            shown++;
            if (shown >= 8) break;
        }

        p.sendSystemMessage(LegacyAmpersand.parse("&8Tip: /inspect to toggle off."));
    }

    private static String fmt(final long ts) {
        try {
            return new SimpleDateFormat("MM-dd HH:mm").format(new Date(ts));
        } catch (Throwable t) {
            return String.valueOf(ts);
        }
    }
}