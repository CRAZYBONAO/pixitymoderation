package org.howie.pixity.moderation.neoforge.alts.smart;

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

import org.howie.pixity.moderation.neoforge.alts.AltsMenu;
import org.howie.pixity.moderation.neoforge.alts.AltsService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;
import org.howie.pixity.moderation.neoforge.tp.TpService;
import org.howie.pixity.moderation.neoforge.util.NameResolver;

import java.util.*;

public final class SmartAltsSuggestMenu extends ChestMenu {

    private static final int SIZE = 27;
    private final SimpleContainer top;

    private final MinecraftServer server;
    private final TpService perms;
    private final AltsService alts;
    private final SmartAltService smart;

    private final ServerPlayer viewer;
    private final ServerPlayer target;

    private List<UUID> suggestions = List.of();

    public SmartAltsSuggestMenu(int id, Inventory inv,
                                MinecraftServer server, TpService perms,
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

        ItemStack head = new ItemStack(Items.SPYGLASS);
        head.set(DataComponents.CUSTOM_NAME,
                LegacyAmpersand.parse("&b&lPossible Alts"));

        List<Component> lore = new ArrayList<>();
        lore.add(LegacyAmpersand.parse("&7Target: &f" + target.getGameProfile().getName()));
        head.set(DataComponents.LORE, new ItemLore(lore));

        top.setItem(4, head);

        ItemStack back = new ItemStack(Items.OAK_DOOR);
        back.set(DataComponents.CUSTOM_NAME, LegacyAmpersand.parse("&cBack"));
        top.setItem(22, back);

        Set<UUID> already = new HashSet<>(alts.altsOf(target.getUUID()));
        suggestions = smart.suggest(target.getUUID(), already);

        int slot = 10;
        for (UUID u : suggestions) {
            if (slot > 16) break;

            String name = NameResolver.nameOrUuid(server, u);
            if (name == null) name = u.toString();

            ItemStack it = new ItemStack(Items.PLAYER_HEAD);
            it.set(DataComponents.CUSTOM_NAME,
                    LegacyAmpersand.parse("&e" + name));

            List<Component> ll = new ArrayList<>();
            ll.add(LegacyAmpersand.parse("&8UUID: &7" + u));
            ll.add(LegacyAmpersand.parse("&aClick to link"));

            it.set(DataComponents.LORE, new ItemLore(ll));

            top.setItem(slot++, it);
        }
    }

    @Override
    public void clicked(int slot, int button, ClickType type, Player player) {
        if (!(player instanceof ServerPlayer sp)) return;

        if (slot == 22) {
            sp.openMenu(AltsMenu.provider(server, perms, alts, smart, sp, target));
            return;
        }

        if (slot >= 10 && slot <= 16) {
            int idx = slot - 10;

            if (idx < suggestions.size()) {
                UUID other = suggestions.get(idx);
                alts.link(server, sp, target.getUUID(), other);

                sp.sendSystemMessage(LegacyAmpersand.parse("&aLinked suggested alt."));
                sp.openMenu(AltsMenu.provider(server, perms, alts, smart, sp, target));
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    public static net.minecraft.world.MenuProvider provider(
            MinecraftServer server, TpService perms,
            AltsService alts, SmartAltService smart,
            ServerPlayer viewer, ServerPlayer target) {

        return new net.minecraft.world.SimpleMenuProvider(
                (id, inv, ply) -> new SmartAltsSuggestMenu(id, inv, server, perms, alts, smart, viewer, target),
                LegacyAmpersand.parse("&cPossible Alts")
        );
    }
}