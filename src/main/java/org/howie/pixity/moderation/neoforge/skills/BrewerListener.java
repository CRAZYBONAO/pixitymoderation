package org.howie.pixity.moderation.neoforge.skills;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.BrewingStandBlock;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import org.howie.pixity.moderation.PixityModerationNeoForge;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber
public class BrewerListener {

    private static final Map<BlockPos, ServerPlayer> lastUser = new HashMap<>();
    private static final Map<BlockPos, String> lastPotionState = new HashMap<>();
    private static final Map<BlockPos, String[]> lastPotions = new HashMap<>();




    @SubscribeEvent
    public static void onInteract(PlayerInteractEvent.RightClickBlock event) {

        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        var level = event.getLevel();
        var pos = event.getPos();

        if (level.getBlockState(pos).getBlock() instanceof BrewingStandBlock) {
            lastUser.put(pos, player);
        }
    }




    @SubscribeEvent
    public static void onTick(LevelTickEvent.Post event) {

        if (!(event.getLevel() instanceof ServerLevel level)) return;

        var skills = PixityModerationNeoForge.SKILL_SERVICE;
        var abilities = PixityModerationNeoForge.ABILITY_ENGINE;

        for (var entry : lastUser.entrySet()) {

            BlockPos pos = entry.getKey();
            ServerPlayer player = entry.getValue();

            if (player == null || !player.isAlive()) continue;

            var be = level.getBlockEntity(pos);
            if (!(be instanceof BrewingStandBlockEntity brew)) continue;






            String[] previous = lastPotions.computeIfAbsent(pos, p -> new String[3]);

            for (int i = 0; i < 3; i++) {

                ItemStack stack = brew.getItem(i);
                if (stack.isEmpty()) continue;

                PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
                if (contents == null || contents.potion().isEmpty()) continue;

                var potion = contents.potion().get();
                String current = net.minecraft.core.registries.BuiltInRegistries.POTION
                        .getKey(potion.value())
                        .toString();

                String lastPotion = previous[i];

                if (lastPotion == null || !lastPotion.equals(current)) {


                    if (current.equals("minecraft:awkward")) {
                        previous[i] = current;
                        continue;
                    }

                    System.out.println("BREW DETECTED: " + current);

                    SkillXpRouter.onBrew(player, stack, skills);

                    int levelSkill = skills.get(player.getUUID()).getLevel(SkillType.BREWER);

                    if (abilities.isEnabled(player, AbilityType.STEADY_HANDS)
                            && abilities.roll(player, levelSkill * 0.34)) {

                        upgradePotion(stack);
                    }

                    if (abilities.isEnabled(player, AbilityType.BREW_EFFICIANDO)
                            && abilities.roll(player, levelSkill * 0.34)) {

                        player.addItem(stack.copy());
                    }
                }

                previous[i] = current;
            }
        }
    }




    private static void upgradePotion(ItemStack stack) {

        PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
        if (contents == null) return;

        var optional = contents.potion();
        if (optional.isEmpty()) return;

        var potion = optional.get();

        if (potion.value() == Potions.HEALING) {
            stack.set(DataComponents.POTION_CONTENTS,
                    new PotionContents(Potions.STRONG_HEALING));
        }

        if (potion.value() == Potions.SWIFTNESS) {
            stack.set(DataComponents.POTION_CONTENTS,
                    new PotionContents(Potions.STRONG_SWIFTNESS));
        }

        if (potion.value() == Potions.STRENGTH) {
            stack.set(DataComponents.POTION_CONTENTS,
                    new PotionContents(Potions.STRONG_STRENGTH));
        }
    }
}

