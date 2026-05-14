package org.howie.pixity.moderation.neoforge.invsee;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.UUID;


public final class InvSeeMenu extends AbstractContainerMenu {

    private final Container top;
    private final int rows;

    private final ItemStack[] initial;

    private final UUID targetUuid;
    private final boolean enderChest;
    private final boolean editable;
    private final ServerPlayer viewer;

    public InvSeeMenu(final int id,
                      final Inventory viewerInv,
                      final InvSeeContainer top,
                      final int rows,
                      final ServerPlayer viewer,
                      final UUID targetUuid,
                      final boolean enderChest,
                      final boolean editable) {
        super(menuTypeFor(rows), id);
        this.top = top;
        this.rows = rows;
        this.viewer = viewer;
        this.targetUuid = targetUuid;
        this.enderChest = enderChest;
        this.editable = editable;

        this.initial = new ItemStack[top.getContainerSize()];
        for (int i = 0; i < top.getContainerSize(); i++) {
            ItemStack s = top.getItem(i);
            this.initial[i] = (s == null) ? ItemStack.EMPTY : s.copy();
        }

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < 9; c++) {
                this.addSlot(new Slot(top, c + r * 9, 8 + c * 18, 18 + r * 18));
            }
        }

        int yOff = 18 + rows * 18 + 14;

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 9; c++) {
                this.addSlot(new Slot(viewerInv, c + r * 9 + 9, 8 + c * 18, yOff + r * 18));
            }
        }

        for (int c = 0; c < 9; c++) {
            this.addSlot(new Slot(viewerInv, c, 8 + c * 18, yOff + 58));
        }
    }

    @Override
    public boolean stillValid(final Player p) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(final Player player, final int index) {
        if (!editable) return ItemStack.EMPTY;

        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();

            int containerSlots = rows * 9;
            if (index < containerSlots) {
                if (!this.moveItemStackTo(stack, containerSlots, this.slots.size(), true)) return ItemStack.EMPTY;
            } else {
                if (!this.moveItemStackTo(stack, 0, containerSlots, false)) return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
            else slot.setChanged();
        }
        return itemstack;
    }

    @Override
    public void removed(final Player player) {
        super.removed(player);

        if (!editable) return;
        if (!(player instanceof ServerPlayer)) return;

        ServerPlayer target = viewer.server.getPlayerList().getPlayer(targetUuid);
        if (target == null) return;

        try {
            if (enderChest) {
                var ec = target.getEnderChestInventory();
                for (int i = 0; i < Math.min(ec.getContainerSize(), top.getContainerSize()); i++) {
                    ec.setItem(i, top.getItem(i).copy());
                }
            } else {
                var inv = target.getInventory();
                int max = Math.min(36, top.getContainerSize());
                for (int i = 0; i < max; i++) {
                    inv.setItem(i, top.getItem(i).copy());
                }
            }

            try {
                int limit = enderChest ? 27 : 36;
                java.util.Map<String, Integer> removed = new java.util.LinkedHashMap<>();
                java.util.Map<String, Integer> added = new java.util.LinkedHashMap<>();

                for (int i = 0; i < Math.min(limit, top.getContainerSize()); i++) {
                    ItemStack before = (initial != null && i < initial.length && initial[i] != null) ? initial[i] : ItemStack.EMPTY;
                    ItemStack after = top.getItem(i);
                    if (after == null) after = ItemStack.EMPTY;

                    if (!ItemStack.isSameItemSameComponents(before, after) || before.getCount() != after.getCount()) {
                        tallyDelta(before, after, removed, added);
                    }
                }

                if (!removed.isEmpty() || !added.isEmpty()) {
                    InvAuditLogger al = InvAuditLogger.get();
                    if (al != null) {
                        al.logInvEdit(viewer, target.getUUID(), target.getGameProfile().getName(), enderChest, removed, added);
                    }
                }
            } catch (Throwable ignored2) {}

        } catch (Throwable ignored) {
        }
    }

    private static void tallyDelta(final ItemStack before, final ItemStack after,
                                   final java.util.Map<String, Integer> removed,
                                   final java.util.Map<String, Integer> added) {
        try {
            String bId = (before == null || before.isEmpty()) ? null : before.getItem().builtInRegistryHolder().key().location().toString();
            String aId = (after == null || after.isEmpty()) ? null : after.getItem().builtInRegistryHolder().key().location().toString();

            int b = (before == null) ? 0 : before.getCount();
            int a = (after == null) ? 0 : after.getCount();

            if (bId != null && aId != null && bId.equals(aId)) {
                int diff = a - b;
                if (diff > 0) inc(added, aId, diff);
                else if (diff < 0) inc(removed, bId, -diff);
                return;
            }

            if (bId != null && b > 0) inc(removed, bId, b);
            if (aId != null && a > 0) inc(added, aId, a);
        } catch (Throwable ignored) {}
    }

    private static void inc(final java.util.Map<String, Integer> map, final String key, final int amt) {
        if (key == null || amt <= 0) return;
        Integer cur = map.get(key);
        map.put(key, (cur == null ? 0 : cur) + amt);
    }


    private static MenuType<?> menuTypeFor(final int rows) {
        return switch (rows) {
            case 1 -> MenuType.GENERIC_9x1;
            case 2 -> MenuType.GENERIC_9x2;
            case 3 -> MenuType.GENERIC_9x3;
            case 4 -> MenuType.GENERIC_9x4;
            case 5 -> MenuType.GENERIC_9x5;
            default -> MenuType.GENERIC_9x6;
        };
    }
}
