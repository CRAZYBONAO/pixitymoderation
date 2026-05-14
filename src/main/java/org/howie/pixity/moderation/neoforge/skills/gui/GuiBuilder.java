package org.howie.pixity.moderation.neoforge.skills.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;

public class GuiBuilder {

    public static MenuProvider create(Component title, int rows, MenuFactory factory) {

        return new MenuProvider() {

            @Override
            public Component getDisplayName() {
                return title;
            }

            @Override
            public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {

                SimpleContainer container = new SimpleContainer(rows * 9);

                return factory.create(id, inv, container);
            }
        };
    }

    public interface MenuFactory {
        AbstractContainerMenu create(int id, Inventory inv, SimpleContainer container);
    }
}