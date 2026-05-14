package org.howie.pixity.moderation.neoforge.giveaway;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.ItemLore;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

import java.util.ArrayList;
import java.util.List;

public class GiveawayGUI {

    public static void open(ServerPlayer p,
                            GiveawayService service,
                            GiveawayChatPromptService prompts) {

        SimpleContainer cont = new SimpleContainer(27);

        cont.setItem(10, save());
        cont.setItem(11, deposit(service));
        cont.setItem(12, toggle(service));
        cont.setItem(13, time(service));
        cont.setItem(14, start(service));
        cont.setItem(15, delete());
        cont.setItem(16, preset());

        p.openMenu(new SimpleMenuProvider(
                (id, inv, player) -> new ChestMenu(MenuType.GENERIC_9x3, id, inv, cont, 3) {

                    @Override
                    public void clicked(int slot, int button, ClickType type, net.minecraft.world.entity.player.Player player) {

                        if (!(player instanceof ServerPlayer sp)) return;

                        switch (slot) {

                            case 10 -> {
                                sp.closeContainer();
                                prompts.requestSave(sp, service);
                            }

                            case 11 -> GiveawayDepositGUI.open(sp, service);

                            case 12 -> {
                                service.toggleMode();
                                open(sp, service, prompts);
                            }

                            case 13 -> {

                                if (button == 1) {
                                    service.addTime(-30);
                                }
                                else if (type == ClickType.QUICK_MOVE) {
                                    service.addTime(5);
                                }
                                else {
                                    service.addTime(30);
                                }

                                open(sp, service, prompts);
                            }

                            case 14 -> {
                                service.start(sp.server, service.getTime());
                                sp.closeContainer();
                            }

                            case 15 -> {
                                service.deletePreset("preset");
                                sp.sendSystemMessage(Component.literal("§cPreset deleted."));
                            }

                            case 16 -> GiveawayPresetMenu.open(sp, service, prompts);
                        }
                    }
                },
                Component.literal("§6Giveaway Creator")
        ));
    }

    private static ItemStack deposit(GiveawayService s) {

        ItemStack it = new ItemStack(Items.HOPPER);

        List<Component> lore = new ArrayList<>();

        if (s.getRewardPool().isEmpty()) {
            lore.add(LegacyAmpersand.parse("&4&lGIVEAWAYS &7&l➤ §cError! No rewards deposited"));
        } else {
            for (var i : s.getRewardPool()) {
                lore.add(Component.literal(
                        "§e• " + i.getCount() + "x " +
                                i.getHoverName().getString()
                ));
            }
        }

        it.set(DataComponents.CUSTOM_NAME,
                Component.literal("§6Deposit Rewards"));

        it.set(DataComponents.LORE, new ItemLore(lore));

        return it;
    }

    private static ItemStack toggle(GiveawayService s) {

        ItemStack it = new ItemStack(Items.COMPARATOR);

        List<Component> lore = new ArrayList<>();

        lore.add(Component.literal(
                "§7Current Mode: §e" + s.getMode().name()
        ));

        it.set(DataComponents.CUSTOM_NAME,
                Component.literal("§dToggle Mode"));

        it.set(DataComponents.LORE, new ItemLore(lore));

        return it;
    }

    private static ItemStack start(GiveawayService s) {

        ItemStack it = new ItemStack(Items.LIME_CONCRETE);

        List<Component> lore = new ArrayList<>();

        lore.add(Component.literal(
                "§7Entry Time: §e" + s.getTime() + "s"
        ));

        it.set(DataComponents.CUSTOM_NAME,
                Component.literal("§aStart Giveaway"));

        it.set(DataComponents.LORE, new ItemLore(lore));

        return it;
    }

    private static ItemStack time(GiveawayService s) {

        ItemStack it = new ItemStack(Items.CLOCK);

        List<Component> lore = new ArrayList<>();

        lore.add(Component.literal("§7Current: §e" + s.getTime() + "s"));
        lore.add(Component.literal("§7Left Click: +30s"));
        lore.add(Component.literal("§7Right Click: -30s"));
        lore.add(Component.literal("§7Shift Click: +5s"));

        it.set(DataComponents.CUSTOM_NAME,
                Component.literal("§eEntry Time"));

        it.set(DataComponents.LORE, new ItemLore(lore));

        return it;
    }

    private static ItemStack save() {
        ItemStack it = new ItemStack(Items.BOOK);
        it.set(DataComponents.CUSTOM_NAME,
                Component.literal("§bSave Preset"));
        return it;
    }

    private static ItemStack delete() {
        ItemStack it = new ItemStack(Items.BARRIER);
        it.set(DataComponents.CUSTOM_NAME,
                Component.literal("§cDelete Preset"));
        return it;
    }

    private static ItemStack preset() {
        ItemStack it = new ItemStack(Items.CHEST);
        it.set(DataComponents.CUSTOM_NAME,
                Component.literal("§ePresets"));
        return it;
    }
}