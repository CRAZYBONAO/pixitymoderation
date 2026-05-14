package org.howie.pixity.moderation.neoforge.skills;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.howie.pixity.moderation.PixityModerationNeoForge;

public class SkillXpRouter {

    public static void onBlockBreak(ServerPlayer player, Block block, SkillService skills) {

        Integer mining = SkillXpTables.MINING.get(block);
        if (mining != null) {
            skills.addXp(player, SkillType.MINER, mining);
            return;
        }

        Integer wood = SkillXpTables.WOODCUTTING.get(block);
        if (wood != null) {
            skills.addXp(player, SkillType.WOODCUTTER, wood);
            return;
        }

        Integer excav = SkillXpTables.EXCAVATION.get(block);
        if (excav != null) {
            skills.addXp(player, SkillType.EXCAVATION, excav);
        }
    }

    public static void onMobKill(ServerPlayer player, LivingEntity entity, SkillService skills) {

        Integer baseXp = SkillXpTables.HUNTER.get(entity.getType());
        if (baseXp == null) return;

        double scaled = SkillXpListener.applyScaling(entity, baseXp);

        var tag = entity.getPersistentData();

        if (tag.getBoolean("pixity_spawner_mob")) {
            scaled *= 0.25;
        }

        int stack = tag.contains("pixity_mob_stack") ? tag.getInt("pixity_mob_stack") : 1;

        scaled *= stack;

        skills.addXp(player, SkillType.HUNTER, scaled);
    }

    public static void onPlayerKill(ServerPlayer killer, ServerPlayer victim, SkillService skills) {

        if (!SkillKillTracker.canGainXp(killer.getUUID(), victim.getUUID())) {
            return;
        }

        int xp = 100;

        skills.addXp(killer, SkillType.KILLER, xp);
    }


    public static void onBlockPlace(ServerPlayer player, Block block, BlockPos pos, SkillService skills) {


        if (SkillXpTables.BUILDER_BLACKLIST.contains(block)) return;


        if (!SkillPlaceTracker.canGainXp(player.getUUID(), pos)) return;

        int xp = 1;

        skills.addXp(player, SkillType.BUILDER, xp);
    }



    private static boolean isFullyGrown(BlockState state) {

        if (state.getBlock() instanceof CropBlock crop) {
            return crop.isMaxAge(state);
        }

        if (state.getBlock() instanceof NetherWartBlock wart) {
            return state.getValue(NetherWartBlock.AGE) >= 3;
        }

        if (state.getBlock() instanceof CocoaBlock cocoa) {
            return state.getValue(CocoaBlock.AGE) >= 2;
        }

        return false;
    }

    public static void onCropBreak(ServerPlayer player, BlockState state, SkillService skills) {

        Double xp = SkillXpTables.FARMING.get(state.getBlock());
        if (xp == null) return;


        if (!isFullyGrown(state)) return;


        if (!SkillPlaceTracker.canGainXp(player.getUUID(), player.blockPosition())) return;


        if (state.getBlock() == Blocks.MELON_STEM || state.getBlock() == Blocks.PUMPKIN_STEM) {
            return;
        }

        skills.addXp(player, SkillType.FARMER, xp);
    }

    public static void onPlant(ServerPlayer player, Block block, SkillService skills) {




        if (block == Blocks.WHEAT
                || block == Blocks.CARROTS
                || block == Blocks.POTATOES
                || block == Blocks.BEETROOTS
                || block == Blocks.MELON_STEM
                || block == Blocks.PUMPKIN_STEM
                || block == Blocks.NETHER_WART) {

            skills.addXp(player, SkillType.FARMER, 1);
        }
    }


    public static void onCraft(ServerPlayer player, ItemStack result, SkillService skills) {

        if (result.isEmpty()) return;

        int base = 2;

        int rarityBonus = switch (result.getRarity()) {
            case COMMON -> 0;
            case UNCOMMON -> 1;
            case RARE -> 2;
            case EPIC -> 3;
        };

        int tierBonus = getTierBonus(result);


        String namespace = BuiltInRegistries.ITEM.getKey(result.getItem()).getNamespace();
        int modBonus = namespace.equals("minecraft") ? 0 : 2;

        int count = result.getCount();

        double xp = (base + rarityBonus + tierBonus + modBonus) * count;

        skills.addXp(player, SkillType.CRAFTER, xp);
    }

    private static int getTierBonus(ItemStack stack) {

        String id = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();


        if (id.contains("netherite")) return 6;
        if (id.contains("diamond")) return 5;
        if (id.contains("gold")) return 3;
        if (id.contains("iron")) return 2;
        if (id.contains("stone")) return 1;

        return 0;
    }

