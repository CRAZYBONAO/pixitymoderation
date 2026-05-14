package org.howie.pixity.moderation.neoforge.spawners;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

public class SpawnerListener {

    private final RankService perms;
    private final SpawnerStackService stacks;
    private final SpawnerHologramService holograms = new SpawnerHologramService();

    public SpawnerListener(RankService perms,
                           SpawnerStackService stacks) {
        this.perms = perms;
        this.stacks = stacks;
    }





    @SubscribeEvent
    public void onBreak(BlockEvent.BreakEvent e) {

        if (!(e.getPlayer() instanceof ServerPlayer player)) return;

        BlockState state = e.getState();
        if (state.getBlock() != Blocks.SPAWNER) return;

        if (!perms.hasPerm(player, "pixity.spawners.break"))
            return;

        boolean bypass =
                perms.hasPerm(player, "pixity.spawners.nosilkneed");

        boolean silk =
                player.getMainHandItem()
                        .getEnchantmentLevel(
                                player.server.registryAccess()
                                        .lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
                                        .getOrThrow(Enchantments.SILK_TOUCH)
                        ) > 0;

        if (!silk && !bypass) return;

        ServerLevel level = (ServerLevel) e.getLevel();
        BlockPos pos = e.getPos();

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof SpawnerBlockEntity spawner)) return;


        String mob = stacks.getMob(spawner);

        int stack = stacks.getStack(spawner);


        if (stack > 1) {

            stacks.setStack(spawner, stack - 1);

            holograms.remove(level, pos);
            holograms.spawn(
                    level,
                    pos,
                    format(mob, stack - 1)
            );

            stacks.dropStacked(
                    level,
                    pos,
                    mob,
                    1
            );

            spawner.setChanged();
            e.setCanceled(true);
            return;
        }


        e.setCanceled(true);

        holograms.remove(level, pos);

        level.setBlockAndUpdate(
                pos,
                Blocks.AIR.defaultBlockState()
        );

        stacks.dropStacked(
                level,
                pos,
                mob,
                1
        );
    }





    @SubscribeEvent
    public void onPlace(PlayerInteractEvent.RightClickBlock e) {

        if (!(e.getEntity() instanceof ServerPlayer player)) return;

        ItemStack item = player.getMainHandItem();

        if (!SpawnerAPI.isSpawner(item)) return;

        String mob = SpawnerAPI.getType(item);

        ServerLevel level = (ServerLevel) player.level();

        BlockPos pos = e.getPos();

        BlockEntity be = level.getBlockEntity(pos);





        if (be instanceof SpawnerBlockEntity spawner) {

            String existing = stacks.getMob(spawner);

            if (!existing.equals(mob)) return;

            int current = stacks.getStack(spawner);
            int max = SpawnerStackService.MAX_STACK;

            int add = Math.min(item.getCount(), max - current);

            if (add <= 0) return;

            stacks.setStack(spawner, current + add);

            holograms.remove(level, pos);
            holograms.spawn(
                    level,
                    pos,
                    format(mob, current + add)
            );

            item.shrink(add);


            e.setCanceled(true);
            return;
        }





        BlockPos placePos = pos.relative(e.getFace());

        level.getServer().execute(() -> {

            BlockEntity delayed =
                    level.getBlockEntity(placePos);

            if (!(delayed instanceof SpawnerBlockEntity spawner))
                return;

            SpawnerAPI.apply(
                    spawner,
                    net.minecraft.resources.ResourceLocation.parse(mob),
                    level,
                    placePos
            );

            stacks.setMob(spawner, mob);
            stacks.setStack(spawner, 1);

            holograms.spawn(
                    level,
                    placePos,
                    format(mob, 1)
            );

            spawner.setChanged();
        });
    }





    private String format(String mob, int stack) {

        String nice =
                mob.replace("minecraft:", "")
                        .replace("_", " ");

        nice =
                Character.toUpperCase(nice.charAt(0))
                        + nice.substring(1);

        return "§e" + nice + " Spawner\n§7Stack: §f" + stack;
    }
}