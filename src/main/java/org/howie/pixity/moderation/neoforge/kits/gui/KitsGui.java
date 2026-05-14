package org.howie.pixity.moderation.neoforge.kits.gui;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;

import net.minecraft.world.SimpleMenuProvider;

import org.howie.pixity.moderation.neoforge.economy.CurrencyFormatter;
import org.howie.pixity.moderation.neoforge.economy.CurrencyType;
import org.howie.pixity.moderation.neoforge.economy.EconomyService;
import org.howie.pixity.moderation.neoforge.kits.*;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

import java.util.*;

import static org.howie.pixity.moderation.neoforge.kits.KitManager.formatTime;

public final class KitsGui {

    private KitsGui() {}



    public static void open(final ServerPlayer player,
                            final KitManager kitManager,
                            final EconomyService economy) {

        final List<Kit> list = kitManager.allKits().stream()
                .sorted(Comparator.comparing(k -> k.name))
                .toList();

        final Map<Integer, String> slotToKey = new HashMap<>();
        final KitsReadOnlyContainer cont =
                new KitsReadOnlyContainer(player, kitManager, economy, slotToKey);

        int slot = 0;

        for (Kit k : list) {
            if (slot >= cont.getContainerSize()) break;

            ItemStack icon = kitManager.getIcon(player, k);
            icon.set(DataComponents.CUSTOM_NAME,
                    kitManager.renderDisplayName(k.displayNameRaw));

            boolean canUse = kitManager.canUseKit(player, k);
            long remaining = kitManager.remainingCooldownSeconds(player, k);

            List<Component> lore = new ArrayList<>();
            lore.add(LegacyAmpersand.parse("&7Kit:&r " + k.name));



            if (!canUse) {
                lore.add(LegacyAmpersand.parse("§cNo permission"));
                lore.add(LegacyAmpersand.parse("§cNeed: pixitymoderation.kit." + k.name));
            } else if (remaining > 0) {
                lore.add(LegacyAmpersand.parse("&eCooldown: §c" + formatTime(remaining)));
            } else {
                lore.add(LegacyAmpersand.parse("§aReady to claim"));
            }



            if (k.price > 0) {

                boolean isFree = kitManager.getFinalPrice(player, k) <= 0;

                CurrencyType currency = CurrencyType.valueOf(
                        k.currency == null ? "MONEY" : k.currency
                );

                double finalPrice = kitManager.getFinalPrice(player, k);

                if (isFree) {
                    lore.add(LegacyAmpersand.parse("§aPrice: FREE"));
                }
                else if (finalPrice < k.price) {
                    lore.add(LegacyAmpersand.parse("§aDiscounted: "
                            + CurrencyFormatter.format(currency, finalPrice)));

                    lore.add(LegacyAmpersand.parse("§7Original: "
                            + CurrencyFormatter.format(currency, k.price)));
                }
                else {
                    lore.add(LegacyAmpersand.parse("Price: "
                            + CurrencyFormatter.format(currency, k.price)));
                }


                double bal = economy.get(player, currency);

                if (finalPrice > 0) {
                    if (bal >= finalPrice) {
                        lore.add(LegacyAmpersand.parse("§aYou can afford this"));
                    } else {
                        lore.add(LegacyAmpersand.parse("§cYou cannot afford this"));
                    }
                }
            }


            if (k.cooldownSeconds > 0) {
                lore.add(LegacyAmpersand.parse("&eCooldown: &c" + formatTime(remaining)));
            }

            icon.set(DataComponents.LORE, new ItemLore(lore));

            cont.setIcon(slot, icon);
            slotToKey.put(slot, k.name);
            slot++;
        }

        MenuConstructor ctor = (id, inv, p) -> ChestMenu.sixRows(id, inv, cont);

        player.openMenu(new SimpleMenuProvider(
                ctor,
                buildTitle(player, economy)
        ));
    }



