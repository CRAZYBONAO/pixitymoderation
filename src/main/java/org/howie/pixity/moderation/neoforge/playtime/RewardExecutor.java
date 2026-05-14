package org.howie.pixity.moderation.neoforge.playtime;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import org.howie.pixity.moderation.neoforge.economy.CurrencyType;
import org.howie.pixity.moderation.neoforge.economy.EconomyService;

import java.util.List;

public final class RewardExecutor {

    private static EconomyService economy;

    public static void init(EconomyService econ) {
        economy = econ;
    }

    public static void execute(ServerPlayer player, List<String> rewards) {

        for (String reward : rewards) {

            try {

                if (reward.startsWith("command:")) {
                    runCommand(player, reward.substring(8));
                    continue;
                }

                if (reward.startsWith("tokens:")) {
                    add(player, CurrencyType.TOKENS, reward.substring(7));
                    continue;
                }

                if (reward.startsWith("money:")) {
                    add(player, CurrencyType.MONEY, reward.substring(6));
                    continue;
                }

                if (reward.startsWith("coins:")) {
                    add(player, CurrencyType.COINS, reward.substring(6));
                    continue;
                }

                if (reward.startsWith("item:")) {
                    giveItem(player, reward.substring(5));
                    continue;
                }

            } catch (Exception ignored) {}
        }
    }

    private static void runCommand(ServerPlayer player, String cmd) {

        String parsed =
                cmd.replace("%player%", player.getGameProfile().getName());

        CommandSourceStack source =
                player.server.createCommandSourceStack()
                        .withSuppressedOutput();

        player.server.getCommands().performPrefixedCommand(
                source,
                parsed
        );
    }

    private static void add(
            ServerPlayer player,
            CurrencyType type,
            String value
    ) {

        double amount = Double.parseDouble(value);

        economy.add(
                player.getUUID(),
                type,
                amount
        );
    }

    private static void giveItem(ServerPlayer player, String data) {

        String[] split = data.split(":");

        if (split.length < 2) return;

        String id = split[0] + ":" + split[1];

        int count = 1;

        if (split.length >= 3) {
            count = Integer.parseInt(split[2]);
        }

        ItemStack item =
                BuiltInRegistries.ITEM
                        .getOptional(ResourceLocation.parse(id))
                        .map(ItemStack::new)
                        .orElse(new ItemStack(Items.STONE));

        item.setCount(count);

        player.getInventory().add(item);
    }
}