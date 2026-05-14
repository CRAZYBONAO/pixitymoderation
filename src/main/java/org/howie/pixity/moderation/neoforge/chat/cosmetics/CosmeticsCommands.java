package org.howie.pixity.moderation.neoforge.chat.cosmetics;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

public class CosmeticsCommands {

    private final ChatCosmeticsService cosmetics;
    private final CosmeticsGui gui;

    public CosmeticsCommands(ChatCosmeticsService cosmetics,
                             CosmeticsGui gui) {
        this.cosmetics = cosmetics;
        this.gui = gui;
    }

    public void register(CommandDispatcher<CommandSourceStack> d) {



        d.register(Commands.literal("cosmetics")
                .requires(src -> src.getEntity() instanceof ServerPlayer)
                .executes(ctx -> {

                    ServerPlayer p =
                            (ServerPlayer) ctx.getSource().getEntity();

                    if (p == null) return 0;

                    gui.openMain(p);
                    return 1;
                })
        );



        d.register(Commands.literal("clearchatcolor")
                .requires(src -> src.getEntity() instanceof ServerPlayer)
                .executes(ctx -> {

                    ServerPlayer p =
                            (ServerPlayer) ctx.getSource().getEntity();

                    if (p == null) return 0;

                    cosmetics.clearChat(p.getUUID());

                    p.sendSystemMessage(
                            Component.literal("§9§lCOSMETICS §7>> §aChat color cleared.")
                    );

                    return 1;
                })
        );



        d.register(Commands.literal("clearchatgradient")
                .requires(src -> src.getEntity() instanceof ServerPlayer)
                .executes(ctx -> {

                    ServerPlayer p =
                            (ServerPlayer) ctx.getSource().getEntity();

                    if (p == null) return 0;

                    cosmetics.clearChat(p.getUUID());

                    p.sendSystemMessage(
                            Component.literal("§9§lCOSMETICS §7>> §aChat gradient cleared.")
                    );

                    return 1;
                })
        );



        d.register(Commands.literal("clearnamecolor")
                .requires(src -> src.getEntity() instanceof ServerPlayer)
                .executes(ctx -> {

                    ServerPlayer p =
                            (ServerPlayer) ctx.getSource().getEntity();

                    if (p == null) return 0;

                    cosmetics.clearName(p.getUUID());

                    p.sendSystemMessage(
                            Component.literal("§9§lCOSMETICS §7>> §aName color cleared.")
                    );

                    return 1;
                })
        );


        d.register(Commands.literal("clearnamegradient")
                .requires(src -> src.getEntity() instanceof ServerPlayer)
                .executes(ctx -> {

                    ServerPlayer p =
                            (ServerPlayer) ctx.getSource().getEntity();

                    if (p == null) return 0;

                    cosmetics.clearName(p.getUUID());

                    p.sendSystemMessage(
                            Component.literal("§9§lCOSMETICS §7>> §aName gradient cleared.")
                    );

                    return 1;
                })
        );



        d.register(Commands.literal("clearglow")
                .requires(src -> src.getEntity() instanceof ServerPlayer)
                .executes(ctx -> {

                    ServerPlayer p =
                            (ServerPlayer) ctx.getSource().getEntity();

                    if (p == null) return 0;

                    cosmetics.clearGlow(p);

                    p.sendSystemMessage(
                            Component.literal("§9§lCOSMETICS §7>> §aGlow cleared.")
                    );

                    return 1;
                })
        );
    }
}