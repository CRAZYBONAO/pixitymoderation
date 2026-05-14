package org.howie.pixity.moderation.neoforge.mod;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

import org.howie.pixity.moderation.chat.MuteManager;
import org.howie.pixity.moderation.neoforge.freeze.FreezeService;
import org.howie.pixity.moderation.neoforge.invsee.InvSeeGui;
import org.howie.pixity.moderation.neoforge.jail.JailService;
import org.howie.pixity.moderation.neoforge.punish.PunishmentManager;
import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

import java.util.UUID;

public class ConfirmContainer extends SimpleContainer {

    private final ServerPlayer staff;
    private final ServerPlayer target;

    private final FreezeService freeze;
    private final JailService jail;
    private final PunishmentManager punish;
    private final RankService ranks;
    private final MuteManager mutes;

    private final ModAction action;
    private final ModGui gui;

    public ConfirmContainer(ServerPlayer staff,
                            ServerPlayer target,
                            FreezeService freeze,
                            JailService jail,
                            PunishmentManager punish,
                            RankService ranks,
                            MuteManager mutes,
                            ModAction action,
                            ModGui gui) {

        super(27);

        this.staff = staff;
        this.target = target;
        this.freeze = freeze;
        this.jail = jail;
        this.punish = punish;
        this.ranks = ranks;
        this.mutes = mutes;
        this.action = action;
        this.gui = gui;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {

        if (slot != 11) return ItemStack.EMPTY;

        UUID id = target.getUUID();
        MinecraftServer server = staff.server;

        switch (action) {

            case FREEZE -> {
                if (freeze.isFrozen(id))
                    freeze.unfreeze(server, staff, id,
                            target.getName().getString(), "GUI");
                else
                    freeze.freeze(server, staff, target, "GUI");
            }

            case JAIL -> jail.jail(server, staff, target,
                    "default", 300L, "GUI Jail");

            case MUTE -> {

                mutes.tempMute(
                        id,
                        staff.getName().getString(),
                        300_000L,
                        "GUI Mute"
                );

                staff.sendSystemMessage(
                        LegacyAmpersand.parse(
                                "&4&lPUNISHMENTS &7&l➤ &aMuted &f" +
                                        target.getName().getString()
                        )
                );

                target.sendSystemMessage(
                        LegacyAmpersand.parse(
                                "&4&lPUNISHMENTS &7&l➤ &cYou have been muted by &e" + staff + " &cfor &e5m"

                        )
                );
            }

            case KICK -> punish.kick(server, staff, target, "GUI Kick");

            case TEMPBAN -> {

                punish.ban(
                        server,
                        staff,
                        id,
                        target.getGameProfile().getName(),
                        3600L,
                        "GUI Tempban"
                );

                staff.sendSystemMessage(
                        LegacyAmpersand.parse(
                                "&4&lPUNISHMENTS &7&l➤ &aTemp banned &f" +
                                        target.getName().getString()
                        )
                );
            }

            case BAN -> {

                punish.ban(
                        server,
                        staff,
                        id,
                        target.getGameProfile().getName(),
                        null,
                        "GUI Ban"
                );

                staff.sendSystemMessage(
                        LegacyAmpersand.parse(
                                "&4&lPUNISHMENTS &7&l➤ &cBanned &f" +
                                        target.getName().getString()
                        )
                );
            }

            case IPBAN -> {
                punish.ipBan(server, staff, target, "GUI IP Ban");
            }

            case WARN -> punish.warn(staff, target, "GUI Warn");

            case TELEPORT ->
                    staff.teleportTo(
                            target.getX(),
                            target.getY(),
                            target.getZ()
                    );

            case INVSEE -> {
                staff.closeContainer();
                InvSeeGui.openInv(staff, target, true);
                return ItemStack.EMPTY;
            }

            case ENDERCHEST -> {
                staff.closeContainer();
                InvSeeGui.openEnder(staff, target, true);
                return ItemStack.EMPTY;
            }

            case NOTES -> {
                staff.closeContainer();
                server.getCommands().performPrefixedCommand(
                        staff.createCommandSourceStack(),
                        "notes " + target.getName().getString());
            }

            case HISTORY -> {
                server.getCommands().performPrefixedCommand(
                        staff.createCommandSourceStack(),
                        "history " + target.getName().getString());
            }
        }

        gui.open(staff, target);
        return ItemStack.EMPTY;
    }
}