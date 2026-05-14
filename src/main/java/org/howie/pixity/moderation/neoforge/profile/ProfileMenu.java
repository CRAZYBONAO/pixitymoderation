package org.howie.pixity.moderation.neoforge.profile;

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
import net.minecraft.world.item.component.ItemLore;

import org.howie.pixity.moderation.neoforge.alts.AltsService;
import org.howie.pixity.moderation.neoforge.alts.smart.SmartAltService;
import org.howie.pixity.moderation.neoforge.freeze.FreezeService;
import org.howie.pixity.moderation.neoforge.notes.NotesService;
import org.howie.pixity.moderation.neoforge.reports.ReportsService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

import java.util.*;

public final class ProfileMenu extends ChestMenu {


    private static final int ROWS = 4;
    private static final int SIZE = 9 * ROWS;

    private final SimpleContainer top;

    private final FreezeService freeze;
    private final NotesService notes;
    private final ReportsService reports;
    private final AltsService alts;
    private final SmartAltService smart;

    private final ServerPlayer viewer;
    private final ServerPlayer target;

    public ProfileMenu(int id,
                       Inventory inv,
                       FreezeService freeze,
                       NotesService notes,
                       ReportsService reports,
                       AltsService alts,
                       SmartAltService smart,
                       ServerPlayer viewer,
                       ServerPlayer target) {

        super(MenuType.GENERIC_9x4, id, inv, new SimpleContainer(SIZE), ROWS);

        this.top = (SimpleContainer) this.getContainer();

        this.freeze = freeze;
        this.notes = notes;
        this.reports = reports;
        this.alts = alts;
        this.smart = smart;
        this.viewer = viewer;
        this.target = target;

        render();
    }

    private void render() {

        for (int i = 0; i < SIZE; i++) top.setItem(i, ItemStack.EMPTY);

        boolean isFrozen = freeze != null && freeze.isFrozen(target.getUUID());




        ItemStack info = new ItemStack(Items.NAME_TAG);

        info.set(DataComponents.CUSTOM_NAME,
                Component.literal(target.getGameProfile().getName()).withStyle(ChatFormatting.AQUA));

        List<Component> lore = new ArrayList<>();
        lore.add(LegacyAmpersand.parse("&7UUID: &e" + target.getUUID()));
        lore.add(LegacyAmpersand.parse("&bFrozen: " + (isFrozen ? "&aYES" : "&cNO")));

        int noteCount = notes != null ? notes.list(target.getUUID()).size() : 0;
        lore.add(LegacyAmpersand.parse("&cNotes: &e" + noteCount));

        info.set(DataComponents.LORE, new ItemLore(lore));
        top.setItem(4, info);




        top.setItem(20, button(Items.CHEST, LegacyAmpersand.parse("&cNotes")));
        top.setItem(21, button(Items.BOOK, LegacyAmpersand.parse("&4Reports")));
        top.setItem(22, button(Items.PLAYER_HEAD, LegacyAmpersand.parse("&cAlts")));

        top.setItem(31, button(Items.BARRIER, LegacyAmpersand.parse("&4Close")));
    }

    private static ItemStack button(net.minecraft.world.item.Item item, Component name) {
        ItemStack it = new ItemStack(item);
        it.set(DataComponents.CUSTOM_NAME, name);
        return it;
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {

        if (!(player instanceof ServerPlayer sp)) return;

        switch (slotId) {
            case 20 -> sp.sendSystemMessage(LegacyAmpersand.parse("&e&lPROFILE &7&l➤ Opening notes..."));
            case 21 -> sp.sendSystemMessage(LegacyAmpersand.parse("&e&lPROFILE &7&l➤ Opening reports..."));
            case 22 -> sp.sendSystemMessage(LegacyAmpersand.parse("&e&lPROFILE &7&l➤ Opening alts..."));
            case 31 -> sp.closeContainer();
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }


}
