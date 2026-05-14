package org.howie.pixity.moderation.neoforge.invsee;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;


public final class InvSeeContainer extends SimpleContainer {

    private final boolean editable;

    public InvSeeContainer(final int size, final boolean editable) {
        super(size);
        this.editable = editable;
    }

    @Override
    public boolean stillValid(final Player p) {
        return true;
    }

    @Override
    public ItemStack removeItem(final int slot, final int amount) {
        if (!editable) return ItemStack.EMPTY;
        return super.removeItem(slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(final int slot) {
        if (!editable) return ItemStack.EMPTY;
        return super.removeItemNoUpdate(slot);
    }

    @Override
    public void setItem(final int slot, final ItemStack stack) {
        if (!editable) return;
        super.setItem(slot, stack);
    }

    @Override
    public boolean canPlaceItem(final int slot, final ItemStack stack) {
        return editable && super.canPlaceItem(slot, stack);
    }
}
