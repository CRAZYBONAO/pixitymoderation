package org.howie.pixity.moderation.neoforge.shop;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.Level;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;
import org.howie.pixity.moderation.neoforge.rank.RankService;

import java.util.HashMap;
import java.util.Map;

public class SellWandListener {

    private final ShopService shopService;
    private final EconomyBridge econ;
    private final RankService rankService;

    public SellWandListener(ShopService shopService, EconomyBridge econ, RankService rankService) {
        this.shopService = shopService;
        this.econ = econ;
        this.rankService = rankService;
    }

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent.RightClickBlock e) {

        if (!(e.getEntity() instanceof ServerPlayer player)) return;

        ItemStack held = player.getMainHandItem();

        if (!SellWandService.isWand(held)) return;

        Level level = player.level();

        BlockEntity be = level.getBlockEntity(e.getPos());
        if (be == null) return;

        net.minecraft.world.Container container = null;



        if (be instanceof net.minecraft.world.level.block.entity.ChestBlockEntity chest) {

            var state = chest.getBlockState();
            var block = state.getBlock();

            if (block instanceof net.minecraft.world.level.block.ChestBlock chestBlock) {

                var type = state.getValue(net.minecraft.world.level.block.ChestBlock.TYPE);

                if (type != net.minecraft.world.level.block.state.properties.ChestType.SINGLE) {

                    var direction = state.getValue(net.minecraft.world.level.block.ChestBlock.FACING);

                    net.minecraft.core.BlockPos otherPos =
                            type == net.minecraft.world.level.block.state.properties.ChestType.LEFT
                                    ? e.getPos().relative(direction.getClockWise())
                                    : e.getPos().relative(direction.getCounterClockWise());

                    BlockEntity otherBe = level.getBlockEntity(otherPos);

                    if (otherBe instanceof net.minecraft.world.level.block.entity.ChestBlockEntity otherChest) {

                        container = new net.minecraft.world.CompoundContainer(chest, otherChest);

                    }
                }
            }

            if (container == null) {
                container = chest;
            }
        }

        if (container == null && be instanceof RandomizableContainerBlockEntity rc) {
            container = rc;
        }

        if (container == null) return;

        double total = 0;

        Map<String, Integer> soldItems = new HashMap<>();


        double wandMultiplier = SellWandService.getMultiplier(held);

        double totalMultiplier = BoostService.getTotalMultiplier(player, wandMultiplier, rankService);

        double rankMultiplier = getRankMultiplier(player);
        double boostMultiplier = getBoostMultiplier(player);
        double globalBoost = GlobalBoostService.getMultiplier();

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);

            if (stack.isEmpty()) continue;

            double price = getSellPrice(stack);

            if (price <= 0) continue;

            int amount = stack.getCount();

            double base = price * amount;
            double finalValue = base * totalMultiplier;

            total += finalValue;

            String itemName = stack.getHoverName().getString();




            soldItems.put(itemName,
                    soldItems.getOrDefault(itemName, 0) + amount);

            container.setItem(i, ItemStack.EMPTY);
        }

        if (total <= 0) return;



        player.sendSystemMessage(LegacyAmpersand.parse("&e&lSELLWANDS &7>> &eSold:"));

        for (Map.Entry<String, Integer> entry : soldItems.entrySet()) {

            player.sendSystemMessage(
                    LegacyAmpersand.parse(
                            "&7- &c" + entry.getValue() + " " + entry.getKey()
                    )
            );
        }

        player.sendSystemMessage(Component.literal(""));

        player.sendSystemMessage(
                LegacyAmpersand.parse(
                        "&eTotal: &a$" + (int) total +
                                " &7(&fWand &c" + wandMultiplier + "x" +
                                " &7| Rank &b" + rankMultiplier + "x" +
                                " &7| Boost &d" + boostMultiplier + "x" +
                                " &7| Global &6" + globalBoost + "x" +
                                " &7→ &a" + totalMultiplier + "x&7)"
                )
        );

        econ.give(player, total, "money");

        int uses = SellWandService.getUses(held) - 1;

        if (uses <= 0) {
            held.shrink(1);
            return;
        }

        SellWandService.setUses(held, uses);

        e.setCanceled(true);
    }



    private double getRankMultiplier(ServerPlayer player) {
        if (rankService.hasPerm(player, "group.legendary")) return 2.0;
        if (rankService.hasPerm(player, "group.master")) return 1.75;
        if (rankService.hasPerm(player, "group.mystic")) return 1.5;
        if (rankService.hasPerm(player, "group.elite")) return 1.25;
        if (rankService.hasPerm(player, "group.shiny")) return 1.1;
        return 1.0;
    }

    private double getBoostMultiplier(ServerPlayer player) {
        if (rankService.hasPerm(player, "pixity.money.boost.2.5")) return 2.5;
        if (rankService.hasPerm(player, "pixity.money.boost.2")) return 2.0;
        if (rankService.hasPerm(player, "pixity.money.boost.1.75")) return 1.75;
        if (rankService.hasPerm(player, "pixity.money.boost.1.5")) return 1.5;
        if (rankService.hasPerm(player, "pixity.money.boost.1.25")) return 1.25;
        return 1.0;
    }





    private double getSellPrice(ItemStack stack) {

        for (Shop shop : shopService.getAllShops()) {
            for (ShopItem item : shop.items) {

                if (stack.getItem().toString().equals(item.item)) {
                    return item.sell;
                }
            }
        }

        return 0;
    }
}