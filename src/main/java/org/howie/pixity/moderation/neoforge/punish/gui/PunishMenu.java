package org.howie.pixity.moderation.neoforge.punish.gui;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;

import org.howie.pixity.moderation.chat.MuteManager;
import org.howie.pixity.moderation.neoforge.freeze.FreezeService;
import org.howie.pixity.moderation.neoforge.jail.JailService;
import org.howie.pixity.moderation.neoforge.punish.PunishmentManager;
import org.howie.pixity.moderation.neoforge.rank.RankService;

import java.util.*;

public final class PunishMenu extends ChestMenu {

    private static final int SIZE = 9 * 3;

    private final SimpleContainer top;

    private final UUID targetUuid;
    private final String targetName;

    private final PunishmentManager punish;
    private final MuteManager mutes;
    private final FreezeService freeze;
    private final JailService jail;
    private final RankService ranks;

    private final ServerPlayer viewer;

    public PunishMenu(int id,
                      Inventory inv,
                      UUID targetUuid,
                      String targetName,
                      PunishmentManager punish,
                      MuteManager mutes,
                      FreezeService freeze,
                      JailService jail,
                      RankService ranks,
                      ServerPlayer viewer) {

        super(net.minecraft.world.inventory.MenuType.GENERIC_9x3,
                id,
                inv,
                new SimpleContainer(SIZE),
                3);

        this.top = (SimpleContainer) this.getContainer();

        this.targetUuid = targetUuid;
        this.targetName = targetName;

        this.punish = punish;
        this.mutes = mutes;
        this.freeze = freeze;
        this.jail = jail;
        this.ranks = ranks;

        this.viewer = viewer;

        render();
    }

    private void render() {
        for (int i = 0; i < SIZE; i++) top.setItem(i, ItemStack.EMPTY);

        ItemStack head = item(Items.PLAYER_HEAD,
                "Punish: " + targetName,
                "Select an action");

        top.setItem(4, head);

        top.setItem(10, item(Items.PAPER, "Warn", "Send warning"));
        top.setItem(11, item(Items.FEATHER, "Mute", "Open mute menu"));
        top.setItem(12, item(Items.IRON_SWORD, "Kick", "Kick player"));
        top.setItem(13, item(Items.TNT, "Ban", "Open ban menu"));
        top.setItem(14, item(Items.PACKED_ICE, "Freeze", "Toggle freeze"));
        top.setItem(15, item(Items.IRON_BARS, "Jail", "Jail player"));

        top.setItem(22, item(Items.BARRIER, "Close"));
    }

    private ItemStack item(net.minecraft.world.item.Item item, String name, String... lore) {
        ItemStack it = new ItemStack(item);

        it.set(DataComponents.CUSTOM_NAME,
                Component.literal(name).withStyle(ChatFormatting.YELLOW));

        if (lore != null && lore.length > 0) {
            List<Component> ls = new ArrayList<>();
            for (String s : lore) {
                ls.add(Component.literal(s).withStyle(ChatFormatting.GRAY));
            }
            it.set(DataComponents.LORE, new ItemLore(ls));
        }

        return it;
    }

    @Override
    public void clicked(int slot, int button, ClickType type, Player player) {
        if (!(player instanceof ServerPlayer sp)) return;


        ServerPlayer target = sp.server.getPlayerList().getPlayer(targetUuid);
        if (target == null) return;

        switch (slot) {

            case 10 -> {
                if (punish != null) {
                    punish.warn(sp, target, "Warned via GUI");
                }
            }

            case 11 -> {
                sp.sendSystemMessage(Component.literal("Mute GUI coming next"));
            }

            case 12 -> {
                if (punish != null) {
                    punish.kick(sp.server, sp, target, "Kicked via GUI");
                }
            }

            case 13 -> {
                sp.sendSystemMessage(Component.literal("Ban GUI coming next"));
            }

            case 14 -> {
                if (freeze != null) {
                    if (!freeze.isFrozen(targetUuid)) {
                        freeze.freeze(sp.server, sp, target, "GUI");
                    } else {
                        freeze.unfreeze(sp.server, sp, targetUuid, target.getGameProfile().getName(), "GUI");
                    }
                }
            }

            case 15 -> {
                if (jail != null) {
                    jail.jail(sp.server, sp, target, "default", 300L, "Jailed via GUI");
                }
            }

            case 22 -> sp.closeContainer();
        }


    }


    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    public static net.minecraft.world.MenuProvider provider(UUID uuid,
                                                            String name,
                                                            PunishmentManager punish,
                                                            MuteManager mutes,
                                                            FreezeService freeze,
                                                            JailService jail,
                                                            RankService ranks,
                                                            ServerPlayer viewer) {

        return new net.minecraft.world.SimpleMenuProvider(
                (id, inv, ply) -> new PunishMenu(id, inv, uuid, name, punish, mutes, freeze, jail, ranks, viewer),
                Component.literal("Punish: " + name)
        );
    }
}