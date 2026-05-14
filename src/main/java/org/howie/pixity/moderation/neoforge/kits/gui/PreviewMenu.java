package org.howie.pixity.moderation.neoforge.kits.gui;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

import org.howie.pixity.moderation.neoforge.kits.*;

public class PreviewMenu extends ChestMenu {

    private final KitManager kits;
    private final Kit kit;
    private final ServerPlayer player;

    public PreviewMenu(int id, Inventory inv, SimpleContainer cont, KitManager kits, Kit kit, ServerPlayer player) {
        super(net.minecraft.world.inventory.MenuType.GENERIC_9x6, id, inv, cont, 6);
        this.kits = kits;
        this.kit = kit;
        this.player = player;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public void clicked(int slot, int button, net.minecraft.world.inventory.ClickType type, Player p) {
        if (slot == 53) {
            KitsUiLogic.tryClaim(player, kits, kit.name);
            player.closeContainer();
        }
    }
}