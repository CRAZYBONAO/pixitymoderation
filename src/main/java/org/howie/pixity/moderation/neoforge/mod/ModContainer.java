package org.howie.pixity.moderation.neoforge.mod;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

import org.howie.pixity.moderation.chat.MuteManager;
import org.howie.pixity.moderation.neoforge.freeze.FreezeService;
import org.howie.pixity.moderation.neoforge.jail.JailService;
import org.howie.pixity.moderation.neoforge.punish.PunishmentManager;
import org.howie.pixity.moderation.neoforge.rank.RankService;

public class ModContainer extends SimpleContainer {

    private final ServerPlayer staff;
    private final ServerPlayer target;

    private final FreezeService freeze;
    private final JailService jail;
    private final PunishmentManager punish;
    private final RankService ranks;
    private final MuteManager mutes;

    private final ModGui gui;

    public ModContainer(ServerPlayer staff,
                        ServerPlayer target,
                        FreezeService freeze,
                        JailService jail,
                        PunishmentManager punish,
                        RankService ranks,
                        MuteManager mutes,
                        ModGui gui) {

        super(27);

        this.staff = staff;
        this.target = target;
        this.freeze = freeze;
        this.jail = jail;
        this.punish = punish;
        this.ranks = ranks;
        this.mutes = mutes;
        this.gui = gui;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {

        switch (slot) {

            case 10 -> gui.openConfirm(staff,target,ModAction.WARN);
            case 11 -> gui.openConfirm(staff,target,ModAction.FREEZE);
            case 12 -> gui.openConfirm(staff,target,ModAction.JAIL);
            case 13 -> gui.openConfirm(staff,target,ModAction.MUTE);
            case 14 -> gui.openConfirm(staff,target,ModAction.KICK);
            case 15 -> gui.openConfirm(staff,target,ModAction.TEMPBAN);
            case 16 -> gui.openConfirm(staff,target,ModAction.BAN);

            case 19 -> gui.openConfirm(staff,target,ModAction.IPBAN);
            case 20 -> gui.openConfirm(staff,target,ModAction.INVSEE);
            case 21 -> gui.openConfirm(staff,target,ModAction.ENDERCHEST);

            case 23 -> gui.openConfirm(staff,target,ModAction.TELEPORT);
            case 24 -> gui.openConfirm(staff,target,ModAction.NOTES);
            case 25 -> gui.openConfirm(staff,target,ModAction.HISTORY);
        }

        return ItemStack.EMPTY;
    }
}