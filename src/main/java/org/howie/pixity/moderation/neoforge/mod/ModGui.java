package org.howie.pixity.moderation.neoforge.mod;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.SimpleMenuProvider;

import org.howie.pixity.moderation.chat.MuteManager;
import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.state.PlayerStateService;
import org.howie.pixity.moderation.neoforge.freeze.FreezeService;
import org.howie.pixity.moderation.neoforge.jail.JailService;
import org.howie.pixity.moderation.neoforge.punish.PunishmentManager;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

import java.util.UUID;

public final class ModGui {

    private final PlayerStateService state;
    private final FreezeService freeze;
    private final JailService jail;
    private final PunishmentManager punish;
    private final RankService ranks;
    private final MuteManager mutes;

    public ModGui(PlayerStateService state,
                  FreezeService freeze,
                  JailService jail,
                  PunishmentManager punish,
                  RankService ranks,
                  MuteManager mutes) {

        this.state = state;
        this.freeze = freeze;
        this.jail = jail;
        this.punish = punish;
        this.ranks = ranks;
        this.mutes = mutes;
    }

    private boolean has(ServerPlayer p, String perm) {
        return ranks != null && ranks.hasPerm(p, perm);
    }

    public void open(ServerPlayer staff, ServerPlayer target) {

        ModContainer cont = new ModContainer(
                staff,
                target,
                freeze,
                jail,
                punish,
                ranks,
                mutes,
                this
        );

        ItemStack filler = new ItemStack(Items.BLACK_STAINED_GLASS_PANE);
        filler.set(DataComponents.CUSTOM_NAME, Component.literal(""));

        for (int i = 0; i < 27; i++) {
            cont.setItem(i, filler.copy());
        }


        ItemStack head = new ItemStack(Items.PLAYER_HEAD);
        head.set(DataComponents.CUSTOM_NAME,
                Component.literal("§c" + target.getName().getString()));

        head.set(DataComponents.PROFILE,
                new net.minecraft.world.item.component.ResolvableProfile(
                        target.getGameProfile()
                ));

        cont.setItem(4, head);

        if (has(staff,"pixity.mod.warn"))
            cont.setItem(10, item(Items.BOOK, "§eWarn"));

        if (has(staff,"pixity.mod.freeze"))
            cont.setItem(11, item(Items.ICE, "§bFreeze"));

        if (has(staff,"pixity.mod.jail"))
            cont.setItem(12, item(Items.IRON_BARS, "§6Jail"));

        if (has(staff,"pixity.mod.mute"))
            cont.setItem(13, item(Items.PAPER, "§dMute"));

        if (has(staff,"pixity.mod.kick"))
            cont.setItem(14, item(Items.BARRIER, "§cKick"));

        if (has(staff,"pixity.mod.tempban"))
            cont.setItem(15, item(Items.REDSTONE_BLOCK, "§4Temp Ban"));

        if (has(staff,"pixity.mod.ban"))
            cont.setItem(16, item(Items.NETHERITE_BLOCK, "§4Ban"));

        if (has(staff,"pixity.mod.ipban"))
            cont.setItem(19, item(Items.TNT, "§4IP Ban"));

        if (has(staff,"pixity.mod.invsee"))
            cont.setItem(20, item(Items.CHEST, "§6Inventory"));

        if (has(staff,"pixity.mod.endersee"))
            cont.setItem(21, item(Items.ENDER_CHEST, "§5Ender Chest"));

        if (has(staff,"pixity.mod.teleport"))
            cont.setItem(23, item(Items.ENDER_PEARL, "§bTeleport"));

        if (has(staff,"pixity.mod.notes"))
            cont.setItem(24, item(Items.WRITABLE_BOOK, "§aStaff Notes"));

        if (has(staff,"pixity.mod.history"))
            cont.setItem(25, item(Items.NAME_TAG, "§7History"));

        staff.openMenu(new SimpleMenuProvider(
                (id, inv, p) -> new ChestMenu(
                        MenuType.GENERIC_9x3,
                        id,
                        inv,
                        cont,
                        3
                ),
                LegacyAmpersand.parse("§6[Mod] §f" + target.getName().getString())
        ));
    }

    public void openConfirm(ServerPlayer staff, ServerPlayer target, ModAction action) {

        ConfirmContainer cont = new ConfirmContainer(
                staff,
                target,
                freeze,
                jail,
                punish,
                ranks,
                mutes,
                action,
                this
        );
        cont.setItem(11, item(Items.LIME_WOOL, "§aCONFIRM"));
        cont.setItem(15, item(Items.RED_WOOL, "§cCANCEL"));

        MenuConstructor ctor = (id, inv, p) ->
                ChestMenu.threeRows(id, inv, cont);

        staff.openMenu(new SimpleMenuProvider(
                ctor,
                Component.literal("§cConfirm: §7" + action.name())
        ));
    }

    private ItemStack item(Item item, String name) {
        ItemStack stack = new ItemStack(item);
        stack.set(DataComponents.CUSTOM_NAME,
                LegacyAmpersand.parse(name));
        return stack;
    }
}