    public static void openCategory(ServerPlayer player,
                                    KitManager kitManager,
                                    EconomyService economy,
                                    KitCategory category) {

        final List<Kit> list = kitManager.allKits().stream()
                .filter(k -> KitCategory.from(k.category) == category)
                .sorted(Comparator.comparing(k -> k.name))
                .toList();

        final Map<Integer, String> slotToKey = new HashMap<>();
        final KitsReadOnlyContainer cont =
                new KitsReadOnlyContainer(player, kitManager, economy, slotToKey);

        ItemStack filler = new ItemStack(Items.BLACK_STAINED_GLASS_PANE);
        filler.set(DataComponents.CUSTOM_NAME, Component.literal(""));

        for (int i = 0; i < 54; i++)
            cont.setIcon(i, filler);

        int slot = 10;

        for (Kit k : list) {

            if (slot % 9 == 8) slot += 2;

            ItemStack icon = kitManager.getIcon(player, k).copy();

            icon.set(
                    DataComponents.CUSTOM_NAME,
                    kitManager.renderDisplayName(k.displayNameRaw)
            );

            boolean hasPerm = kitManager.canUseKit(player, k);
            long remaining = kitManager.remainingCooldownSeconds(player, k);

            List<Component> lore = new ArrayList<>();

            if (!hasPerm) {
                lore.add(LegacyAmpersand.parse("&cNo Permission"));
                lore.add(LegacyAmpersand.parse("&7pixity.kits.claim." + k.name));
            }


            else if (k.price <= 0) {
                lore.add(LegacyAmpersand.parse("&aFREE"));
            }
            else {

                CurrencyType currency = CurrencyType.valueOf(
                        k.currency == null ? "MONEY" : k.currency
                );

                double finalPrice = kitManager.getFinalPrice(player, k);

                lore.add(LegacyAmpersand.parse(
                        "&6Price: &e" + CurrencyFormatter.format(currency, finalPrice)
                ));

                double bal = economy.get(player, currency);

                if (bal >= finalPrice) {
                    lore.add(LegacyAmpersand.parse("&aYou can afford this"));
                } else {
                    lore.add(LegacyAmpersand.parse("&cYou cannot afford this"));
                }
            }

            if (remaining > 0) {
                lore.add(LegacyAmpersand.parse(
                        "&eCooldown: &c" + formatTime(remaining)
                ));
            } else {
                lore.add(LegacyAmpersand.parse("&aReady to claim"));
            }

            lore.add(Component.empty());
            lore.add(LegacyAmpersand.parse("&7Left Click: &fClaim"));
            lore.add(LegacyAmpersand.parse("&7Right Click: &fPreview"));

            icon.set(DataComponents.LORE, new ItemLore(lore));

            cont.setIcon(slot, icon);
            slotToKey.put(slot, k.name);

            slot++;
        }

        ItemStack back = new ItemStack(Items.ARROW);
        back.set(
                DataComponents.CUSTOM_NAME,
                LegacyAmpersand.parse("&c← Back")
        );

        cont.setIcon(49, back);

        player.openMenu(new SimpleMenuProvider(
                (id, inv, p) -> new ChestMenu(
                        net.minecraft.world.inventory.MenuType.GENERIC_9x6,
                        id, inv, cont, 6) {

                    @Override
                    public void clicked(int slot, int button,
                                        net.minecraft.world.inventory.ClickType type,
                                        net.minecraft.world.entity.player.Player p) {

                        if (!(p instanceof ServerPlayer sp)) return;

                        if (slot == 49) {
                            KitsCategoryGui.open(sp, kitManager, economy);
                            return;
                        }

                        String key = slotToKey.get(slot);
                        if (key == null) return;

                        kitManager.getKit(key).ifPresent(kit -> {

                            if (button == 1) {
                                KitPreviewGui.open(sp, kitManager, economy, kit);
                                return;
                            }

                            kitManager.tryClaimKit(sp, kit);
                        });
                    }
                },
                Component.literal("§c" + category.name() + " Kits")
        ));
    }

    private static Component buildTitle(ServerPlayer player, EconomyService economy) {

        double money = economy.get(player, CurrencyType.MONEY);
        double coins = economy.get(player, CurrencyType.COINS);
        double tokens = economy.get(player, CurrencyType.TOKENS);

        return Component.literal("§cKits §7| ")
                .append(LegacyAmpersand.parse("§a$" + (int) money))
                .append(LegacyAmpersand.parse(" §7| "))
                .append(LegacyAmpersand.parse("§e⛁ " + (int) coins))
                .append(LegacyAmpersand.parse(" §7| "))
                .append(LegacyAmpersand.parse("§b✦ " + (int) tokens));
    }
}