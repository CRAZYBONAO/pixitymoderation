package org.howie.pixity.moderation.neoforge.chat.cosmetics.gui;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.ItemLore;

import org.howie.pixity.moderation.neoforge.shop.EconomyBridge;
import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

import java.util.ArrayList;
import java.util.List;

public class CosmeticEvolutionMenu {

    public static void open(ServerPlayer p,
                            EconomyBridge econ,
                            RankService rankService,
                            String base) {

        SimpleContainer cont = new SimpleContainer(27);

        ItemStack filler = new ItemStack(Items.BLACK_STAINED_GLASS_PANE);
        filler.set(DataComponents.CUSTOM_NAME, Component.literal(""));

        for (int i = 0; i < 27; i++) {
            cont.setItem(i, filler.copy());
        }


        double evolvedCost = 500;
        double finalCost = 2500;

        cont.setItem(10, buildStage(
                "&a&lBASE",
                "Already unlocked",
                true
        ));


        boolean hasEvolved = rankService.hasPerm(p, "pixity.cosmetic." + base + ".evolved");

        cont.setItem(13, buildStage(
                "&e&lEVOLVED",
                hasEvolved ? "&aUnlocked" : "&7Cost: &b" + (int)evolvedCost + " Tokens",
                hasEvolved
        ));


        boolean hasFinal = rankService.hasPerm(p, "pixity.cosmetic." + base + ".final");

        cont.setItem(16, buildStage(
                "&c&lFINAL",
                hasFinal ? "&aUnlocked" : "&7Cost: &b" + (int)finalCost + " Tokens",
                hasFinal
        ));



        p.openMenu(new SimpleMenuProvider(
                (id, inv, player) -> new ChestMenu(MenuType.GENERIC_9x3, id, inv, cont, 3) {

                    @Override
                    public void clicked(int slot, int button, ClickType type, net.minecraft.world.entity.player.Player player) {

                        if (!(player instanceof ServerPlayer sp)) return;

                        if (slot == 13 && !hasEvolved) {

                            if (econ.get(sp, "tokens") >= evolvedCost) {

                                econ.take(sp, evolvedCost, "tokens");

                                grant(sp, base + ".evolved");

                                sp.sendSystemMessage(LegacyAmpersand.parse("&aUnlocked EVOLVED!"));

                                sp.closeContainer();
                            } else {
                                sp.sendSystemMessage(LegacyAmpersand.parse("&cNot enough tokens!"));
                            }
                        }

                        if (slot == 16 && !hasFinal) {

                            if (econ.get(sp, "tokens") >= finalCost) {

                                econ.take(sp, evolvedCost, "tokens");

                                grant(sp, base + ".final");

                                sp.sendSystemMessage(LegacyAmpersand.parse("&aUnlocked FINAL!"));

                                sp.closeContainer();
                            } else {
                                sp.sendSystemMessage(LegacyAmpersand.parse("&cNot enough tokens!"));
                            }
                        }
                    }
                },
                LegacyAmpersand.parse("&6&lEVOLUTION: " + base.toUpperCase())
        ));
    }

    private static ItemStack buildStage(String name, String desc, boolean unlocked) {

        ItemStack item = new ItemStack(unlocked ? Items.LIME_STAINED_GLASS_PANE : Items.RED_STAINED_GLASS_PANE);

        item.set(DataComponents.CUSTOM_NAME,
                LegacyAmpersand.parse(name));

        List<Component> lore = new ArrayList<>();

        lore.add(LegacyAmpersand.parse(desc));

        item.set(DataComponents.LORE,
                new ItemLore(lore));

        return item;
    }

    private static void grant(ServerPlayer p, String node) {



        new RankService().addPermission(p, "pixity.cosmetic." + node);
    }
}