package org.howie.pixity.moderation.neoforge.rankup.gui;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.*;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.*;
import net.minecraft.network.chat.Component;
import net.minecraft.core.component.DataComponents;

import org.howie.pixity.moderation.neoforge.rankup.RankupService;
import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.shop.EconomyBridge;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

public class RankupConfirmMenu {

    public static void open(ServerPlayer p,
                            RankService rankService,
                            EconomyBridge econ) {

        SimpleContainer cont = new SimpleContainer(27);

        ItemStack filler = new ItemStack(Items.GRAY_STAINED_GLASS_PANE);
        filler.set(DataComponents.CUSTOM_NAME, Component.literal(""));
        for (int i = 0; i < 27; i++) cont.setItem(i, filler.copy());


        ItemStack confirm = new ItemStack(Items.LIME_WOOL);
        confirm.set(DataComponents.CUSTOM_NAME,
                LegacyAmpersand.parse("&a&lCONFIRM"));
        cont.setItem(11, confirm);


        ItemStack cancel = new ItemStack(Items.RED_WOOL);
        cancel.set(DataComponents.CUSTOM_NAME,
                LegacyAmpersand.parse("&c&lCANCEL"));
        cont.setItem(15, cancel);

        p.openMenu(new SimpleMenuProvider(
                (id, inv, player) -> new ChestMenu(MenuType.GENERIC_9x3, id, inv, cont, 3) {

                    @Override
                    public void clicked(int slot, int button, ClickType type, net.minecraft.world.entity.player.Player player) {

                        if (!(player instanceof ServerPlayer sp)) return;

                        if (slot == 11) {
                            RankupService.rankup(sp, rankService, econ);
                            sp.closeContainer();
                        }

                        if (slot == 15) {
                            sp.closeContainer();
                        }
                    }
                },
                LegacyAmpersand.parse("&cConfirm Rankup")
        ));
    }
}