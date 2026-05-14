package org.howie.pixity.moderation.neoforge.kits.gui;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.SimpleMenuProvider;

import org.howie.pixity.moderation.neoforge.economy.EconomyService;
import org.howie.pixity.moderation.neoforge.kits.KitCategory;
import org.howie.pixity.moderation.neoforge.kits.KitManager;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

import java.util.ArrayList;
import java.util.List;

public final class KitsCategoryGui {

    public static void open(ServerPlayer player,
                            KitManager kits,
                            EconomyService economy) {

        SimpleContainer cont = new SimpleContainer(27);

        ItemStack filler = new ItemStack(Items.BLACK_STAINED_GLASS_PANE);
        filler.set(DataComponents.CUSTOM_NAME, Component.literal(""));

        for (int i = 0; i < 27; i++)
            cont.setItem(i, filler);

        cont.setItem(9, category(
                kits,
                KitCategory.FREE,
                Items.CHEST,
                "&a&lFREE",
                "&7Claim the free rank kits here!"
        ));

        cont.setItem(11, category(
                kits,
                KitCategory.PURCHASE,
                Items.GOLD_INGOT,
                "&a&lPURCHASABLE",
                "&7Claim the ingame purchaseable kits here"
        ));

        cont.setItem(13, category(
                kits,
                KitCategory.DONATOR,
                Items.DIAMOND,
                "&b&lDONATOR",
                "&7Claim the donator rank kits here"
        ));

        cont.setItem(15, category(
                kits,
                KitCategory.EVENT,
                Items.NETHER_STAR,
                "&d&lEVENTS",
                "&7Claim the event kits here (resets every season)"
        ));

        cont.setItem(17, category(
                kits,
                KitCategory.VOTE,
                Items.EMERALD,
                "&9&lVOTE",
                "&7Claim the voting kits here (resets monthly)"
        ));

        player.openMenu(new SimpleMenuProvider(
                (id, inv, p) -> new ChestMenu(
                        net.minecraft.world.inventory.MenuType.GENERIC_9x3,
                        id,
                        inv,
                        cont,
                        3
                ) {

                    @Override
                    public void clicked(int slot, int button,
                                        net.minecraft.world.inventory.ClickType type,
                                        net.minecraft.world.entity.player.Player p) {

                        if (!(p instanceof ServerPlayer sp)) return;

                        switch (slot) {
                            case 9 -> KitsGui.openCategory(sp, kits, economy, KitCategory.FREE);
                            case 11 -> KitsGui.openCategory(sp, kits, economy, KitCategory.PURCHASE);
                            case 13 -> KitsGui.openCategory(sp, kits, economy, KitCategory.DONATOR);
                            case 15 -> KitsGui.openCategory(sp, kits, economy, KitCategory.EVENT);
                            case 17 -> KitsGui.openCategory(sp, kits, economy, KitCategory.VOTE);
                        }
                    }
                },
                Component.literal("§cKits")
        ));
    }

    private static ItemStack category(
            KitManager kits,
            KitCategory cat,
            net.minecraft.world.item.Item item,
            String name,
            String desc
    ) {

        ItemStack it = new ItemStack(item);

        it.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);

        it.set(DataComponents.CUSTOM_NAME,
                LegacyAmpersand.parse(name));

        long count = kits.allKits().stream()
                .filter(k -> KitCategory.from(k.category) == cat)
                .count();

        List<Component> lore = new ArrayList<>();
        lore.add(LegacyAmpersand.parse(desc));
        lore.add(Component.empty());
        lore.add(LegacyAmpersand.parse("&7Kits: &e" + count));
        lore.add(LegacyAmpersand.parse("&aClick to open"));

        it.set(DataComponents.LORE, new ItemLore(lore));

        return it;
    }
}