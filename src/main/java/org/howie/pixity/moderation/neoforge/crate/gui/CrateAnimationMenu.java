package org.howie.pixity.moderation.neoforge.crate.gui;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.inventory.*;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.ItemLore;

import net.neoforged.neoforge.common.NeoForge;
import net.minecraft.server.MinecraftServer;

import org.howie.pixity.moderation.neoforge.crate.*;

import java.util.*;

public class CrateAnimationMenu {

    private static final int[] ROLL_SLOTS = {
            10,11,12,13,14,15,16
    };

    public static void open(ServerPlayer player, String crateId) {

        var crate = CrateManager.get(crateId);
        if (crate == null) return;

        SimpleContainer cont = new SimpleContainer(27);

        ItemStack filler = new ItemStack(Items.BLACK_STAINED_GLASS_PANE);
        filler.set(DataComponents.CUSTOM_NAME, Component.literal(""));

        for (int i = 0; i < 27; i++) {
            cont.setItem(i, filler.copy());
        }

        player.openMenu(new SimpleMenuProvider(
                (id, inv, p) -> new ChestMenu(MenuType.GENERIC_9x3, id, inv, cont, 3),
                Component.literal("Crate")
        ));

        startAnimation(player, cont, crateId);
    }

    private static void startAnimation(ServerPlayer player,
                                       SimpleContainer cont,
                                       String crateId) {

        var crate = CrateManager.get(crateId);
        if (crate == null) return;



        Random random = new Random();

        for (int slot : ROLL_SLOTS) {

            var reward =
                    crate.rewards.get(
                            random.nextInt(
                                    crate.rewards.size()
                            )
                    );

            cont.setItem(
                    slot,
                    createDisplayItem(reward)
            );
        }



        var finalReward = CrateService.roll(crate);

        final int totalSteps = 40;

        final int baseDelay = 2;

        runStep(
                player,
                cont,
                crate,
                finalReward,
                0,
                totalSteps,
                baseDelay
        );
    }

    private static void runStep(ServerPlayer player,
                                SimpleContainer cont,
                                CrateManager.Crate crate,
                                CrateManager.Reward finalReward,
                                int step,
                                int totalSteps,
                                int baseDelay) {

        if (player.containerMenu == null) return;


        for (int i = 0; i < ROLL_SLOTS.length - 1; i++) {
            cont.setItem(ROLL_SLOTS[i],
                    cont.getItem(ROLL_SLOTS[i + 1]));
        }



        var randomReward = crate.rewards.get(
                new Random().nextInt(crate.rewards.size())
        );

        cont.setItem(
                ROLL_SLOTS[ROLL_SLOTS.length - 1],
                createDisplayItem(randomReward)
        );

        cont.setChanged();

        player.containerMenu.broadcastChanges();

        player.playNotifySound(
                SoundEvents.STONE_BUTTON_CLICK_ON,
                net.minecraft.sounds.SoundSource.PLAYERS,
                0.5f,
                1.2f
        );


        if (step >= totalSteps) {

            player.playNotifySound(
                    net.minecraft.sounds.SoundEvents.PLAYER_LEVELUP,
                    net.minecraft.sounds.SoundSource.PLAYERS,
                    1f,
                    1f
            );

            cont.setItem(13, createDisplayItem(finalReward));

            CrateService.giveReward(player, finalReward);

            return;


        }

        int delay;



        if (step < 10) {

            delay = 3;
        }


        else if (step < 20) {

            delay = 5;
        }



        else if (step < 30) {

            delay = 7;
        }


        else {

            delay = 10 + ((step - 30) * 2);
        }


        player.getServer().tell(
                new net.minecraft.server.TickTask(
                        player.getServer().getTickCount() + delay,
                        () -> runStep(player, cont, crate, finalReward,
                                step + 1, totalSteps, baseDelay)
                )
        );
    }

