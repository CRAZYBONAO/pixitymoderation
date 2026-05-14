package org.howie.pixity.moderation.neoforge.staff;

import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.core.component.DataComponents;

import org.howie.pixity.moderation.neoforge.state.PlayerStateManager;
import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class StaffModeService {


    public static final String PERM_STAFFMODE = "pixity.staffmode";
    public static final String PERM_TOOL = "pixity.staffmode.tool";

    private final PlayerStateManager states;
    private final RankService ranks;

    private final Map<UUID, Snapshot> enabled = new ConcurrentHashMap<>();

    public StaffModeService(final PlayerStateManager states,
                            final RankService ranks) {
        this.states = states;
        this.ranks = ranks;
    }

    public boolean isEnabled(final UUID u) {
        return u != null && enabled.containsKey(u);
    }

    public boolean toggle(final MinecraftServer server, final ServerPlayer p) {
        if (p == null) return false;

        if (ranks == null || !ranks.hasPerm(p, PERM_STAFFMODE)) {
            p.sendSystemMessage(LegacyAmpersand.parse("&c&lSTAFFMODE &7&l➤ &cError! No permission."));
            return false;
        }

        UUID u = p.getUUID();

        if (enabled.containsKey(u)) {
            disable(server, p);
            return false;
        } else {
            enable(server, p);
            return true;
        }
    }

    private void enable(final MinecraftServer server, final ServerPlayer p) {
        UUID u = p.getUUID();
        Snapshot s = new Snapshot();

        s.wasVanish = states.isVanished(u);
        s.wasFly = states.isFlying(u);
        s.wasGod = states.isGod(u);

        s.dim = p.level().dimension().location().toString();
        s.x = p.getX();
        s.y = p.getY();
        s.z = p.getZ();
        s.yRot = p.getYRot();
        s.xRot = p.getXRot();

        try {
            if (p.gameMode != null) {
                s.gamemode = p.gameMode.getGameModeForPlayer();
            }
        } catch (Throwable ignored) {}

        s.inv = saveInventory(p.getInventory());
        enabled.put(u, s);

        if (!s.wasVanish) states.toggleVanish(server, p);
        if (!s.wasFly) states.toggleFly(p);

        giveStaffKit(p);

        p.sendSystemMessage(LegacyAmpersand.parse("&c&lSTAFFMODE &7&l➤ &eStaff Mode: &aON"));
    }

    private void disable(final MinecraftServer server, final ServerPlayer p) {
        UUID u = p.getUUID();
        Snapshot s = enabled.remove(u);
        if (s == null) return;

        restoreInventory(p.getInventory(), s.inv);

        try {
            if (server != null) {
                ResourceKey<net.minecraft.world.level.Level> key =
                        ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(s.dim));

                var level = server.getLevel(key);
                if (level != null) {
                    p.teleportTo(level, s.x, s.y, s.z, s.yRot, s.xRot);
                }
            }
        } catch (Throwable ignored) {}

        try {
            if (s.gamemode != null && p.gameMode != null) {
                p.setGameMode(s.gamemode);
            }
        } catch (Throwable ignored) {}

        if (!s.wasVanish && states.isVanished(u)) states.toggleVanish(server, p);
        if (!s.wasFly && states.isFlying(u)) states.toggleFly(p);

        p.sendSystemMessage(LegacyAmpersand.parse("&c&lSTAFFMODE &7&l➤ &eStaff Mode: &cOFF"));
    }

    private static CompoundTag saveInventory(final Inventory inv) {
        CompoundTag tag = new CompoundTag();
        try {
            ListTag list = new ListTag();
            inv.save(list);
            tag.put("inv", list);
            tag.putInt("selected", inv.selected);
        } catch (Throwable ignored) {}
        return tag;
    }

    private static void restoreInventory(final Inventory inv, final CompoundTag tag) {
        try {
            inv.clearContent();
            if (tag != null && tag.contains("inv")) {
                inv.load(tag.getList("inv", 10));
            }
            if (tag != null && tag.contains("selected")) {
                inv.selected = tag.getInt("selected");
            }
        } catch (Throwable ignored) {}
    }

    private static void giveStaffKit(final ServerPlayer p) {
        Inventory inv = p.getInventory();
        inv.clearContent();

        inv.setItem(0, named(new ItemStack(Items.BLAZE_ROD), "§bFreeze Tool"));
        inv.setItem(1, named(new ItemStack(Items.COMPASS), "§cTeleport To"));
        inv.setItem(2, named(new ItemStack(Items.LEAD), "§9Bring Here"));
        inv.setItem(3, named(new ItemStack(Items.PAPER), "§eHistory"));
        inv.setItem(4, named(new ItemStack(Items.BOOK), "§aInspect"));

        inv.selected = 0;
    }

    private static ItemStack named(final ItemStack it, final String name) {
        it.set(DataComponents.CUSTOM_NAME, Component.literal(name));
        return it;
    }

    private static final class Snapshot {
        boolean wasVanish;
        boolean wasFly;
        boolean wasGod;

        String dim;
        double x, y, z;
        float yRot, xRot;
        GameType gamemode;

        CompoundTag inv;
    }


}
