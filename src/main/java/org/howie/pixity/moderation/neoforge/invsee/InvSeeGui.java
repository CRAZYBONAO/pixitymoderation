package org.howie.pixity.moderation.neoforge.invsee;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;

import org.howie.pixity.moderation.neoforge.tp.gui.MenuProviderLike;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

import java.util.UUID;

public final class InvSeeGui {

    private InvSeeGui() {}

    public static void openInv(final ServerPlayer viewer, final ServerPlayer target, final boolean editable) {
        final int rows = 6;
        final InvSeeContainer cont = new InvSeeContainer(rows * 9, editable);

        var inv = target.getInventory();
        for (int i = 0; i < 36; i++) cont.setItem(i, inv.getItem(i).copy());


        try {
            cont.setItem(36, inv.offhand.get(0).copy());
            for (int i = 0; i < 4; i++) cont.setItem(37 + i, inv.armor.get(i).copy());
        } catch (Throwable ignored) {}

        Component title = LegacyAmpersand.parse("&8InvSee: &f" + target.getGameProfile().getName() + (editable ? " &a[EDIT]" : " &7[VIEW]"));
        UUID targetId = target.getUUID();

        MenuProviderLike.open(viewer,
                (id, viewerInv) -> new InvSeeMenu(id, viewerInv, cont, rows, viewer, targetId, false, editable),
                title);
    }

    public static void openEnder(final ServerPlayer viewer, final ServerPlayer target, final boolean editable) {
        final int rows = 3;
        final InvSeeContainer cont = new InvSeeContainer(rows * 9, editable);

        var ec = target.getEnderChestInventory();
        for (int i = 0; i < Math.min(ec.getContainerSize(), cont.getContainerSize()); i++) cont.setItem(i, ec.getItem(i).copy());

        Component title = LegacyAmpersand.parse("&8EnderSee: &f" + target.getGameProfile().getName() + (editable ? " &a[EDIT]" : " &7[VIEW]"));
        UUID targetId = target.getUUID();

        MenuProviderLike.open(viewer,
                (id, viewerInv) -> new InvSeeMenu(id, viewerInv, cont, rows, viewer, targetId, true, editable),
                title);
    }
}