    public static ItemStack createDisplayItem(
            CrateManager.Reward reward
    ) {

        try {

            ItemStack item;



            if (
                    "item".equalsIgnoreCase(
                            reward.type
                    )
            ) {

                var itemType =
                        net.minecraft.core.registries.BuiltInRegistries.ITEM.get(

                                net.minecraft.resources.ResourceLocation.parse(
                                        reward.item
                                )
                        );

                item =
                        new ItemStack(
                                itemType,
                                Math.max(1, reward.amount)
                        );



                if (
                        reward.enchants != null
                                &&
                                !reward.enchants.isEmpty()
                ) {

                    item.set(
                            DataComponents.ENCHANTMENT_GLINT_OVERRIDE,
                            true
                    );
                }
            }



            else {

                String icon =
                        reward.displayItem;

                if (
                        icon == null
                                ||
                                icon.isBlank()
                ) {

                    icon = "minecraft:paper";
                }

                var itemType =
                        net.minecraft.core.registries.BuiltInRegistries.ITEM.get(

                                net.minecraft.resources.ResourceLocation.parse(
                                        icon
                                )
                        );

                item =
                        new ItemStack(itemType);




                if (
                        reward.pokemonData != null
                                &&
                                !reward.pokemonData.isBlank()
                                &&
                                icon.equalsIgnoreCase(
                                        "cobblemon:pokemon_model"
                                )
                ) {

                    try {

                        var tag =
                                net.minecraft.nbt.TagParser.parseTag(
                                        reward.pokemonData
                                );

                        item.applyComponents(
                                net.minecraft.core.component.DataComponentPatch.CODEC
                                        .parse(net.minecraft.nbt.NbtOps.INSTANCE, tag)
                                        .result()
                                        .orElseThrow()
                        );

                    } catch (Exception e) {

                        System.out.println(
                                "[Pixity] Failed to parse pokemonData: "
                                        + reward.pokemonData
                        );

                        e.printStackTrace();
                    }
                }
                }


            if (
                    reward.customModelData != null
            ) {

                item.set(

                        DataComponents.CUSTOM_MODEL_DATA,

                        new net.minecraft.world.item.component.CustomModelData(
                                reward.customModelData
                        )
                );
            }



            var rarity =
                    CrateManager.getRarity(
                            reward.rarity
                    );

            String color =
                    rarity != null
                            ? rarity.color
                            : "&7";



            String display =
                    reward.displayName;

            if (
                    display == null
                            ||
                            display.isBlank()
            ) {

                if (
                        "item".equalsIgnoreCase(
                                reward.type
                        )
                ) {

                    display =
                            reward.item;
                }

                else {

                    display =
                            "Reward";
                }
            }

            item.set(
                    DataComponents.CUSTOM_NAME,

                    org.howie.pixity.moderation.chat.CachedText.of(

                            color + "&l" + display
                    )
            );



            java.util.List<Component> lore =
                    new java.util.ArrayList<>();


            if (
                    reward.enchants != null
                            &&
                            !reward.enchants.isEmpty()
            ) {

                for (var enchant : reward.enchants.entrySet()) {

                    String enchantName =
                            enchant.getKey()
                                    .replace("minecraft:", "")
                                    .replace("_", " ");

                    enchantName =
                            enchantName.substring(0, 1).toUpperCase()
                                    + enchantName.substring(1);

                    lore.add(

                            org.howie.pixity.moderation.chat.CachedText.of(

                                    "&7"
                                            + enchantName
                                            + " "
                                            + toRoman(
                                            enchant.getValue()
                                    )
                            )
                    );
                }

                lore.add(
                        Component.empty()
                );
            }



            lore.add(

                    org.howie.pixity.moderation.chat.CachedText.of(

                            color
                                    + "&l"
                                    + reward.rarity.toUpperCase()
                    )
            );

            item.set(
                    DataComponents.LORE,
                    new ItemLore(lore)
            );

            return item;

        } catch (Exception e) {

            e.printStackTrace();

            return new ItemStack(
                    Items.BARRIER
            );
        }
    }

    private static String toRoman(int num) {

        return switch (num) {

            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            case 6 -> "VI";
            case 7 -> "VII";
            case 8 -> "VIII";
            case 9 -> "IX";
            case 10 -> "X";

            default -> String.valueOf(num);
        };
    }
}