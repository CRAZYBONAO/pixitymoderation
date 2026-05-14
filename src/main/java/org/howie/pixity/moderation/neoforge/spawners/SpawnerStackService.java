package org.howie.pixity.moderation.neoforge.spawners;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;

public class SpawnerStackService {


    private static final String MOB_KEY = "pixity_mob";
    private static final String STACK_KEY = "pixity_stack";
    public static final int MAX_STACK = 64;

    private final SpawnerDatabase db;
    private final SpawnerConfig config;

    public SpawnerStackService(
            SpawnerDatabase db,
            SpawnerConfig config
    ) {
        this.db = db;
        this.config = config;
    }





    public int getStack(SpawnerBlockEntity spawner) {

        CompoundTag tag =
                spawner.getPersistentData();

        return tag.contains(STACK_KEY)
                ? tag.getInt(STACK_KEY)
                : 1;
    }





    public void setStack(SpawnerBlockEntity spawner, int amount) {

        CompoundTag tag = spawner.getPersistentData();

        tag.putInt(STACK_KEY, amount);


        if (!tag.contains(MOB_KEY)) {
            tag.putString(MOB_KEY, getMobFromSpawner(spawner));
        }

        save(spawner, amount);

        spawner.setChanged();
    }

    public void setMob(SpawnerBlockEntity spawner, String mob) {

        CompoundTag tag = spawner.getPersistentData();

        tag.putString(MOB_KEY, mob);

        spawner.setChanged();
    }





    public void addStack(SpawnerBlockEntity spawner, int amount) {

        int current = getStack(spawner);

        setStack(spawner, current + amount);
    }





    public ItemStack createStacked(String mob, int amount) {

        ItemStack item =
                SpawnerAPI.create(mob);

        item.setCount(amount);

        return item;
    }





    public void dropStacked(
            ServerLevel level,
            BlockPos pos,
            String mob,
            int amount
    ) {

        ItemStack item =
                createStacked(mob, amount);

        net.minecraft.world.Containers.dropItemStack(
                level,
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                item
        );
    }

    private void save(
            SpawnerBlockEntity spawner,
            int stack
    ) {

        var pos = spawner.getBlockPos();

        var level =
                (net.minecraft.server.level.ServerLevel)
                        spawner.getLevel();

        if (!level.dimension()
                .location()
                .toString()
                .equals("minecraft:overworld"))
            return;

        String mob = getMob(spawner);

        db.save(
                level.dimension().location().toString(),
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                mob,
                stack
        );
    }

    public void loadAll(ServerLevel level) {

        var data = db.load();

        data.forEach((key, value) -> {

            String[] split = key.split(":");

            String world = split[0];

            if (!level.dimension()
                    .location()
                    .toString()
                    .equals(world))
                return;

            int x = Integer.parseInt(split[1]);
            int y = Integer.parseInt(split[2]);
            int z = Integer.parseInt(split[3]);

            var be =
                    level.getBlockEntity(
                            new BlockPos(x,y,z)
                    );

            if (be instanceof SpawnerBlockEntity spawner) {

                setStack(spawner, value.stack());
            }
        });
    }

    public String getMob(SpawnerBlockEntity spawner) {

        CompoundTag tag = spawner.getPersistentData();

        if (tag.contains(MOB_KEY))
            return tag.getString(MOB_KEY);

        return getMobFromSpawner(spawner);
    }

    private String getMobFromSpawner(SpawnerBlockEntity spawner) {

        var level =
                (ServerLevel) spawner.getLevel();

        var tag =
                spawner.saveWithFullMetadata(
                        level.registryAccess()
                );

        if (tag.contains("SpawnData")) {

            var spawn = tag.getCompound("SpawnData");

            if (spawn.contains("entity")) {

                var entity = spawn.getCompound("entity");

                if (entity.contains("id")) {
                    return entity.getString("id");
                }
            }
        }

        return "minecraft:pig";
    }


}