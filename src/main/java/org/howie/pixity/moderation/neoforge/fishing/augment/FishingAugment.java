package org.howie.pixity.moderation.neoforge.fishing.augment;

import net.minecraft.world.item.Items;

import java.util.List;

import static org.howie.pixity.moderation.neoforge.fishing.FishingItemIds.*;


public enum FishingAugment {





    HOTSPOT("Hotspot", 50000, List.of(
            Req.item(Items.GLISTERING_MELON_SLICE, 16),
            Req.item(Items.STRING, 32),
            Req.item(Items.GOLDEN_APPLE, 3),
            Req.item(Items.OAK_BOAT, 1),
            Req.custom(DOLPHIN_TREASURE, 4)
    )),

    STORM("Call of the Storm", 40000, List.of(
            Req.item(Items.WATER_BUCKET, 1),
            Req.item(Items.OAK_BOAT, 1),
            Req.item(Items.COD, 16),
            Req.item(Items.LILY_PAD, 4),
            Req.item(Items.SPONGE, 1)
    )),

    SATURATION("Saturation", 35000, List.of(
            Req.item(Items.COOKED_BEEF, 16),
            Req.item(Items.CAKE, 1),
            Req.item(Items.COD, 16),
            Req.item(Items.BAKED_POTATO, 12)
    )),

    SAGE("Sage", 57500, List.of(
            Req.item(Items.GOLD_BLOCK, 8),
            Req.custom(CRAB_CLAW, 16),
            Req.custom(CRAB_SCALE, 16),
            Req.custom(DOLPHIN_TREASURE, 3),
            Req.item(Items.GUNPOWDER, 4),
            Req.item(Items.REDSTONE, 16),
            Req.item(Items.SUGAR, 16)
    )),

    BIOME("Biome Disruption", 60000, List.of(
            Req.item(Items.OAK_LOG, 16),
            Req.item(Items.SNOWBALL, 16),
            Req.item(Items.SAND, 32),
            Req.item(Items.KELP, 32),
            Req.item(Items.WATER_BUCKET, 1),
            Req.custom(DOLPHIN_TREASURE, 12),
            Req.custom(CRAB_SCALE, 8)
    )),

    PRECISION("Precision Cutting", 70000, List.of(
            Req.item(Items.ANVIL, 1),
            Req.item(Items.DIAMOND_SWORD, 1),
            Req.item(Items.IRON_AXE, 1),
            Req.item(Items.COBBLESTONE, 16),
            Req.item(Items.EMERALD, 2),
            Req.item(Items.LAPIS_LAZULI, 3),
            Req.item(Items.WATER_BUCKET, 1),
            Req.item(Items.WITHER_SKELETON_SKULL, 1),
            Req.item(Items.NAUTILUS_SHELL, 16)
    )),

    CRAB_BAIT("Crab Bait", 40000, List.of(
            Req.item(Items.WATER_BUCKET, 1),
            Req.custom(CRAB_CLAW, 8),
            Req.custom(CRAB_SCALE, 8),
            Req.item(Items.STRING, 64)
    )),

    INTELLECT("Intellect", 50000, List.of(
            Req.item(Items.LAPIS_BLOCK, 20),
            Req.item(Items.BOOK, 8),
            Req.item(Items.DIAMOND, 8),
            Req.item(Items.EMERALD_BLOCK, 8),
            Req.custom(CRAB_SCALE, 16)
    )),

    PERCEPTION("Perception", 75000, List.of(
            Req.item(Items.GLASS, 32),
            Req.item(Items.GLOWSTONE, 4),
            Req.item(Items.TURTLE_EGG, 3),
            Req.item(Items.NAUTILUS_SHELL, 10),
            Req.custom(CRAB_CLAW, 10)
    )),

    TROPHY("Trophy", 60000, List.of(
            Req.item(Items.IRON_BLOCK, 32),
            Req.item(Items.GOLD_BLOCK, 16),
            Req.item(Items.DIAMOND_BLOCK, 12),
            Req.item(Items.EMERALD_BLOCK, 12),
            Req.custom(SQUID_TENTACLE, 12),
            Req.item(Items.HEART_OF_THE_SEA, 1)
    )),

    SOLAR("Solar Rage", 70000, List.of(
            Req.item(Items.GOLD_BLOCK, 8),
            Req.item(Items.DIAMOND_BLOCK, 5),
            Req.item(Items.EMERALD_BLOCK, 12),
            Req.custom(CRAB_CLAW, 12),
            Req.custom(CRAB_SCALE, 12),
            Req.custom(SQUID_TENTACLE, 10)
    )),

    MASTER("Master Fisherman", 120000, List.of(
            Req.item(Items.WITHER_SKELETON_SKULL, 2),
            Req.item(Items.GHAST_TEAR, 8),
            Req.custom(CRAB_CLAW, 8),
            Req.custom(CRAB_SCALE, 8),
            Req.custom(DOLPHIN_TREASURE, 8),
            Req.custom(SQUID_TENTACLE, 12),
            Req.item(Items.HEART_OF_THE_SEA, 1),
            Req.item(Items.NAUTILUS_SHELL, 16)
    ));

    public final String display;
    public final int entropyCost;
    public final List<Req> requirements;

    FishingAugment(String display, int entropyCost, List<Req> req) {
        this.display = display;
        this.entropyCost = entropyCost;
        this.requirements = req;
    }


    public static class Req {

        public final net.minecraft.world.item.Item item;
        public final String customId;
        public final int amount;

        private Req(net.minecraft.world.item.Item item, String customId, int amount) {
            this.item = item;
            this.customId = customId;
            this.amount = amount;
        }

        public static Req item(net.minecraft.world.item.Item item, int amount) {
            return new Req(item, null, amount);
        }

        public static Req custom(String id, int amount) {
            return new Req(null, id, amount);
        }
    }
}