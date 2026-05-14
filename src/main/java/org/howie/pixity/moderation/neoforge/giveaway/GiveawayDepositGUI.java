package org.howie.pixity.moderation.neoforge.giveaway;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

public class GiveawayDepositGUI {

    public static void open(ServerPlayer p, GiveawayService service) {

        SimpleContainer cont = new SimpleContainer(54);

        p.openMenu(new SimpleMenuProvider(
                (id, inv, player) -> new ChestMenu(MenuType.GENERIC_9x6, id, inv, cont, 6) {

                    @Override
                    public void removed(net.minecraft.world.entity.player.Player player) {

                        service.getRewardPool().clear();

                        for (int i = 0; i < cont.getContainerSize(); i++) {
                            ItemStack item = cont.getItem(i);
                            if (!item.isEmpty()) {
                                service.getRewardPool().add(item.copy());
                            }
                        }

                        player.sendSystemMessage(LegacyAmpersand.parse("&4&lGIVEAWAYS &7&l➤ §aRewards saved."));
                    }
                },
                LegacyAmpersand.parse("&4&lGIVEAWAYS &7&l➤ §aDeposit the rewards in the menu....")
        ));
    }
}