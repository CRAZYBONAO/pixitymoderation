package org.howie.pixity.moderation.neoforge.inspect;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

public final class InspectCommands {

    private final InspectService svc;

    public InspectCommands(final InspectService svc) {
        this.svc = svc;
    }

    public void register(CommandDispatcher<CommandSourceStack> d) {

        d.register(Commands.literal("inspect")

                .requires(src -> {
                    if (!(src.getEntity() instanceof ServerPlayer p)) return false;
                    return src.hasPermission(2) || svc.canUse(p);
                })

                .executes(ctx -> {
                    ServerPlayer p = ctx.getSource().getPlayerOrException();

                    boolean now = svc.toggle(p.getUUID());

                    ctx.getSource().sendSuccess(() ->
                            LegacyAmpersand.parse(now
                                    ? "&aInspect enabled."
                                    : "&cInspect disabled."
                            ), false);

                    return 1;
                })

                .then(Commands.literal("on")
                        .executes(ctx -> {
                            ServerPlayer p = ctx.getSource().getPlayerOrException();

                            svc.setInspecting(p.getUUID(), true);

                            ctx.getSource().sendSuccess(() ->
                                            LegacyAmpersand.parse("&aInspect enabled."),
                                    false
                            );

                            return 1;
                        })
                )

                .then(Commands.literal("off")
                        .executes(ctx -> {
                            ServerPlayer p = ctx.getSource().getPlayerOrException();

                            svc.setInspecting(p.getUUID(), false);

                            ctx.getSource().sendSuccess(() ->
                                            LegacyAmpersand.parse("&cInspect disabled."),
                                    false
                            );

                            return 1;
                        })
                )
        );
    }
}