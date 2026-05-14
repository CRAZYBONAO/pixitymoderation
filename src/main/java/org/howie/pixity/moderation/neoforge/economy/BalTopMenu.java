package org.howie.pixity.moderation.neoforge.economy;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;

import java.util.*;

public final class BalTopMenu extends ChestMenu {

    private final SimpleContainer top;

    public BalTopMenu(int id, Inventory inv, MinecraftServer server,
                      EconomyService econ, CurrencyType type) {

        super(MenuType.GENERIC_9x6, id, inv, new SimpleContainer(54), 6);

        this.top = (SimpleContainer) this.getContainer();

        render(server, econ, type);
    }

    private void render(MinecraftServer server, EconomyService econ, CurrencyType type) {

        List<Map.Entry<UUID, Double>> sorted = new ArrayList<>();

        for (UUID u : econ.getAllUsers()) {
            sorted.add(Map.entry(u, econ.get(u, type)));
        }

        sorted.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        int slot = 0;

        for (int i = 0; i < sorted.size() && slot < 45; i++) {

            UUID u = sorted.get(i).getKey();
            double value = sorted.get(i).getValue();

            String name = server.getProfileCache() != null
                    ? server.getProfileCache().get(u).map(p -> p.getName()).orElse(u.toString())
                    : u.toString();

            ItemStack head = new ItemStack(Items.PLAYER_HEAD);

            head.set(DataComponents.CUSTOM_NAME,
                    Component.literal("§e#" + (i + 1) + " §f" + name));

            List<Component> lore = new ArrayList<>();
            lore.add(Component.literal("§7Balance: §a" + CurrencyFormatter.format(type, value)));

            head.set(DataComponents.LORE, new ItemLore(lore));

            top.setItem(slot++, head);
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    public static net.minecraft.world.MenuProvider provider(
            MinecraftServer server, EconomyService econ, CurrencyType type) {

        return new net.minecraft.world.SimpleMenuProvider(
                (id, inv, ply) -> new BalTopMenu(id, inv, server, econ, type),
                Component.literal("BalTop")
        );
    }
}