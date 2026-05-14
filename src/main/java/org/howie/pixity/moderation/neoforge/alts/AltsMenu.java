package org.howie.pixity.moderation.neoforge.alts;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
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

import org.howie.pixity.moderation.neoforge.alts.smart.SmartAltService;
import org.howie.pixity.moderation.neoforge.alts.smart.SmartAltsSuggestMenu;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;
import org.howie.pixity.moderation.neoforge.tp.TpService;
import org.howie.pixity.moderation.neoforge.tp.gui.ChatPromptService;
import org.howie.pixity.moderation.neoforge.util.NameResolver;

import java.util.*;

public final class AltsMenu extends ChestMenu {

    private static final int SIZE = 27;
    private final SimpleContainer top;

    private final MinecraftServer server;
    private final TpService perms;
    private final AltsService alts;
    private final SmartAltService smart;

    private final ServerPlayer viewer;
    private final ServerPlayer target;

    public AltsMenu(int id, Inventory inv, MinecraftServer server, TpService perms,
                    AltsService alts, SmartAltService smart,
                    ServerPlayer viewer, ServerPlayer target) {

        super(MenuType.GENERIC_9x3, id, inv, new SimpleContainer(SIZE), 3);

        this.top = (SimpleContainer) this.getContainer();
        this.server = server;
        this.perms = perms;
        this.alts = alts;
        this.smart = smart;
        this.viewer = viewer;
        this.target = target;

        render();
    }

    private void render() {
        for (int i = 0; i < SIZE; i++) top.setItem(i, ItemStack.EMPTY);

        ItemStack head = new ItemStack(Items.NAME_TAG);
        head.set(DataComponents.CUSTOM_NAME,
                LegacyAmpersand.parse("&b&lAlts for &e" + target.getGameProfile().getName()));

        Set<UUID> set = alts.altsOf(target.getUUID());

        List<Component> lore = new ArrayList<>();
        lore.add(LegacyAmpersand.parse("&7Linked: &f" + set.size()));
        head.set(DataComponents.LORE, new ItemLore(lore));

        top.setItem(4, head);

        top.setItem(10, button(Items.ANVIL, "&aLink alt"));
        top.setItem(11, button(Items.BARRIER, "&eUnlink alt"));
        top.setItem(20, button(Items.SPYGLASS, "&bPossible Alts"));
        top.setItem(22, button(Items.OAK_DOOR, "&cBack"));

        int slot = 12;
        for (UUID u : set) {
            if (slot > 16) break;

            String name = NameResolver.nameOrUuid(server, u);
            if (name == null) name = u.toString();

            ItemStack it = new ItemStack(Items.PLAYER_HEAD);
            it.set(DataComponents.CUSTOM_NAME,
                    LegacyAmpersand.parse("&e" + name));

            List<Component> ll = new ArrayList<>();
            ll.add(LegacyAmpersand.parse("&8UUID: &7" + u));

            it.set(DataComponents.LORE, new ItemLore(ll));

            top.setItem(slot++, it);
        }
    }

    @Override
    public void clicked(int slotId, int button, ClickType type, Player player) {
        if (!(player instanceof ServerPlayer sp)) return;

        switch (slotId) {

            case 10 -> {
                sp.closeContainer();
                ChatPromptService.prompt(sp, "Enter player:", name -> {
                    UUID other = NameResolver.uuid(server, name);
                    if (other != null) alts.link(server, sp, target.getUUID(), other);
                    sp.openMenu(provider(server, perms, alts, smart, sp, target));
                });
            }

            case 11 -> {
                sp.closeContainer();
                ChatPromptService.prompt(sp, "Enter player:", name -> {
                    UUID other = NameResolver.uuid(server, name);
                    if (other != null) alts.unlink(server, sp, target.getUUID(), other);
                    sp.openMenu(provider(server, perms, alts, smart, sp, target));
                });
            }

            case 20 -> sp.openMenu(SmartAltsSuggestMenu.provider(server, perms, alts, smart, sp, target));
            case 22 -> sp.closeContainer();
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    private static ItemStack button(net.minecraft.world.item.Item item, String name) {
        ItemStack it = new ItemStack(item);
        it.set(DataComponents.CUSTOM_NAME, LegacyAmpersand.parse(name));
        return it;
    }

    public static net.minecraft.world.MenuProvider provider(
            MinecraftServer server, TpService perms,
            AltsService alts, SmartAltService smart,
            ServerPlayer viewer, ServerPlayer target) {

        return new net.minecraft.world.SimpleMenuProvider(
                (id, inv, ply) -> new AltsMenu(id, inv, server, perms, alts, smart, viewer, target),
                LegacyAmpersand.parse("&c&lALTS")
        );
    }
}