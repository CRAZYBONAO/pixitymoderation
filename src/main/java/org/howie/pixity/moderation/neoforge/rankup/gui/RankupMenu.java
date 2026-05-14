package org.howie.pixity.moderation.neoforge.rankup.gui;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.*;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.*;
import net.minecraft.network.chat.Component;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.ItemLore;

import org.howie.pixity.moderation.neoforge.rankup.RankupService;
import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.shop.EconomyBridge;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

import java.util.ArrayList;
import java.util.List;

public class RankupMenu {

    public static void open(ServerPlayer p,
                            RankService rankService,
                            EconomyBridge econ) {

        String current = RankupService.getCurrentRank(p, rankService);
        String next = RankupService.getNextRank(current);
        double cost = RankupService.getNextCost(current);

        if (next == null) {
            p.sendSystemMessage(Component.literal("§aYou are max rank!"));
            return;
        }

        SimpleContainer cont = new SimpleContainer(27);


        ItemStack filler = new ItemStack(Items.BLACK_STAINED_GLASS_PANE);
        filler.set(DataComponents.CUSTOM_NAME, Component.literal(""));
        for (int i = 0; i < 27; i++) cont.setItem(i, filler.copy());


        ItemStack currentItem = new ItemStack(Items.PAPER);
        currentItem.set(DataComponents.CUSTOM_NAME,
                LegacyAmpersand.parse("&eCurrent: &f" + current));
        cont.setItem(11, currentItem);


        ItemStack nextItem = new ItemStack(Items.EMERALD);

        List<Component> lore = new ArrayList<>();
        lore.add(LegacyAmpersand.parse("&7Cost: &a$" + (int) cost));
        lore.add(Component.literal(""));
        lore.add(LegacyAmpersand.parse("&eClick to rank up"));

        nextItem.set(DataComponents.CUSTOM_NAME,
                LegacyAmpersand.parse("&aNext: &f" + next));
        nextItem.set(DataComponents.LORE, new ItemLore(lore));

        cont.setItem(15, nextItem);


        p.openMenu(new SimpleMenuProvider(
                (id, inv, player) -> new ChestMenu(MenuType.GENERIC_9x3, id, inv, cont, 3) {

                    @Override
                    public void clicked(int slot, int button, ClickType type, net.minecraft.world.entity.player.Player player) {

                        if (!(player instanceof ServerPlayer sp)) return;

                        if (slot == 15) {
                            sp.closeContainer();
                            RankupConfirmMenu.open(sp, rankService, econ);
                        }
                    }
                },
                LegacyAmpersand.parse("&6&lRANK UP")
        ));
    }
}