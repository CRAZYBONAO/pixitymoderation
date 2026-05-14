package org.howie.pixity.moderation.neoforge.spawners;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

import java.util.ArrayList;
import java.util.List;


public class SpawnerCommands {

    private final RankService perms;

    public SpawnerCommands(RankService perms) {
        this.perms = perms;
    }

    public void register(CommandDispatcher<CommandSourceStack> d) {

        d.register(Commands.literal("spawners")

                .requires(src ->
                        !(src.getEntity() instanceof ServerPlayer p)
                                || perms.hasPerm(p, "pixity.spawners.list")
                                || perms.hasPerm(p, "pixity.spawners.admin")
                )

                .then(Commands.literal("list")
                        .executes(ctx -> {

                            List<String> mobs = new ArrayList<>();

                            for (ResourceLocation id : BuiltInRegistries.ENTITY_TYPE.keySet()) {
                                mobs.add(id.getPath());
                            }

                            mobs.sort(String::compareTo);

                            ctx.getSource().sendSuccess(() ->
                                    LegacyAmpersand.parse(
                                            "&6&lSPAWNERS &7&l➤ &eAvailable: &f"
                                                    + String.join("&7, &f", mobs)
                                    ), false);

                            return 1;
                        }))





                .then(Commands.literal("give")
                        .requires(src ->
                                !(src.getEntity() instanceof ServerPlayer p)
                                        || perms.hasPerm(p, "pixity.spawners.give")
                                        || perms.hasPerm(p, "pixity.spawners.admin")
                        )

                        .then(Commands.argument("player", StringArgumentType.word())

                                .then(Commands.argument("mob", StringArgumentType.word())

                                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))

                                                .executes(ctx -> {

                                                    ServerPlayer target =
                                                            ctx.getSource()
                                                                    .getServer()
                                                                    .getPlayerList()
                                                                    .getPlayerByName(
                                                                            StringArgumentType.getString(ctx, "player")
                                                                    );

                                                    if (target == null) return 0;

                                                    String mob =
                                                            StringArgumentType.getString(ctx, "mob");

                                                    int amount =
                                                            IntegerArgumentType.getInteger(ctx, "amount");

                                                    var item =
                                                            SpawnerAPI.create("minecraft:" + mob);

                                                    item.setCount(amount);

                                                    target.getInventory().add(item);

                                                    String nice =
                                                            mob.replace("_", " ");

                                                    nice =
                                                            Character.toUpperCase(nice.charAt(0))
                                                                    + nice.substring(1);

                                                    final int fAmount = amount;
                                                    final String fNice = nice;
                                                    final String fPlayer = target.getGameProfile().getName();

                                                    ctx.getSource().sendSuccess(() ->
                                                            LegacyAmpersand.parse(
                                                                    "&6&lSPAWNERS &7&l➤ &e"
                                                                            + fAmount + "x &c"
                                                                            + fNice
                                                                            + " spawner &7was given to &e"
                                                                            + fPlayer + "."
                                                            ), false);

                                                    return 1;
                                                })
                                        )

                                        .executes(ctx -> {

                                            ServerPlayer target =
                                                    ctx.getSource()
                                                            .getServer()
                                                            .getPlayerList()
                                                            .getPlayerByName(
                                                                    StringArgumentType.getString(ctx, "player")
                                                            );

                                            if (target == null) return 0;

                                            String mob =
                                                    StringArgumentType.getString(ctx, "mob");

                                            var item =
                                                    SpawnerAPI.create("minecraft:" + mob);

                                            target.getInventory().add(item);

                                            String nice =
                                                    mob.replace("_", " ");

                                            nice =
                                                    Character.toUpperCase(nice.charAt(0))
                                                            + nice.substring(1);

                                            final String fNice = nice;
                                            final String fPlayer = target.getGameProfile().getName();

                                            ctx.getSource().sendSuccess(() ->
                                                    LegacyAmpersand.parse(
                                                            "&6&lSPAWNERS &7&l➤ &e1x &c"
                                                                    + fNice
                                                                    + " spawner &7was given to &e"
                                                                    + fPlayer + "."
                                                    ), false);

                                            return 1;
                                        })
                                )
                        )
                )
        );
    }
}