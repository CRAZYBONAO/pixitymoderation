package org.howie.pixity.moderation.neoforge.spawners;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public class MobStackDeathListener {

    @SubscribeEvent
    public void onDamage(LivingIncomingDamageEvent e) {

        if (!(e.getEntity() instanceof Mob mob)) return;

        var tag = mob.getPersistentData();

        if (!tag.contains("pixity_mob_stack")) return;
        if (!tag.getBoolean("pixity_spawner_mob")) return;

        int stack = tag.getInt("pixity_mob_stack");

        float damage = e.getAmount();
        float healthAfter = mob.getHealth() - damage;


        if (healthAfter > 0) return;


        if (stack <= 1) return;

        ServerLevel level = (ServerLevel) mob.level();





        var lootTables = level.getServer().reloadableRegistries();

        LootTable table = lootTables.getLootTable(mob.getLootTable());

        var killer =
                e.getSource().getEntity() instanceof net.minecraft.world.entity.player.Player p ? p : null;

        int looting = 0;

        if (killer != null) {

            var registry = level.registryAccess()
                    .lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT);

            var lootingEnchant = registry.getOrThrow(net.minecraft.world.item.enchantment.Enchantments.LOOTING);

            looting = net.minecraft.world.item.enchantment.EnchantmentHelper.getItemEnchantmentLevel(
                    lootingEnchant,
                    killer.getMainHandItem()
            );
        }

        LootParams params = new LootParams.Builder(level)
                .withParameter(LootContextParams.THIS_ENTITY, mob)
                .withParameter(LootContextParams.ORIGIN, mob.position())
                .withParameter(LootContextParams.DAMAGE_SOURCE, e.getSource())
                .withOptionalParameter(LootContextParams.LAST_DAMAGE_PLAYER, killer)
                .withOptionalParameter(
                        LootContextParams.TOOL,
                        killer != null ? killer.getMainHandItem() : ItemStack.EMPTY
                )
                .create(net.minecraft.world.level.storage.loot.parameters.LootContextParamSets.ENTITY);


        int rolls = 1 + looting;

        for (int i = 0; i < rolls; i++) {
            for (ItemStack drop : table.getRandomItems(params)) {
                mob.spawnAtLocation(drop, 0.0f);
            }
        }





        int xp = mob.getExperienceReward(level, mob);

        if (xp > 0) {
            net.minecraft.world.entity.ExperienceOrb.award(
                    level,
                    mob.position(),
                    xp
            );
        }





        stack--;

        tag.putInt("pixity_mob_stack", stack);

        e.setCanceled(true);

        mob.setHealth(mob.getMaxHealth());
        mob.invulnerableTime = 0;
        mob.hurtTime = 0;
        mob.hurtDuration = 0;

        MobStackHologram.update(mob, stack);
    }
}