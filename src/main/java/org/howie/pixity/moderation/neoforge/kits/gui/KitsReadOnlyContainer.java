package org.howie.pixity.moderation.neoforge.kits.gui;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;

import org.howie.pixity.moderation.neoforge.economy.EconomyService;
import org.howie.pixity.moderation.neoforge.kits.KitManager;

import java.util.Map;

public final class KitsReadOnlyContainer extends SimpleContainer {

    private final ServerPlayer player;
    private final KitManager kits;
    private final EconomyService economy;
    private final Map<Integer, String> slotToKey;

    public KitsReadOnlyContainer(ServerPlayer player,
                                 KitManager kits,
                                 EconomyService economy,
                                 Map<Integer, String> slotToKey) {

        super(54);
        this.player = player;
        this.kits = kits;
        this.economy = economy;
        this.slotToKey = slotToKey;
    }

    public void setIcon(int slot, ItemStack stack) {
        super.setItem(slot, stack.copy());
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {}

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return false;
    }

    @Override
    public void clearContent() {}
}