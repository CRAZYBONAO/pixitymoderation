package org.howie.pixity.moderation.neoforge.skills;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.howie.pixity.moderation.PixityModerationNeoForge;

@EventBusSubscriber
public class DurabilityListener {




    @SubscribeEvent
    public static void onTick(PlayerTickEvent.Post event) {

        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        var inv = player.getInventory();

        for (int i = 0; i < inv.getContainerSize(); i++) {

            ItemStack item = inv.getItem(i);

            if (!shouldCancelDamage(player, item)) continue;

            repair(item);
        }




        for (ItemStack armor : player.getInventory().armor) {

            if (!shouldCancelDamage(player, armor)) continue;

            repair(armor);
        }
    }




    public static boolean shouldCancelDamage(ServerPlayer player, ItemStack item) {

        if (item.isEmpty() || !item.isDamageableItem()) return false;

        var skills = PixityModerationNeoForge.SKILL_SERVICE;
        var abilities = PixityModerationNeoForge.ABILITY_ENGINE;

        var data = skills.get(player.getUUID());




        if (item.is(net.minecraft.tags.ItemTags.PICKAXES)) {
            return data.getLevel(SkillType.MINER) >= 75
                    && abilities.isEnabled(player, AbilityType.MINERS_BEST_FRIEND);
        }




        if (item.is(net.minecraft.tags.ItemTags.SHOVELS)) {
            return data.getLevel(SkillType.EXCAVATION) >= 75
                    && abilities.isEnabled(player, AbilityType.EXCAVATORS_BEST_FRIEND);
        }




        if (item.is(net.minecraft.tags.ItemTags.AXES)) {
            return data.getLevel(SkillType.WOODCUTTER) >= 75
                    && abilities.isEnabled(player, AbilityType.WOODCUTTERS_BEST_FRIEND);
        }




        if (item.is(net.minecraft.tags.ItemTags.HOES)) {
            return data.getLevel(SkillType.FARMER) >= 75
                    && abilities.isEnabled(player, AbilityType.FARMERS_DREAM);
        }




        if (item.is(net.minecraft.tags.ItemTags.SWORDS)) {
            return data.getLevel(SkillType.HUNTER) >= 75
                    && abilities.isEnabled(player, AbilityType.HUNTERS_DREAM);
        }




        if (item.getItem() instanceof ArmorItem) {
            return data.getLevel(SkillType.KILLER) >= 75
                    && abilities.isEnabled(player, AbilityType.KILLERS_DREAM);
        }

        return false;
    }




    private static void repair(ItemStack item) {

        if (item.getDamageValue() > 0) {
            item.setDamageValue(item.getDamageValue() - 1);
        }
    }
}