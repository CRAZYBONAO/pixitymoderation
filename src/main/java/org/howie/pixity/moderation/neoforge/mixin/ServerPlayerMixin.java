package org.howie.pixity.moderation.neoforge.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.howie.pixity.moderation.chat.NickHolder;
import org.howie.pixity.moderation.neoforge.afk.AfkHolder;
import org.howie.pixity.moderation.neoforge.tab.TabFormatting;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {

    @Inject(
            method = "getTabListDisplayName",
            at = @At("HEAD"),
            cancellable = true
    )
    private void pixity$tabName(CallbackInfoReturnable<Component> cir) {

        ServerPlayer self = (ServerPlayer)(Object)this;

        if (NickHolder.INSTANCE == null) {
            return;
        }

        String nick =
                NickHolder.INSTANCE.getDisplayName(self);

        String full =
                TabFormatting.buildPrefix(self) + " " + nick;

        if (AfkHolder.INSTANCE != null &&
                AfkHolder.INSTANCE.isAfk(self.getUUID())) {

            full = "§8[§7AFK§8] §r" + full;
        }

        cir.setReturnValue(
                LegacyAmpersand.parse(full)
        );
    }
}