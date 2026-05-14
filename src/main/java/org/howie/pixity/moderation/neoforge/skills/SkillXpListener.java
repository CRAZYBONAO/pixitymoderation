package org.howie.pixity.moderation.neoforge.skills;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.howie.pixity.moderation.PixityModerationNeoForge;
import org.howie.pixity.moderation.chat.TextFormatter;


@EventBusSubscriber
public class SkillXpListener {





    private static SkillService skills;

    private static final ThreadLocal<Boolean> DRILLING = ThreadLocal.withInitial(() -> false);
    private static final ThreadLocal<Boolean> DIGGING = ThreadLocal.withInitial(() -> false);

    public static void init(SkillService service) {
        skills = service;
    }




    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {

        if (skills == null) return;
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;

        var state = event.getState();
        var block = state.getBlock();




        SkillXpRouter.onCropBreak(player, state, skills);
        SkillXpRouter.onBlockBreak(player, block, skills);





        if (SkillXpTables.WOODCUTTING.containsKey(block)
                && player.getMainHandItem().is(ItemTags.AXES)
                && PixityModerationNeoForge.ACTIVE_ABILITIES.isActive(player, AbilityType.TREECAPITATOR)
                && state.is(BlockTags.LOGS)) {

            TreecapitatorHelper.breakTree(player, event.getPos());
        }









        if (PixityModerationNeoForge.ACTIVE_ABILITIES.isActive(player, AbilityType.MINERS_FRENZY)) {

            int level = skills.get(player.getUUID()).getLevel(SkillType.MINER);
            int amplifier = Math.min(4, 1 + (level / 25));

            if (!player.hasEffect(net.minecraft.world.effect.MobEffects.DIG_SPEED)) {
                player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                        net.minecraft.world.effect.MobEffects.DIG_SPEED,
                        40,
                        amplifier,
                        false,
                        false
                ));
            }
        }




        if (SkillXpTables.WOODCUTTING.containsKey(block)
                && PixityModerationNeoForge.ACTIVE_ABILITIES.isActive(player, AbilityType.WOODCUTTER_FRENZY)) {



            int level = skills.get(player.getUUID()).getLevel(SkillType.WOODCUTTER);
            int amplifier = Math.min(4, 1 + (level / 25));

            if (!player.hasEffect(net.minecraft.world.effect.MobEffects.DIG_SPEED)) {
                player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                        net.minecraft.world.effect.MobEffects.DIG_SPEED,
                        40,
                        amplifier,
                        false,
                        false
                ));
            }
        }






        if (PixityModerationNeoForge.ACTIVE_ABILITIES.isActive(player, AbilityType.DRILL)) {

            var level = (ServerLevel) player.level();
            var origin = event.getPos();
            var face = player.getDirection();

            if (DRILLING.get()) return;
            DRILLING.set(true);

            try {

                for (int x = -1; x <= 1; x++) {
                    for (int y = -1; y <= 1; y++) {

                        BlockPos target;

                        if (face == net.minecraft.core.Direction.UP || face == net.minecraft.core.Direction.DOWN) {
                            target = origin.offset(x, 0, y);
                        } else if (face == net.minecraft.core.Direction.NORTH || face == net.minecraft.core.Direction.SOUTH) {
                            target = origin.offset(x, y, 0);
                        } else {
                            target = origin.offset(0, x, y);
                        }

                        if (target.equals(origin)) continue;

                        var state2 = level.getBlockState(target);


                        if (!player.getMainHandItem().isCorrectToolForDrops(state2)) continue;


                        if (state2.getBlock() != block) continue;

                        SkillXpRouter.onBlockBreak(player, state2.getBlock(), skills);

                        level.destroyBlock(target, true, player);
                    }

                }

            } finally {
                DRILLING.set(false);
            }
        }







        if (SkillXpTables.MINING.containsKey(block)
                && player.getMainHandItem().is(ItemTags.PICKAXES)) {

            int level = skills.get(player.getUUID()).getLevel(SkillType.MINER);




            if (PixityModerationNeoForge.ABILITY_ENGINE.isEnabled(player, AbilityType.MINERS_LUCK)
                    && meetsPassiveRequirement(player, AbilityType.MINERS_LUCK)
                    && PixityModerationNeoForge.ABILITY_ENGINE.roll(player, level * 0.25)) {

                KeepHelper.duplicateDrops(player, block, state, event.getPos());
            }
        }






        if (SkillXpTables.EXCAVATION.containsKey(block)
                && player.getMainHandItem().is(net.minecraft.tags.ItemTags.SHOVELS)) {

            int level = skills.get(player.getUUID()).getLevel(SkillType.EXCAVATION);









            if (PixityModerationNeoForge.ACTIVE_ABILITIES.isActive(player, AbilityType.QUICK_DIG)) {

                if (DIGGING.get()) return;
                DIGGING.set(true);

                try {

                    var origin = event.getPos();

                    for (int x = -1; x <= 1; x++) {
                        for (int z = -1; z <= 1; z++) {

                            var pos = origin.offset(x, 0, z);

                            if (pos.equals(origin)) continue;

                            var state2 = player.level().getBlockState(pos);


                            if (!player.getMainHandItem().isCorrectToolForDrops(state2)) continue;

                            if (!SkillXpTables.EXCAVATION.containsKey(state2.getBlock())) continue;

                            ((ServerLevel) player.level()).destroyBlock(pos, true, player);
                        }
                    }

                } finally {
                    DIGGING.set(false);
                }
            }




            if (SkillXpTables.EXCAVATION.containsKey(block)
                    && player.getMainHandItem().is(ItemTags.SHOVELS)) {

                int level2 = skills.get(player.getUUID()).getLevel(SkillType.EXCAVATION);

                if (PixityModerationNeoForge.ABILITY_ENGINE.isEnabled(player, AbilityType.EXCAVATORS_KEEP)
                        && meetsPassiveRequirement(player, AbilityType.EXCAVATORS_KEEP)
                        && PixityModerationNeoForge.ABILITY_ENGINE.roll(player, level2 * 0.25)) {

                    KeepHelper.duplicateDrops(player, block, state, event.getPos());
                }
            }
        }




        if (SkillXpTables.WOODCUTTING.containsKey(block)
                && player.getMainHandItem().is(ItemTags.AXES)) {

            int level = skills.get(player.getUUID()).getLevel(SkillType.WOODCUTTER);

            if (PixityModerationNeoForge.ABILITY_ENGINE.isEnabled(player, AbilityType.WOODCUTTERS_KEEP)
                    && meetsPassiveRequirement(player, AbilityType.WOODCUTTERS_KEEP)
                    && PixityModerationNeoForge.ABILITY_ENGINE.roll(player, level * 0.25)) {

                KeepHelper.duplicateDrops(player, block, state, event.getPos());
            }
        }


        if (SkillXpTables.FARMING.containsKey(block)) {

            int level = skills.get(player.getUUID()).getLevel(SkillType.FARMER);

            if (PixityModerationNeoForge.ABILITY_ENGINE.isEnabled(player, AbilityType.FARMERS_KEEP)
                    && meetsPassiveRequirement(player, AbilityType.FARMERS_KEEP)
                    && PixityModerationNeoForge.ABILITY_ENGINE.roll(player, level * 0.25)) {

                KeepHelper.duplicateCropOnly(player, block, event.getPos());
            }
        }





        if (SkillXpTables.FARMING.containsKey(block)) {

            int level = skills.get(player.getUUID()).getLevel(SkillType.FARMER);

            if (PixityModerationNeoForge.ABILITY_ENGINE.isEnabled(player, AbilityType.MAGNETIC_CROPS)
                    && meetsPassiveRequirement(player, AbilityType.MAGNETIC_CROPS)
                    && PixityModerationNeoForge.ABILITY_ENGINE.roll(player, level * 0.75)) {

                KeepHelper.duplicateDrops(player, block, state, event.getPos());
            }
        }




        if (SkillXpTables.FARMING.containsKey(block)) {

            int level = skills.get(player.getUUID()).getLevel(SkillType.FARMER);

            if (PixityModerationNeoForge.ABILITY_ENGINE.isEnabled(player, AbilityType.REPLANTER)
                    && meetsPassiveRequirement(player, AbilityType.REPLANTER)
                    && PixityModerationNeoForge.ABILITY_ENGINE.roll(player, level * 1.0)) {

                var levelObj = (ServerLevel) player.level();
                var pos = event.getPos();

                levelObj.getServer().execute(() -> {

                    var below = levelObj.getBlockState(pos.below());




                    if (block instanceof net.minecraft.world.level.block.CropBlock crop) {

                        if (!below.is(net.minecraft.world.level.block.Blocks.FARMLAND)) return;

                        var stateNew = crop.defaultBlockState();


                        for (var prop : stateNew.getProperties()) {
                            if (prop instanceof net.minecraft.world.level.block.state.properties.IntegerProperty intProp) {
                                stateNew = stateNew.setValue(intProp, 0);
                                break;
                            }
                        }

                        levelObj.setBlock(pos, stateNew, 3);
                        return;
                    }




                    if (block == net.minecraft.world.level.block.Blocks.SUGAR_CANE) {

                        if (!below.is(net.minecraft.world.level.block.Blocks.SUGAR_CANE)) {
                            levelObj.setBlock(pos, net.minecraft.world.level.block.Blocks.SUGAR_CANE.defaultBlockState(), 3);
                        }
                        return;
                    }




                    if (block == net.minecraft.world.level.block.Blocks.CACTUS) {

                        if (!below.is(net.minecraft.world.level.block.Blocks.CACTUS)) {
                            levelObj.setBlock(pos, net.minecraft.world.level.block.Blocks.CACTUS.defaultBlockState(), 3);
                        }
                        return;
                    }




                    if (block == net.minecraft.world.level.block.Blocks.COCOA) {


                        for (var dir : net.minecraft.core.Direction.Plane.HORIZONTAL) {

                            var attachPos = pos.relative(dir);
                            var attachState = levelObj.getBlockState(attachPos);

                            if (attachState.is(net.minecraft.tags.BlockTags.JUNGLE_LOGS)) {

                                var cocoa = net.minecraft.world.level.block.Blocks.COCOA.defaultBlockState()
                                        .setValue(net.minecraft.world.level.block.CocoaBlock.FACING, dir)
                                        .setValue(net.minecraft.world.level.block.CocoaBlock.AGE, 0);

                                levelObj.setBlock(pos, cocoa, 3);
                                return;
                            }
                        }
                    }




                    if (block == net.minecraft.world.level.block.Blocks.MELON
                            || block == net.minecraft.world.level.block.Blocks.PUMPKIN) {


                        return;
                    }




                    if (block == net.minecraft.world.level.block.Blocks.SEA_PICKLE) {

                        levelObj.setBlock(pos,
                                net.minecraft.world.level.block.Blocks.SEA_PICKLE.defaultBlockState()
                                        .setValue(net.minecraft.world.level.block.SeaPickleBlock.PICKLES, 1),
                                3);
                        return;
                    }

                });
            }
        }

    }

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {

        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        var block = event.getState().getBlock();

        if (SkillXpTables.EXCAVATION.containsKey(block)
                && PixityModerationNeoForge.ACTIVE_ABILITIES.isActive(player, AbilityType.DIGGING_FRENZY)) {

            event.setNewSpeed(9999f);
        }
    }




    @SubscribeEvent
    public static void onKill(net.neoforged.neoforge.event.entity.living.LivingDeathEvent event) {

        if (skills == null) return;

        if (!(event.getSource().getEntity() instanceof ServerPlayer killer)) return;

        var entity = event.getEntity();




        if (entity instanceof ServerPlayer victim) {


            SkillXpRouter.onPlayerKill(killer, victim, skills);




            int level = skills.get(killer.getUUID()).getLevel(SkillType.KILLER);

            if (PixityModerationNeoForge.ABILITY_ENGINE.isEnabled(killer, AbilityType.KILLERS_KEEP)
                    && PixityModerationNeoForge.ABILITY_ENGINE.roll(killer, level * 0.25)) {


                double bonus = 50 + (level * 5);

                skills.addXp(killer, SkillType.KILLER, bonus);
            }

            return;
        }




        SkillXpRouter.onMobKill(killer, entity, skills);




        int level = skills.get(killer.getUUID()).getLevel(SkillType.HUNTER);

        if (PixityModerationNeoForge.ABILITY_ENGINE.isEnabled(killer, AbilityType.HUNTERS_KEEP)
                && PixityModerationNeoForge.ABILITY_ENGINE.roll(killer, level * 0.25)) {

            var levelAccessor = (net.minecraft.server.level.ServerLevel) killer.level();

            var lootContext = new net.minecraft.world.level.storage.loot.LootParams.Builder(levelAccessor)
                    .withParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.THIS_ENTITY, entity)
                    .withParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.ORIGIN, entity.position())
                    .withParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.DAMAGE_SOURCE, event.getSource())
                    .withOptionalParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.LAST_DAMAGE_PLAYER, killer)
                    .create(net.minecraft.world.level.storage.loot.parameters.LootContextParamSets.ENTITY);

            var lootTable = levelAccessor.getServer()
                    .reloadableRegistries()
                    .getLootTable(entity.getLootTable());

            var generated = lootTable.getRandomItems(lootContext);

            for (var item : generated) {
                entity.spawnAtLocation(item);
            }
        }
    }




    @SubscribeEvent
    public static void onCraft(PlayerEvent.ItemCraftedEvent event) {

        if (skills == null) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        SkillXpRouter.onCraft(player, event.getCrafting(), skills);

        int level = skills.get(player.getUUID()).getLevel(SkillType.CRAFTER);

        if (PixityModerationNeoForge.ABILITY_ENGINE.isEnabled(player, AbilityType.CRAFTERS_KEEP)
                && PixityModerationNeoForge.ABILITY_ENGINE.roll(player, level * 0.25)) {

            player.addItem(event.getCrafting().copy());
        }
    }

    @SubscribeEvent
    public static void onPlace(BlockEvent.EntityPlaceEvent event) {

        if (skills == null) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        var state = event.getPlacedBlock();
        var block = state.getBlock();
        var pos = event.getPos();

        SkillXpRouter.onBlockPlace(player, block, pos, skills);
        SkillXpRouter.onPlant(player, block, skills);




        int level = skills.get(player.getUUID()).getLevel(SkillType.BUILDER);

        if (PixityModerationNeoForge.ABILITY_ENGINE.isEnabled(player, AbilityType.BUILDERS_KEEP)
                && PixityModerationNeoForge.ABILITY_ENGINE.roll(player, level * 0.25)) {

            player.addItem(new net.minecraft.world.item.ItemStack(block));
        }
    }

    @SubscribeEvent
    public static void onBrewTake(PlayerContainerEvent.Open event) {

    }

    @SubscribeEvent
    public static void onEnchant(net.neoforged.neoforge.event.entity.player.PlayerEnchantItemEvent event) {

        if (skills == null) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        ItemStack item = event.getEnchantedItem();


        int xp = getEnchantMaterialXp(item);

        xp += item.getEnchantments().size() * 5;

        SkillXpRouter.onEnchant(player, item, xp, skills);
    }





    @SubscribeEvent
    public static void onTick(ServerTickEvent.Post event) {

        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
            SkillXpBuffer.tick(player);
            PixityModerationNeoForge.ACTIVE_ABILITIES.tick(player);
        }
    }





    private static int getEnchantMaterialXp(ItemStack item) {

        var i = item.getItem();


        if (i == Items.NETHERITE_SWORD || i == Items.NETHERITE_PICKAXE ||
                i == Items.NETHERITE_AXE || i == Items.NETHERITE_SHOVEL ||
                i == Items.NETHERITE_HELMET || i == Items.NETHERITE_CHESTPLATE ||
                i == Items.NETHERITE_LEGGINGS || i == Items.NETHERITE_BOOTS || i == Items.NETHERITE_HOE) {
            return 500;
        }


        if (i == Items.DIAMOND_SWORD || i == Items.DIAMOND_PICKAXE ||
                i == Items.DIAMOND_AXE || i == Items.DIAMOND_SHOVEL ||
                i == Items.DIAMOND_HELMET || i == Items.DIAMOND_CHESTPLATE ||
                i == Items.DIAMOND_LEGGINGS || i == Items.DIAMOND_BOOTS || i == Items.DIAMOND_HOE) {
            return 75;
        }

        if (i == Items.BOOK) {
            return 50;
        }


        if (i == Items.GOLDEN_SWORD || i == Items.GOLDEN_PICKAXE ||
                i == Items.GOLDEN_AXE || i == Items.GOLDEN_SHOVEL ||
                i == Items.GOLDEN_HELMET || i == Items.GOLDEN_CHESTPLATE ||
                i == Items.GOLDEN_LEGGINGS || i == Items.GOLDEN_BOOTS || i == Items.GOLDEN_HOE) {
            return 25;
        }




        if (i == Items.IRON_SWORD || i == Items.IRON_PICKAXE ||
                i == Items.IRON_AXE || i == Items.IRON_SHOVEL ||
                i == Items.IRON_HELMET || i == Items.IRON_CHESTPLATE ||
                i == Items.IRON_LEGGINGS || i == Items.IRON_BOOTS || i == Items.IRON_HOE) {
            return 15;
        }


        if (i == Items.STONE_SWORD || i == Items.STONE_PICKAXE ||
                i == Items.STONE_AXE || i == Items.STONE_SHOVEL || i == Items.STONE_HOE) {
            return 5;
        }


        if (i == Items.WOODEN_SWORD || i == Items.WOODEN_PICKAXE ||
                i == Items.WOODEN_AXE || i == Items.WOODEN_SHOVEL ||
                i == Items.LEATHER_HELMET || i == Items.LEATHER_CHESTPLATE ||
                i == Items.LEATHER_LEGGINGS || i == Items.LEATHER_BOOTS || i == Items.WOODEN_HOE) {
            return 1;
        }

        return 1;
    }

    public static double applyScaling(net.minecraft.world.entity.LivingEntity entity, int baseXp) {

        double healthFactor = entity.getMaxHealth() / 20.0;


        double scaling = healthFactor * 0.25;


        var difficulty = entity.level().getDifficulty();

        double diffBonus = switch (difficulty) {
            case EASY -> 0.0;
            case NORMAL -> 0.10;
            case HARD -> 0.25;
            default -> 0.0;
        };

        scaling += diffBonus;

        return baseXp * (1.0 + scaling);
    }

    @SubscribeEvent
    public static void onAbilityTick(net.neoforged.neoforge.event.tick.ServerTickEvent.Post event) {

        var abilities = PixityModerationNeoForge.ACTIVE_ABILITIES;

        for (var player : event.getServer().getPlayerList().getPlayers()) {

            for (AbilityType ability : AbilityType.values()) {

                long remaining = abilities.getRemainingActive(player, ability);

                if (remaining > 0) {

                    player.displayClientMessage(
                            TextFormatter.parse(
                                    "<gradient:#FF0000:#FFFFFF:#FF0000:#FFFFFF:#FF0000>&lPIXITY</gradient> " +
                                            "&7➤ &e" + ability.name().replace("_", " ") +
                                            " &7(" + ActiveAbilityManager.formatTime(remaining) + ")"
                            ),
                            true
                    );
                }




                if (remaining <= 0 && abilities.isActive(player, ability)) {

                    player.sendSystemMessage(
                            TextFormatter.parse(
                                    "<gradient:#FF0000:#FFFFFF:#FF0000:#FFFFFF:#FF0000>&lPIXITY SKILLS</gradient> &7&l➤ " +
                                            "&c" + ability.name().replace("_", " ") + " &7has expired."
                            )
                    );
                }
            }
        }
    }

    private static boolean isSpawnerMob(LivingEntity entity) {
        return entity.getPersistentData().getBoolean("pixity_spawner_mob");
    }

    private static boolean meetsPassiveRequirement(ServerPlayer player, AbilityType ability) {

        var skills = PixityModerationNeoForge.SKILL_SERVICE;

        int required = AbilityRequirements.getRequiredLevel(ability);
        SkillType skill = AbilityRequirements.getSkill(ability);

        int level = skills.get(player.getUUID()).getLevel(skill);

        return level >= required;
    }








}