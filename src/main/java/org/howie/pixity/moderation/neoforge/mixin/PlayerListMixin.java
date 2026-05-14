package org.howie.pixity.moderation.neoforge.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.server.players.PlayerList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public class PlayerListMixin {

    @Inject(
            method = "broadcastSystemMessage(Lnet/minecraft/network/chat/Component;Z)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void pixity$blockJoinLeave(Component message, boolean overlay, CallbackInfo ci) {

        String msg = message.getString();

        if (msg.endsWith("joined the game")
                || msg.endsWith("left the game")) {
            ci.cancel();
        }
    }
}