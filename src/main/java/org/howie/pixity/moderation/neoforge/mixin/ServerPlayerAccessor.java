package org.howie.pixity.moderation.neoforge.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerPlayer.class)
public interface ServerPlayerAccessor {

    @Accessor("listName")
    void pixity$setListName(Component name);
}