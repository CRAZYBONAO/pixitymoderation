package org.howie.pixity.moderation.neoforge.voucher;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class VoucherCommand {

    private static final org.howie.pixity.moderation.neoforge.rank.RankService RANKS =
            new org.howie.pixity.moderation.neoforge.rank.RankService();

    private static boolean has(ServerPlayer p, String perm) {
        return p != null && (
                RANKS.hasPerm(p, perm)
                        || RANKS.hasPerm(p, "pixity.admin")
        );
    }

    public static void register(CommandDispatcher<CommandSourceStack> d) {

        d.register(Commands.literal("vouchers")

                .requires(src -> {





                    if (src.getEntity() == null) {
                        return true;
                    }





                    if (src.getEntity() instanceof ServerPlayer p) {
                        return has(p, "pixity.voucher");
                    }

                    return false;
                })




                .then(Commands.literal("create")
                        .requires(src -> src.getEntity() instanceof ServerPlayer p &&
                                has(p, "pixity.voucher.create"))
                        .then(Commands.argument("name", StringArgumentType.string())
                                .then(Commands.argument("data", StringArgumentType.greedyString())
                                        .executes(ctx -> {

                                            ServerPlayer player = ctx.getSource().getPlayerOrException();

                                            String name = StringArgumentType.getString(ctx, "name").toLowerCase();
                                            String data = StringArgumentType.getString(ctx, "data");

                                            try {

                                                String[] split = data.split("\\|\\|", 3);

                                                if (split.length < 2) {
                                                    ctx.getSource().sendFailure(Component.literal(
                                                            "§cUsage: /vouchers create <name> \"display||command||item/hand\""
                                                    ));
                                                    return 0;
                                                }

                                                String display = split[0];
                                                String cmd = split[1];
                                                String itemArg = split.length >= 3 ? split[2] : "hand";

                                                ItemStack baseItem;

                                                if (itemArg.equalsIgnoreCase("hand")) {

                                                    ItemStack held = player.getMainHandItem();

                                                    if (held == null || held.isEmpty()) {
                                                        ctx.getSource().sendFailure(Component.literal("§cHold an item in your hand."));
                                                        return 0;
                                                    }

                                                    baseItem = held.copy();

                                                } else {
                                                    baseItem = new ItemStack(net.minecraft.world.item.Items.PAPER);
                                                }

                                                VoucherManager.create(name, display, cmd, baseItem);

                                                ctx.getSource().sendSuccess(() ->
                                                        Component.literal("§aCreated voucher: " + name), true);

                                                return 1;

                                            } catch (Exception e) {
                                                e.printStackTrace();

                                                ctx.getSource().sendFailure(Component.literal("§cError creating voucher. Check console."));
                                                return 0;
                                            }
                                        })
                                )
                        )
                )




                .then(Commands.literal("give")
                        .requires(src -> {





                            if (src.getEntity() == null) {
                                return true;
                            }





                            if (src.getEntity() instanceof ServerPlayer p) {
                                return has(p, "pixity.voucher.give");
                            }

                            return false;
                        })
                        .then(Commands.argument("name", StringArgumentType.string())
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(ctx -> {

                                            String name = StringArgumentType.getString(ctx, "name").toLowerCase();
                                            ServerPlayer target = EntityArgument.getPlayer(ctx, "player");

                                            var data = VoucherManager.get(name);

                                            if (data == null) {
                                                ctx.getSource().sendFailure(Component.literal("§cVoucher not found."));
                                                return 0;
                                            }




                                            ItemStack item = VoucherService.buildItem(
                                                    data.item,
                                                    name,
                                                    data.display,
                                                    data.command
                                            );

                                            target.getInventory().add(item);

                                            ctx.getSource().sendSuccess(() ->
                                                    Component.literal("§aGave voucher " + name + " to " + target.getName().getString()), true);

                                            return 1;
                                        })
                                )
                        )
                )




                .then(Commands.literal("list")
                        .requires(src -> src.getEntity() instanceof ServerPlayer p &&
                                has(p, "pixity.voucher.list"))
                        .executes(ctx -> {

                            var list = VoucherManager.getAll();

                            if (list.isEmpty()) {
                                ctx.getSource().sendFailure(Component.literal("§cNo vouchers exist."));
                                return 0;
                            }

                            ctx.getSource().sendSuccess(() ->
                                    Component.literal("§eVouchers: §f" + String.join(", ", list)), false);

                            return 1;
                        })
                )




                .then(Commands.literal("delete")
                        .requires(src -> src.getEntity() instanceof ServerPlayer p &&
                                has(p, "pixity.voucher.delete"))
                        .then(Commands.argument("name", StringArgumentType.string())
                                .executes(ctx -> {

                                    String name = StringArgumentType.getString(ctx, "name").toLowerCase();

                                    boolean removed = VoucherManager.delete(name);

                                    if (!removed) {
                                        ctx.getSource().sendFailure(Component.literal("§cVoucher not found."));
                                        return 0;
                                    }

                                    ctx.getSource().sendSuccess(() ->
                                            Component.literal("§aDeleted voucher: " + name), true);

                                    return 1;
                                })
                        )
                )




                .then(Commands.literal("reload")
                        .requires(src -> src.getEntity() instanceof ServerPlayer p &&
                                has(p, "pixity.voucher.reload"))
                        .executes(ctx -> {

                            VoucherManager.load();

                            ctx.getSource().sendSuccess(() ->
                                    Component.literal("§aVouchers reloaded."), true);

                            return 1;
                        })
                )
        );
    }
}