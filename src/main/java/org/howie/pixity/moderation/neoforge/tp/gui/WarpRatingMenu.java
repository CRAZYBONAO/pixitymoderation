package org.howie.pixity.moderation.neoforge.tp.gui;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;
import org.howie.pixity.moderation.neoforge.tp.PlayerWarp;

public class WarpRatingMenu extends ChestMenu {

    private final PlayerWarp warp;

    public WarpRatingMenu(int id, Inventory inv, PlayerWarp warp) {
        super(MenuType.GENERIC_9x3, id, inv, new SimpleContainer(27), 3);
        this.warp = warp;
        render();
    }

    private void render() {

        for (int i = 0; i < 5; i++) {
            int stars = i + 1;

            ItemStack star = new ItemStack(Items.NETHER_STAR);
            star.set(
                    DataComponents.CUSTOM_NAME,
                    LegacyAmpersand.parse(
                            "&6Rate " + stars + " &eStar" + (stars > 1 ? "s" : "")
                    )
            );

            getContainer().setItem(11 + i, star);
        }

        ItemStack info = new ItemStack(Items.PAPER);
        info.set(
                DataComponents.CUSTOM_NAME,
                LegacyAmpersand.parse(
                        "&eRating: " + String.format("%.1f", warp.getRating())
                )
        );

        getContainer().setItem(4, info);
    }

    @Override
    public void clicked(int slot, int button, ClickType type, Player player) {
        if (!(player instanceof ServerPlayer sp)) return;

        if (slot >= 11 && slot <= 15) {
            int stars = slot - 10;


            if (warp.owner != null && warp.owner.equals(sp.getUUID())) {
                sp.sendSystemMessage(LegacyAmpersand.parse("&d&lPLAYER WARPS &7&l➤ You cannot rate your own warp."));
                return;
            }

            warp.rate(sp.getUUID(), stars);

            sp.sendSystemMessage(LegacyAmpersand.parse("&d&lPLAYER WARPS &7&l➤ You rated this warp &e" + stars + " stars."));
            sp.closeContainer();
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}