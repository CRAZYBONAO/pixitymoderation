package org.howie.pixity.moderation.neoforge.tp.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public final class MenuProviderLike {

    @FunctionalInterface
    public interface Factory {
        AbstractContainerMenu create(int id, Inventory inv);
    }

    private MenuProviderLike() {}

    public static void open(final ServerPlayer p, final Factory f, final Component title) {
        final MenuProvider prov = new SimpleMenuProvider((id, inv, pl) -> f.create(id, inv), title);
        p.openMenu(prov);
    }
}