    public static void onEnchant(ServerPlayer player, ItemStack result, int cost, SkillService skills) {

        if (result.isEmpty()) return;

        int enchantValue = 0;

        ItemEnchantments enchants = result.get(DataComponents.ENCHANTMENTS);

        if (enchants != null) {
            for (var entry : enchants.entrySet()) {

                Holder<Enchantment> ench = entry.getKey();
                int level = entry.getIntValue();

                int weight = getEnchantWeight(ench.value());

                enchantValue += level * weight;
            }
        }

        double xp = (cost * 2) + enchantValue;

        skills.addXp(player, SkillType.ENCHANTER, xp);

        int level = skills.get(player.getUUID()).getLevel(SkillType.ENCHANTER);

        if (PixityModerationNeoForge.ABILITY_ENGINE.isEnabled(player, AbilityType.ENCHANTERS_KEEP)
                && PixityModerationNeoForge.ABILITY_ENGINE.roll(player, level * 0.25)) {

            player.giveExperienceLevels(1);
        }
    }

    private static int getEnchantWeight(Enchantment ench) {

        String id = ench.toString().toLowerCase();


        if (id.contains("sharpness") || id.contains("power")) return 3;
        if (id.contains("protection")) return 3;


        if (id.contains("efficiency") || id.contains("unbreaking")) return 2;


        if (id.contains("mending") || id.contains("fortune")) return 4;

        return 1;
    }


    public static void onBrew(ServerPlayer player, ItemStack stack, SkillService skills) {

        var contents = stack.get(DataComponents.POTION_CONTENTS);
        if (contents == null || contents.potion().isEmpty()) return;


        var potionHolder = contents.potion().get();
        var potionId = net.minecraft.core.registries.BuiltInRegistries.POTION.getKey(potionHolder.value()).toString();




        int baseXp = switch (potionId) {


            case "minecraft:swiftness",
                 "minecraft:healing",
                 "minecraft:strength",
                 "minecraft:fire_resistance" -> 50;


            case "minecraft:leaping",
                 "minecraft:water_breathing",
                 "minecraft:night_vision" -> 75;


            case "minecraft:regeneration",
                 "minecraft:invisibility",
                 "minecraft:slow_falling" -> 120;


            case "minecraft:turtle_master" -> 200;

            default -> 40;
        };




        int bonus = 0;

        if (potionId.contains("strong")) {
            bonus += 50;
        }

        if (potionId.contains("long")) {
            bonus += 30;
        }

        if (stack.is(Items.SPLASH_POTION)) {
            bonus += 40;
        }

        if (stack.is(Items.LINGERING_POTION)) {
            bonus += 80;
        }

        int total = baseXp + bonus;




        double wisdom = PixityModerationNeoForge.STAT_ENGINE.getWisdom(player);

        total *= (1 + (wisdom * 0.01));




        if (!BrewCooldown.canGain(player.getUUID())) return;




        skills.addXp(player, SkillType.BREWER, total);
    }

    public static void onPokemonLevelUp(ServerPlayer player, int newLevel, SkillService skills, double multiplier) {

        double xp = 10 + (newLevel * 2);

        int level = skills.get(player.getUUID()).getLevel(SkillType.TRAINER);




        if (PixityModerationNeoForge.ABILITY_ENGINE.isEnabled(player, AbilityType.TRAINERS_INSIGHT)) {
            xp *= (1.0 + (level * 0.01));
        }




        if (PixityModerationNeoForge.ACTIVE_ABILITIES.isActive(player, AbilityType.ADRENALINE_RUSH)) {
            xp *= 1.5;
        }

        skills.addXp(player, SkillType.TRAINER, xp);
    }

    public static void onPokemonCatch(ServerPlayer player, boolean shiny, boolean legendary, boolean mythical, SkillService skills, double multiplier) {

        double xp;

        if (legendary) {
            xp = shiny ? 5000 : 1000;
        } else if (mythical) {
            xp = shiny ? 500 : 150;
        } else {
            xp = shiny ? 25 : 10;
        }

        int level = skills.get(player.getUUID()).getLevel(SkillType.PROFESSOR);




        if (PixityModerationNeoForge.ABILITY_ENGINE.isEnabled(player, AbilityType.PRECISION_THROW)) {
            xp *= (1.0 + (level * 0.01));
        }




        if (PixityModerationNeoForge.ACTIVE_ABILITIES.isActive(player, AbilityType.CAPTURE_AURA)) {
            xp *= 2.0;
        }

        xp *= multiplier;

        skills.addXp(player, SkillType.PROFESSOR, xp);
    }
}