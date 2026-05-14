package org.howie.pixity.moderation.neoforge.crate;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class CrateService {

    private static final Random RANDOM = new Random();

    public static CrateManager.Reward roll(CrateManager.Crate crate) {

        int totalWeight = 0;

        for (var r : crate.rewards) {
            totalWeight += r.weight;
        }

        int roll = RANDOM.nextInt(totalWeight);

        int current = 0;

        for (var r : crate.rewards) {
            current += r.weight;

            if (roll < current) {
                return r;
            }
        }

        return crate.rewards.get(0);
    }

    public static void giveReward(ServerPlayer player, CrateManager.Reward reward) {

        if (reward.type.equalsIgnoreCase("command")) {

            if (reward.commands != null) {
                for (String cmdRaw : reward.commands) {

                    String cmd = cmdRaw.replace("%player%", player.getName().getString());

                    player.server.getCommands().performPrefixedCommand(
                            player.server.createCommandSourceStack(),
                            cmd
                    );
                }
            }
        }

        else if (reward.type.equalsIgnoreCase("item")) {

            try {

                var item =
                        net.minecraft.core.registries.BuiltInRegistries.ITEM.get(
                                net.minecraft.resources.ResourceLocation.parse(
                                        reward.item
                                )
                        );

                if (item == null) {
                    return;
                }

                ItemStack stack =
                        new ItemStack(
                                item,
                                Math.max(1, reward.amount)
                        );



                if (
                        reward.displayName != null
                                &&
                                !reward.displayName.isBlank()
                ) {

                    String id =
                            reward.item.toLowerCase();

                    boolean gear =

                            id.contains("helmet")
                                    ||
                                    id.contains("chestplate")
                                    ||
                                    id.contains("leggings")
                                    ||
                                    id.contains("boots")
                                    ||
                                    id.contains("sword")
                                    ||
                                    id.contains("pickaxe")
                                    ||
                                    id.contains("axe")
                                    ||
                                    id.contains("shovel")
                                    ||
                                    id.contains("hoe")
                                    ||
                                    id.contains("fishing_rod");

                    if (gear) {

                        stack.set(

                                net.minecraft.core.component.DataComponents.CUSTOM_NAME,

                                org.howie.pixity.moderation.chat.CachedText.of(
                                        reward.displayName
                                )
                        );
                    }
                }



                if (
                        reward.enchants != null
                                &&
                                !reward.enchants.isEmpty()
                ) {

                    for (var enchant : reward.enchants.entrySet()) {

                        try {

                            var enchantId =
                                    enchant.getKey()
                                            .replace("minecraft:", "");

                            switch (enchantId) {

                                case "protection" -> stack.enchant(
                                        player.registryAccess()
                                                .lookupOrThrow(
                                                        net.minecraft.core.registries.Registries.ENCHANTMENT
                                                )
                                                .getOrThrow(
                                                        net.minecraft.world.item.enchantment.Enchantments.PROTECTION
                                                ),

                                        enchant.getValue()
                                );

                                case "unbreaking" -> stack.enchant(
                                        player.registryAccess()
                                                .lookupOrThrow(
                                                        net.minecraft.core.registries.Registries.ENCHANTMENT
                                                )
                                                .getOrThrow(
                                                        net.minecraft.world.item.enchantment.Enchantments.UNBREAKING
                                                ),

                                        enchant.getValue()
                                );

                                case "sharpness" -> stack.enchant(
                                        player.registryAccess()
                                                .lookupOrThrow(
                                                        net.minecraft.core.registries.Registries.ENCHANTMENT
                                                )
                                                .getOrThrow(
                                                        net.minecraft.world.item.enchantment.Enchantments.SHARPNESS
                                                ),

                                        enchant.getValue()
                                );

                                case "efficiency" -> stack.enchant(
                                        player.registryAccess()
                                                .lookupOrThrow(
                                                        net.minecraft.core.registries.Registries.ENCHANTMENT
                                                )
                                                .getOrThrow(
                                                        net.minecraft.world.item.enchantment.Enchantments.EFFICIENCY
                                                ),

                                        enchant.getValue()
                                );

                                case "lure" -> stack.enchant(
                                        player.registryAccess()
                                                .lookupOrThrow(
                                                        net.minecraft.core.registries.Registries.ENCHANTMENT
                                                )
                                                .getOrThrow(
                                                        net.minecraft.world.item.enchantment.Enchantments.LURE
                                                ),

                                        enchant.getValue()
                                );
                            }

                        } catch (Exception ignored) {
                        }
                    }
                }



                if (
                        reward.enchants != null
                                &&
                                !reward.enchants.isEmpty()
                ) {

                    stack.set(
                            net.minecraft.core.component.DataComponents.ENCHANTMENT_GLINT_OVERRIDE,
                            true
                    );
                }

                player.getInventory().add(stack);

            } catch (Exception e) {

                System.out.println(
                        "[Pixity] Invalid item: "
                                + reward.item
                );

                e.printStackTrace();
            }
        }


        broadcastIfNeeded(player, reward);
    }

    private static void broadcastIfNeeded(ServerPlayer player, CrateManager.Reward reward) {

        if (reward.rarity == null) return;

        var rarity = CrateManager.getRarity(reward.rarity);

        if (rarity == null || !rarity.broadcast) return;

        String color = rarity.color != null ? rarity.color : "&6";

        var msg = org.howie.pixity.moderation.chat.CachedText.of(
                "&e&lCRATES &7&l➤" + color + "&l" + player.getName().getString()
                        + " won a " + reward.rarity.toUpperCase() + " reward!"
        );

        player.server.getPlayerList().broadcastSystemMessage(msg, false);
    }
}