package org.howie.pixity.moderation.neoforge.state;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PlayerStateManager {


    public static final String PERM_VANISH = "pixity.vanish";
    public static final String PERM_VANISH_SEE = "pixity.vanish.see";
    public static final String PERM_FLY = "pixity.freefly";
    public static final String PERM_GOD = "pixity.god";

    private final SQLitePlayerStateStore store;
    private final RankService ranks;

    private final Set<UUID> vanished = ConcurrentHashMap.newKeySet();
    private final Set<UUID> flying = ConcurrentHashMap.newKeySet();
    private final Set<UUID> god = ConcurrentHashMap.newKeySet();

    public PlayerStateManager(final SQLitePlayerStateStore store, final RankService ranks) {
        this.store = store;
        this.ranks = ranks;

        PlayerStateData d = store.load();
        if (d.vanished != null) vanished.addAll(d.vanished);
        if (d.flying != null) flying.addAll(d.flying);
        if (d.god != null) god.addAll(d.god);
    }

    private boolean has(ServerPlayer p, String perm) {
        return ranks != null && ranks.hasPerm(p, perm);
    }

    public boolean isVanished(UUID u) { return u != null && vanished.contains(u); }
    public boolean isFlying(UUID u) { return u != null && flying.contains(u); }
    public boolean isGod(UUID u) { return u != null && god.contains(u); }

    public boolean toggleVanish(MinecraftServer server, ServerPlayer p) {
        if (!has(p, PERM_VANISH)) {
            p.sendSystemMessage(LegacyAmpersand.parse("&4&lPERMISSIONS &7&l➤> &cError! No permission."));
            return false;
        }

        UUID u = p.getUUID();
        boolean on;

        if (vanished.contains(u)) {
            vanished.remove(u);
            on = false;
            applyVanish(p, false);
            p.sendSystemMessage(LegacyAmpersand.parse("&2&lSTAFF &7&l➤ &aVanish: &cOFF"));
        } else {
            vanished.add(u);
            on = true;
            applyVanish(p, true);
            p.sendSystemMessage(LegacyAmpersand.parse("&2&lSTAFF &7&l➤ &aVanish: ON"));
        }

        refreshVisibility(server);
        persist();
        return on;
    }

    public boolean toggleFly(ServerPlayer p) {
        if (!has(p, PERM_FLY)) {
            p.sendSystemMessage(LegacyAmpersand.parse("&4&lPERMISSIONS &7&l➤> &cError! No permission."));
            return false;
        }

        UUID u = p.getUUID();
        boolean on;

        if (flying.contains(u)) {
            flying.remove(u);
            on = false;
            applyFly(p, false);
            p.sendSystemMessage(LegacyAmpersand.parse("&6&lFLIGHT &7&l➤ &aFly: &cOFF"));
        } else {
            flying.add(u);
            on = true;
            applyFly(p, true);
            p.sendSystemMessage(LegacyAmpersand.parse("&6&lFLIGHT &7&l➤ &aFly: ON"));
        }

        persist();
        return on;
    }

    public boolean toggleGod(ServerPlayer p) {
        if (!has(p, PERM_GOD)) {
            p.sendSystemMessage(LegacyAmpersand.parse("&4&lPERMISSIONS &7&l➤> &cError! No permission."));
            return false;
        }

        UUID u = p.getUUID();
        boolean on;

        if (god.contains(u)) {
            god.remove(u);
            on = false;
            applyGod(p, false);
            p.sendSystemMessage(LegacyAmpersand.parse("&6&lGOD &7&l➤ &aGod: &cOFF"));
        } else {
            god.add(u);
            on = true;
            applyGod(p, true);
            p.sendSystemMessage(LegacyAmpersand.parse("&6&lGOD &7&l➤ &aGod: ON"));
        }

        persist();
        return on;
    }

    public void applyAllOnJoin(MinecraftServer server, ServerPlayer p) {
        UUID u = p.getUUID();

        applyVanish(p, vanished.contains(u));
        applyFly(p, flying.contains(u));
        applyGod(p, god.contains(u));

        refreshVisibility(server);
    }

    private void refreshVisibility(MinecraftServer server) {
        if (server == null) return;

        for (ServerPlayer viewer : server.getPlayerList().getPlayers()) {
            for (ServerPlayer target : server.getPlayerList().getPlayers()) {

                if (viewer == target) continue;

                boolean vanished = isVanished(target.getUUID());
                boolean canSee = has(viewer, PERM_VANISH_SEE);

                try {
                    if (vanished && !canSee) {
                        viewer.connection.send(
                                new ClientboundRemoveEntitiesPacket(target.getId())
                        );
                    } else {
                        viewer.server.execute(() ->
                                viewer.serverLevel().getChunkSource().move(target)
                        );
                    }
                } catch (Throwable ignored) {}
            }
        }
    }

    public void applyVanish(ServerPlayer p, boolean on) {
        if (on) {
            p.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, Integer.MAX_VALUE, 0, false, false, false));
            p.setInvisible(true);
        } else {
            p.removeEffect(MobEffects.INVISIBILITY);
            p.setInvisible(false);
        }
    }

    public void applyFly(ServerPlayer p, boolean on) {
        boolean creativeOrSpec = p.isCreative() || p.isSpectator();
        var ab = p.getAbilities();

        if (on) {
            ab.mayfly = true;
        } else if (!creativeOrSpec) {
            ab.mayfly = false;
            ab.flying = false;
        }

        p.onUpdateAbilities();
    }

    public void applyGod(ServerPlayer p, boolean on) {
        p.setInvulnerable(on);
        if (on) {
            p.setHealth(p.getMaxHealth());
            p.getFoodData().setFoodLevel(20);
            p.getFoodData().setSaturation(20.0f);
        }
    }

    private void persist() {
        PlayerStateData d = new PlayerStateData();
        d.vanished.addAll(vanished);
        d.flying.addAll(flying);
        d.god.addAll(god);
        store.save(d);
    }


}
