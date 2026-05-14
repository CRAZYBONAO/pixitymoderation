package org.howie.pixity.moderation.neoforge.state;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.chat.MuteManager;
import org.howie.pixity.moderation.neoforge.freeze.FreezeService;
import org.howie.pixity.moderation.neoforge.jail.JailService;
import org.howie.pixity.moderation.neoforge.punish.PunishmentManager;
import org.howie.pixity.moderation.neoforge.afk.AfkService;

import java.util.UUID;

public final class PlayerStateService {

    private final PlayerStateManager core;
    private final FreezeService freeze;
    private final JailService jail;
    private final MuteManager mutes;
    private final AfkService afk;

    public PlayerStateService(
            PlayerStateManager core,
            FreezeService freeze,
            JailService jail,
            MuteManager mutes,
            AfkService afk
    ) {
        this.core = core;
        this.freeze = freeze;
        this.jail = jail;
        this.mutes = mutes;
        this.afk = afk;
    }

    public boolean isVanished(UUID u) { return core.isVanished(u); }
    public boolean isFlying(UUID u) { return core.isFlying(u); }
    public boolean isGod(UUID u) { return core.isGod(u); }

    public boolean isFrozen(UUID u) { return freeze != null && freeze.isFrozen(u); }
    public boolean isJailed(UUID u) { return jail != null && jail.isJailed(u); }
    public boolean isMuted(UUID u) { return mutes != null && mutes.isMuted(u); }
    public boolean isAFK(UUID u) { return afk != null && afk.isAfk(u); }

    public boolean canMove(UUID u) {
        if (isFrozen(u)) return false;
        if (isJailed(u)) return false;
        return true;
    }

    public boolean canInteract(UUID u) {
        if (isFrozen(u)) return false;
        if (isJailed(u)) return false;
        return true;
    }

    public boolean canCommand(UUID u) {
        if (isFrozen(u)) return false;
        if (isJailed(u)) return false;
        return true;
    }

    public boolean canChat(UUID u) {
        if (isMuted(u)) return false;

        if (isFrozen(u)) {
            if (freeze != null && freeze.config() != null && freeze.config().blockChat) {
                return false;
            }
        }

        return true;
    }

    public void applyOnJoin(MinecraftServer server, ServerPlayer p) {
        core.applyAllOnJoin(server, p);
    }
}